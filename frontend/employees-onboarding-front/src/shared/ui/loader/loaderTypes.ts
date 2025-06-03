export type LoaderSize = 'sm' | 'md' | 'lg' | 'xl';
export type LoaderVariant = 'spinner' | 'dots' | 'bar';

export interface LoaderProps {
  size?: LoaderSize;
  variant?: LoaderVariant;
  className?: string;
}