import axios, { AxiosError } from 'axios';
import axiosRetry from 'axios-retry';

// Interfaces
export interface AuthResponse {
  token: string;
  refreshToken: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN' | 'MODERATOR';
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN' | 'MODERATOR';
  enabled: boolean;
  phone?: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl?: string;
  stock: number;
  discountPrice?: number;
  featured?: boolean;
  rating?: number;
  reviewCount?: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CartItem {
  productId: number;
  quantity: number;
  product?: Product;
}

export interface Cart {
  id: number;
  items: CartItem[];
  total: number;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  id: number;
  userId?: number;
  items: OrderItem[];
  total?: number; // Deprecated, use totalAmount
  totalAmount?: number; // Backend uses this
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  createdAt: string;
  orderNumber?: string;
  shippingAddress?: string;
  notes?: string;
}

export interface UserStats {
  totalOrders: number;
  totalProducts: number;
  totalSpent: number;
  memberSince: string;
}

export interface UpdateProfileData {
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
}

export interface LoyaltyPoints {
  totalPoints: number;
  earnedThisMonth: number;
  earnedTotal: number;
  redeemedTotal: number;
  expiringSoon: number;
  memberSince: string;
  tier: 'BRONZE' | 'SILVER' | 'GOLD';
  availableDiscountPercent: number;
  pointsForNextDiscount: number;
}

export interface LoyaltyTransaction {
  id: number;
  transactionType: string;
  transactionTypeName: string;
  points: number;
  amountSpent?: number;
  orderId?: number;
  orderNumber?: string;
  description: string;
  createdAt: string;
  expiresAt?: string;
  active: boolean;
  expired: boolean;
}

export interface ApiError {
  message: string;
  status: number;
}

// Axios instance
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Configurar retry logic
axiosRetry(api, {
  retries: 2, // Reducir reintentos de 3 a 2
  retryDelay: axiosRetry.exponentialDelay, // Delay exponencial: 1s, 2s
  retryCondition: (error) => {
    // NO reintentar en errores 4xx (errores del cliente, permisos, etc)
    if (error.response?.status && error.response.status >= 400 && error.response.status < 500) {
      return false;
    }
    
    // Reintentar solo en errores de red o errores 5xx del servidor
    return axiosRetry.isNetworkOrIdempotentRequestError(error) || 
           (error.response?.status !== undefined && error.response.status >= 500);
  },
  onRetry: (retryCount, error) => {
    console.warn(`🔄 Reintento ${retryCount}/2 para ${error.config?.url}`, error.message);
  },
});

// Token management
const getAuthData = () => {
  const token = localStorage.getItem('baby-cash-token');
  const refreshToken = localStorage.getItem('baby-cash-refresh-token');
  return { token, refreshToken };
};

const setAuthData = (token: string, refreshToken: string) => {
  localStorage.setItem('baby-cash-token', token);
  localStorage.setItem('baby-cash-refresh-token', refreshToken);
};

const clearAuthData = () => {
  localStorage.removeItem('baby-cash-token');
  localStorage.removeItem('baby-cash-refresh-token');
  localStorage.removeItem('baby-cash-user');
};

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const { token } = getAuthData();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor with refresh token logic
let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  for (const prom of failedQueue) {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  }
  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Solo manejar refresh si la URL NO es de login o register
    const isAuthEndpoint = originalRequest.url?.includes('/auth/login') || 
                          originalRequest.url?.includes('/auth/register');

    if (error.response?.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => { throw err; });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const { refreshToken } = getAuthData();

      if (!refreshToken) {
        isRefreshing = false;
        clearAuthData();
        // No redirigir automáticamente, dejar que el componente maneje el error
        throw error;
      }

      try {
        const { data } = await axios.post(
          `${api.defaults.baseURL}/auth/refresh`,
          { refreshToken }
        );
        
        setAuthData(data.token, data.refreshToken);
        api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
        originalRequest.headers.Authorization = `Bearer ${data.token}`;
        processQueue(null, data.token);
        
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        clearAuthData();
        // No redirigir automáticamente, dejar que el componente maneje el error
        throw refreshError;
      } finally {
        isRefreshing = false;
      }
    }

    throw error;
  }
);

