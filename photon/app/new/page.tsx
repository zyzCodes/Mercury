"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useUser } from "@/hooks/useUser"
import { createGoal, type CreateGoalRequest } from "@/lib/goals-api"
import Image from "next/image"

type Step = 1 | 2 | 3 | 4 | 5;

interface GoalFormData {
  title: string;
  description: string;
  imageUrl: string;
  startDate: string;
  endDate: string;
}

export default function NewGoalPage() {
  const { user, loading, isAuthenticated } = useUser()
  const router = useRouter()
  const [currentStep, setCurrentStep] = useState<Step>(1)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  
  const [formData, setFormData] = useState<GoalFormData>({
    title: "",
    description: "",
    imageUrl: "",
    startDate: "",
    endDate: "",
  })

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, loading, router])

  if (loading || !isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  const totalSteps = 5;
  const progress = (currentStep / totalSteps) * 100;

  const handleNext = () => {
    if (currentStep < totalSteps) {
      setCurrentStep((currentStep + 1) as Step)
    }
  }

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep((currentStep - 1) as Step)
    }
  }

  const handleSkip = () => {
    handleNext()
  }

  const canProceed = () => {
    switch (currentStep) {
      case 1:
        return formData.title.trim().length > 0
      case 2:
        return true // Description is optional
      case 3:
        return true // Image is optional
      case 4:
        return formData.startDate && formData.endDate && formData.startDate <= formData.endDate
      case 5:
        return true // Review step
      default:
        return false
    }
  }

  const handleSubmit = async () => {
    if (!user) return

    setIsSubmitting(true)
    setError(null)

    try {
      const goalData: CreateGoalRequest = {
        title: formData.title,
        description: formData.description || null,
        imageUrl: formData.imageUrl || null,
        startDate: formData.startDate,
        endDate: formData.endDate,
        userId: user.id,
      }

      await createGoal(goalData)
      router.push('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create goal')
      setIsSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Header */}
      <nav className="bg-white/80 backdrop-blur-sm shadow-sm sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <button
              onClick={() => router.push('/dashboard')}
              className="flex items-center text-gray-700 hover:text-gray-900 transition"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
              </svg>
              Back to Dashboard
            </button>
            <div className="text-sm text-gray-600">
              Step {currentStep} of {totalSteps}
            </div>
          </div>
        </div>
      </nav>

      {/* Progress Bar */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 pt-6">
        <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
          <div 
            className="h-full bg-gradient-to-r from-blue-500 to-purple-500 transition-all duration-500 ease-out"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      {/* Main Content */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white rounded-2xl shadow-xl p-8 md:p-12">
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {/* Step 1: Goal Title */}
          {currentStep === 1 && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center mb-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-3">
                  What&apos;s your goal? 🎯
                </h1>
                <p className="text-lg text-gray-600">
                  Give your goal a clear, inspiring title
                </p>
              </div>

              <div>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g., Run a marathon, Learn Spanish, Build an app..."
                  className="w-full px-6 py-4 text-xl border-2 border-gray-300 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition outline-none"
                  maxLength={100}
                  autoFocus
                />
                <div className="mt-2 text-right text-sm text-gray-500">
                  {formData.title.length}/100 characters
                </div>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <p className="text-sm text-blue-800">
                  💡 <strong>Tip:</strong> Make it specific and action-oriented. 
                  &quot;Run a 5K in under 30 minutes&quot; is better than &quot;Get fit&quot;.
                </p>
              </div>
            </div>
          )}

          {/* Step 2: Description */}
          {currentStep === 2 && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center mb-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-3">
                  Tell us more about it 📝
                </h1>
                <p className="text-lg text-gray-600">
                  Add a brief description (optional)
                </p>
              </div>

              <div>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Why is this goal important to you? What will you achieve?"
                  rows={6}
                  className="w-full px-6 py-4 text-lg border-2 border-gray-300 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition outline-none resize-none"
                  maxLength={500}
                />
                <div className="mt-2 text-right text-sm text-gray-500">
                  {formData.description.length}/500 characters
                </div>
              </div>
            </div>
          )}

          {/* Step 3: Image */}
          {currentStep === 3 && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center mb-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-3">
                  Add an inspiring image 📸
                </h1>
                <p className="text-lg text-gray-600">
                  Visual motivation can be powerful (optional)
                </p>
              </div>

              <div>
                <input
                  type="url"
                  value={formData.imageUrl}
                  onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                  placeholder="Paste an image URL (https://...)"
                  className="w-full px-6 py-4 text-lg border-2 border-gray-300 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition outline-none"
                />
              </div>

              {formData.imageUrl && (
                <div className="mt-6">
                  <p className="text-sm font-medium text-gray-700 mb-3">Preview:</p>
                  <div className="relative w-full h-64 rounded-xl overflow-hidden bg-gray-100">
                    <Image
                      src={formData.imageUrl}
                      alt="Goal preview"
                      fill
                      className="object-cover"
                      onError={() => setFormData({ ...formData, imageUrl: "" })}
                    />
                  </div>
                </div>
              )}

              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <p className="text-sm text-gray-700">
                  📌 <strong>Note:</strong> Image upload functionality coming soon! 
                  For now, you can paste a URL from the web.
                </p>
              </div>
            </div>
          )}

          {/* Step 4: Dates */}
          {currentStep === 4 && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center mb-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-3">
                  When will you work on it? 📅
                </h1>
                <p className="text-lg text-gray-600">
                  Set your start and target completion dates
                </p>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Start Date
                  </label>
                  <input
                    type="date"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    className="w-full px-4 py-3 text-lg border-2 border-gray-300 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition outline-none"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Target End Date
                  </label>
                  <input
                    type="date"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    min={formData.startDate}
                    className="w-full px-4 py-3 text-lg border-2 border-gray-300 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition outline-none"
                  />
                </div>
              </div>

              {formData.startDate && formData.endDate && (
                <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                  <p className="text-sm text-green-800">
                    ✨ Your goal timeline is {Math.ceil((new Date(formData.endDate).getTime() - new Date(formData.startDate).getTime()) / (1000 * 60 * 60 * 24))} days
                  </p>
                </div>
              )}

              {formData.startDate && formData.endDate && formData.startDate > formData.endDate && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                  <p className="text-sm text-red-800">
                    ⚠️ End date must be after start date
                  </p>
                </div>
              )}
            </div>
          )}

          {/* Step 5: Review & Submit */}
          {currentStep === 5 && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center mb-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-3">
                  Review your goal 🎉
                </h1>
                <p className="text-lg text-gray-600">
                  Everything look good? Let&apos;s make it happen!
                </p>
              </div>

              <div className="space-y-6">
                {/* Title */}
                <div className="bg-gray-50 rounded-xl p-6">
                  <h3 className="text-sm font-medium text-gray-600 mb-2">Goal Title</h3>
                  <p className="text-xl font-semibold text-gray-900">{formData.title}</p>
                </div>

                {/* Description */}
                {formData.description && (
                  <div className="bg-gray-50 rounded-xl p-6">
                    <h3 className="text-sm font-medium text-gray-600 mb-2">Description</h3>
                    <p className="text-gray-900">{formData.description}</p>
                  </div>
                )}

                {/* Image */}
                {formData.imageUrl && (
                  <div className="bg-gray-50 rounded-xl p-6">
                    <h3 className="text-sm font-medium text-gray-600 mb-3">Image</h3>
                    <div className="relative w-full h-48 rounded-lg overflow-hidden">
                      <Image
                        src={formData.imageUrl}
                        alt="Goal"
                        fill
                        className="object-cover"
                      />
                    </div>
                  </div>
                )}

                {/* Dates */}
                <div className="bg-gray-50 rounded-xl p-6 grid md:grid-cols-2 gap-4">
                  <div>
                    <h3 className="text-sm font-medium text-gray-600 mb-2">Start Date</h3>
                    <p className="text-lg font-medium text-gray-900">
                      {new Date(formData.startDate).toLocaleDateString('en-US', { 
                        month: 'long', 
                        day: 'numeric', 
                        year: 'numeric' 
                      })}
                    </p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-600 mb-2">Target End Date</h3>
                    <p className="text-lg font-medium text-gray-900">
                      {new Date(formData.endDate).toLocaleDateString('en-US', { 
                        month: 'long', 
                        day: 'numeric', 
                        year: 'numeric' 
                      })}
                    </p>
                  </div>
                </div>

              </div>
            </div>
          )}

          {/* Navigation Buttons */}
          <div className="flex justify-between items-center mt-12 pt-6 border-t border-gray-200">
            <button
              onClick={handleBack}
              disabled={currentStep === 1}
              className="px-6 py-3 text-gray-700 font-medium rounded-lg hover:bg-gray-100 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Back
            </button>

            <div className="flex gap-3">
              {currentStep !== 1 && currentStep !== 4 && currentStep !== 5 && (
                <button
                  onClick={handleSkip}
                  className="px-6 py-3 text-gray-600 font-medium rounded-lg hover:bg-gray-100 transition"
                >
                  Skip
                </button>
              )}

              {currentStep < totalSteps ? (
                <button
                  onClick={handleNext}
                  disabled={!canProceed()}
                  className="px-8 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold rounded-lg hover:from-blue-700 hover:to-purple-700 transition disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
                >
                  Continue
                </button>
              ) : (
                <button
                  onClick={handleSubmit}
                  disabled={isSubmitting}
                  className="px-8 py-3 bg-gradient-to-r from-green-600 to-blue-600 text-white font-semibold rounded-lg hover:from-green-700 hover:to-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
                >
                  {isSubmitting ? 'Creating...' : 'Create Goal 🚀'}
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Step Indicators */}
        <div className="flex justify-center gap-2 mt-8">
          {Array.from({ length: totalSteps }, (_, i) => i + 1).map((step) => (
            <div
              key={step}
              className={`h-2 rounded-full transition-all duration-300 ${
                step === currentStep
                  ? 'w-8 bg-blue-600'
                  : step < currentStep
                  ? 'w-2 bg-blue-400'
                  : 'w-2 bg-gray-300'
              }`}
            />
          ))}
        </div>
      </main>

      <style jsx>{`
        @keyframes fadeIn {
          from {
            opacity: 0;
            transform: translateY(10px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        .animate-fadeIn {
          animation: fadeIn 0.5s ease-out;
        }
      `}</style>
    </div>
  )
}
