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
  const [ordersTotalPages, setOrdersTotalPages] = useState(0);
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
      const err = error as { response?: { data?: { message?: string }; status?: number } };
      logger.error('❌ Error completo:', error);
      logger.error('❌ Error response:', err.response);
      logger.error('❌ Error response data:', err.response?.data);
      logger.error('❌ Error status:', err.response?.status);
      logger.error('❌ Error message:', err.response?.data?.message);
      logger.error('❌ Error details:', JSON.stringify(err.response?.data, null, 2));

      // Mostrar errores de validación específicos
      if (error.response?.data?.errors) {
        const errors = error.response.data.errors;
        const errorMessages = Object.values(errors).join(', ');
        toast.error(`Errores de validación: ${errorMessages}`);
      } else {
        toast.error(error.response?.data?.message || 'Error al guardar producto');
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
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-baby-blue"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-baby-cream py-8 pt-24">
      <div className="container mx-auto px-4">
        <h1 className="text-4xl font-bold text-baby-blue mb-8">Panel de Administración</h1>

        {/* Tabs */}
        <div className="flex gap-4 mb-8 border-b">
          <button
            onClick={() => {
              setActiveTab('dashboard');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'dashboard' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
          >
            <Home size={20} />
            Dashboard
          </button>
          <button
            onClick={() => {
              setActiveTab('products');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'products' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
          >
            <Box size={20} />
            Productos
          </button>
          <button
            onClick={() => {
              setActiveTab('orders');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'orders' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
          >
            <ShoppingCart size={20} />
            Órdenes
          </button>
          <button
            onClick={() => {
              setActiveTab('blogs');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'blogs' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
          >
            <BookOpen size={20} />
            Blogs
          </button>
          <button
            onClick={() => {
              setActiveTab('testimonials');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'testimonials' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
          >
            <Star size={20} />
            Testimonios
          </button>
          <button
            onClick={() => {
              setActiveTab('messages');
              setSearchTerm('');
            }}
            className={`px-6 py-3 font-semibold transition-colors flex items-center gap-2 ${activeTab === 'messages' ? 'text-baby-blue border-b-2 border-baby-blue' : 'text-gray-600 hover:text-baby-blue'}`}
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
              <div className="bg-red-50 border-l-4 border-red-500 rounded-xl p-6 shadow-lg">
                <div className="flex items-center gap-3 mb-4">
                  <AlertTriangle className="w-6 h-6 text-red-500" />
                  <h3 className="text-lg font-bold text-red-700">
                    ⚠️ Alerta: Stock Bajo - Acción Requerida
                  </h3>
                </div>
                <p className="text-sm text-red-600 mb-4">
                  Los siguientes productos necesitan reabastecimiento urgente:
                </p>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                  {lowStockProducts.map((product) => (
                    <div
                      key={product.id}
                      className="bg-white border border-red-200 rounded-lg p-3 hover:shadow-md transition-shadow"
                    >
                      <p className="font-semibold text-sm">{product.name}</p>
                      <p className="text-red-600 text-xs font-bold">
                        ⚠️ Stock: {product.stock} unidades
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <div className="bg-gradient-to-br from-baby-blue to-baby-purple text-white rounded-xl shadow-lg p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white/80 text-sm">Total Ventas</p>
                    <p className="text-2xl font-bold">${totalSales.toLocaleString('es-CO')}</p>
                  </div>
                  <DollarSign className="w-10 h-10 text-white/80" />
                </div>
              </div>

              <div className="bg-gradient-to-br from-baby-pink to-baby-mint text-white rounded-xl shadow-lg p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white/80 text-sm">Total Productos</p>
                    <p className="text-2xl font-bold">{totalProducts}</p>
                  </div>
                  <Package className="w-10 h-10 text-white/80" />
                </div>
              </div>

              <div className="bg-gradient-to-br from-baby-purple to-baby-blue text-white rounded-xl shadow-lg p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white/80 text-sm">Órdenes</p>
                    <p className="text-2xl font-bold">{orders.length}</p>
                  </div>
                  <ShoppingCart className="w-10 h-10 text-white/80" />
                </div>
              </div>

              <div className="bg-gradient-to-br from-baby-mint to-baby-pink text-white rounded-xl shadow-lg p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-white/80 text-sm">Pendientes</p>
                    <p className="text-2xl font-bold">{pendingOrders}</p>
                  </div>
                  <AlertTriangle className="w-10 h-10 text-white/80" />
                </div>
              </div>
            </div>

            {/* Charts Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Sales by Status */}
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h3 className="text-lg font-bold mb-4">Órdenes por Estado</h3>
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
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h3 className="text-lg font-bold mb-4">Top 5 Productos con Más Stock</h3>
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
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h3 className="text-lg font-bold mb-4">Productos por Categoría</h3>
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
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h3 className="text-lg font-bold mb-4">Órdenes Recientes (Últimas 7)</h3>
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
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Gestión de Productos</h2>
              <button
                onClick={() => {
                  resetProductForm();
                  setEditingProduct(null);
                  setShowProductForm(true);
                }}
                className="bg-baby-blue text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-baby-blue/90"
              >
                <Plus className="w-5 h-5" />
                Nuevo Producto
              </button>
            </div>

            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Buscar productos..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
              />
            </div>

            {/* Products Table */}
            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Producto
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Categoría
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Precio
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Stock
                      </th>
                      <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">
                        Destacado
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
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
                              className="w-12 h-12 object-cover rounded"
                            />
                            <div>
                              <p className="font-semibold">{product.name}</p>
                              <p className="text-sm text-gray-600 truncate max-w-xs">
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
                            className={`px-2 py-1 rounded-full text-xs ${(() => {
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
                            className={`p-2 rounded-lg transition-colors ${
                              product.featured
                                ? 'bg-yellow-100 text-yellow-600 hover:bg-yellow-200'
                                : 'bg-gray-100 text-gray-400 hover:bg-gray-200'
                            }`}
                            title={product.featured ? 'Quitar de destacados' : 'Destacar producto'}
                          >
                            <Star className={`w-5 h-5 ${product.featured ? 'fill-current' : ''}`} />
                          </button>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-2">
                            <button
                              onClick={() => handleEditProduct(product)}
                              className="text-baby-blue hover:text-baby-blue/80"
                              title="Editar producto"
                            >
                              <Edit className="w-5 h-5" />
                            </button>
                            <button
                              onClick={() => handleDeleteProduct(product.id)}
                              className="text-red-500 hover:text-red-700"
                              title="Eliminar producto"
                            >
                              <Trash2 className="w-5 h-5" />
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
              <div className="flex justify-center items-center gap-2 mt-6">
                <button
                  onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
                  disabled={currentPage === 0}
                  className="px-4 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Anterior
                </button>

                <div className="flex gap-1">
                  {Array.from({ length: totalPages }, (_, i) => i).map((i) => (
                    <button
                      key={`page-number-${i}`}
                      onClick={() => setCurrentPage(i)}
                      className={`px-4 py-2 border rounded-lg ${
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
                  className="px-4 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Siguiente
                </button>

                <span className="text-sm text-gray-600 ml-4">
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
            <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
              <div>
                <h2 className="text-3xl font-bold text-gray-800 mb-2">Gestión de Órdenes</h2>
                <p className="text-gray-600">Administra y da seguimiento a todas las órdenes</p>
              </div>

              {/* Quick Stats */}
              <div className="flex gap-3">
                <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 px-4 py-2 rounded-lg border border-yellow-200">
                  <p className="text-xs text-yellow-700 font-medium">Pendientes</p>
                  <p className="text-xl font-bold text-yellow-800">
                    {orders.filter((o) => o.status === 'PENDING').length}
                  </p>
                </div>
                <div className="bg-gradient-to-br from-blue-50 to-blue-100 px-4 py-2 rounded-lg border border-blue-200">
                  <p className="text-xs text-blue-700 font-medium">Procesando</p>
                  <p className="text-xl font-bold text-blue-800">
                    {orders.filter((o) => o.status === 'PROCESSING').length}
                  </p>
                </div>
                <div className="bg-gradient-to-br from-green-50 to-green-100 px-4 py-2 rounded-lg border border-green-200">
                  <p className="text-xs text-green-700 font-medium">Completadas</p>
                  <p className="text-xl font-bold text-green-800">
                    {orders.filter((o) => o.status === 'DELIVERED').length}
                  </p>
                </div>
              </div>
            </div>

            {/* Filter */}
            <div className="flex justify-end">
              <div className="relative min-w-[200px]">
                <Filter className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <select
                  value={orderStatusFilter}
                  onChange={(e) => setOrderStatusFilter(e.target.value)}
                  className="w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:outline-none focus:ring-2 focus:ring-baby-blue focus:border-baby-blue transition appearance-none bg-white font-semibold"
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
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
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
                    className="bg-white rounded-lg shadow hover:shadow-md transition-shadow border border-gray-100"
                  >
                    {/* Card Header - More compact */}
                    <div className="bg-gradient-to-r from-baby-blue to-baby-pink px-4 py-2 flex justify-between items-center">
                      <div className="flex items-center gap-2 text-white">
                        <ShoppingBag className="w-4 h-4" />
                        <h3 className="font-bold">Orden #{order.id}</h3>
                      </div>
                      <div
                        className={`px-2 py-1 rounded-full text-xs font-bold border ${config.color} flex items-center gap-1`}
                      >
                        <StatusIcon className="w-3 h-3" />
                        {config.label}
                      </div>
                    </div>

                    {/* Card Body - More compact */}
                    <div className="p-3 space-y-2">
                      {/* Date and Amount in one row */}
                      <div className="flex justify-between items-center text-sm">
                        <div className="flex items-center gap-2 text-gray-600">
                          <Clock className="w-3 h-3" />
                          <span>
                            {new Date(order.createdAt).toLocaleDateString('es-CO', {
                              month: 'short',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                          </span>
                        </div>
                        <div className="font-bold text-lg text-baby-blue">
                          ${(order.totalAmount || order.total || 0).toLocaleString('es-CO')}
                        </div>
                      </div>

                      {/* Order Details - More compact */}
                      {order.shippingAddress && (
                        <div className="flex items-start gap-2 text-xs text-gray-600 bg-gray-50 rounded p-2">
                          <MapPin className="w-3 h-3 mt-0.5 flex-shrink-0" />
                          <p className="line-clamp-1">{order.shippingAddress}</p>
                        </div>
                      )}

                      {order.notes && (
                        <div className="text-xs text-gray-600 bg-blue-50 rounded p-2">
                          <p className="line-clamp-1">📝 {order.notes}</p>
                        </div>
                      )}

                      {/* Products Count */}
                      <div className="flex items-center gap-1 text-xs text-gray-600">
                        <Package className="w-3 h-3" />
                        <span>{order.items?.length || 0} producto(s)</span>
                      </div>

                      {/* Status Update - Inline */}
                      <div className="pt-2 border-t">
                        <div className="relative">
                          <select
                            id={`order-status-${order.id}`}
                            value={order.status}
                            onChange={(e) =>
                              handleUpdateOrderStatus(order.id, e.target.value as Order['status'])
                            }
                            className="w-full px-3 py-1.5 text-sm border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue font-semibold transition hover:border-baby-blue appearance-none bg-white pr-8"
                          >
                            <option value="PENDING">Pendiente</option>
                            <option value="PROCESSING">Procesando</option>
                            <option value="SHIPPED">Enviada</option>
                            <option value="DELIVERED">Entregada</option>
                            <option value="CANCELLED">Cancelada</option>
                          </select>
                          <div className="absolute right-2 top-1/2 transform -translate-y-1/2 pointer-events-none">
                            <StatusIcon className="w-4 h-4 text-gray-400" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {orders.length === 0 && (
              <div className="text-center py-16 bg-white rounded-2xl shadow-lg">
                <ShoppingBag className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p className="text-xl font-semibold text-gray-600">No se encontraron órdenes</p>
                <p className="text-gray-500 mt-2">
                  {orderStatusFilter !== 'ALL'
                    ? `No hay órdenes con estado "${orderStatusFilter}"`
                    : 'Aún no hay órdenes registradas'}
                </p>
              </div>
            )}

            {/* Order Pagination */}
            {Math.ceil(ordersTotalElements / ordersPerPage) > 1 && (
              <div className="flex flex-col sm:flex-row justify-center items-center gap-4 mt-8 bg-white rounded-xl p-4 shadow-lg">
                <button
                  onClick={() => setCurrentOrderPage((prev) => Math.max(0, prev - 1))}
                  disabled={currentOrderPage === 0}
                  className="px-6 py-2 bg-baby-blue text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-baby-blue/90 transition font-semibold"
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
                        className={`w-10 h-10 rounded-lg font-bold transition ${
                          currentOrderPage === i
                            ? 'bg-gradient-to-r from-baby-blue to-baby-pink text-white shadow-lg scale-110'
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
                  className="px-6 py-2 bg-baby-blue text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-baby-blue/90 transition font-semibold"
                >
                  Siguiente →
                </button>

                <span className="text-sm text-gray-600 font-medium bg-gray-100 px-4 py-2 rounded-lg">
                  Página {currentOrderPage + 1} de {Math.ceil(ordersTotalElements / ordersPerPage)}{' '}
                  · {ordersTotalElements} órdenes
                </span>
              </div>
            )}
          </div>
        )}

        {/* Product Form Modal */}
        {showProductForm && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6">
              <div className="flex justify-between items-center mb-6">
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
                  <X className="w-6 h-6" />
                </button>
              </div>

              <form onSubmit={handleProductSubmit} className="space-y-4">
                <div>
                  <label htmlFor="product-name" className="block text-sm font-semibold mb-1">
                    Nombre * (mínimo 3 caracteres)
                  </label>
                  <input
                    id="product-name"
                    type="text"
                    value={productForm.name}
                    onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                    className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                    required
                    minLength={3}
                    maxLength={200}
                  />
                </div>

                <div>
                  <label htmlFor="product-description" className="block text-sm font-semibold mb-1">
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
                    className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                    required
                    minLength={10}
                    maxLength={2000}
                    placeholder="Escribe una descripción detallada del producto (mínimo 10 caracteres)"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="product-price" className="block text-sm font-semibold mb-1">
                      Precio * (mayor a 0)
                    </label>
                    <input
                      id="product-price"
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={productForm.price}
                      onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                      required
                      placeholder="Ej: 50000"
                    />
                  </div>
                  <div>
                    <label htmlFor="product-discount" className="block text-sm font-semibold mb-1">
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
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                      placeholder="Opcional (Ej: 40000 si el precio es 50000)"
                    />
                    <p className="text-xs text-gray-500 mt-1">
                      Este es el precio que pagará el cliente después del descuento. Debe ser menor
                      al precio normal. Dejar vacío si no hay descuento.
                    </p>
                  </div>
                </div>

                <div>
                  <label htmlFor="product-stock" className="block text-sm font-semibold mb-1">
                    Stock * (cantidad disponible)
                  </label>
                  <input
                    id="product-stock"
                    type="number"
                    min="0"
                    value={productForm.stock}
                    onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })}
                    className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                    required
                    placeholder="Ej: 100"
                  />
                </div>

                <div>
                  <label htmlFor="product-category" className="block text-sm font-semibold mb-1">
                    Categoría *
                  </label>
                  <select
                    id="product-category"
                    value={productForm.category}
                    onChange={(e) => setProductForm({ ...productForm, category: e.target.value })}
                    className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
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
                  <label htmlFor="product-image" className="block text-sm font-semibold mb-1">
                    URL de Imagen *
                  </label>
                  <input
                    id="product-image"
                    type="url"
                    value={productForm.imageUrl}
                    onChange={(e) => setProductForm({ ...productForm, imageUrl: e.target.value })}
                    className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                    required
                  />
                </div>

                <div className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    id="featured"
                    checked={productForm.featured}
                    onChange={(e) => setProductForm({ ...productForm, featured: e.target.checked })}
                    className="w-5 h-5"
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
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="flex-1 px-4 py-2 bg-baby-blue text-white rounded-lg hover:bg-baby-blue/90 flex items-center justify-center gap-2"
                  >
                    <Save className="w-5 h-5" />
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
            <h2 className="text-2xl font-bold mb-6">Gestión de Testimonios</h2>
            <TestimonialsManager />
          </div>
        )}

        {/* Messages Tab */}
        {activeTab === 'messages' && (
          <div>
            <h2 className="text-2xl font-bold mb-6">Gestión de Mensajes</h2>
            <ContactMessagesManager />
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPanel;