// Auth service
export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', { email, password });
    setAuthData(data.token, data.refreshToken);
    return data;
  },

  async register(userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
  }): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/register', userData);
    setAuthData(data.token, data.refreshToken);
    return data;
  },

  async logout(): Promise<void> {
    const { refreshToken } = getAuthData();
    if (refreshToken) {
      await api.post('/auth/logout', { refreshToken });
    }
    clearAuthData();
  },

  async forgotPassword(email: string): Promise<{ message: string }> {
    const { data } = await api.post<{ message: string }>('/auth/forgot-password', { email });
    return data;
  },

  async validateResetToken(token: string): Promise<{ valid: boolean }> {
    const { data } = await api.get<{ valid: boolean }>(`/auth/validate-reset-token/${token}`);
    return data;
  },

  async resetPassword(token: string, newPassword: string, confirmPassword: string): Promise<{ message: string }> {
    const { data } = await api.post<{ message: string }>('/auth/reset-password', {
      token,
      newPassword,
      confirmPassword,
    });
    return data;
  },

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem('baby-cash-user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated(): boolean {
    const { token } = getAuthData();
    return !!token;
  },
};

// Product service
export const productService = {
  async getAll(page = 0, size = 10): Promise<PagedResponse<Product>> {
    const { data } = await api.get<PagedResponse<Product>>(`/products?page=${page}&size=${size}`);
    return data;
  },

  async getById(id: number): Promise<Product> {
    const { data } = await api.get<Product>(`/products/${id}`);
    return data;
  },

  async getByCategory(category: string): Promise<PagedResponse<Product>> {
    const { data } = await api.get<PagedResponse<Product>>(`/products/category/${category}`);
    return data;
  },

  async getFeatured(): Promise<Product[]> {
    const { data } = await api.get<Product[]>('/products/featured');
    return data;
  },

  async search(query: string): Promise<PagedResponse<Product>> {
    const { data } = await api.get<PagedResponse<Product>>('/products/search', { params: { q: query } });
    return data;
  },
};

// Cart service
export const cartService = {
  async getCart(): Promise<Cart> {
    const { data } = await api.get<Cart>('/cart');
    return data;
  },

  async addItem(productId: number, quantity: number): Promise<Cart> {
    const { data } = await api.post<Cart>('/cart/add', { productId, quantity });
    return data;
  },

  async updateItem(itemId: number, quantity: number): Promise<Cart> {
    const { data } = await api.put<Cart>(`/cart/items/${itemId}?quantity=${quantity}`);
    return data;
  },

  async removeItem(itemId: number): Promise<Cart> {
    const { data } = await api.delete<Cart>(`/cart/items/${itemId}`);
    return data;
  },

  async clearCart(): Promise<void> {
    await api.delete('/cart/clear');
  },
};

// Order service
export const orderService = {
  async createOrder(orderData: { 
    shippingAddress: string; 
    notes?: string; 
    items: Array<{ productId: number; quantity: number }> 
  }): Promise<Order> {
    const { data } = await api.post<Order>('/orders', orderData);
    return data;
  },

  async getMyOrders(): Promise<PagedResponse<Order>> {
    const { data } = await api.get<PagedResponse<Order>>('/orders');
    return data;
  },

  async getOrderById(id: number): Promise<Order> {
    const { data } = await api.get<Order>(`/orders/${id}`);
    return data;
  },

  async cancelOrder(id: number): Promise<Order> {
    const { data } = await api.put<Order>(`/orders/${id}/cancel`);
    return data;
  },
};

// User service
export const userService = {
  async getStats(): Promise<UserStats> {
    const { data } = await api.get<UserStats>('/users/stats');
    return data;
  },

  async updateProfile(profileData: UpdateProfileData): Promise<User> {
    const { data } = await api.put<User>('/users/profile', profileData);
    return data;
  },

  async getProfile(): Promise<User> {
    const { data } = await api.get<User>('/users/profile');
    return data;
  },
};

