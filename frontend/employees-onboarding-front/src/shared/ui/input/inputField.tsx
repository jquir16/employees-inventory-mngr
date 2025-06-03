import React from 'react'
import { cva, type VariantProps } from 'class-variance-authority'

const inputVariants = cva(
  'flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
  {
    variants: {
      inputSize: {
        default: 'h-10',
        sm: 'h-9 px-2',
        lg: 'h-11 px-4',
      },
    },
    defaultVariants: {
      inputSize: 'default',
    },
  }
)

export interface InputFieldProps
  extends React.InputHTMLAttributes<HTMLInputElement>,
    VariantProps<typeof inputVariants> {
  leftIcon?: React.ReactNode
  rightIcon?: React.ReactNode
  containerClass?: string
}

const Input = React.forwardRef<HTMLInputElement, InputFieldProps>(
  ({ className, inputSize, leftIcon, rightIcon, containerClass, ...props }, ref) => {
    return (
      <div className={`relative ${containerClass}`}>
        {leftIcon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
            {leftIcon}
          </div>
        )}
        <input
          className={inputVariants({ 
            inputSize, 
            className, 
            ...(leftIcon && { pl: '10' }), 
            ...(rightIcon && { pr: '10' }) 
          })}
          ref={ref}
          {...props}
        />
        {rightIcon && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
            {rightIcon}
          </div>
        )}
      </div>
    )
  }
)

Input.displayName = 'Input'

export { Input }