import NextAuth from "next-auth"

declare module "next-auth" {
  interface Session {
    accessToken?: string
    provider?: string
    providerId?: string
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    accessToken?: string
    provider?: string
    providerId?: string
  }
}
