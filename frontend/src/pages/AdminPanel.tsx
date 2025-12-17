import {
  AlertTriangle,
  BookOpen,
  Box,
  CheckCircle,
  Clock,
  DollarSign,
  Edit,
  Filter,
  Home,
  Loader,
  Mail,
  MapPin,
  Package,
  Plus,
  Save,
  Search,
  ShoppingBag,
  ShoppingCart,
  Star,
  Trash2,
  Truck,
  X,
  XCircle,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { BlogsManager } from '../components/admin/BlogsManager';
import { ContactMessagesManager } from '../components/admin/ContactMessagesManager';
import { TestimonialsManager } from '../components/admin/TestimonialsManager';
import { useAuth } from '../contexts/AuthContext';
import { adminService, productService, type Order, type Product } from '../services/api';
import { logger } from '../utils/logger';

type Tab = 'dashboard' | 'products' | 'orders' | 'blogs' | 'testimonials' | 'messages';

const AdminPanel = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('dashboard');
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Paginación productos
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);
  const productsPerPage = 10;

  // Paginación órdenes
  const [currentOrderPage, setCurrentOrderPage] = useState(0);
  const ordersPerPage = 4;
  const [_ordersTotalPages, setOrdersTotalPages] = useState(0);
  const [ordersTotalElements, setOrdersTotalElements] = useState(0);

  // Filtro de órdenes
  const [orderStatusFilter, setOrderStatusFilter] = useState<string>('ALL');

  const [showProductForm, setShowProductForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [productForm, setProductForm] = useState({
    name: '',
    description: '',
    price: '',
    discountPrice: '',
    category: 'HEALTHCARE',
    stock: '',
    imageUrl: '',
    featured: false,
  });

  useEffect(() => {
    if (!user || (user.role !== 'ADMIN' && user.role !== 'MODERATOR')) {
      toast.error('No tienes permisos para acceder al panel de administración');
      navigate('/');
    }
  }, [user, navigate]);

  // Solo cargar datos cuando esté en la pestaña de dashboard, products u orders
  useEffect(() => {
    if (activeTab === 'dashboard' || activeTab === 'products' || activeTab === 'orders') {
      loadData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, currentOrderPage, activeTab, orderStatusFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      logger.log('🔄 Recargando datos del panel...');

      // Solo cargar lo necesario según la pestaña activa
      if (activeTab === 'products' || activeTab === 'dashboard') {
        const productsData = await productService.getAll(currentPage, productsPerPage);
        logger.log('✅ Productos cargados:', productsData.content.length);
        setProducts(productsData.content);
        setTotalPages(productsData.totalPages);
        setTotalProducts(productsData.totalElements);
      }

      if (activeTab === 'orders' || activeTab === 'dashboard') {
        try {
          let ordersData;
          if (orderStatusFilter === 'ALL') {
            ordersData = await adminService.getAllOrders(currentOrderPage, ordersPerPage);
          } else {
            ordersData = await adminService.getOrdersByStatus(
              orderStatusFilter as Order['status'],
              currentOrderPage,
              ordersPerPage
            );
          }

          logger.log('✅ Órdenes cargadas:', ordersData.content?.length || 0);
          setOrders(ordersData.content || []);
          setOrdersTotalPages(ordersData.totalPages || 0);
          setOrdersTotalElements(ordersData.totalElements || 0);
        } catch (orderError: unknown) {
          // Silenciar error 403 de órdenes si el usuario no tiene permisos
          if ((orderError as { response?: { status?: number } })?.response?.status !== 403) {
            logger.error('Error al cargar órdenes:', orderError);
          }
          setOrders([]);
          setOrdersTotalPages(0);
          setOrdersTotalElements(0);
        }
      }
    } catch (error: unknown) {
      logger.error('Error al cargar datos:', error);
      if ((error as { response?: { status?: number } })?.response?.status !== 403) {
        toast.error('Error al cargar datos del panel');
      }
    } finally {
      setLoading(false);
    }
  };

  // Usar totalAmount que es lo que devuelve el backend
  const totalSales = orders.reduce(
    (sum, order) => sum + (order.totalAmount || order.total || 0),
    0
  );
  const pendingOrders = orders.filter((o) => o.status === 'PENDING').length;
  const lowStockProducts = products.filter((p) => p.stock < 10);

  const filteredProducts = products.filter(
    (p) =>
      p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.category.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Orders are loaded server-side with pagination & status filtering
  const filteredOrders = orders; // kept for compatibility with existing UI

  const handleProductSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const price = Number.parseFloat(productForm.price);
      const discountPrice = productForm.discountPrice
        ? Number.parseFloat(productForm.discountPrice)
        : undefined;

      // Validar que el precio con descuento sea menor al precio normal
      if (discountPrice && discountPrice >= price) {
        toast.error('El precio final con descuento debe ser menor al precio normal');
        return;
      }

      const productData = {
        name: productForm.name,
        description: productForm.description,
        price: price,
        discountPrice: discountPrice,
        category: productForm.category,
        stock: Number.parseInt(productForm.stock),
        imageUrl: productForm.imageUrl || undefined,
        featured: productForm.featured,
        enabled: true, // Agregar enabled por defecto
      };

      logger.log('🔄 Enviando producto:', productData);
      logger.log('� Producto JSON:', JSON.stringify(productData, null, 2));
      logger.log(
        '�🔑 Token en localStorage:',
        localStorage.getItem('baby-cash-token') ? 'Existe' : 'NO EXISTE'
      );
      logger.log('👤 Usuario:', localStorage.getItem('baby-cash-user'));

      if (editingProduct) {
        logger.log('✏️ Actualizando producto ID:', editingProduct.id);
        const result = await adminService.updateProduct(editingProduct.id, productData);
        logger.log('✅ Producto actualizado:', result);
        toast.success('Producto actualizado exitosamente');
      } else {
        logger.log('➕ Creando nuevo producto');
        const result = await adminService.createProduct(productData);
        logger.log('✅ Producto creado:', result);
        toast.success('Producto creado exitosamente');
      }

      setShowProductForm(false);
      setEditingProduct(null);
      resetProductForm();
      loadData();
    } catch (error: unknown) {
      const err = error as {
        response?: {
          data?: { message?: string; errors?: Record<string, string> };
          status?: number;
        };
      };
      logger.error('❌ Error completo:', error);
      logger.error('❌ Error response:', err.response);
      logger.error('❌ Error response data:', err.response?.data);
      logger.error('❌ Error status:', err.response?.status);
      logger.error('❌ Error message:', err.response?.data?.message);
      logger.error('❌ Error details:', JSON.stringify(err.response?.data, null, 2));

      // Mostrar errores de validación específicos
      if (err.response?.data?.errors) {
        const errors = err.response.data.errors;
        const errorMessages = Object.values(errors).join(', ');
        toast.error(`Errores de validación: ${errorMessages}`);
      } else {
        toast.error(err.response?.data?.message || 'Error al guardar producto');
      }
    }
  };

  const handleEditProduct = (product: Product) => {
    setEditingProduct(product);
    setProductForm({
      name: product.name,
      description: product.description,
      price: product.price.toString(),
      discountPrice: product.discountPrice?.toString() || '',
      category: product.category,
      stock: product.stock.toString(),
      imageUrl: product.imageUrl || '',
      featured: product.featured || false,
    });
    setShowProductForm(true);
  };

  const handleDeleteProduct = async (id: number) => {
    if (!confirm('¿Estás seguro de eliminar este producto?')) return;
    try {
      await adminService.deleteProduct(id);
      logger.log('✅ Producto eliminado exitosamente');
      toast.success('Producto eliminado exitosamente');
      loadData();
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      logger.error('❌ Error al eliminar producto:', error);
      logger.error('❌ Error response:', err.response?.data);
      toast.error(err.response?.data?.message || 'Error al eliminar producto');
    }
  };

  const handleToggleFeatured = async (id: number) => {
    try {
      logger.log('⭐ Toggling featured para producto ID:', id);
      await adminService.toggleProductFeatured(id);
      logger.log('✅ Featured actualizado exitosamente');
      toast.success('Estado de destacado actualizado');
      loadData();
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      logger.error('❌ Error al actualizar featured:', error);
      logger.error('❌ Error response:', err.response?.data);
      toast.error(err.response?.data?.message || 'Error al actualizar producto');
    }
  };

  const resetProductForm = () => {
    setProductForm({
      name: '',
      description: '',
      price: '',
      discountPrice: '',
      category: 'HEALTHCARE',
      stock: '',
      imageUrl: '',
      featured: false,
    });
  };

  const handleUpdateOrderStatus = async (orderId: number, status: Order['status']) => {
    try {
      await adminService.updateOrderStatus(orderId, status);
      toast.success('Estado de orden actualizado');
      loadData();
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      logger.error('Error al actualizar orden:', error);
      toast.error(err.response?.data?.message || 'Error al actualizar orden');
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="border-baby-blue size-16 animate-spin rounded-full border-y-2"></div>
      </div>
    );
  }

  return (
    <div className="bg-baby-cream min-h-screen py-8 pt-24">
      <div className="container mx-auto px-4">
        <h1 className="text-baby-blue mb-8 text-4xl font-bold">Panel de Administración</h1>

        {/* Tabs */}
        <div className="mb-8 flex gap-4 border-b">
          <button
            onClick={() => {
              setActiveTab('dashboard');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'dashboard' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <Home size={20} />
            Dashboard
          </button>
          <button
            onClick={() => {
              setActiveTab('products');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'products' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <Box size={20} />
            Productos
          </button>
          <button
            onClick={() => {
              setActiveTab('orders');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'orders' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <ShoppingCart size={20} />
            Órdenes
          </button>
          <button
            onClick={() => {
              setActiveTab('blogs');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'blogs' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <BookOpen size={20} />
            Blogs
          </button>
          <button
            onClick={() => {
              setActiveTab('testimonials');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'testimonials' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <Star size={20} />
            Testimonios
          </button>
          <button
            onClick={() => {
              setActiveTab('messages');
              setSearchTerm('');
            }}
            className={`flex items-center gap-2 px-6 py-3 font-semibold transition-colors ${activeTab === 'messages' ? 'text-baby-blue border-baby-blue border-b-2' : 'hover:text-baby-blue text-gray-600'}`}
          >
            <Mail size={20} />
            Mensajes
          </button>
        </div>

        {/* Dashboard Tab */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6">
            {/* Low Stock Alert - MOVED TO TOP */}
            {lowStockProducts.length > 0 && (
              <div className="rounded-xl border-l-4 border-red-500 bg-red-50 p-6 shadow-lg">
                <div className="mb-4 flex items-center gap-3">
                  <AlertTriangle className="size-6 text-red-500" />
                  <h3 className="text-lg font-bold text-red-700">
                    ⚠️ Alerta: Stock Bajo - Acción Requerida
                  </h3>
                </div>
                <p className="mb-4 text-sm text-red-600">
                  Los siguientes productos necesitan reabastecimiento urgente:
                </p>
                <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-3">
                  {lowStockProducts.map((product) => (
                    <div
                      key={product.id}
                      className="rounded-lg border border-red-200 bg-white p-3 transition-shadow hover:shadow-md"
                    >
                      <p className="text-sm font-semibold">{product.name}</p>
                      <p className="text-xs font-bold text-red-600">
                        ⚠️ Stock: {product.stock} unidades
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Stats Cards */}
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
              <div className="from-baby-blue to-baby-purple rounded-xl bg-gradient-to-br p-6 text-white shadow-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-white/80">Total Ventas</p>
                    <p className="text-2xl font-bold">${totalSales.toLocaleString('es-CO')}</p>
                  </div>
                  <DollarSign className="size-10 text-white/80" />
                </div>
              </div>

              <div className="from-baby-pink to-baby-mint rounded-xl bg-gradient-to-br p-6 text-white shadow-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-white/80">Total Productos</p>
                    <p className="text-2xl font-bold">{totalProducts}</p>
                  </div>
                  <Package className="size-10 text-white/80" />
                </div>
              </div>

              <div className="from-baby-purple to-baby-blue rounded-xl bg-gradient-to-br p-6 text-white shadow-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-white/80">Órdenes</p>
                    <p className="text-2xl font-bold">{orders.length}</p>
                  </div>
                  <ShoppingCart className="size-10 text-white/80" />
                </div>
              </div>

              <div className="from-baby-mint to-baby-pink rounded-xl bg-gradient-to-br p-6 text-white shadow-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-white/80">Pendientes</p>
                    <p className="text-2xl font-bold">{pendingOrders}</p>
                  </div>
                  <AlertTriangle className="size-10 text-white/80" />
                </div>
              </div>
            </div>

            {/* Download CSV Button */}
            <div className="mb-6 flex justify-end">
              <button
                onClick={() => {
                  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
                  window.open(`${API_URL}/reports/dashboard/csv`, '_blank');
                  toast.success('Descargando extracto del dashboard...');
                }}
                className="flex items-center gap-2 rounded-lg bg-green-600 px-6 py-3 text-white shadow-lg transition-colors hover:bg-green-700"
              >
                <Save className="size-5" />
                Descargar Extracto CSV
              </button>
            </div>

            {/* Charts Section */}
            <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
              {/* Sales by Status */}
              <div className="rounded-xl bg-white p-6 shadow-lg">
                <h3 className="mb-4 text-lg font-bold">Órdenes por Estado</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={[
                        {
                          name: 'Pendientes',
                          value: orders.filter((o) => o.status === 'PENDING').length,
                          fill: '#FBBF24',
                        },
                        {
                          name: 'Procesando',
                          value: orders.filter((o) => o.status === 'PROCESSING').length,
                          fill: '#60A5FA',
                        },
                        {
                          name: 'En Envío',
                          value: orders.filter((o) => o.status === 'SHIPPED').length,
                          fill: '#A78BFA',
                        },
                        {
                          name: 'Entregadas',
                          value: orders.filter((o) => o.status === 'DELIVERED').length,
                          fill: '#34D399',
                        },
                        {
                          name: 'Canceladas',
                          value: orders.filter((o) => o.status === 'CANCELLED').length,
                          fill: '#F87171',
                        },
                      ].filter((item) => item.value > 0)}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={100}
                      label
                    >
                      {[
                        {
                          name: 'Pendientes',
                          value: orders.filter((o) => o.status === 'PENDING').length,
                          fill: '#FBBF24',
                        },
                        {
                          name: 'Procesando',
                          value: orders.filter((o) => o.status === 'PROCESSING').length,
                          fill: '#60A5FA',
                        },
                        {
                          name: 'En Envío',
                          value: orders.filter((o) => o.status === 'SHIPPED').length,
                          fill: '#A78BFA',
                        },
                        {
                          name: 'Entregadas',
                          value: orders.filter((o) => o.status === 'DELIVERED').length,
                          fill: '#34D399',
                        },
                        {
                          name: 'Canceladas',
                          value: orders.filter((o) => o.status === 'CANCELLED').length,
                          fill: '#F87171',
                        },
                      ]
                        .filter((item) => item.value > 0)
                        .map((entry) => (
                          <Cell key={`cell-${entry.name}`} fill={entry.fill} />
                        ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              {/* Top Products by Stock */}
              <div className="rounded-xl bg-white p-6 shadow-lg">
                <h3 className="mb-4 text-lg font-bold">Top 5 Productos con Más Stock</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart
                    data={[...products]
                      .sort((a, b) => b.stock - a.stock)
                      .slice(0, 5)
                      .map((p) => ({
                        name: p.name.length > 15 ? p.name.substring(0, 15) + '...' : p.name,
                        stock: p.stock,
                      }))}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="stock" fill="#9CA3E8" />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              {/* Products by Category */}
              <div className="rounded-xl bg-white p-6 shadow-lg">
                <h3 className="mb-4 text-lg font-bold">Productos por Categoría</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart
                    data={[
                      {
                        categoria: 'Ropa',
                        cantidad: products.filter((p) => p.category === 'CLOTHING').length,
                      },
                      {
                        categoria: 'Juguetes',
                        cantidad: products.filter((p) => p.category === 'TOYS').length,
                      },
                      {
                        categoria: 'Alimentos',
                        cantidad: products.filter((p) => p.category === 'FOOD').length,
                      },
                      {
                        categoria: 'Muebles',
                        cantidad: products.filter((p) => p.category === 'FURNITURE').length,
                      },
                      {
                        categoria: 'Salud',
                        cantidad: products.filter((p) => p.category === 'HEALTHCARE').length,
                      },
                      {
                        categoria: 'Accesorios',
                        cantidad: products.filter((p) => p.category === 'ACCESSORIES').length,
                      },
                      {
                        categoria: 'Libros',
                        cantidad: products.filter((p) => p.category === 'BOOKS').length,
                      },
                      {
                        categoria: 'Otros',
                        cantidad: products.filter((p) => p.category === 'OTHER').length,
                      },
                    ].filter((item) => item.cantidad > 0)}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="categoria" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="cantidad" fill="#FBB7D4" />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              {/* Orders by Date */}
              <div className="rounded-xl bg-white p-6 shadow-lg">
                <h3 className="mb-4 text-lg font-bold">Órdenes Recientes (Últimas 7)</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={orders
                      .slice(0, 7)
                      .reverse()
                      .map((order, i) => ({
                        orden: `#${i + 1}`,
                        monto: order.totalAmount || order.total || 0,
                      }))}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="orden" />
                    <YAxis />
                    <Tooltip formatter={(value: number) => `$${value.toLocaleString('es-CO')}`} />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="monto"
                      stroke="#9CA3E8"
                      strokeWidth={2}
                      name="Monto"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}

        {/* Products Tab */}
        {activeTab === 'products' && (
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold">Gestión de Productos</h2>
              <button
                onClick={() => {
                  resetProductForm();
                  setEditingProduct(null);
                  setShowProductForm(true);
                }}
                className="bg-baby-blue hover:bg-baby-blue/90 flex items-center gap-2 rounded-lg px-4 py-2 text-white"
              >
                <Plus className="size-5" />
                Nuevo Producto
              </button>
            </div>

            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 size-5 -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                placeholder="Buscar productos..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="focus:ring-baby-blue w-full rounded-lg border py-2 pl-10 pr-4 focus:outline-none focus:ring-2"
              />
            </div>

            {/* Products Table */}
            <div className="overflow-hidden rounded-xl bg-white shadow-lg">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                        Producto
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                        Categoría
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                        Precio
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                        Stock
                      </th>
                      <th className="px-6 py-3 text-center text-xs font-medium uppercase text-gray-500">
                        Destacado
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium uppercase text-gray-500">
                        Acciones
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {filteredProducts.map((product) => (
                      <tr key={product.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-3">
                            <img
                              src={product.imageUrl}
                              alt={product.name}
                              className="size-12 rounded object-cover"
                            />
                            <div>
                              <p className="font-semibold">{product.name}</p>
                              <p className="max-w-xs truncate text-sm text-gray-600">
                                {product.description}
                              </p>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm">{product.category}</td>
                        <td className="px-6 py-4">
                          <p className="font-semibold">${product.price.toLocaleString('es-CO')}</p>
                          {product.discountPrice && (
                            <p className="text-sm text-green-600">
                              ${product.discountPrice.toLocaleString('es-CO')}
                            </p>
                          )}
                        </td>
                        <td className="px-6 py-4">
                          <span
                            className={`rounded-full px-2 py-1 text-xs ${(() => {
                              if (product.stock < 10) return 'bg-red-100 text-red-800';
                              if (product.stock < 20) return 'bg-yellow-100 text-yellow-800';
                              return 'bg-green-100 text-green-800';
                            })()}`}
                          >
                            {product.stock} unidades
                          </span>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <button
                            onClick={() => handleToggleFeatured(product.id)}
                            className={`rounded-lg p-2 transition-colors ${
                              product.featured
                                ? 'bg-yellow-100 text-yellow-600 hover:bg-yellow-200'
                                : 'bg-gray-100 text-gray-400 hover:bg-gray-200'
                            }`}
                            title={product.featured ? 'Quitar de destacados' : 'Destacar producto'}
                          >
                            <Star className={`size-5 ${product.featured ? 'fill-current' : ''}`} />
                          </button>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-2">
                            <button
                              onClick={() => handleEditProduct(product)}
                              className="text-baby-blue hover:text-baby-blue/80"
                              title="Editar producto"
                            >
                              <Edit className="size-5" />
                            </button>
                            <button
                              onClick={() => handleDeleteProduct(product.id)}
                              className="text-red-500 hover:text-red-700"
                              title="Eliminar producto"
                            >
                              <Trash2 className="size-5" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Paginación */}
            {totalPages > 1 && (
              <div className="mt-6 flex items-center justify-center gap-2">
                <button
                  onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
                  disabled={currentPage === 0}
                  className="rounded-lg border px-4 py-2 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Anterior
                </button>

                <div className="flex gap-1">
                  {Array.from({ length: totalPages }, (_, i) => i).map((i) => (
                    <button
                      key={`page-number-${i}`}
                      onClick={() => setCurrentPage(i)}
                      className={`rounded-lg border px-4 py-2 ${
                        currentPage === i ? 'bg-baby-blue text-white' : 'hover:bg-gray-50'
                      }`}
                    >
                      {i + 1}
                    </button>
                  ))}
                </div>

                <button
                  onClick={() => setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1))}
                  disabled={currentPage === totalPages - 1}
                  className="rounded-lg border px-4 py-2 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Siguiente
                </button>

                <span className="ml-4 text-sm text-gray-600">
                  Página {currentPage + 1} de {totalPages} ({totalProducts} productos total)
                </span>
              </div>
            )}
          </div>
        )}

        {/* Orders Tab */}
        {activeTab === 'orders' && (
          <div className="space-y-6">
            {/* Header with Stats */}
            <div className="flex flex-col items-start justify-between gap-4 lg:flex-row lg:items-center">
              <div>
                <h2 className="mb-2 text-3xl font-bold text-gray-800">Gestión de Órdenes</h2>
                <p className="text-gray-600">Administra y da seguimiento a todas las órdenes</p>
              </div>

              {/* Quick Stats */}
              <div className="flex gap-3">
                <div className="rounded-lg border border-yellow-200 bg-gradient-to-br from-yellow-50 to-yellow-100 px-4 py-2">
                  <p className="text-xs font-medium text-yellow-700">Pendientes</p>
                  <p className="text-xl font-bold text-yellow-800">
                    {orders.filter((o) => o.status === 'PENDING').length}
                  </p>
                </div>
                <div className="rounded-lg border border-blue-200 bg-gradient-to-br from-blue-50 to-blue-100 px-4 py-2">
                  <p className="text-xs font-medium text-blue-700">Procesando</p>
                  <p className="text-xl font-bold text-blue-800">
                    {orders.filter((o) => o.status === 'PROCESSING').length}
                  </p>
                </div>
                <div className="rounded-lg border border-green-200 bg-gradient-to-br from-green-50 to-green-100 px-4 py-2">
                  <p className="text-xs font-medium text-green-700">Completadas</p>
                  <p className="text-xl font-bold text-green-800">
                    {orders.filter((o) => o.status === 'DELIVERED').length}
                  </p>
                </div>
              </div>
            </div>

            {/* Filter */}
            <div className="flex justify-end">
              <div className="relative min-w-[200px]">
                <Filter className="absolute left-4 top-1/2 size-5 -translate-y-1/2 text-gray-400" />
                <select
                  value={orderStatusFilter}
                  onChange={(e) => setOrderStatusFilter(e.target.value)}
                  className="focus:ring-baby-blue focus:border-baby-blue w-full appearance-none rounded-xl border-2 bg-white py-3 pl-12 pr-4 font-semibold transition focus:outline-none focus:ring-2"
                >
                  <option value="ALL">Todos los Estados</option>
                  <option value="PENDING">Pendientes</option>
                  <option value="PROCESSING">Procesando</option>
                  <option value="SHIPPED">Enviadas</option>
                  <option value="DELIVERED">Entregadas</option>
                  <option value="CANCELLED">Canceladas</option>
                </select>
              </div>
            </div>

            {/* Orders Grid */}
            <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
              {filteredOrders.map((order) => {
                const statusConfig = {
                  PENDING: {
                    color: 'bg-yellow-100 text-yellow-800 border-yellow-200',
                    icon: Clock,
                    label: 'Pendiente',
                  },
                  PROCESSING: {
                    color: 'bg-blue-100 text-blue-800 border-blue-200',
                    icon: Loader,
                    label: 'Procesando',
                  },
                  SHIPPED: {
                    color: 'bg-indigo-100 text-indigo-800 border-indigo-200',
                    icon: Truck,
                    label: 'Enviada',
                  },
                  DELIVERED: {
                    color: 'bg-green-100 text-green-800 border-green-200',
                    icon: CheckCircle,
                    label: 'Entregada',
                  },
                  CANCELLED: {
                    color: 'bg-red-100 text-red-800 border-red-200',
                    icon: XCircle,
                    label: 'Cancelada',
                  },
                };

                const config = statusConfig[order.status];
                const StatusIcon = config.icon;

                return (
                  <div
                    key={order.id}
                    className="rounded-lg border border-gray-100 bg-white shadow transition-shadow hover:shadow-md"
                  >
                    {/* Card Header - More compact */}
                    <div className="from-baby-blue to-baby-pink flex items-center justify-between bg-gradient-to-r px-4 py-2">
                      <div className="flex items-center gap-2 text-white">
                        <ShoppingBag className="size-4" />
                        <h3 className="font-bold">Orden #{order.id}</h3>
                      </div>
                      <div
                        className={`rounded-full border px-2 py-1 text-xs font-bold ${config.color} flex items-center gap-1`}
                      >
                        <StatusIcon className="size-3" />
                        {config.label}
                      </div>
                    </div>

                    {/* Card Body - More compact */}
                    <div className="space-y-2 p-3">
                      {/* Date and Amount in one row */}
                      <div className="flex items-center justify-between text-sm">
                        <div className="flex items-center gap-2 text-gray-600">
                          <Clock className="size-3" />
                          <span>
                            {new Date(order.createdAt).toLocaleDateString('es-CO', {
                              month: 'short',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                          </span>
                        </div>
                        <div className="text-baby-blue text-lg font-bold">
                          ${(order.totalAmount || order.total || 0).toLocaleString('es-CO')}
                        </div>
                      </div>

                      {/* Order Details - More compact */}
                      {order.shippingAddress && (
                        <div className="flex items-start gap-2 rounded bg-gray-50 p-2 text-xs text-gray-600">
                          <MapPin className="mt-0.5 size-3 shrink-0" />
                          <p className="line-clamp-1">{order.shippingAddress}</p>
                        </div>
                      )}

                      {order.notes && (
                        <div className="rounded bg-blue-50 p-2 text-xs text-gray-600">
                          <p className="line-clamp-1">📝 {order.notes}</p>
                        </div>
                      )}

                      {/* Products Count */}
                      <div className="flex items-center gap-1 text-xs text-gray-600">
                        <Package className="size-3" />
                        <span>{order.items?.length || 0} producto(s)</span>
                      </div>

                      {/* Status Update - Inline */}
                      <div className="border-t pt-2">
                        <div className="relative">
                          <select
                            id={`order-status-${order.id}`}
                            value={order.status}
                            onChange={(e) =>
                              handleUpdateOrderStatus(order.id, e.target.value as Order['status'])
                            }
                            className="focus:ring-baby-blue hover:border-baby-blue w-full appearance-none rounded-lg border bg-white px-3 py-1.5 pr-8 text-sm font-semibold transition focus:outline-none focus:ring-2"
                          >
                            <option value="PENDING">Pendiente</option>
                            <option value="PROCESSING">Procesando</option>
                            <option value="SHIPPED">Enviada</option>
                            <option value="DELIVERED">Entregada</option>
                            <option value="CANCELLED">Cancelada</option>
                          </select>
                          <div className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2">
                            <StatusIcon className="size-4 text-gray-400" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {orders.length === 0 && (
              <div className="rounded-2xl bg-white py-16 text-center shadow-lg">
                <ShoppingBag className="mx-auto mb-4 size-16 text-gray-300" />
                <p className="text-xl font-semibold text-gray-600">No se encontraron órdenes</p>
                <p className="mt-2 text-gray-500">
                  {orderStatusFilter !== 'ALL'
                    ? `No hay órdenes con estado "${orderStatusFilter}"`
                    : 'Aún no hay órdenes registradas'}
                </p>
              </div>
            )}

            {/* Order Pagination */}
            {Math.ceil(ordersTotalElements / ordersPerPage) > 1 && (
              <div className="mt-8 flex flex-col items-center justify-center gap-4 rounded-xl bg-white p-4 shadow-lg sm:flex-row">
                <button
                  onClick={() => setCurrentOrderPage((prev) => Math.max(0, prev - 1))}
                  disabled={currentOrderPage === 0}
                  className="bg-baby-blue hover:bg-baby-blue/90 rounded-lg px-6 py-2 font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-50"
                >
                  ← Anterior
                </button>

                <div className="flex gap-2">
                  {Array.from(
                    { length: Math.ceil(ordersTotalElements / ordersPerPage) },
                    (_, i) => (
                      <button
                        key={`order-page-${i}`}
                        onClick={() => setCurrentOrderPage(i)}
                        className={`size-10 rounded-lg font-bold transition ${
                          currentOrderPage === i
                            ? 'from-baby-blue to-baby-pink scale-110 bg-gradient-to-r text-white shadow-lg'
                            : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                        }`}
                      >
                        {i + 1}
                      </button>
                    )
                  )}
                </div>

                <button
                  onClick={() =>
                    setCurrentOrderPage((prev) =>
                      Math.min(Math.ceil(filteredOrders.length / ordersPerPage) - 1, prev + 1)
                    )
                  }
                  disabled={
                    currentOrderPage === Math.ceil(filteredOrders.length / ordersPerPage) - 1
                  }
                  className="bg-baby-blue hover:bg-baby-blue/90 rounded-lg px-6 py-2 font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-50"
                >
                  Siguiente →
                </button>

                <span className="rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-600">
                  Página {currentOrderPage + 1} de {Math.ceil(ordersTotalElements / ordersPerPage)}{' '}
                  · {ordersTotalElements} órdenes
                </span>
              </div>
            )}
          </div>
        )}

        {/* Product Form Modal */}
        {showProductForm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
            <div className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl bg-white p-6 shadow-2xl">
              <div className="mb-6 flex items-center justify-between">
                <h3 className="text-2xl font-bold">
                  {editingProduct ? 'Editar Producto' : 'Nuevo Producto'}
                </h3>
                <button
                  onClick={() => {
                    setShowProductForm(false);
                    setEditingProduct(null);
                    resetProductForm();
                  }}
                  className="text-gray-500 hover:text-gray-700"
                >
                  <X className="size-6" />
                </button>
              </div>

              <form onSubmit={handleProductSubmit} className="space-y-4">
                <div>
                  <label htmlFor="product-name" className="mb-1 block text-sm font-semibold">
                    Nombre * (mínimo 3 caracteres)
                  </label>
                  <input
                    id="product-name"
                    type="text"
                    value={productForm.name}
                    onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                    className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                    required
                    minLength={3}
                    maxLength={200}
                  />
                </div>

                <div>
                  <label htmlFor="product-description" className="mb-1 block text-sm font-semibold">
                    Descripción * (mínimo 10 caracteres){' '}
                    <span className="text-xs text-gray-500">
                      {productForm.description.length}/2000
                    </span>
                  </label>
                  <textarea
                    id="product-description"
                    value={productForm.description}
                    onChange={(e) =>
                      setProductForm({ ...productForm, description: e.target.value })
                    }
                    rows={3}
                    className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                    required
                    minLength={10}
                    maxLength={2000}
                    placeholder="Escribe una descripción detallada del producto (mínimo 10 caracteres)"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="product-price" className="mb-1 block text-sm font-semibold">
                      Precio * (mayor a 0)
                    </label>
                    <input
                      id="product-price"
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={productForm.price}
                      onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
                      className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                      required
                      placeholder="Ej: 50000"
                    />
                  </div>
                  <div>
                    <label htmlFor="product-discount" className="mb-1 block text-sm font-semibold">
                      Precio Final con Descuento
                    </label>
                    <input
                      id="product-discount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={productForm.discountPrice}
                      onChange={(e) =>
                        setProductForm({ ...productForm, discountPrice: e.target.value })
                      }
                      className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                      placeholder="Opcional (Ej: 40000 si el precio es 50000)"
                    />
                    <p className="mt-1 text-xs text-gray-500">
                      Este es el precio que pagará el cliente después del descuento. Debe ser menor
                      al precio normal. Dejar vacío si no hay descuento.
                    </p>
                  </div>
                </div>

                <div>
                  <label htmlFor="product-stock" className="mb-1 block text-sm font-semibold">
                    Stock * (cantidad disponible)
                  </label>
                  <input
                    id="product-stock"
                    type="number"
                    min="0"
                    value={productForm.stock}
                    onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })}
                    className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                    required
                    placeholder="Ej: 100"
                  />
                </div>

                <div>
                  <label htmlFor="product-category" className="mb-1 block text-sm font-semibold">
                    Categoría *
                  </label>
                  <select
                    id="product-category"
                    value={productForm.category}
                    onChange={(e) => setProductForm({ ...productForm, category: e.target.value })}
                    className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                    required
                  >
                    <option value="HEALTHCARE">Cuidado de Salud</option>
                    <option value="CLOTHING">Ropa</option>
                    <option value="FOOD">Alimentación</option>
                    <option value="TOYS">Juguetes</option>
                    <option value="FURNITURE">Muebles</option>
                    <option value="ACCESSORIES">Accesorios</option>
                    <option value="BOOKS">Libros</option>
                    <option value="OTHER">Otro</option>
                  </select>
                </div>

                <div>
                  <label htmlFor="product-image" className="mb-1 block text-sm font-semibold">
                    URL de Imagen *
                  </label>
                  <input
                    id="product-image"
                    type="url"
                    value={productForm.imageUrl}
                    onChange={(e) => setProductForm({ ...productForm, imageUrl: e.target.value })}
                    className="focus:ring-baby-blue w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2"
                    required
                  />
                </div>

                <div className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    id="featured"
                    checked={productForm.featured}
                    onChange={(e) => setProductForm({ ...productForm, featured: e.target.checked })}
                    className="size-5"
                  />
                  <label htmlFor="featured" className="text-sm font-semibold">
                    Producto Destacado
                  </label>
                </div>

                <div className="flex gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      setShowProductForm(false);
                      setEditingProduct(null);
                      resetProductForm();
                    }}
                    className="flex-1 rounded-lg border border-gray-300 px-4 py-2 hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="bg-baby-blue hover:bg-baby-blue/90 flex flex-1 items-center justify-center gap-2 rounded-lg px-4 py-2 text-white"
                  >
                    <Save className="size-5" />
                    {editingProduct ? 'Actualizar' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Blogs Tab */}
        {activeTab === 'blogs' && (
          <div>
            <BlogsManager />
          </div>
        )}

        {/* Testimonials Tab */}
        {activeTab === 'testimonials' && (
          <div>
            <h2 className="mb-6 text-2xl font-bold">Gestión de Testimonios</h2>
            <TestimonialsManager />
          </div>
        )}

        {/* Messages Tab */}
        {activeTab === 'messages' && (
          <div>
            <h2 className="mb-6 text-2xl font-bold">Gestión de Mensajes</h2>
            <ContactMessagesManager />
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPanel;
