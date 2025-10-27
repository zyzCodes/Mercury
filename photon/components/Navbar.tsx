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
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false)

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
      <nav className="bg-white/70 backdrop-blur-xl border-b border-gray-200/50 shadow-sm">
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
      <nav className="bg-white/70 backdrop-blur-xl border-b border-gray-200/50 shadow-sm sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <button
              onClick={onBack || (() => router.push("/dashboard"))}
              className="flex items-center text-gray-700 hover:text-gray-900 transition-all duration-200 hover:translate-x-[-2px]"
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
    <nav className="bg-white/70 backdrop-blur-xl border-b border-white/20 shadow-sm/50 sticky top-0 z-50">
      <div className="mx-auto px-4 sm:px-6 lg:px-12 xl:px-16">
        <div className="flex justify-between h-16">
          {/* Left side - Logo and Nav Links */}
          <div className="flex items-center space-x-8">
            {/* Logo */}
            <button
              onClick={() => handleNavigation("/dashboard")}
              className="text-xl font-semibold text-gray-900 hover:text-blue-600 transition-all duration-200"
            >
              Mercury
            </button>

            {/* Desktop Navigation Links */}
            {showNavLinks && (
              <div className="hidden md:flex space-x-2">
                <button
                  onClick={() => handleNavigation("/dashboard")}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                    isActivePath("/dashboard")
                      ? "bg-blue-500/10 text-blue-600 shadow-sm"
                      : "text-gray-600 hover:bg-gray-900/5 hover:text-gray-900"
                  }`}
                >
                  Dashboard
                </button>
                <button
                  onClick={() => handleNavigation("/tasks")}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                    isActivePath("/tasks")
                      ? "bg-blue-500/10 text-blue-600 shadow-sm"
                      : "text-gray-600 hover:bg-gray-900/5 hover:text-gray-900"
                  }`}
                >
                  Tasks
                </button>
              </div>
            )}
          </div>

          {/* Right side - Action buttons and Profile (Desktop) */}
          <div className="hidden md:flex items-center space-x-3">
            <button
              onClick={() => handleNavigation('/tasks')}
              className="bg-white/60 backdrop-blur-md text-blue-600 border border-blue-400/30 px-4 py-2 rounded-xl hover:bg-white/80 hover:border-blue-500/50 hover:shadow-md hover:scale-[1.02] transition-all duration-200 flex items-center gap-2"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span className="text-sm font-medium">Weekly Tasks</span>
            </button>
            <button
              onClick={() => handleNavigation('/new')}
              className="bg-gradient-to-r from-blue-500 to-purple-600 text-white px-4 py-2 rounded-xl hover:from-blue-600 hover:to-purple-700 hover:shadow-lg hover:scale-[1.02] transition-all duration-200 flex items-center gap-2"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              <span className="text-sm font-medium">New Goal</span>
            </button>
            {user && user.avatarUrl && (
              <div className="relative">
                <button
                  onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                  className="w-10 h-10 rounded-full border-2 border-white/50 hover:border-blue-400/70 hover:shadow-lg hover:scale-105 transition-all duration-200 overflow-hidden focus:outline-none focus:ring-2 focus:ring-blue-400/50 focus:ring-offset-2 backdrop-blur-sm"
                >
                  <Image
                    src={user.avatarUrl}
                    alt="Profile"
                    width={40}
                    height={40}
                    className="w-full h-full object-cover"
                  />
                </button>

                {/* Profile Dropdown Menu */}
                {isProfileMenuOpen && (
                  <>
                    {/* Backdrop to close menu when clicking outside */}
                    <div
                      className="fixed inset-0 z-10"
                      onClick={() => setIsProfileMenuOpen(false)}
                    />

                    {/* Dropdown */}
                    <div className="absolute right-0 mt-3 w-64 bg-white/80 backdrop-blur-xl rounded-2xl shadow-2xl border border-white/30 z-20 overflow-hidden">
                      {/* User Info Section */}
                      <div className="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-5">
                        <div className="flex items-center space-x-3">
                          <Image
                            src={user.avatarUrl}
                            alt="Profile"
                            width={48}
                            height={48}
                            className="w-12 h-12 rounded-full border-2 border-white shadow-lg"
                          />
                          <div className="text-white">
                            <p className="font-semibold text-sm">
                              {user.name || user.username || 'User'}
                            </p>
                            {user.email && (
                              <p className="text-xs text-blue-100 truncate">
                                {user.email}
                              </p>
                            )}
                          </div>
                        </div>
                      </div>

                      {/* Encouraging Message */}
                      <div className="px-4 py-4 bg-blue-50/50 backdrop-blur-sm border-b border-gray-200/30">
                        <p className="text-sm text-gray-700 font-medium text-center">
                          Keep crushing your goals! 🚀
                        </p>
                      </div>

                      {/* Sign Out Button */}
                      <div className="p-2">
                        <button
                          onClick={handleSignOut}
                          className="w-full flex items-center justify-center gap-2 px-4 py-3 text-red-600 hover:bg-red-50/80 rounded-xl transition-all duration-200 font-medium hover:scale-[1.02]"
                        >
                          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                          </svg>
                          Sign Out
                        </button>
                      </div>
                    </div>
                  </>
                )}
              </div>
            )}
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
          className="fixed inset-0 bg-gray-900/30 backdrop-blur-sm z-40 md:hidden"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Mobile menu panel */}
      <div
        className={`fixed top-0 right-0 h-screen w-64 bg-white shadow-xl z-50 transform transition-transform duration-300 ease-in-out md:hidden overflow-hidden ${
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
            <div className="px-4 py-3 border-b border-gray-200">
              <div className="flex items-center space-x-3">
                {user.avatarUrl && (
                  <Image
                    src={user.avatarUrl}
                    alt="Profile"
                    width={40}
                    height={40}
                    className="w-10 h-10 rounded-full border-2 border-gray-200"
                  />
                )}
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-gray-900 truncate">
                    {user.name || user.username}
                  </p>
                  {user.email && (
                    <p className="text-xs text-gray-500 truncate">{user.email}</p>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Mobile navigation links - scrollable area */}
          <div className="flex-1 overflow-y-auto min-h-0">
            <div className="px-2 py-4 space-y-2">
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

              {/* Action buttons in mobile menu */}
              <div className="pt-2 mt-2 border-t border-gray-200 space-y-2">
                <button
                  onClick={() => handleNavigation("/tasks")}
                  className="w-full bg-white text-blue-600 border-2 border-blue-600 px-4 py-3 rounded-lg hover:bg-blue-50 transition flex items-center justify-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span className="font-medium">Weekly Tasks</span>
                </button>
                <button
                  onClick={() => handleNavigation("/new")}
                  className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white px-4 py-3 rounded-lg hover:from-blue-700 hover:to-purple-700 transition flex items-center justify-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                  <span className="font-medium">New Goal</span>
                </button>
              </div>
            </div>
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
