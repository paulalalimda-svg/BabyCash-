# ADMIN PAGE - PANEL DE ADMINISTRACIÓN

## 🎯 Visión General

La **Admin Page** es el panel de administración con:
- Dashboard con estadísticas
- CRUD de productos
- Gestión de órdenes
- Gestión de usuarios
- Solo accesible para rol ADMIN

---

## 📁 Ubicación

```
frontend/src/pages/Admin.tsx
```

---

## 🔒 Protección de Ruta

```tsx
// App.tsx
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/auth/ProtectedRoute';
import AdminRoute from './components/auth/AdminRoute';
import Admin from './pages/Admin';

function App() {
  return (
    <Routes>
      {/* Rutas públicas */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      
      {/* Rutas protegidas */}
      <Route path="/mi-cuenta" element={
        <ProtectedRoute>
          <MiCuenta />
        </ProtectedRoute>
      } />
      
      {/* Rutas de admin */}
      <Route path="/admin/*" element={
        <AdminRoute>
          <Admin />
        </AdminRoute>
      } />
    </Routes>
  );
}
```

---

## 🛡️ Componente: AdminRoute

```tsx
// components/auth/AdminRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../common/Loader';

export default function AdminRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  
  // ✅ Mostrar loader mientras carga
  if (loading) {
    return <Loader />;
  }
  
  // ✅ Si no está autenticado, redirigir a login
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  // ✅ Si no es admin, redirigir a home
  if (user.role !== 'ADMIN') {
    alert('No tienes permisos para acceder a esta página');
    return <Navigate to="/" replace />;
  }
  
  // ✅ Si es admin, mostrar contenido
  return <>{children}</>;
}
```

---

## 🏗️ Estructura de Admin Page

```tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from '../components/layout/AdminLayout';
import AdminDashboard from '../components/admin/AdminDashboard';
import AdminProducts from '../components/admin/AdminProducts';
import AdminOrders from '../components/admin/AdminOrders';
import AdminUsers from '../components/admin/AdminUsers';

export default function Admin() {
  return (
    <AdminLayout>
      <Routes>
        <Route path="/" element={<Navigate to="/admin/dashboard" replace />} />
        <Route path="/dashboard" element={<AdminDashboard />} />
        <Route path="/products" element={<AdminProducts />} />
        <Route path="/orders" element={<AdminOrders />} />
        <Route path="/users" element={<AdminUsers />} />
      </Routes>
    </AdminLayout>
  );
}
```

---

## 🎨 Componente: AdminLayout

