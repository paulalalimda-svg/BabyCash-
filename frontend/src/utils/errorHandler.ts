/**
 * Utilidad para manejo consistente de errores en la aplicación
 * Proporciona feedback visual al usuario y logging condicional
 */

import toast from 'react-hot-toast';
import { handleApiError } from '../services/api';
import { logger } from './logger';

interface ErrorHandlerOptions {
  /**
   * Mostrar notificación toast al usuario
   * @default true
   */
  showToast?: boolean;

  /**
   * Mensaje personalizado a mostrar (si no se proporciona, usa el mensaje del error)
   */
  customMessage?: string;

  /**
   * Loguear el error en consola (solo en desarrollo)
   * @default true
   */
  logToConsole?: boolean;

  /**
   * Contexto adicional para debugging
   */
  context?: string;

  /**
   * Tipo de notificación
   * @default 'error'
   */
  toastType?: 'error' | 'warning' | 'info';
}

/**
 * Maneja errores de forma consistente
 * 
 * @example
 * try {
 *   await api.fetchData();
 * } catch (error) {
 *   handleError(error, {
 *     customMessage: 'No se pudieron cargar los productos',
 *     context: 'Productos.tsx fetchData()'
 *   });
 * }
 */
export const handleError = (
  error: unknown,
  options: ErrorHandlerOptions = {}
): string => {
  const {
    showToast = true,
    customMessage,
    logToConsole = true,
    context,
    toastType = 'error',
  } = options;

  // Obtener mensaje de error legible
  const errorMessage = customMessage || handleApiError(error);

  // Loguear en consola (solo en desarrollo)
  if (logToConsole) {
    if (context) {
      logger.failed(`[${context}]`, errorMessage, error);
    } else {
      logger.failed('Error:', errorMessage, error);
    }
  }

  // Mostrar notificación al usuario
  if (showToast) {
    switch (toastType) {
      case 'error':
        toast.error(errorMessage);
        break;
      case 'warning':
        toast(errorMessage, { icon: '⚠️' });
        break;
      case 'info':
        toast(errorMessage, { icon: 'ℹ️' });
        break;
    }
  }

  return errorMessage;
};

/**
 * Wrapper para operaciones asíncronas con manejo de errores
 * 
 * @example
 * const data = await withErrorHandling(
 *   () => productService.getAll(),
 *   { context: 'ProductsPage', customMessage: 'Error al cargar productos' }
 * );
 */
export const withErrorHandling = async <T>(
  operation: () => Promise<T>,
  options: ErrorHandlerOptions = {}
): Promise<T | null> => {
  try {
    return await operation();
  } catch (error) {
    handleError(error, options);
    return null;
  }
};

/**
 * Hook personalizado para manejo de errores en componentes
 * 
 * @example
 * const { executeWithErrorHandling } = useErrorHandler('ProductsPage');
 * 
 * const loadProducts = executeWithErrorHandling(
 *   async () => {
 *     const data = await productService.getAll();
 *     setProducts(data);
 *   },
 *   { customMessage: 'Error al cargar productos' }
 * );
 */
export const useErrorHandler = (componentName: string) => {
  const executeWithErrorHandling = async (
    operation: () => Promise<void>,
    options: Omit<ErrorHandlerOptions, 'context'> = {}
  ): Promise<void> => {
    try {
      await operation();
    } catch (error) {
      handleError(error, {
        ...options,
        context: componentName,
      });
    }
  };

  return { executeWithErrorHandling };
};

export default handleError;
