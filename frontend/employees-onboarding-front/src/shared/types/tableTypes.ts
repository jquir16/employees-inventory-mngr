export type ColumnDefinition<T> = {
  key: string
  header: string
  width?: string
  render?: (item: T) => React.ReactNode
  align?: 'left' | 'center' | 'right'
}

export type TableQueryParams = {
  page?: number
  pageSize?: number
  sortBy?: string
  sortDirection?: 'asc' | 'desc'
  filters?: Record<string, any>
}

export type PaginatedResponse<T> = {
  data: T[]
  total: number
  page: number
  pageSize: number
}