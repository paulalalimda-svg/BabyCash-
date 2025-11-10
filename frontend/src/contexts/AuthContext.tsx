import { createContext, useState, useContext, useMemo, useEffect, type ReactNode } from 'react';
import { authService, type AuthResponse, type User, handleApiError } from '../services/api';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

// ==================== TIPOS ====================

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone?: string;
  }) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isModerator: boolean;
}

// ==================== CONTEXTO ====================

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// ==================== PROVIDER ====================

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Cargar usuario al iniciar
  useEffect(() => {
    const loadUser = () => {
      try {
        const userData = authService.getCurrentUser();
        if (userData && authService.isAuthenticated()) {
          setUser(userData);
        }
      } catch (error) {
        logger.error('Error al cargar usuario:', error);
        // Limpiar datos corruptos
        localStorage.removeItem('baby-cash-auth');
        localStorage.removeItem('baby-cash-user');
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, []);

  // ========== LOGIN ==========
  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response: AuthResponse = await authService.login(email, password);

      // Construir objeto de usuario
      const userData: User = {
        id: 0, // El backend no lo devuelve aún, pero no es crítico
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        enabled: true,
      };

      // Guardar usuario en localStorage
      localStorage.setItem('baby-cash-user', JSON.stringify(userData));
      setUser(userData);
      toast.success(`¡Bienvenido, ${response.firstName}!`);
    } catch (error) {
      const errorMessage = handleApiError(error);
      toast.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // ========== REGISTER ==========
  const register = async (userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone?: string;
  }) => {
    try {
      setLoading(true);
      const response: AuthResponse = await authService.register(userData);

      // Construir objeto de usuario
      const newUser: User = {
        id: 0,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        enabled: true,
      };

      // Guardar usuario en localStorage
      localStorage.setItem('baby-cash-user', JSON.stringify(newUser));
      setUser(newUser);
      toast.success(`¡Cuenta creada exitosamente! Bienvenido, ${response.firstName}!`);
    } catch (error) {
      const errorMessage = handleApiError(error);
      toast.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // ========== LOGOUT ==========
  const logout = async () => {
    try {
      setLoading(true);
      await authService.logout();
      setUser(null);
      toast.success('Sesión cerrada correctamente');
    } catch (error) {
      logger.error('Error al cerrar sesión:', error);
      // Limpiar localmente aunque falle en el servidor
      setUser(null);
      localStorage.removeItem('baby-cash-auth');
      localStorage.removeItem('baby-cash-user');
    } finally {
      setLoading(false);
    }
  };

  // ========== COMPUTED VALUES ==========
  const isAuthenticated = useMemo(() => !!user && authService.isAuthenticated(), [user]);
  const isAdmin = useMemo(() => user?.role === 'ADMIN', [user]);
  const isModerator = useMemo(() => user?.role === 'MODERATOR', [user]);

  // ========== CONTEXT VALUE ==========
  const contextValue = useMemo(
    () => ({
      user,
      loading,
      login,
      register,
      logout,
      isAuthenticated,
      isAdmin,
      isModerator,
    }),
    [user, loading, isAuthenticated, isAdmin, isModerator]
  );

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

// ==================== HOOK ====================

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
