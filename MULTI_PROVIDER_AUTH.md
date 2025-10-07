# Multi-Provider Authentication System

## Overview

Mercury now supports authentication from multiple providers including GitHub, Google, and is future-ready for JWT-based authentication. The system uses a flexible provider-based architecture that allows users to sign in with different OAuth providers or custom authentication methods.

## Architecture

### Database Schema

The `users` table uses a composite approach:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(255) NOT NULL,        -- 'github', 'google', 'jwt', etc.
    provider_id VARCHAR(255) NOT NULL,     -- Provider-specific user ID
    github_id VARCHAR(255),                -- Legacy field (optional)
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    avatar_url VARCHAR(500),
    bio TEXT,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(provider, provider_id)
);
```

### Key Fields

- **`provider`**: The authentication provider (e.g., 'github', 'google', 'jwt')
- **`provider_id`**: The unique identifier from that provider
  - GitHub: GitHub user ID (numeric string)
  - Google: Google sub (subject identifier)
  - JWT: Email or custom identifier
- **`github_id`**: Legacy field for backward compatibility

## Supported Providers

### 1. GitHub OAuth

**Setup:**
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Set callback URL: `http://localhost:3000/api/auth/callback/github`
4. Copy Client ID and Client Secret to `.env`

**User Data Stored:**
- Provider: `github`
- Provider ID: GitHub user ID
- Username: GitHub login
- Email: GitHub email
- Name: GitHub name
- Avatar: GitHub avatar URL
- Bio: GitHub bio
- Location: GitHub location

**Example Database Entry:**
```json
{
  "provider": "github",
  "providerId": "12345678",
  "githubId": "12345678",
  "username": "octocat",
  "email": "octocat@github.com",
  "name": "The Octocat"
}
```

### 2. Google OAuth

**Setup:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Set authorized redirect URI: `http://localhost:3000/api/auth/callback/google`
6. Copy Client ID and Client Secret to `.env`

**User Data Stored:**
- Provider: `google`
- Provider ID: Google sub (subject identifier)
- Username: Email prefix (before @)
- Email: Google email
- Name: Google display name
- Avatar: Google profile picture URL

**Example Database Entry:**
```json
{
  "provider": "google",
  "providerId": "104567890123456789012",
  "githubId": null,
  "username": "john.doe",
  "email": "john.doe@gmail.com",
  "name": "John Doe"
}
```

### 3. JWT Authentication (Future)

**Planned Implementation:**
- Provider: `jwt`
- Provider ID: User email or UUID
- Token-based authentication without OAuth
- Perfect for custom authentication flows

**Example Database Entry:**
```json
{
  "provider": "jwt",
  "providerId": "user@example.com",
  "githubId": null,
  "username": "user",
  "email": "user@example.com"
}
```

## Environment Variables

### Required for All Environments

```env
# Database
POSTGRES_DB=mercury
POSTGRES_USER=mercury_user
POSTGRES_PASSWORD=your_secure_password

# NextAuth
NEXTAUTH_SECRET=your_random_secret_32_chars
NEXTAUTH_URL=http://localhost:3000
```

### GitHub OAuth (Optional but Recommended)

```env
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

### Google OAuth (Optional)

```env
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

## API Endpoints

### Provider-Based Endpoints (Recommended)

```
POST   /api/users
       Body: { provider, providerId, username, email, name, avatarUrl, bio, location }
       Creates or updates a user

GET    /api/users/provider/{provider}/{providerId}
       Gets user by provider and provider ID

GET    /api/users/provider/{provider}
       Gets all users using a specific provider

GET    /api/users/exists/provider/{provider}/{providerId}
       Checks if user exists for provider
```

### Legacy Endpoints (Backward Compatibility)

```
GET    /api/users/github/{githubId}
       Gets user by GitHub ID (legacy)

GET    /api/users/exists/github/{githubId}
       Checks if GitHub user exists (legacy)
```

### General Endpoints

```
GET    /api/users
       Gets all users

GET    /api/users/{id}
       Gets user by database ID

GET    /api/users/username/{username}
       Gets user by username

GET    /api/users/email/{email}
       Gets user by email

DELETE /api/users/{id}
       Deletes a user
```

## Authentication Flow

### 1. User Clicks Sign In
User chooses their preferred provider (GitHub or Google).

### 2. OAuth Redirect
NextAuth.js redirects to the provider's authorization page.

### 3. User Authorizes
User grants permission on the provider's site.

### 4. OAuth Callback
Provider redirects back to Mercury with authorization code.

### 5. NextAuth Processing
NextAuth exchanges code for access token and user profile.

### 6. User Creation/Update
```typescript
// In NextAuth signIn callback
if (provider === 'github') {
  await createOrUpdateUser({
    provider: 'github',
    providerId: githubProfile.id.toString(),
    username: githubProfile.login,
    email: user.email,
    name: user.name,
    avatarUrl: user.image,
    bio: githubProfile.bio,
    location: githubProfile.location
  });
} else if (provider === 'google') {
  await createOrUpdateUser({
    provider: 'google',
    providerId: googleProfile.sub,
    username: user.email?.split('@')[0],
    email: user.email,
    name: user.name,
    avatarUrl: user.image
  });
}
```

### 7. Session Created
NextAuth creates a session with provider info:
```typescript
{
  user: { name, email, image },
  provider: 'github' | 'google',
  providerId: '12345',
  accessToken: '...'
}
```

### 8. Dashboard Access
Frontend uses `useUser()` hook to fetch complete user data from backend:
```typescript
const { user, loading, error } = useUser();
// user contains all database fields including provider info
```