// Admin service
export const adminService = {
  // Products
  async createProduct(product: Omit<Product, 'id'>): Promise<Product> {
    const { data } = await api.post<Product>('/admin/products', product);
    return data;
  },

  async updateProduct(id: number, product: Partial<Product>): Promise<Product> {
    const { data } = await api.put<Product>(`/admin/products/${id}`, product);
    return data;
  },

  async deleteProduct(id: number): Promise<void> {
    await api.delete(`/admin/products/${id}`);
  },

  async toggleProductFeatured(id: number): Promise<Product> {
    const { data } = await api.put<Product>(`/admin/products/${id}/toggle-featured`);
    return data;
  },

  // Orders
  async getAllOrders(page = 0, size = 10): Promise<PagedResponse<Order>> {
    const { data } = await api.get<PagedResponse<Order>>('/admin/orders', {
      params: { page, size }
    });
    return data;
  },

  async getOrdersByStatus(status: Order['status'], page = 0, size = 10): Promise<PagedResponse<Order>> {
    const { data } = await api.get<PagedResponse<Order>>('/admin/orders', {
      params: { status, page, size }
    });
    return data;
  },

  async updateOrderStatus(id: number, status: Order['status']): Promise<Order> {
    const { data } = await api.put<Order>(`/admin/orders/${id}/status`, null, {
      params: { status }
    });
    return data;
  },

  async getOrderStats(): Promise<any> {
    const { data } = await api.get('/admin/orders/stats');
    return data;
  },

  // Testimonials
  async getAllTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials/admin/all');
    return data;
  },

  async getPendingTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials/admin/pending');
    return data;
  },

  async approveTestimonial(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/approve`);
    return data;
  },

  async rejectTestimonial(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/reject`);
    return data;
  },

  async toggleTestimonialFeatured(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/toggle-featured`);
    return data;
  },

  async deleteTestimonial(id: number): Promise<void> {
    await api.delete(`/testimonials/admin/${id}`);
  },

  async getTestimonialStats(): Promise<any> {
    const { data } = await api.get('/testimonials/admin/stats');
    return data;
  },
};

// Loyalty service
export const loyaltyService = {
  async getPoints(): Promise<LoyaltyPoints> {
    const { data } = await api.get<LoyaltyPoints>('/loyalty/points');
    return data;
  },

  async getHistory(page = 0, size = 10): Promise<PagedResponse<LoyaltyTransaction>> {
    const { data } = await api.get<PagedResponse<LoyaltyTransaction>>('/loyalty/history', {
      params: { page, size }
    });
    return data;
  },

  async redeemPoints(points: number): Promise<void> {
    await api.post('/loyalty/redeem', { points });
  },
};

// Blog interfaces
export interface BlogPost {
  id: number;
  title: string;
  slug: string;
  excerpt?: string;
  content: string;
  imageUrl?: string;
  published: boolean;
  featured: boolean;
  viewCount: number;
  tags: string[];
  author: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BlogPostRequest {
  title: string;
  excerpt?: string;
  content: string;
  imageUrl?: string;
  published?: boolean;
  featured?: boolean;
  tags?: string[];
}

// Blog service
export const blogService = {
  async getPosts(page = 0, size = 10): Promise<PagedResponse<BlogPost>> {
    const { data } = await api.get<PagedResponse<BlogPost>>('/blog', {
      params: { page, size }
    });
    return data;
  },

  async getAllPostsAdmin(page = 0, size = 10): Promise<PagedResponse<BlogPost>> {
    const { data } = await api.get<PagedResponse<BlogPost>>('/blog/admin/all', {
      params: { page, size }
    });
    return data;
  },

  async getPostById(id: number): Promise<BlogPost> {
    const { data } = await api.get<BlogPost>(`/blog/${id}`);
    return data;
  },

  async getPostBySlug(slug: string): Promise<BlogPost> {
    const { data } = await api.get<BlogPost>(`/blog/slug/${slug}`);
    return data;
  },

  async getFeaturedPosts(): Promise<BlogPost[]> {
    const { data } = await api.get<BlogPost[]>('/blog/featured');
    return data;
  },

  async searchPosts(query: string, page = 0, size = 10): Promise<PagedResponse<BlogPost>> {
    const { data } = await api.get<PagedResponse<BlogPost>>('/blog/search', {
      params: { q: query, page, size }
    });
    return data;
  },

  async getPostsByTag(tag: string, page = 0, size = 10): Promise<PagedResponse<BlogPost>> {
    const { data} = await api.get<PagedResponse<BlogPost>>(`/blog/tag/${tag}`, {
      params: { page, size }
    });
    return data;
  },

  async getMostViewedPosts(): Promise<BlogPost[]> {
    const { data } = await api.get<BlogPost[]>('/blog/most-viewed');
    return data;
  },

  async getMyPosts(page = 0, size = 10): Promise<PagedResponse<BlogPost>> {
    const { data } = await api.get<PagedResponse<BlogPost>>('/blog/author/me', {
      params: { page, size }
    });
    return data;
  },

  async createPost(request: BlogPostRequest): Promise<BlogPost> {
    const { data } = await api.post<BlogPost>('/blog', request);
    return data;
  },

  async updatePost(id: number, request: BlogPostRequest): Promise<BlogPost> {
    const { data } = await api.put<BlogPost>(`/blog/${id}`, request);
    return data;
  },

  async deletePost(id: number): Promise<void> {
    await api.delete(`/blog/${id}`);
  },

  async publishPost(id: number): Promise<BlogPost> {
    const { data } = await api.put<BlogPost>(`/blog/${id}/publish`);
    return data;
  },

  async unpublishPost(id: number): Promise<BlogPost> {
    const { data } = await api.put<BlogPost>(`/blog/${id}/unpublish`);
    return data;
  },

  async toggleFeatured(id: number): Promise<BlogPost> {
    const { data } = await api.put<BlogPost>(`/blog/${id}/toggle-featured`);
    return data;
  },
};

// Blog Comment Interfaces
export interface CommentUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface BlogComment {
  id: number;
  content: string;
  blogPostId: number;
  user: CommentUser;
  parentCommentId?: number;
  approved: boolean;
  createdAt: string;
  updatedAt: string;
  replies: BlogComment[];
}

export interface CommentRequest {
  content: string;
  parentCommentId?: number;
}

// Blog Comment Service
export const commentService = {
  async getComments(postId: number): Promise<BlogComment[]> {
    const { data } = await api.get<BlogComment[]>(`/blog/${postId}/comments`);
    return data;
  },

  async getCommentsCount(postId: number): Promise<number> {
    const { data } = await api.get<number>(`/blog/${postId}/comments/count`);
    return data;
  },

  async createComment(postId: number, request: CommentRequest): Promise<BlogComment> {
    const { data } = await api.post<BlogComment>(`/blog/${postId}/comments`, request);
    return data;
  },

  async updateComment(postId: number, commentId: number, request: CommentRequest): Promise<BlogComment> {
    const { data } = await api.put<BlogComment>(`/blog/${postId}/comments/${commentId}`, request);
    return data;
  },

  async deleteComment(postId: number, commentId: number): Promise<void> {
    await api.delete(`/blog/${postId}/comments/${commentId}`);
  },

  async approveComment(postId: number, commentId: number): Promise<BlogComment> {
    const { data } = await api.post<BlogComment>(`/blog/${postId}/comments/${commentId}/approve`);
    return data;
  },

  async getPendingComments(): Promise<BlogComment[]> {
    const { data } = await api.get<BlogComment[]>('/blog/admin/comments/pending');
    return data;
  },

  async getPendingCommentsCount(): Promise<number> {
    const { data } = await api.get<number>('/blog/admin/comments/pending/count');
    return data;
  },
};

// Testimonial Interfaces
export interface Testimonial {
  id: number;
  name: string;
  message: string;
  rating: number;
  avatar?: string;
  location?: string;
  approved: boolean;
  featured: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TestimonialRequest {
  name: string;
  message: string;
  rating: number;
  avatar?: string;
  location?: string;
}

export interface TestimonialStats {
  total: number;
  totalApproved: number;
  totalPending: number;
  totalFeatured: number;
}

// Testimonial Service
export const testimonialService = {
  async getTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials');
    return data;
  },

  async getFeaturedTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials/featured');
    return data;
  },

  async getTestimonialById(id: number): Promise<Testimonial> {
    const { data } = await api.get<Testimonial>(`/testimonials/${id}`);
    return data;
  },

  async createTestimonial(request: TestimonialRequest): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>('/testimonials', request);
    return data;
  },

  // Admin endpoints
  async getAllTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials/admin/all');
    return data;
  },

  async getPendingTestimonials(): Promise<Testimonial[]> {
    const { data } = await api.get<Testimonial[]>('/testimonials/admin/pending');
    return data;
  },

  async getStats(): Promise<TestimonialStats> {
    const { data } = await api.get<TestimonialStats>('/testimonials/admin/stats');
    return data;
  },

  async updateTestimonial(id: number, request: TestimonialRequest): Promise<Testimonial> {
    const { data } = await api.put<Testimonial>(`/testimonials/admin/${id}`, request);
    return data;
  },

  async deleteTestimonial(id: number): Promise<void> {
    await api.delete(`/testimonials/admin/${id}`);
  },

  async approveTestimonial(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/approve`);
    return data;
  },

  async rejectTestimonial(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/reject`);
    return data;
  },

  async toggleFeatured(id: number): Promise<Testimonial> {
    const { data } = await api.post<Testimonial>(`/testimonials/admin/${id}/toggle-featured`);
    return data;
  },

  async getAllTestimonialsPaged(page = 0, size = 10): Promise<PagedResponse<Testimonial>> {
    const { data } = await api.get<PagedResponse<Testimonial>>('/testimonials/admin/paged', {
      params: { page, size }
    });
    return data;
  },
};

// Contact Info Interfaces
export interface ContactInfo {
  id: number;
  companyName: string;
  phone: string;
  email: string;
  address: string;
  city?: string;
  country?: string;
  facebook?: string;
  instagram?: string;
  twitter?: string;
  whatsapp?: string;
  businessHours?: string;
  businessHoursDetails?: string;
  latitude?: number;
  longitude?: number;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ContactInfoRequest {
  companyName: string;
  phone: string;
  email: string;
  address: string;
  city?: string;
  country?: string;
  facebook?: string;
  instagram?: string;
  twitter?: string;
  whatsapp?: string;
  businessHours?: string;
  businessHoursDetails?: string;
  latitude?: number;
  longitude?: number;
  description?: string;
}

// Contact Info Service
export const contactInfoService = {
  async getContactInfo(): Promise<ContactInfo> {
    const { data } = await api.get<ContactInfo>('/contact-info');
    return data;
  },

  async updateContactInfo(request: ContactInfoRequest): Promise<ContactInfo> {
    const { data } = await api.put<ContactInfo>('/contact-info', request);
    return data;
  },

  async isConfigured(): Promise<boolean> {
    const { data } = await api.get<boolean>('/contact-info/status');
    return data;
  },
};

// Contact Message Interfaces
export interface ContactMessage {
  id: number;
  name: string;
  email: string;
  phone?: string;
  subject: string;
  message: string;
  ipAddress?: string;
  status: 'NEW' | 'READ' | 'REPLIED' | 'ARCHIVED';
  readAt?: string;
  repliedAt?: string;
  adminNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ContactMessageRequest {
  name: string;
  email: string;
  phone?: string;
  subject: string;
  message: string;
}

// Contact Message Service
export const contactMessageService = {
  // Public endpoint
  async sendMessage(request: ContactMessageRequest): Promise<ContactMessage> {
    const { data } = await api.post<ContactMessage>('/contact/send', request);
    return data;
  },

  // Admin endpoints
  async getAllMessages(): Promise<ContactMessage[]> {
    const { data } = await api.get<ContactMessage[]>('/contact/admin/messages');
    return data;
  },

  async getAllMessagesPaged(page = 0, size = 10): Promise<PagedResponse<ContactMessage>> {
    const { data } = await api.get<PagedResponse<ContactMessage>>('/contact/admin/messages/paged', {
      params: { page, size }
    });
    return data;
  },

  async getNewMessages(): Promise<ContactMessage[]> {
    const { data } = await api.get<ContactMessage[]>('/contact/admin/messages/new');
    return data;
  },

  async countNewMessages(): Promise<number> {
    const { data } = await api.get<{ count: number }>('/contact/admin/messages/new/count');
    return data.count;
  },

  async getRecentMessages(): Promise<ContactMessage[]> {
    const { data } = await api.get<ContactMessage[]>('/contact/admin/messages/recent');
    return data;
  },

  async getMessageById(id: number): Promise<ContactMessage> {
    const { data } = await api.get<ContactMessage>(`/contact/admin/messages/${id}`);
    return data;
  },

  async markAsRead(id: number): Promise<ContactMessage> {
    const { data } = await api.post<ContactMessage>(`/contact/admin/messages/${id}/read`);
    return data;
  },

  async markAsReplied(id: number, adminNotes?: string): Promise<ContactMessage> {
    const { data } = await api.post<ContactMessage>(`/contact/admin/messages/${id}/reply`, 
      adminNotes ? { adminNotes } : {}
    );
    return data;
  },

  async archiveMessage(id: number): Promise<ContactMessage> {
    const { data } = await api.post<ContactMessage>(`/contact/admin/messages/${id}/archive`);
    return data;
  },

  async unarchiveMessage(id: number): Promise<ContactMessage> {
    const { data } = await api.post<ContactMessage>(`/contact/admin/messages/${id}/unarchive`);
    return data;
  },

  async deleteMessage(id: number): Promise<void> {
    await api.delete(`/contact/admin/messages/${id}`);
  },
};

// Error handler
export const handleApiError = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string }>;
    
    if (axiosError.response) {
      const status = axiosError.response.status;
      const message = axiosError.response.data?.message;

      switch (status) {
        case 400:
          return message || 'Solicitud inválida';
        case 401:
          return 'No autorizado. Por favor inicia sesión';
        case 403:
          return 'No tienes permisos para realizar esta acción';
        case 404:
          return 'Recurso no encontrado';
        case 409:
          return message || 'Conflicto en la solicitud';
        case 429:
          return 'Demasiadas solicitudes. Por favor intenta más tarde';
        case 500:
          return 'Error del servidor. Por favor intenta más tarde';
        default:
          return message || `Error ${status}`;
      }
    }

    if (axiosError.request) {
      return 'No se pudo conectar al servidor';
    }
  }

  return 'Error desconocido';
};

export default api;
