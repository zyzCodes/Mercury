"use client"

import { useRouter } from "next/navigation"

export default function LandingPage() {
  const router = useRouter()

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Navbar */}
      <nav className="bg-white/80 backdrop-blur-sm shadow-sm sticky top-0 z-50">
        <div className="mx-auto px-4 sm:px-6 lg:px-12 xl:px-16">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              Mercury
            </h1>
            <button
              onClick={() => router.push('/login')}
              className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-lg hover:from-blue-700 hover:to-purple-700 transition shadow-md hover:shadow-lg"
            >
              Sign In
            </button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="px-4 sm:px-6 lg:px-12 xl:px-16 py-20 sm:py-32">
        <div className="max-w-6xl mx-auto text-center">
          <h2 className="text-5xl sm:text-6xl lg:text-7xl font-extrabold text-gray-900 mb-6">
            Track Your Goals,
            <br />
            <span className="bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              Build Better Habits
            </span>
          </h2>
          <p className="text-xl sm:text-2xl text-gray-600 mb-12 max-w-3xl mx-auto">
            Mercury helps you set annual goals, build daily habits, and track your progress—all in one beautiful, intuitive platform.
          </p>
          <button
            onClick={() => router.push('/login')}
            className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:from-blue-700 hover:to-purple-700 transition shadow-xl hover:shadow-2xl transform hover:scale-105"
          >
            Get Started Free
          </button>
        </div>
      </section>

      {/* Features Section */}
      <section className="px-4 sm:px-6 lg:px-12 xl:px-16 py-20 bg-white">
        <div className="max-w-6xl mx-auto">
          <h3 className="text-3xl sm:text-4xl font-bold text-center text-gray-900 mb-16">
            Everything You Need to Succeed
          </h3>
          <div className="grid md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="text-center p-6 rounded-xl hover:shadow-lg transition">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                </svg>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Goal Tracking</h4>
              <p className="text-gray-600">
                Set annual goals with clear timelines, track progress, and celebrate milestones along the way.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="text-center p-6 rounded-xl hover:shadow-lg transition">
              <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Daily Habits</h4>
              <p className="text-gray-600">
                Build consistent habits with daily tasks, streak tracking, and visual progress indicators.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="text-center p-6 rounded-xl hover:shadow-lg transition">
              <div className="w-16 h-16 bg-gradient-to-br from-pink-500 to-pink-600 rounded-xl flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <h4 className="text-xl font-semibold text-gray-900 mb-2">Weekly Calendar</h4>
              <p className="text-gray-600">
                Visualize your week at a glance with an intuitive calendar view of all your tasks and habits.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="px-4 sm:px-6 lg:px-12 xl:px-16 py-20">
        <div className="max-w-4xl mx-auto bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl shadow-2xl p-12 text-center">
          <h3 className="text-3xl sm:text-4xl font-bold text-white mb-4">
            Ready to Achieve Your Goals?
          </h3>
          <p className="text-xl text-blue-100 mb-8">
            Join Mercury today and start building the life you've always wanted.
          </p>
          <button
            onClick={() => router.push('/login')}
            className="bg-white text-blue-600 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-gray-100 transition shadow-xl transform hover:scale-105"
          >
            Sign Up Now
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-8">
        <div className="mx-auto px-4 sm:px-6 lg:px-12 xl:px-16 text-center">
          <p className="text-gray-400">
            © {new Date().getFullYear()} Mercury. Track your goals, build better habits.
          </p>
        </div>
      </footer>
    </div>
  )
}
