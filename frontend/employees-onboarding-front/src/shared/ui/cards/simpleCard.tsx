import { cva, type VariantProps } from 'class-variance-authority'

const simpleCardVariants = cva(
  'border rounded-lg overflow-hidden shadow-sm',
  {
    variants: {
      variant: {
        default: 'bg-card text-card-foreground',
        muted: 'bg-muted/50',
      },
      padding: {
        none: 'p-0',
        sm: 'p-4',
        md: 'p-6',
        lg: 'p-8',
      },
    },
    defaultVariants: {
      variant: 'default',
      padding: 'md',
    },
  }
)

interface SimpleCardProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof simpleCardVariants> {}

export function SimpleCard({
  className,
  variant,
  padding,
  ...props
}: SimpleCardProps) {
  return (
    <div className={simpleCardVariants({ variant, padding, className })} {...props} />
  )
}