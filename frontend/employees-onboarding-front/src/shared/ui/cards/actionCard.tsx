import Link from 'next/link'
import { cva, type VariantProps } from 'class-variance-authority'
import { ArrowRight } from 'lucide-react'

const cardVariants = cva(
  'border rounded-lg overflow-hidden shadow-sm transition-all hover:shadow-md',
  {
    variants: {
      variant: {
        default: 'bg-card text-card-foreground',
        primary: 'bg-primary/5 border-primary/20',
        secondary: 'bg-secondary/5 border-secondary/20',
        destructive: 'bg-destructive/5 border-destructive/20',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
)

interface ActionCardProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof cardVariants> {
  title: string
  description: string
  icon: React.ReactNode
  href: string
  actionText: string
  iconBg?: string
}

export function ActionCard({
  className,
  variant,
  title,
  description,
  icon,
  href,
  actionText,
  iconBg = 'bg-primary',
  ...props
}: ActionCardProps) {
  return (
    <div className={cardVariants({ variant, className })} {...props}>
      <div className="p-6">
        <div className="flex items-start gap-4">
          <div className={`${iconBg} p-3 rounded-lg text-white`}>
            {icon}
          </div>
          <div className="flex-1">
            <h3 className="text-lg font-semibold">{title}</h3>
            <p className="text-sm text-muted-foreground mt-1">{description}</p>
          </div>
        </div>
      </div>
      <div className="border-t px-6 py-3 bg-muted/10">
        <Link
          href={href}
          className="inline-flex items-center text-sm font-medium text-primary hover:text-primary/80 transition-colors"
        >
          {actionText}
          <ArrowRight className="ml-1 h-4 w-4" />
        </Link>
      </div>
    </div>
  )
}