## Frontend Usage

### Custom Hook

```typescript
import { useUser } from '@/hooks/useUser';

function MyComponent() {
  const { user, loading, error, session, isAuthenticated } = useUser();
  
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!isAuthenticated) return <div>Please sign in</div>;
  
  return (
    <div>
      <h1>Hello, {user.name}</h1>
      <p>Provider: {user.provider}</p>
      <p>Email: {user.email}</p>
    </div>
  );
}
```

### API Functions

```typescript
import { getUserByProvider, createOrUpdateUser } from '@/lib/api';

// Get user by provider
const user = await getUserByProvider('github', '12345');

// Create/update user
const newUser = await createOrUpdateUser({
  provider: 'google',
  providerId: 'abc123',
  username: 'john',
  email: 'john@gmail.com'
});
```

## Backend Usage

### Service Layer

```java
@Service
public class UserService {
    // Create or update by provider
    public UserDTO createOrUpdateUser(CreateUserRequest request) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(
            request.getProvider(), 
            request.getProviderId()
        );
        // ...
    }
    
    // Get user by provider
    public Optional<UserDTO> getUserByProviderAndProviderId(
        String provider, 
        String providerId
    ) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(UserDTO::new);
    }
}
```

## Adding New Providers

### 1. Add Provider to NextAuth

```typescript
// app/api/auth/[...nextauth]/route.ts
import NewProvider from "next-auth/providers/newprovider"

providers: [
  GitHubProvider({ ... }),
  GoogleProvider({ ... }),
  NewProvider({
    clientId: process.env.NEW_PROVIDER_CLIENT_ID!,
    clientSecret: process.env.NEW_PROVIDER_CLIENT_SECRET!,
  })
]
```

### 2. Handle Provider in SignIn Callback

```typescript
else if (provider === 'newprovider') {
  const profile = profile as any;
  await createOrUpdateUser({
    provider: 'newprovider',
    providerId: profile.id,
    username: profile.username,
    email: user.email,
    name: user.name,
    avatarUrl: user.image
  });
}
```

### 3. Add Provider to JWT/Session

```typescript
// Already handled automatically by provider === account.provider
```

### 4. Update Next.js Image Config (if needed)

```typescript
// next.config.ts
remotePatterns: [
  {
    protocol: 'https',
    hostname: 'new-provider-cdn.com',
    pathname: '/**',
  }
]
```

### 5. Add Environment Variables

```env
NEW_PROVIDER_CLIENT_ID=...
NEW_PROVIDER_CLIENT_SECRET=...
```

## Migration from GitHub-Only

If you have existing users with only `githubId`, they will still work:

1. Legacy `findByGithubId()` methods are preserved
2. Existing users can be migrated to provider-based system
3. On next login, users will be updated with provider info

**Migration Script (Optional):**
```sql
UPDATE users 
SET provider = 'github', 
    provider_id = github_id 
WHERE provider IS NULL 
  AND github_id IS NOT NULL;
```

## Best Practices

### 1. Always Use Provider + Provider ID
When creating new features, use `provider` and `providerId` instead of provider-specific fields.

```java
// Good
userRepository.findByProviderAndProviderId("github", "12345")

// Avoid (unless for backward compatibility)
userRepository.findByGithubId("12345")
```

### 2. Validate Provider Values
Only accept known providers:

```java
public enum AuthProvider {
    GITHUB("github"),
    GOOGLE("google"),
    JWT("jwt");
    
    private final String value;
    // ...
}
```

### 3. Handle Username Conflicts
Different providers might have username conflicts:

```typescript
// For Google, use email prefix + provider suffix if needed
username: user.email?.split('@')[0] + '_google'
```

### 4. Store Provider-Specific Data
Keep provider-specific fields (like `bio` for GitHub) optional:

```java
private String bio;  // Only populated for GitHub users
```

## Security Considerations

1. **Unique Constraint**: The `(provider, provider_id)` combination is unique
2. **Email Verification**: Trust provider's email verification
3. **Access Tokens**: Stored in session, not database
4. **Provider ID**: Never expose raw provider IDs to users (use database ID)
5. **CSRF Protection**: NextAuth handles CSRF automatically

## Troubleshooting

### User Not Created
- Check backend logs: `docker-compose logs backend`
- Verify database connection
- Check if provider/providerId are being sent correctly

### Google Images Not Loading
- Ensure `lh3.googleusercontent.com` is in Next.js image config
- Check browser console for image errors

### Multiple Accounts for Same User
- Happens if user signs in with GitHub and Google using different emails
- Consider implementing account linking feature
- Or use email as the primary identifier

### Legacy Users Not Working
- Run migration script to add provider/providerId
- Or keep using legacy `findByGithubId()` for old users

## Future Enhancements

1. **Account Linking**: Allow users to link multiple providers
2. **Primary Provider**: Let users choose which provider is primary
3. **Provider Migration**: Move from one provider to another
4. **2FA**: Add two-factor authentication support
5. **Magic Links**: Email-based authentication
6. **Passwordless**: WebAuthn/FIDO2 support

## Summary

✅ **Multi-provider support**: GitHub, Google, JWT-ready  
✅ **Flexible architecture**: Easy to add new providers  
✅ **Backward compatible**: Legacy GitHub-only users still work  
✅ **Type-safe**: Full TypeScript support  
✅ **Well-documented**: Clear API and examples  
✅ **Production-ready**: Proper validation and error handling  

The system is now ready to scale with your authentication needs!
