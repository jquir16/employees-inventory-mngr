import { cva, type VariantProps } from 'class-variance-authority'
import { ArrowUp, ArrowDown, TrendingUp } from 'lucide-react'

const statCardVariants = cva(
  'border rounded-lg p-6 shadow-sm',
  {
    variants: {
      variant: {
        default: 'bg-card text-card-foreground',
        positive: 'bg-green-50 border-green-100',
        negative: 'bg-red-50 border-red-100',
        warning: 'bg-yellow-50 border-yellow-100',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
)

interface StatCardProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof statCardVariants> {
  title: string
  value: string | number
  change?: number
  description?: string
}

export function StatCard({
  className,
  variant,
  title,
  value,
  change,
  description,
  ...props
}: StatCardProps) {
  return (
    <div className={statCardVariants({ variant, className })} {...props}>
      <h3 className="text-sm font-medium text-muted-foreground">{title}</h3>
      <p className="text-2xl font-bold mt-1">{value}</p>
      
      {change !== undefined && (
        <div className="flex items-center mt-2">
          {change >= 0 ? (
            <ArrowUp className="h-4 w-4 text-green-500" />
          ) : (
            <ArrowDown className="h-4 w-4 text-red-500" />
          )}
          <span className={`ml-1 text-sm ${
            change >= 0 ? 'text-green-600' : 'text-red-600'
          }`}>
            {Math.abs(change)}%
          </span>
          {description && (
            <span className="ml-2 text-sm text-muted-foreground">
              {description}
            </span>
          )}
        </div>
      )}
    </div>
  )
}