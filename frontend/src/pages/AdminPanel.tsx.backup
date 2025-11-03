
import { useState } from 'react';
// @ts-ignore
import { motion } from 'framer-motion';
// @ts-ignore
import {
  Package,
  Users,
  ShoppingCart,
  TrendingUp,
  AlertTriangle,
  Plus,
  Edit,
  Trash2,
  Search,
  Filter,
  Download,
  Eye,
  Truck,
  Bell,
  BarChart3,
  DollarSign
} 
from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Input from '../components/ui/input';
import { PRODUCTS } from '../data/products';

// Datos mock para el panel de administrador
const mockProducts = PRODUCTS.map((product, index) => ({
  id: `${index + 1}`,
  nombre: product.TITULO,
  descripcion: product.DESCRIPCION,
  precio: product.PRECIO,
  stock: Math.floor(Math.random() * 100) + 10,
  categoria: product.CATEGORIA,
  imagen: product.FOTO,
  destacado: Math.random() > 0.7,
}));

const mockVentas = [
  { id: '1', cliente: 'María García', productos: 3, total: 156000, fecha: '2025-01-15', estado: 'Completada' },
  { id: '2', cliente: 'Juan López', productos: 2, total: 89000, fecha: '2025-01-14', estado: 'Enviada' },
  { id: '3', cliente: 'Ana Rodríguez', productos: 5, total: 230000, fecha: '2025-01-13', estado: 'Pendiente' },
  { id: '4', cliente: 'Carlos Pérez', productos: 1, total: 45000, fecha: '2025-01-12', estado: 'Completada' },
];

const mockUsuarios = [
  { id: '1', nombre: 'María García', email: 'maria@email.com', fechaRegistro: '2024-12-01', compras: 15 },
  { id: '2', nombre: 'Juan López', email: 'juan@email.com', fechaRegistro: '2024-11-15', compras: 8 },
  { id: '3', nombre: 'Ana Rodríguez', email: 'ana@email.com', fechaRegistro: '2024-10-20', compras: 22 },
  { id: '4', nombre: 'Carlos Pérez', email: 'carlos@email.com', fechaRegistro: '2025-01-05', compras: 3 },
];

const mockEnvios = [
  { id: '1', venta: '#001', cliente: 'María García', direccion: 'Calle 123 #45-67', estado: 'En tránsito', fecha: '2025-01-15' },
  { id: '2', venta: '#002', cliente: 'Juan López', direccion: 'Carrera 45 #12-34', estado: 'Entregado', fecha: '2025-01-14' },
  { id: '3', venta: '#003', cliente: 'Ana Rodríguez', direccion: 'Avenida 80 #23-45', estado: 'Preparando', fecha: '2025-01-13' },
];

// Datos para gráficas
const ventasPorDia = [
  { dia: 'Lun', ventas: 12, color: '#A7D8FF' },
  { dia: 'Mar', ventas: 19, color: '#FFC1E3' },
  { dia: 'Mié', ventas: 8, color: '#C8F7DC' },
  { dia: 'Jue', ventas: 15, color: '#BB86FC' },
  { dia: 'Vie', ventas: 25, color: '#F9FAFB' },
  { dia: 'Sáb', ventas: 18, color: '#ffc658' },
  { dia: 'Dom', ventas: 10, color: '#ff7c7c' },
];

const productosMasVendidos = [
  { nombre: 'Pañal Winny Et.2', ventas: 45, color: '#A7D8FF' },
  { nombre: 'Toallitas Húmedas', ventas: 38, color: '#FFC1E3' },
  { nombre: 'Crema Antipañalitis', ventas: 32, color: '#C8F7DC' },
  { nombre: 'Shampoo Bebé', ventas: 28, color: '#BB86FC' },
  { nombre: 'Jabón Líquido', ventas: 25, color: '#F9FAFB' },
  { nombre: 'Biberón Avent', ventas: 22, color: '#82ca9d' },
  { nombre: 'Body Algodón', ventas: 19, color: '#ffc658' },
  { nombre: 'Juguete Sonajero', ventas: 15, color: '#ff7c7c' },
];

