'use client'

import { useState, useRef, useEffect } from 'react'

interface EmojiPickerProps {
  value: string | null
  onChange: (emoji: string) => void
  label?: string
}

const EMOJI_OPTIONS = [
  '🎯', '🚀', '💪', '📚', '📈', '💰',
  '🎨', '🎵', '👟', '💯', '🌟', '✨',
  '🔥', '💡', '🌈', '🎓', '🏆', '⚡',
  '🌱', '🎪', '🎭', '🏅', '🎸', '📝',
]

export default function EmojiPicker({ value, onChange, label = 'Choose an icon' }: EmojiPickerProps) {
  const [isOpen, setIsOpen] = useState(false)
  const [dropdownPosition, setDropdownPosition] = useState({ top: 0, left: 0, width: 0 })
  const buttonRef = useRef<HTMLButtonElement>(null)

  useEffect(() => {
    if (isOpen && buttonRef.current) {
      const rect = buttonRef.current.getBoundingClientRect()
      setDropdownPosition({
        top: rect.bottom + window.scrollY + 8,
        left: rect.left + window.scrollX,
        width: rect.width
      })
    }
  }, [isOpen])

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-gray-700">
        {label}
      </label>

      <div>
        <button
          ref={buttonRef}
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className="w-full px-4 py-3 text-left border border-gray-300 rounded-lg hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
        >
          {value ? (
            <span className="flex items-center gap-3">
              <span className="text-2xl">{value}</span>
              <span className="text-gray-600">Click to change</span>
            </span>
          ) : (
            <span className="text-gray-400">Select an emoji</span>
          )}
        </button>

        {isOpen && (
          <>
            <div
              className="fixed inset-0 z-40"
              onClick={() => setIsOpen(false)}
            />
            <div
              className="fixed z-50 bg-white border border-gray-200 rounded-lg shadow-2xl p-4 max-h-80 overflow-y-auto"
              style={{
                top: `${dropdownPosition.top}px`,
                left: `${dropdownPosition.left}px`,
                width: `${dropdownPosition.width}px`
              }}
            >
              <div className="grid grid-cols-6 gap-2">
                {EMOJI_OPTIONS.map((emoji) => (
                  <button
                    key={emoji}
                    type="button"
                    onClick={() => {
                      onChange(emoji)
                      setIsOpen(false)
                    }}
                    className={`p-3 text-2xl rounded-lg hover:bg-gray-100 transition-colors ${
                      value === emoji ? 'bg-blue-100 ring-2 ring-blue-500' : ''
                    }`}
                  >
                    {emoji}
                  </button>
                ))}
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  )
}