```tsx
// components/layout/AdminLayout.tsx
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const location = useLocation();
  const { user, logout } = useAuth();
  
  const isActive = (path: string) => location.pathname.startsWith(path);
  
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-lg">
        <div className="p-6">
          <h2 className="text-2xl font-bold text-baby-pink">Baby Cash Admin</h2>
          <p className="text-sm text-gray-600">{user?.name}</p>
        </div>
        
        <nav className="mt-6">
          <Link
            to="/admin/dashboard"
            className={`block px-6 py-3 ${
              isActive('/admin/dashboard')
                ? 'bg-baby-pink text-white'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            📊 Dashboard
          </Link>
          
          <Link
            to="/admin/products"
            className={`block px-6 py-3 ${
              isActive('/admin/products')
                ? 'bg-baby-pink text-white'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            🛍️ Productos
          </Link>
          
          <Link
            to="/admin/orders"
            className={`block px-6 py-3 ${
              isActive('/admin/orders')
                ? 'bg-baby-pink text-white'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            📦 Órdenes
          </Link>
          
          <Link
            to="/admin/users"
            className={`block px-6 py-3 ${
              isActive('/admin/users')
                ? 'bg-baby-pink text-white'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            👥 Usuarios
          </Link>
        </nav>
        
        <div className="absolute bottom-0 w-64 p-6">
          <Link to="/" className="block px-4 py-2 bg-gray-200 rounded mb-2 text-center">
            🏠 Volver a la Tienda
          </Link>
          <button
            onClick={logout}
            className="w-full px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
          >
            Cerrar Sesión
          </button>
        </div>
      </aside>
      
      {/* Main Content */}
      <main className="flex-grow overflow-y-auto p-8">
        {children}
      </main>
    </div>
  );
}
```

---

## 📊 Componente: AdminDashboard

```tsx
// components/admin/AdminDashboard.tsx
import { useEffect, useState } from 'react';
import { statsAPI } from '../../services/statsAPI';

export default function AdminDashboard() {
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalOrders: 0,
    totalUsers: 0,
    totalRevenue: 0,
    recentOrders: [],
  });
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    statsAPI.getAdminStats()
      .then(response => setStats(response.data))
      .finally(() => setLoading(false));
  }, []);
  
  if (loading) return <Loader />;
  
  return (
    <div>
      <h1 className="text-4xl font-bold mb-8">Dashboard</h1>
      
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <StatCard
          icon="🛍️"
          title="Productos"
          value={stats.totalProducts}
          color="bg-blue-500"
        />
        <StatCard
          icon="📦"
          title="Órdenes"
          value={stats.totalOrders}
          color="bg-green-500"
        />
        <StatCard
          icon="👥"
          title="Usuarios"
          value={stats.totalUsers}
          color="bg-purple-500"
        />
        <StatCard
          icon="💰"
          title="Ingresos"
          value={formatPrice(stats.totalRevenue)}
          color="bg-yellow-500"
        />
      </div>
      
      {/* Recent Orders */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-semibold mb-4">Órdenes Recientes</h2>
        <table className="w-full">
          <thead>
            <tr className="border-b">
              <th className="text-left py-2">ID</th>
              <th className="text-left py-2">Cliente</th>
              <th className="text-left py-2">Total</th>
              <th className="text-left py-2">Estado</th>
              <th className="text-left py-2">Fecha</th>
            </tr>
          </thead>
          <tbody>
            {stats.recentOrders.map((order: any) => (
              <tr key={order.id} className="border-b">
                <td className="py-2">#{order.id}</td>
                <td className="py-2">{order.user.name}</td>
                <td className="py-2">{formatPrice(order.total)}</td>
                <td className="py-2">
                  <span className={`px-2 py-1 rounded text-sm ${
                    order.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                    order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-red-100 text-red-800'
                  }`}>
                    {order.status}
                  </span>
                </td>
                <td className="py-2">{formatDate(order.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function StatCard({ icon, title, value, color }: any) {
  return (
    <div className={`${color} text-white rounded-lg p-6`}>
      <div className="text-4xl mb-2">{icon}</div>
      <p className="text-sm opacity-80">{title}</p>
      <p className="text-3xl font-bold">{value}</p>
    </div>
  );
}
```

---

## 🛍️ Componente: AdminProducts (CRUD)

```tsx
// components/admin/AdminProducts.tsx
import { useState } from 'react';
import { useAdminCrud } from '../../hooks/useAdminCrud';
import { Product } from '../../types/product.types';
import ProductFormModal from './ProductFormModal';
import Button from '../common/Button';

export default function AdminProducts() {
  const { items: products, loading, create, update, remove } = useAdminCrud<Product>('/products');
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [showModal, setShowModal] = useState(false);
  
  const handleCreate = () => {
    setSelectedProduct(null);
    setShowModal(true);
  };
  
  const handleEdit = (product: Product) => {
    setSelectedProduct(product);
    setShowModal(true);
  };
  
  const handleDelete = async (id: number) => {
    if (confirm('¿Seguro que deseas eliminar este producto?')) {
      await remove(id);
    }
  };
  
  const handleSave = async (data: any) => {
    if (selectedProduct) {
      await update(selectedProduct.id, data);
    } else {
      await create(data);
    }
    setShowModal(false);
  };
  
  if (loading) return <Loader />;
  
  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold">Productos</h1>
        <Button onClick={handleCreate}>+ Nuevo Producto</Button>
      </div>
      
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="text-left px-6 py-3">Imagen</th>
              <th className="text-left px-6 py-3">Nombre</th>
              <th className="text-left px-6 py-3">Precio</th>
              <th className="text-left px-6 py-3">Stock</th>
              <th className="text-left px-6 py-3">Categoría</th>
              <th className="text-left px-6 py-3">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="border-b">
                <td className="px-6 py-4">
                  <img src={product.imageUrl} alt={product.name} className="w-12 h-12 object-cover rounded" />
                </td>
                <td className="px-6 py-4">{product.name}</td>
                <td className="px-6 py-4">{formatPrice(product.price)}</td>
                <td className="px-6 py-4">{product.stock}</td>
                <td className="px-6 py-4">{product.category.name}</td>
                <td className="px-6 py-4">
                  <button
                    onClick={() => handleEdit(product)}
                    className="text-blue-500 hover:underline mr-2"
                  >
                    ✏️ Editar
                  </button>
                  <button
                    onClick={() => handleDelete(product.id)}
                    className="text-red-500 hover:underline"
                  >
                    🗑️ Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      {/* Modal de formulario */}
      {showModal && (
        <ProductFormModal
          product={selectedProduct}
          onSave={handleSave}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo proteges las rutas de admin?"**

> "Uso el componente `AdminRoute` que verifica:
> 1. **Autenticación**: Si no hay user, redirige a /login
> 2. **Rol**: Si user.role !== 'ADMIN', redirige a home y muestra alerta
> 3. **Loading**: Muestra loader mientras verifica
> 
> ```tsx
> <Route path='/admin/*' element={
>   <AdminRoute>
>     <Admin />
>   </AdminRoute>
> } />
> ```
> Es un HOC (Higher-Order Component) que wrappea contenido protegido."

---

**2. "¿Qué es useAdminCrud?"**

> "`useAdminCrud` es un custom hook genérico para CRUD:
> ```tsx
> const { items, loading, create, update, remove } = useAdminCrud<Product>('/products');
> ```
> Internamente:
> - Hace fetch al montar con `useEffect`
> - Provee funciones CRUD (create POST, update PUT, remove DELETE)
> - Maneja loading y errores
> - Refresca lista después de cada operación
> 
> Es reutilizable para productos, órdenes, usuarios, etc."

---

**3. "¿Cómo funciona el dashboard?"**

> "El dashboard llama `statsAPI.getAdminStats()` que retorna:
> ```json
> {
>   'totalProducts': 45,
>   'totalOrders': 123,
>   'totalUsers': 67,
>   'totalRevenue': 5430000,
>   'recentOrders': [...]
> }
> ```
> Backend hace queries SQL para contar registros y sumar totales. Frontend muestra en cards coloridas y tabla de órdenes recientes."

---

**4. "¿Por qué AdminLayout separado?"**

> "AdminLayout tiene sidebar con navegación específica de admin:
> - Dashboard
> - Productos
> - Órdenes
> - Usuarios
> 
> Es diferente de MainLayout (Navbar + Footer). Separar layouts permite diseños distintos para áreas diferentes de la app."

---

## 📝 Checklist de Admin Page

```
✅ Protección con AdminRoute (autenticación + rol)
✅ AdminLayout con sidebar
✅ Dashboard con estadísticas
✅ CRUD de productos (tabla + modal)
✅ Gestión de órdenes
✅ Gestión de usuarios
✅ useAdminCrud hook genérico
✅ Confirmación antes de eliminar
✅ Loading states
✅ Responsive design
```

---

## 🚀 Conclusión

**Admin Page:**
- ✅ Panel completo de administración
- ✅ Protección robusta (auth + role)
- ✅ CRUD genérico reutilizable
- ✅ Dashboard con métricas clave

**Es la herramienta crítica para gestionar la tienda.**

---

## 🎉 Frontend Completo

**Páginas documentadas:**
- ✅ HOME-PAGE.md (bienvenida, destacados)
- ✅ PRODUCTOS-PAGE.md (catálogo, filtros)
- ✅ CARRITO-PAGE.md (checkout)
- ✅ ADMIN-PAGE.md (administración)

**Ahora continúa con:** Base de Datos 🗄️
