interface FooterProps {
  variant?: 'default' | 'subtle'
}

export default function Footer({ variant = 'default' }: FooterProps) {
  if (variant === 'subtle') {
    return (
      <footer className="mt-auto pt-16 pb-8 border-t border-gray-200">
        <div className="mx-auto px-4 sm:px-6 lg:px-12 xl:px-16 text-center space-y-2">
          <p className="text-gray-500 text-sm">
            © {new Date().getFullYear()} Mercury. Track your goals, build better habits.
          </p>
          <p className="text-gray-400 text-xs">
            Built with ❤️ by{' '}
            <a
              href="https://diarana.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-600 hover:text-gray-900 font-medium transition"
            >
              Diego Arana
            </a>
          </p>
        </div>
      </footer>
    )
  }

  return (
    <footer className="bg-gray-900 text-white py-8">
      <div className="mx-auto px-4 sm:px-6 lg:px-12 xl:px-16 text-center space-y-3">
        <p className="text-gray-400">
          © {new Date().getFullYear()} Mercury. Track your goals, build better habits.
        </p>
        <p className="text-gray-500 text-sm">
          Built with ❤️ by{' '}
          <a
            href="https://diarana.com/"
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-400 hover:text-blue-300 font-semibold transition"
          >
            Diego Arana
          </a>
        </p>
      </div>
    </footer>
  )
}
