/**
 * Logger condicional que solo imprime en modo desarrollo
 * Evita exponer información sensible en producción
 */

class Logger {
  private readonly isDev = import.meta.env.DEV;

  private shouldLog(): boolean {
    return this.isDev;
  }

  log(...args: unknown[]): void {
    if (this.shouldLog()) {
      console.log(...args);
    }
  }

  info(...args: unknown[]): void {
    if (this.shouldLog()) {
      console.info(...args);
    }
  }

  warn(...args: unknown[]): void {
    if (this.shouldLog()) {
      console.warn(...args);
    }
  }

  error(...args: unknown[]): void {
    if (this.shouldLog()) {
      console.error(...args);
    }
  }

  debug(...args: unknown[]): void {
    if (this.shouldLog()) {
      console.debug(...args);
    }
  }

  /**
   * Para errores críticos que siempre deben loguearse
   * (incluso en producción, pero de forma segura)
   */
  critical(message: string, error?: unknown): void {
    // En producción, solo loguear mensaje sin detalles sensibles
    if (this.isDev) {
      console.error('🚨 CRITICAL:', message, error);
    } else {
      console.error('Error crítico de aplicación');
    }
  }

  /**
   * Logger con emoji para mejor legibilidad en desarrollo
   */
  success(message: string, ...args: unknown[]): void {
    if (this.shouldLog()) {
      console.log('✅', message, ...args);
    }
  }

  loading(message: string, ...args: unknown[]): void {
    if (this.shouldLog()) {
      console.log('🔄', message, ...args);
    }
  }

  failed(message: string, ...args: unknown[]): void {
    if (this.shouldLog()) {
      console.error('❌', message, ...args);
    }
  }
}

export const logger = new Logger();

// Re-exportar para compatibilidad
export default logger;
