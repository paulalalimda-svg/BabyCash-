// Tipos de productos
// Tipos de productos simulados (array local)
export type ProductMock = {
  TITULO: string;
  DESCRIPCION: string;
  OPCIONES: string;
  PRECIO: number;
  DETALLEPRECIO: string;
  FOTO: string;
  CATEGORIA: string;
};

// Tipos de productos reales (base de datos)
export type Product = {
  id_producto: string;
  nombre_producto: string;
  descripcion_producto: string;
  precio_producto: number;
  imagen_producto: string;
  categoria_producto: string;
  detalle_precio?: string;
  opciones?: string;
};

// Tipos de carrito
export interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
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
}

// Tipos de autenticación
export type User = {
  id: string;
  email: string;
  name: string;
};

export type AuthContextType = {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  loading: boolean;
};

// Tipos de formularios
export type LoginFormData = {
  email: string;
  password: string;
};

export type ContactFormData = {
  name: string;
  email: string;
  message: string;
};

// Tipos de blog y testimonios
export type BlogPost = {
  id: string;
  title: string;
  excerpt: string;
  content: string;
  image: string;
  date: string;
  author: string;
  category: string;
};

// NOTA: Testimonial ahora se importa desde '../services/api'
// para mantener consistencia con los tipos del backend

// Tipos de API responses
export type ApiResponse<T> = {
  success: boolean;
  data: T;
  message?: string;
};

// Tipos de coordenadas para Mapbox
export type Coordinates = {
  longitude: number;
  latitude: number;
};
