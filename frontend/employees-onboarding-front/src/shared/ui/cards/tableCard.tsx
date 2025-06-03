import { SimpleCard } from './simpleCard'

interface TableCardProps {
  title?: string
  description?: string
  actions?: React.ReactNode
  children: React.ReactNode
  className?: string
}

export function TableCard({
  title,
  description,
  actions,
  children,
  className,
}: TableCardProps) {
  return (
    <SimpleCard className={className} padding="none">
      {(title || description || actions) && (
        <div className="border-b p-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            {title && <h3 className="text-lg font-semibold">{title}</h3>}
            {description && (
              <p className="text-sm text-muted-foreground mt-1">{description}</p>
            )}
          </div>
          {actions && <div>{actions}</div>}
        </div>
      )}
      <div className="p-0 overflow-x-auto">{children}</div>
    </SimpleCard>
  )
}