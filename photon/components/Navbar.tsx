"use client"

import { useState } from "react"
import { signOut } from "next-auth/react"
import { useRouter, usePathname } from "next/navigation"
import Image from "next/image"

interface NavbarProps {
  variant?: "default" | "detail" | "minimal"
  user?: {
    name?: string
    username?: string
    email?: string
    avatarUrl?: string
  }
  showNavLinks?: boolean
  customActions?: React.ReactNode
  onBack?: () => void
  backLabel?: string
}

export default function Navbar({
  variant = "default",
  user,
  showNavLinks = false,
  customActions,
  onBack,
  backLabel = "Back to Dashboard",
}: NavbarProps) {
  const router = useRouter()
  const pathname = usePathname()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  const handleSignOut = () => {
    signOut({ callbackUrl: "/login" })
  }

  const handleNavigation = (path: string) => {
    router.push(path)
    setIsMobileMenuOpen(false)
  }

  const isActivePath = (path: string) => {
    return pathname === path
  }

  // Minimal variant for login/public pages
  if (variant === "minimal") {
    return (
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">Mercury</h1>
            </div>
          </div>
        </div>
      </nav>
    )
  }

  // Detail variant for goal/detail pages
  if (variant === "detail") {
    return (
      <nav className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <button
              onClick={onBack || (() => router.push("/dashboard"))}
              className="flex items-center text-gray-700 hover:text-gray-900 transition"
            >
              <svg
                className="w-5 h-5 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M10 19l-7-7m0 0l7-7m-7 7h18"
                />
              </svg>
              <span className="hidden sm:inline">{backLabel}</span>
            </button>
            <div className="flex items-center gap-3">{customActions}</div>
          </div>
        </div>
      </nav>
    )
  }

  // Default variant for main pages (dashboard, tasks, etc.)
  return (
    <nav className="bg-white shadow sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Left side - Logo and Nav Links */}
          <div className="flex items-center space-x-8">
            {/* Logo */}
            <button
              onClick={() => handleNavigation("/dashboard")}
              className="text-xl font-semibold text-gray-900 hover:text-blue-600 transition"
            >
              Mercury
            </button>

            {/* Desktop Navigation Links */}
            {showNavLinks && (
              <div className="hidden md:flex space-x-4">
                <button
                  onClick={() => handleNavigation("/dashboard")}
                  className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                    isActivePath("/dashboard")
                      ? "text-blue-600 border-b-2 border-blue-600"
                      : "text-gray-600 hover:text-gray-900"
                  }`}
                >
                  Dashboard
                </button>
                <button
                  onClick={() => handleNavigation("/tasks")}
                  className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                    isActivePath("/tasks")
                      ? "text-blue-600 border-b-2 border-blue-600"
                      : "text-gray-600 hover:text-gray-900"
                  }`}
                >
                  Tasks
                </button>
              </div>
            )}
          </div>

          {/* Right side - User info and Sign out (Desktop) */}
          <div className="hidden md:flex items-center space-x-4">
            {user && (
              <div className="flex items-center space-x-3">
                {user.avatarUrl && (
                  <Image
                    src={user.avatarUrl}
                    alt="Profile"
                    width={32}
                    height={32}
                    className="w-8 h-8 rounded-full border-2 border-gray-200"
                  />
                )}
                <span className="text-sm text-gray-700">
                  {user.name || user.username || user.email}
                </span>
              </div>
            )}
            <button
              onClick={handleSignOut}
              className="bg-gray-900 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-800 transition"
            >
              Sign Out
            </button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center">
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-gray-900 hover:bg-gray-100 transition"
              aria-label="Toggle mobile menu"
            >
              {isMobileMenuOpen ? (
                <svg
                  className="h-6 w-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              ) : (
                <svg
                  className="h-6 w-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M4 6h16M4 12h16M4 18h16"
                  />
                </svg>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu overlay */}
      {isMobileMenuOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 md:hidden"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Mobile menu panel */}
      <div
        className={`fixed top-0 right-0 h-full w-64 bg-white shadow-xl z-50 transform transition-transform duration-300 ease-in-out md:hidden ${
          isMobileMenuOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <div className="flex flex-col h-full">
          {/* Mobile menu header */}
          <div className="flex items-center justify-between px-4 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">Menu</h2>
            <button
              onClick={() => setIsMobileMenuOpen(false)}
              className="p-2 rounded-md text-gray-700 hover:bg-gray-100 transition"
            >
              <svg
                className="h-6 w-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>

          {/* User info */}
          {user && (
            <div className="px-4 py-4 border-b border-gray-200">
              <div className="flex items-center space-x-3">
                {user.avatarUrl && (
                  <Image
                    src={user.avatarUrl}
                    alt="Profile"
                    width={48}
                    height={48}
                    className="w-12 h-12 rounded-full border-2 border-gray-200"
                  />
                )}
                <div>
                  <p className="text-sm font-semibold text-gray-900">
                    {user.name || user.username}
                  </p>
                  {user.email && (
                    <p className="text-xs text-gray-500">{user.email}</p>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Mobile navigation links */}
          <div className="flex-1 px-2 py-4 space-y-1 overflow-y-auto">
            <button
              onClick={() => handleNavigation("/dashboard")}
              className={`w-full text-left px-4 py-3 rounded-md text-base font-medium transition ${
                isActivePath("/dashboard")
                  ? "bg-blue-50 text-blue-600"
                  : "text-gray-700 hover:bg-gray-100"
              }`}
            >
              Dashboard
            </button>
            {showNavLinks && (
              <button
                onClick={() => handleNavigation("/tasks")}
                className={`w-full text-left px-4 py-3 rounded-md text-base font-medium transition ${
                  isActivePath("/tasks")
                    ? "bg-blue-50 text-blue-600"
                    : "text-gray-700 hover:bg-gray-100"
                }`}
              >
                Tasks
              </button>
            )}
          </div>

          {/* Sign out button */}
          <div className="px-4 py-4 border-t border-gray-200">
            <button
              onClick={handleSignOut}
              className="w-full bg-gray-900 text-white px-4 py-3 rounded-md text-sm font-medium hover:bg-gray-800 transition flex items-center justify-center"
            >
              <svg
                className="w-5 h-5 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                />
              </svg>
              Sign Out
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}
