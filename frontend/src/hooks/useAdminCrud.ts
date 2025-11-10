import { useState, useEffect, useCallback } from 'react';
import { handleApiError } from '../services/api';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

interface UseAdminCrudOptions<T, F = string> {
  service: {
    getAll: (page: number, size: number) => Promise<PagedResponse<T>>;
    delete?: (id: number) => Promise<void>;
  };
  pageSize?: number;
  filterFn?: (items: T[], filter: F) => T[];
  defaultFilter?: F;
  logName?: string;
}

interface UseAdminCrudReturn<T, F> {
  // Data states
  items: T[];
  loading: boolean;
  filter: F;
  setFilter: (filter: F) => void;

  // Pagination states
  currentPage: number;
  totalPages: number;
  totalElements: number;
  setCurrentPage: (page: number) => void;

  // Actions
  loadData: () => Promise<void>;
  handleDelete: (id: number, confirmMessage?: string) => Promise<void>;

  // Pagination helpers
  goToNextPage: () => void;
  goToPreviousPage: () => void;
  canGoNext: boolean;
  canGoPrevious: boolean;
}

/**
 * Hook personalizado para gestionar operaciones CRUD en componentes de administración
 * con paginación, filtrado y carga de datos.
 *
 * @example
 * ```tsx
 * const {
 *   items,
 *   loading,
 *   currentPage,
 *   totalPages,
 *   loadData,
 *   handleDelete,
 * } = useAdminCrud({
 *   service: blogService,
 *   filterFn: (blogs, filter) => {
 *     if (filter === 'published') return blogs.filter(b => b.published);
 *     return blogs;
 *   },
 *   logName: 'blogs',
 * });
 * ```
 */
export function useAdminCrud<T, F = string>(
  options: UseAdminCrudOptions<T, F>
): UseAdminCrudReturn<T, F> {
  const { service, pageSize = 10, filterFn, defaultFilter, logName = 'items' } = options;

  // Data states
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<F>(defaultFilter as F);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Store all items without filter
  const [allItems, setAllItems] = useState<T[]>([]);

  // Load data from API
  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      logger.loading(`Cargando ${logName}... Página ${currentPage + 1}`);

      const response = await service.getAll(currentPage, pageSize);
      logger.success(`${logName} cargados:`, response.content.length);

      setAllItems(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      logger.failed(`Error al cargar ${logName}:`, error);
      // Solo mostrar toast si no es un error 403 (permisos)
      if (error instanceof Error && !error.message.includes('403')) {
        toast.error(handleApiError(error));
      }
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize, logName]);

  // Load data when page or pageSize changes
  useEffect(() => {
    let isMounted = true;

    const load = async () => {
      if (isMounted) {
        await loadData();
      }
    };

    load();

    return () => {
      isMounted = false;
    };
  }, [currentPage, pageSize]);

  // Apply filter to items (client-side filtering)
  useEffect(() => {
    if (filterFn && filter !== undefined) {
      setItems(filterFn(allItems, filter));
    } else {
      setItems(allItems);
    }
  }, [allItems, filter, filterFn]);

  // Delete item
  const handleDelete = async (id: number, confirmMessage?: string) => {
    if (!service.delete) {
      logger.warn('Delete operation not provided in service');
      return;
    }

    const message =
      confirmMessage || `¿Estás seguro de que deseas eliminar este ${logName.slice(0, -1)}?`;
    if (!confirm(message)) return;

    try {
      await service.delete(id);
      toast.success(`${logName.slice(0, -1)} eliminado correctamente`);
      await loadData();
    } catch (error) {
      logger.failed(`Error al eliminar ${logName.slice(0, -1)}:`, error);
      toast.error(handleApiError(error));
    }
  };

  // Pagination helpers
  const goToNextPage = () => {
    setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1));
  };

  const goToPreviousPage = () => {
    setCurrentPage((prev) => Math.max(0, prev - 1));
  };

  const canGoNext = currentPage < totalPages - 1;
  const canGoPrevious = currentPage > 0;

  return {
    // Data
    items,
    loading,
    filter,
    setFilter,

    // Pagination
    currentPage,
    totalPages,
    totalElements,
    setCurrentPage,

    // Actions
    loadData,
    handleDelete,

    // Helpers
    goToNextPage,
    goToPreviousPage,
    canGoNext,
    canGoPrevious,
  };
}

/**
 * Props para el componente PaginationControls
 */
export interface PaginationControlsProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  itemsCount: number;
  itemName: string;
  onPageChange: (page: number) => void;
  onNext: () => void;
  onPrevious: () => void;
  canGoNext: boolean;
  canGoPrevious: boolean;
}
