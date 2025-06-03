import React from 'react';
import { LoaderProps } from './loaderTypes';
import { SIZE_CLASSES, VARIANT_CLASSES } from './loaderConstants';

export const Loader: React.FC<LoaderProps> = ({
  size = 'md',
  variant = 'spinner',
  className = '',
}) => {
  const baseClasses = 'flex items-center justify-center';
  const sizeClasses = SIZE_CLASSES[size];
  const variantClasses = VARIANT_CLASSES[variant];

  if (variant === 'dots') {
    return (
      <div className={`${baseClasses} ${className}`}>
        {[...Array(3)].map((_, i) => (
          <div
            key={i}
            className={`${sizeClasses} bg-primary rounded-full animate-bounce`}
            style={{ animationDelay: `${i * 0.1}s` }}
          />
        ))}
      </div>
    );
  }

  return (
    <div className={`${baseClasses} ${className}`}>
      <div className={`${sizeClasses} ${variantClasses}`} />
    </div>
  );
};