const stockPorCategoria = [
  { name: 'Higiene', value: 45, color: '#A7D8FF' },
  { name: 'Alimentación', value: 32, color: '#FFC1E3' },
  { name: 'Ropa', value: 23, color: '#C8F7DC' },
  { name: 'Juguetes', value: 18, color: '#BB86FC' },
];

const AdminPanel: React.FC = () => {
  const [activeSection, setActiveSection] = useState('dashboard');
  const [searchTerm, setSearchTerm] = useState('');
  const [showProductForm, setShowProductForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState<any>(null);
  const [productForm, setProductForm] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    stock: '',
    categoria: '',
    imagen: '',
  });

  // Filtros
  const filteredProducts = mockProducts.filter(product =>
    product.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.categoria.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredVentas = mockVentas.filter(venta =>
    venta.cliente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    venta.estado.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredUsuarios = mockUsuarios.filter(usuario =>
    usuario.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    usuario.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredEnvios = mockEnvios.filter(envio =>
    envio.cliente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    envio.estado.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Alertas de stock bajo
  const productosStockBajo = mockProducts.filter(product => product.stock < 20);

  const handleProductSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Aquí iría la lógica para agregar/editar producto
    console.log('Producto:', productForm);
    setShowProductForm(false);
    setEditingProduct(null);
    setProductForm({
      nombre: '',
      descripcion: '',
      precio: '',
      stock: '',
      categoria: '',
      imagen: '',
    });
  };

  const handleEditProduct = (product: any) => {
    setEditingProduct(product);
    setProductForm({
      nombre: product.nombre,
      descripcion: product.descripcion,
      precio: product.precio.toString(),
      stock: product.stock.toString(),
      categoria: product.categoria,
      imagen: product.imagen,
    });
    setShowProductForm(true);
  };


// StatCard fuera del componente principal

type StatCardProps = {
  title: string;
  value: string | number;
  icon: React.ElementType;
  color: string;
  change?: string;
};
function StatCard({ title, value, icon: Icon, color, change }: StatCardProps) {
  return (
    <Card className={`bg-gradient-to-br ${color} text-white rounded-2xl shadow-lg border-0`} hover={false}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-white/80 text-sm">{title}</p>
          <p className="text-2xl font-bold">{value}</p>
          {change && (
            <p className="text-white/90 text-xs flex items-center gap-1 mt-1">
              <TrendingUp className="w-3 h-3" />
              {change}
            </p>
          )}
        </div>
        <Icon className="w-8 h-8 text-white/80" />
      </div>
    </Card>
  );
}

  const renderDashboard = () => (
    <div className="space-y-6">
      {/* Tarjetas de resumen */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Ventas"
          value="$1,245,678"
          icon={DollarSign}
          color="from-baby-blue to-baby-purple"
          change="+12% este mes"
        />
        <StatCard
          title="Productos"
          value={mockProducts.length}
          icon={Package}
          color="from-baby-pink to-baby-mint"
          change="+3 nuevos"
        />
        <StatCard
          title="Usuarios"
          value={mockUsuarios.length}
          icon={Users}
          color="from-baby-purple to-baby-blue"
          change="+8 este mes"
        />
        <StatCard
          title="Pedidos Hoy"
          value="23"
          icon={ShoppingCart}
          color="from-baby-pink to-baby-mint"
          change="+5 vs ayer"
        />
      </div>

      {/* Alertas de stock bajo */}
      {productosStockBajo.length > 0 && (
  <Card className="border-l-4 border-red-400 bg-red-50/60 rounded-2xl">
          <div className="flex items-center gap-3 mb-4">
            <AlertTriangle className="w-5 h-5 text-red-500" />
            <h3 className="text-lg font-semibold text-red-700">Alertas de Stock Bajo</h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            {productosStockBajo.map(product => (
              <div key={product.id} className="bg-red-100/80 p-3 rounded-xl border border-red-200">
                <p className="font-medium text-sm">{product.nombre}</p>
                <p className="text-red-600 text-xs">Stock: {product.stock} unidades</p>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Gráficas */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
  <Card className="rounded-2xl shadow-md border-0" hover={false}>
    <h3 className="text-lg font-semibold mb-4">Ventas por Día</h3>
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={ventasPorDia}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="dia" />
        <YAxis />
        <Tooltip />
        <Bar dataKey="ventas">
          {ventasPorDia.map((entry) => (
            <Cell key={`bar-dia-${entry.dia}`} fill={entry.color} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  </Card>

  <Card className="rounded-2xl shadow-md border-0" hover={false}>
    <h3 className="text-lg font-semibold mb-4">Stock por Categoría</h3>
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={stockPorCategoria}
          cx="50%"
          cy="50%"
          outerRadius={90}
          innerRadius={50}
          dataKey="value"
          label={({ name, percent }) => {
            const pct = typeof percent === 'number' ? percent : 0;
            return `${name} ${(pct * 100).toFixed(0)}%`;
          }}
        >
          {stockPorCategoria.map((entry) => (
            <Cell key={entry.name} fill={entry.color} />
          ))}
        </Pie>
        <Tooltip />
      </PieChart>
    </ResponsiveContainer>
  </Card>
      </div>

      {/* Productos más vendidos */}
  <Card className="rounded-2xl shadow-md border-0" hover={false}>
    <h3 className="text-lg font-semibold mb-4">Productos Más Vendidos</h3>
    <ResponsiveContainer width="100%" height={320}>
      <BarChart
        data={productosMasVendidos}
        layout="vertical"
        margin={{ top: 10, right: 30, left: 10, bottom: 10 }}
        barCategoryGap={16}
      >
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis type="number" hide axisLine={false} tick={{ fontSize: 12 }} />
        <YAxis dataKey="nombre" type="category" width={140} tick={{ fontSize: 13, fill: '#1F2937', fontWeight: 500 }} />
        <Tooltip
          cursor={{ fill: '#A7D8FF22' }}
          contentStyle={{ borderRadius: 12, background: '#fff', border: '1px solid #A7D8FF' }}
        />
        <Bar dataKey="ventas" radius={[8, 8, 8, 8]}>
          {productosMasVendidos.map((entry) => (
            <Cell key={`cell-${entry.nombre}`} fill={entry.color} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  </Card>
    </div>
  );

  const renderProductos = () => (
  <div className="space-y-6">
  <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        <h2 className="text-2xl font-bold">Gestión de Productos</h2>
  <Button onClick={() => setShowProductForm(true)} className="flex items-center gap-2 bg-baby-blue hover:bg-baby-pink text-white rounded-xl shadow-md">
          <Plus className="w-4 h-4" />
          Agregar Producto
        </Button>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full max-w-xs">
          <Input
            placeholder="Buscar productos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 rounded-xl border-2 border-baby-blue focus:ring-baby-pink"
          />
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-baby-blue w-5 h-5" />
        </div>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" onClick={() => alert('Funcionalidad de filtros próximamente')}> 
          <Filter className="w-4 h-4" />
          Filtros
        </Button>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" onClick={() => alert('Exportar productos próximamente')}> 
          <Download className="w-4 h-4" />
          Exportar
        </Button>
      </div>

  <Card className="rounded-2xl shadow-md border-0 mt-4">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left p-4">Producto</th>
                <th className="text-left p-4">Categoría</th>
                <th className="text-left p-4">Precio</th>
                <th className="text-left p-4">Stock</th>
                <th className="text-left p-4">Estado</th>
                <th className="text-left p-4">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredProducts.map(product => (
                <tr key={product.id} className="border-b hover:bg-gray-50">
                  <td className="p-4">
                    <div className="flex items-center gap-3">
                      <img src={product.imagen} alt={product.nombre} className="w-12 h-12 object-cover rounded" />
                      <div>
                        <p className="font-medium">{product.nombre}</p>
                        <p className="text-sm text-gray-500 truncate max-w-40">{product.descripcion}</p>
                      </div>
                    </div>
                  </td>
                  <td className="p-4">
                    <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">
                      {product.categoria}
                    </span>
                  </td>
                  <td className="p-4 font-medium">${product.precio.toLocaleString()}</td>
                  <td className="p-4">
                    <span className={`px-2 py-1 rounded-full text-xs ${
                      product.stock < 20 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
                    }`}>
                      {product.stock}
                    </span>
                  </td>
                  <td className="p-4">
                    <span className={`px-2 py-1 rounded-full text-xs ${
                      product.destacado ? 'bg-yellow-100 text-yellow-800' : 'bg-gray-100 text-gray-800'
                    }`}>
                      {product.destacado ? 'Destacado' : 'Normal'}
                    </span>
                  </td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <Button size="sm" variant="outline" onClick={() => handleEditProduct(product)}>
                        <Edit className="w-3 h-3" />
                      </Button>
                      <Button size="sm" variant="danger">
                        <Trash2 className="w-3 h-3" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Modal de formulario de producto */}
      {showProductForm && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center p-4 z-50">
          <div className="w-full max-w-xl bg-white rounded-3xl shadow-2xl p-8 relative border-2 border-baby-blue">
            <h3 className="text-2xl font-bold mb-4 text-baby-blue">
              {editingProduct ? 'Editar Producto' : 'Agregar Producto'}
            </h3>
            <form onSubmit={handleProductSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label htmlFor="nombre" className="block text-sm font-medium mb-1">Nombre</label>
                  <Input
                    id="nombre"
                    value={productForm.nombre}
                    onChange={(e) => setProductForm({...productForm, nombre: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label htmlFor="categoria" className="block text-sm font-medium mb-1">Categoría</label>
                  <select
                    id="categoria"
                    value={productForm.categoria}
                    onChange={(e) => setProductForm({...productForm, categoria: e.target.value})}
                    className="w-full p-2 border rounded-xl border-baby-blue"
                    required
                  >
                    <option value="">Seleccionar categoría</option>
                    <option value="Higiene">Higiene</option>
                    <option value="Alimentación">Alimentación</option>
                    <option value="Ropa">Ropa</option>
                    <option value="Juguetes">Juguetes</option>
                  </select>
                </div>
                <div>
                  <label htmlFor="precio" className="block text-sm font-medium mb-1">Precio</label>
                  <Input
                    id="precio"
                    type="number"
                    value={productForm.precio}
                    onChange={(e) => setProductForm({...productForm, precio: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label htmlFor="stock" className="block text-sm font-medium mb-1">Stock</label>
                  <Input
                    id="stock"
                    type="number"
                    value={productForm.stock}
                    onChange={(e) => setProductForm({...productForm, stock: e.target.value})}
                    required
                  />
                </div>
              </div>
              <div>
                <label htmlFor="descripcion" className="block text-sm font-medium mb-1">Descripción</label>
                <textarea
                  id="descripcion"
                  value={productForm.descripcion}
                  onChange={(e) => setProductForm({...productForm, descripcion: e.target.value})}
                  className="w-full p-2 border rounded-xl border-baby-blue h-24"
                  required
                />
              </div>
              <div>
                <label htmlFor="imagen" className="block text-sm font-medium mb-1">URL de Imagen</label>
                <Input
                  id="imagen"
                  value={productForm.imagen}
                  onChange={(e) => setProductForm({...productForm, imagen: e.target.value})}
                  placeholder="https://ejemplo.com/imagen.jpg"
                />
              </div>
              <div className="flex gap-3 pt-4">
                <Button type="submit" className="flex-1 bg-baby-blue hover:bg-baby-pink text-white rounded-xl">
                  {editingProduct ? 'Actualizar' : 'Agregar'} Producto
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  className="rounded-xl border-baby-blue text-baby-blue"
                  onClick={() => {
                    setShowProductForm(false);
                    setEditingProduct(null);
                    setProductForm({
                      nombre: '',
                      descripcion: '',
                      precio: '',
                      stock: '',
                      categoria: '',
                      imagen: '',
                    });
                  }}
                >
                  Cancelar
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );

  const renderVentas = () => (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        <h2 className="text-2xl font-bold">Gestión de Ventas</h2>
        <Button variant="outline" className="flex items-center gap-2">
          <Download className="w-4 h-4" />
          Exportar Ventas
        </Button>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full max-w-xs">
          <Input
            placeholder="Buscar ventas..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 rounded-xl border-2 border-baby-blue focus:ring-baby-pink"
          />
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-baby-blue w-5 h-5" />
        </div>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Funcionalidad de filtros próximamente')}> 
          <Filter className="w-4 h-4" />
          Filtros
        </Button>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Exportar ventas próximamente')}> 
          <Download className="w-4 h-4" />
          Exportar
        </Button>
      </div>

      <Card>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left p-4">ID</th>
                <th className="text-left p-4">Cliente</th>
                <th className="text-left p-4">Productos</th>
                <th className="text-left p-4">Total</th>
                <th className="text-left p-4">Fecha</th>
                <th className="text-left p-4">Estado</th>
                <th className="text-left p-4">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredVentas.map(venta => (
                <tr key={venta.id} className="border-b hover:bg-gray-50">
                  <td className="p-4 font-medium">#{venta.id}</td>
                  <td className="p-4">{venta.cliente}</td>
                  <td className="p-4">{venta.productos} items</td>
                  <td className="p-4 font-medium">${venta.total.toLocaleString()}</td>
                  <td className="p-4">{venta.fecha}</td>
                  <td className="p-4">
                    {(() => {
                      let estadoClass = 'bg-yellow-100 text-yellow-800';
                      if (venta.estado === 'Completada') estadoClass = 'bg-green-100 text-green-800';
                      else if (venta.estado === 'Enviada') estadoClass = 'bg-blue-100 text-blue-800';
                      return (
                        <span className={`px-2 py-1 rounded-full text-xs ${estadoClass}`}>
                          {venta.estado}
                        </span>
                      );
                    })()}
                  </td>
                  <td className="p-4">
                    <Button size="sm" variant="outline">
                      <Eye className="w-3 h-3" />
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );

  const renderUsuarios = () => (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        <h2 className="text-2xl font-bold">Gestión de Usuarios</h2>
        <Button variant="outline" className="flex items-center gap-2">
          <Download className="w-4 h-4" />
          Exportar Usuarios
        </Button>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full max-w-xs">
          <Input
            placeholder="Buscar usuarios..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 rounded-xl border-2 border-baby-blue focus:ring-baby-pink"
          />
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-baby-blue w-5 h-5" />
        </div>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Funcionalidad de filtros próximamente')}> 
          <Filter className="w-4 h-4" />
          Filtros
        </Button>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Exportar usuarios próximamente')}> 
          <Download className="w-4 h-4" />
          Exportar
        </Button>
      </div>

      <Card>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left p-4">Usuario</th>
                <th className="text-left p-4">Email</th>
                <th className="text-left p-4">Fecha Registro</th>
                <th className="text-left p-4">Compras</th>
                <th className="text-left p-4">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsuarios.map(usuario => (
                <tr key={usuario.id} className="border-b hover:bg-gray-50">
                  <td className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-gradient-to-br from-blue-400 to-purple-600 rounded-full flex items-center justify-center text-white text-sm font-medium">
                        {usuario.nombre.charAt(0)}
                      </div>
                      <span className="font-medium">{usuario.nombre}</span>
                    </div>
                  </td>
                  <td className="p-4">{usuario.email}</td>
                  <td className="p-4">{usuario.fechaRegistro}</td>
                  <td className="p-4">
                    <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">
                      {usuario.compras} compras
                    </span>
                  </td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <Button size="sm" variant="outline">
                        <Eye className="w-3 h-3" />
                      </Button>
                      <Button size="sm" variant="danger">
                        <Trash2 className="w-3 h-3" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );

  const renderEnvios = () => (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        <h2 className="text-2xl font-bold">Gestión de Envíos</h2>
        <Button variant="outline" className="flex items-center gap-2">
          <Download className="w-4 h-4" />
          Exportar Envíos
        </Button>
      </div>

      <div className="flex flex-col sm:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full max-w-xs">
          <Input
            placeholder="Buscar envíos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 rounded-xl border-2 border-baby-blue focus:ring-baby-pink"
          />
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-baby-blue w-5 h-5" />
        </div>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Funcionalidad de filtros próximamente')}> 
          <Filter className="w-4 h-4" />
          Filtros
        </Button>
        <Button variant="outline" className="flex items-center gap-2 rounded-xl border-baby-blue text-baby-blue" style={{ transition: 'none' }} onClick={() => alert('Exportar envíos próximamente')}> 
          <Download className="w-4 h-4" />
          Exportar
        </Button>
      </div>

      <Card>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left p-4">Venta</th>
                <th className="text-left p-4">Cliente</th>
                <th className="text-left p-4">Dirección</th>
                <th className="text-left p-4">Estado</th>
                <th className="text-left p-4">Fecha</th>
                <th className="text-left p-4">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredEnvios.map(envio => (
                <tr key={envio.id} className="border-b hover:bg-gray-50">
                  <td className="p-4 font-medium">{envio.venta}</td>
                  <td className="p-4">{envio.cliente}</td>
                  <td className="p-4">{envio.direccion}</td>
                  <td className="p-4">
                    {(() => {
                      let envioClass = 'bg-yellow-100 text-yellow-800';
                      if (envio.estado === 'Entregado') envioClass = 'bg-green-100 text-green-800';
                      else if (envio.estado === 'En tránsito') envioClass = 'bg-blue-100 text-blue-800';
                      return (
                        <span className={`px-2 py-1 rounded-full text-xs ${envioClass}`}>
                          {envio.estado}
                        </span>
                      );
                    })()}
                  </td>
                  <td className="p-4">{envio.fecha}</td>
                  <td className="p-4">
                    <div className="flex gap-2">
                      <Button size="sm" variant="outline">
                        <Eye className="w-3 h-3" />
                      </Button>
                      <Button size="sm" variant="outline">
                        <Edit className="w-3 h-3" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );

  const renderContent = () => {
    switch (activeSection) {
      case 'dashboard':
        return renderDashboard();
      case 'productos':
        return renderProductos();
      case 'ventas':
        return renderVentas();
      case 'usuarios':
        return renderUsuarios();
      case 'envios':
        return renderEnvios();
      default:
        return renderDashboard();
    }
  };

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: BarChart3 },
    { id: 'productos', label: 'Productos', icon: Package },
    { id: 'ventas', label: 'Ventas', icon: ShoppingCart },
    { id: 'usuarios', label: 'Usuarios', icon: Users },
    { id: 'envios', label: 'Envíos', icon: Truck },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <h1 className="text-xl font-bold text-gray-900">Panel de Administrador</h1>
            <div className="flex items-center gap-4">
              <Button variant="outline" size="sm" className="relative">
                <Bell className="w-4 h-4" />
                {productosStockBajo.length > 0 && (
                  <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                    {productosStockBajo.length}
                  </span>
                )}
              </Button>
              <div className="w-8 h-8 bg-gradient-to-br from-blue-400 to-purple-600 rounded-full flex items-center justify-center text-white text-sm font-medium">
                A
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar */}
          <div className="lg:w-64">
            <Card className="p-4">
              <nav className="space-y-2">
                {menuItems.map(item => {
                  const Icon = item.icon;
                  return (
                    <button
                      key={item.id}
                      onClick={() => {
                        setActiveSection(item.id);
                        setSearchTerm('');
                      }}
                      className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors ${
                        activeSection === item.id
                          ? 'bg-blue-50 text-blue-700 border border-blue-200'
                          : 'text-gray-600 hover:bg-gray-50'
                      }`}
                    >
                      <Icon className="w-4 h-4" />
                      {item.label}
                    </button>
                  );
                })}
              </nav>
            </Card>
          </div>

          {/* Content */}
          <div className="flex-1">
            <motion.div
              key={activeSection}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
            >
              {renderContent()}
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;
