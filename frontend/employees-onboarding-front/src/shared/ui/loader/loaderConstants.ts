import { LoaderSize, LoaderVariant } from './loaderTypes';

export const SIZE_CLASSES: Record<LoaderSize, string> = {
  sm: 'h-4 w-4',
  md: 'h-8 w-8',
  lg: 'h-12 w-12',
  xl: 'h-16 w-16',
};

export const VARIANT_CLASSES: Record<LoaderVariant, string> = {
  spinner: 'animate-spin rounded-full border-t-2 border-b-2 border-primary',
  dots: 'flex space-x-2',
  bar: 'h-1 w-full bg-primary animate-pulse',
};