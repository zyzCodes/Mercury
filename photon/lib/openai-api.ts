/**
 * OpenAI API client for generating habit recommendations
 */

import { AIRecommendationResponse } from '@/types/habits'

const OPENAI_API_KEY = process.env.NEXT_PUBLIC_OPENAI_API_KEY

if (!OPENAI_API_KEY) {
  console.warn('NEXT_PUBLIC_OPENAI_API_KEY is not set. AI features will be disabled.')
}

/**
 * Generate habit recommendations using OpenAI based on goal title and description
 */
export async function generateHabitRecommendations(
  goalTitle: string,
  goalDescription: string
): Promise<AIRecommendationResponse> {
  if (!OPENAI_API_KEY) {
    throw new Error('OpenAI API key is not configured')
  }

  const prompt = `You are a personal productivity coach helping someone achieve their goal. Based on the goal information below, suggest 3-5 specific, actionable habits that will help them achieve this goal.

Goal Title: "${goalTitle}"
Goal Description: "${goalDescription || 'No additional description provided'}"

For each habit, provide:
1. A clear, specific name (e.g., "30-minute morning run")
2. A brief description of what to do
3. Recommended days of the week (as an array of day codes: Mon, Tue, Wed, Thu, Fri, Sat, Sun)
4. A rationale explaining WHY this habit helps achieve the goal

Also provide a brief reasoning (2-3 sentences, max 100 tokens) explaining your overall approach to helping achieve this goal.

Return your response as JSON in this exact format:
{
  "reasoning": "Brief explanation of your overall strategy...",
  "habits": [
    {
      "name": "Habit name",
      "description": "What to do",
      "daysOfWeek": ["Mon", "Wed", "Fri"],
      "rationale": "Why this helps"
    }
  ]
}

Guidelines:
- Be specific and actionable
- Consider frequency and sustainability
- Habits should directly contribute to the goal
- Recommend realistic schedules (don't overload every day)
- Keep names concise (under 50 characters)
- Keep descriptions brief (under 150 characters)
- Keep rationales brief (under 100 characters)`

  try {
    const response = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${OPENAI_API_KEY}`,
      },
      body: JSON.stringify({
        model: 'gpt-5',
        messages: [
          {
            role: 'system',
            content: 'You are a helpful productivity coach that suggests specific, actionable habits. Always respond with valid JSON matching the requested format exactly.'
          },
          {
            role: 'user',
            content: prompt
          }
        ],
        max_completion_tokens: 1000,
        response_format: { type: 'json_object' }
      }),
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(
        errorData.error?.message || `OpenAI API error: ${response.statusText}`
      )
    }

    const data = await response.json()
    const content = data.choices[0]?.message?.content

    if (!content) {
      throw new Error('No content received from OpenAI')
    }

    const parsed: AIRecommendationResponse = JSON.parse(content)

    // Validate response structure
    if (!parsed.reasoning || !Array.isArray(parsed.habits)) {
      throw new Error('Invalid response format from OpenAI')
    }

    // Validate each habit has required fields
    for (const habit of parsed.habits) {
      if (!habit.name || !habit.description || !Array.isArray(habit.daysOfWeek) || !habit.rationale) {
        throw new Error('Invalid habit structure in OpenAI response')
      }
    }

    return parsed
  } catch (error) {
    if (error instanceof Error) {
      throw error
    }
    throw new Error('Failed to generate habit recommendations')
  }
}
