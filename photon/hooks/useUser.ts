import { useEffect, useState } from 'react';
import { useSession } from 'next-auth/react';
import { getUserByProvider, getUserByGithubId, User } from '@/lib/api';

/**
 * Custom hook to fetch and manage user data from the backend
 * Supports multiple authentication providers (GitHub, Google, JWT, etc.)
 */
export function useUser() {
  const { data: session, status } = useSession();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchUser() {
      if (status === 'loading') {
        return;
      }

      // Check if user is authenticated and has provider info
      if (!session?.provider || !session?.providerId) {
        // Try legacy githubId if available
        if (session?.githubId) {
          try {
            setLoading(true);
            const userData = await getUserByGithubId(session.githubId);
            setUser(userData);
            setError(null);
          } catch (err) {
            console.error('Error fetching user:', err);
            setError(err instanceof Error ? err.message : 'Failed to fetch user');
          } finally {
            setLoading(false);
          }
          return;
        }
        
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const userData = await getUserByProvider(session.provider, session.providerId);
        setUser(userData);
        setError(null);
      } catch (err) {
        console.error('Error fetching user:', err);
        setError(err instanceof Error ? err.message : 'Failed to fetch user');
      } finally {
        setLoading(false);
      }
    }

    fetchUser();
  }, [session?.provider, session?.providerId, session?.githubId, status]);

  return {
    user,
    loading,
    error,
    session,
    isAuthenticated: !!session,
  };
}
