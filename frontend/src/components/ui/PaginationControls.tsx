import type { PaginationControlsProps } from '../../hooks/useAdminCrud';

/**
 * Componente reutilizable para controles de paginación en componentes de administración
 */
export const PaginationControls: React.FC<PaginationControlsProps> = ({
  currentPage,
  totalPages,
  totalElements,
  itemsCount,
  itemName,
  onPageChange,
  onNext,
  onPrevious,
  canGoNext,
  canGoPrevious,
}) => {
  if (totalPages <= 1) return null;

  return (
    <div className="mt-8 flex flex-col sm:flex-row items-center justify-between gap-4 bg-white p-4 rounded-lg shadow">
      <div className="text-sm text-gray-600">
        Mostrando {itemsCount} de {totalElements} {itemName} • Página {currentPage + 1} de{' '}
        {totalPages}
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={onPrevious}
          disabled={!canGoPrevious}
          className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Anterior
        </button>

        <div className="flex gap-1">
          {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
            let pageNum;
            if (totalPages <= 5) {
              pageNum = i;
            } else if (currentPage < 3) {
              pageNum = i;
            } else if (currentPage > totalPages - 3) {
              pageNum = totalPages - 5 + i;
            } else {
              pageNum = currentPage - 2 + i;
            }

            return (
              <button
                key={`page-${pageNum}`}
                onClick={() => onPageChange(pageNum)}
                className={`w-10 h-10 rounded ${
                  currentPage === pageNum
                    ? 'bg-baby-blue text-white'
                    : 'bg-gray-200 hover:bg-gray-300 text-gray-700'
                } transition-colors`}
              >
                {pageNum + 1}
              </button>
            );
          })}
        </div>

        <button
          onClick={onNext}
          disabled={!canGoNext}
          className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Siguiente
        </button>
      </div>
    </div>
  );
};
