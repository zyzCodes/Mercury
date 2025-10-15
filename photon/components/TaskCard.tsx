"use client"

interface TaskCardProps {
  name: string
  color: string
  completed?: boolean
  onClick?: () => void
}

export default function TaskCard({ name, color, completed = false, onClick }: TaskCardProps) {
  return (
    <div
      onClick={onClick}
      className={`
        px-3 py-2 rounded-lg text-sm cursor-pointer
        transition-all hover:shadow-md hover:scale-105
        border-l-4 truncate
        ${completed ? 'opacity-60 line-through' : ''}
      `}
      style={{
        backgroundColor: `${color}20`, // 20% opacity for background
        borderLeftColor: color,
        color: color,
      }}
      title={name} // Show full name on hover
    >
      <span className="truncate block">
        {name}
      </span>
    </div>
  )
}

