import NextAuth from "next-auth"
import GitHubProvider from "next-auth/providers/github"
import GoogleProvider from "next-auth/providers/google"
import { createOrUpdateUser } from "@/lib/api"

const handler = NextAuth({
  providers: [
    GitHubProvider({
      clientId: process.env.GITHUB_CLIENT_ID!,
      clientSecret: process.env.GITHUB_CLIENT_SECRET!,
    }),
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
    })
  ],
  callbacks: {
    async signIn({ user, account, profile }) {
      // Create or update user in backend after successful OAuth login
      if (account && profile) {
        try {
          const provider = account.provider; // 'github' or 'google'
          
          if (provider === 'github') {
            const githubProfile = profile as any;
            await createOrUpdateUser({
              provider: 'github',
              providerId: githubProfile.id.toString(),
              username: githubProfile.login || user.name || 'unknown',
              email: user.email || '',
              name: user.name || null,
              avatarUrl: user.image || null,
              bio: githubProfile.bio || null,
              location: githubProfile.location || null,
            });
          } else if (provider === 'google') {
            const googleProfile = profile as any;
            await createOrUpdateUser({
              provider: 'google',
              providerId: googleProfile.sub || googleProfile.id, // Google user ID
              username: user.email?.split('@')[0] || user.name || 'unknown', // Use email prefix as username
              email: user.email || '',
              name: user.name || null,
              avatarUrl: user.image || null,
              bio: null,
              location: null,
            });
          }
          
          console.log(`User created/updated in backend successfully (${provider})`);
        } catch (error) {
          console.error('Failed to create/update user in backend:', error);
          // Still allow sign in even if backend fails
        }
      }
      return true;
    },
    async jwt({ token, account, profile }) {
      if (account) {
        token.accessToken = account.access_token
        token.provider = account.provider
      }
      if (profile) {
        // Store provider-specific ID
        if (account?.provider === 'github') {
          token.providerId = (profile as any).id?.toString()
        } else if (account?.provider === 'google') {
          token.providerId = (profile as any).sub || (profile as any).id
        }
      }
      return token
    },
    async session({ session, token }) {
      session.accessToken = token.accessToken as string
      session.provider = token.provider as string
      session.providerId = token.providerId as string
      return session
    },
  },
  pages: {
    signIn: '/login',
  },
})

export { handler as GET, handler as POST }
