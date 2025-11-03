import { createContext, useState, useContext, useEffect, useMemo, useRef } from 'react';
import { cartService } from '../services/api';
import { useAuth } from './AuthContext';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

export interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  category?: string;
  image?: string;
}

export interface CartContextType {
  items: CartItem[];
  addToCart: (item: CartItem) => void;
  updateQuantity: (id: string, quantity: number) => void;
  removeFromCart: (id: string) => void;
  clearCart: () => void;
  getTotalPrice: () => number;
  getTotalItems: () => number;
  loading: boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);
  const { isAuthenticated } = useAuth();
  const lastToastTime = useRef<number>(0);
  const lastToastItem = useRef<string>('');

  // Cargar carrito desde localStorage o backend
  useEffect(() => {
    const loadCart = async () => {
      if (isAuthenticated) {
        try {
          setLoading(true);
          // Intentar cargar desde backend
          const backendCart = await cartService.getCart();
          const cartItems: CartItem[] = backendCart.items.map(item => ({
            id: String(item.productId),
            name: item.product?.name || 'Producto',
            price: item.product?.price || 0,
            quantity: item.quantity,
            image: item.product?.imageUrl,
          }));
          setItems(cartItems);
        } catch (error) {
          logger.error('Error al cargar carrito del backend:', error);
          // Cargar desde localStorage como fallback
          loadFromLocalStorage();
        } finally {
          setLoading(false);
        }
      } else {
        // Usuario no autenticado: usar localStorage
        loadFromLocalStorage();
      }
    };

    loadCart();
  }, [isAuthenticated]);

  // Guardar carrito en localStorage (siempre, como backup)
  useEffect(() => {
    if (items.length > 0) {
      localStorage.setItem('baby-cash-cart', JSON.stringify(items));
    } else {
      localStorage.removeItem('baby-cash-cart');
    }
  }, [items]);

  const loadFromLocalStorage = () => {
    const savedCart = localStorage.getItem('baby-cash-cart');
    if (savedCart) {
      try {
        setItems(JSON.parse(savedCart));
      } catch (error) {
        logger.error('Error al cargar carrito de localStorage:', error);
      }
    }
  };

  const addToCart = (item: CartItem) => {
    const existing = items.find((i) => i.id === item.id);
    
    // Actualizar estado local PRIMERO (optimistic update)
    if (existing) {
      setItems(
        items.map((i) => (i.id === item.id ? { ...i, quantity: i.quantity + item.quantity } : i))
      );
    } else {
      setItems([...items, item]);
    }

    // Prevenir toast duplicado: solo mostrar si pasaron >500ms o es diferente item
    const now = Date.now();
    if (now - lastToastTime.current > 500 || lastToastItem.current !== item.id) {
      toast.success(`${item.name} agregado al carrito`);
      lastToastTime.current = now;
      lastToastItem.current = item.id;
    }
    
    // Si está autenticado, sincronizar con backend en background (sin esperar)
    if (isAuthenticated) {
      cartService.addItem(Number(item.id), item.quantity).catch((error) => {
        logger.error('Error al sincronizar carrito con backend:', error);
        // No mostrar error al usuario, el estado local ya se actualizó
      });
    }
  };

  const updateQuantity = (id: string, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(id);
      return;
    }

    // Actualizar estado local primero
    setItems((prev) => prev.map((i) => (i.id === id ? { ...i, quantity } : i)));

    // Si está autenticado, sincronizar con backend en background
    if (isAuthenticated) {
      cartService.updateItem(Number(id), quantity).catch((error) => {
        logger.error('Error al actualizar cantidad en backend:', error);
      });
    }
  };

  const removeFromCart = (id: string) => {
    // Actualizar estado local primero
    setItems((prev) => prev.filter((i) => i.id !== id));
    toast.success('Producto eliminado del carrito');

    // Si está autenticado, sincronizar con backend en background
    if (isAuthenticated) {
      cartService.removeItem(Number(id)).catch((error) => {
        logger.error('Error al eliminar del carrito en backend:', error);
      });
    }
  };

  const clearCart = () => {
    // Primero limpiar el estado local y localStorage
    setItems([]);
    localStorage.removeItem('baby-cash-cart');
    
    // Luego intentar limpiar en el backend si está autenticado (en background)
    if (isAuthenticated) {
      cartService.clearCart().catch((error) => {
        logger.error('Error al limpiar carrito en backend:', error);
        // No es crítico si falla, el carrito local ya está limpio
      });
    }
  };

  const getTotalPrice = () => items.reduce((total, item) => total + item.price * item.quantity, 0);
  const getTotalItems = () => items.reduce((total, item) => total + item.quantity, 0);

  // Memoizar el value del contexto para evitar re-renders innecesarios
  const contextValue = useMemo(
    () => ({
      items,
      addToCart,
      updateQuantity,
      removeFromCart,
      clearCart,
      getTotalPrice,
      getTotalItems,
      loading,
    }),
    [items, loading] // Solo recalcular cuando items o loading cambien
  );

  return (
    <CartContext.Provider value={contextValue}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) throw new Error('useCart debe usarse dentro de CartProvider');
  return context;
};
