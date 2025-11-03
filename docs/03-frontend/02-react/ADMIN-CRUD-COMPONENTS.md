# ADMIN CRUD COMPONENTS - COMPONENTES DE ADMINISTRACIÓN

## 🎯 Visión General

Los **Admin CRUD Components** gestionan operaciones de administración:
- **AdminProductForm**: Formulario para crear/editar productos
- **AdminProductTable**: Tabla con lista de productos
- **ConfirmDialog**: Modal de confirmación para eliminar
- Uso del hook `useAdminCrud`

---

## 📁 Ubicación

```
frontend/src/components/admin/AdminProductForm.tsx
frontend/src/components/admin/AdminProductTable.tsx
frontend/src/components/common/ConfirmDialog.tsx
```

---

## 📝 AdminProductForm

```tsx
import { useState, useEffect } from 'react';
import { Product } from '../../types/product.types';
import Button from '../common/Button';
import Input from '../common/Input';
import Modal from '../common/Modal';

interface AdminProductFormProps {
  product: Product | null; // null = crear, Product = editar
  onSave: (data: any) => Promise<void>;
  onClose: () => void;
}

export default function AdminProductForm({
  product,
  onSave,
  onClose,
}: AdminProductFormProps) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: 0,
    stock: 0,
    categoryId: '',
    imageUrl: '',
    discount: 0,
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  // ✅ Cargar datos si es edición
  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        description: product.description,
        price: product.price,
        stock: product.stock,
        categoryId: product.category.id.toString(),
        imageUrl: product.imageUrl,
        discount: product.discount || 0,
      });
    }
  }, [product]);
  
  // ✅ Manejar cambios
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    setFormData({
      ...formData,
      [name]: name === 'price' || name === 'stock' || name === 'discount'
        ? Number(value)
        : value,
    });
    
    // Limpiar error del campo
    if (errors[name]) {
      setErrors({ ...errors, [name]: '' });
    }
  };
  
  // ✅ Validar formulario
  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.name || formData.name.length < 3) {
      newErrors.name = 'Nombre debe tener al menos 3 caracteres';
    }
    
    if (!formData.description || formData.description.length < 10) {
      newErrors.description = 'Descripción debe tener al menos 10 caracteres';
    }
    
    if (formData.price <= 0) {
      newErrors.price = 'Precio debe ser mayor a 0';
    }
    
    if (formData.stock < 0) {
      newErrors.stock = 'Stock no puede ser negativo';
    }
    
    if (!formData.categoryId) {
      newErrors.categoryId = 'Selecciona una categoría';
    }
    
    if (!formData.imageUrl) {
      newErrors.imageUrl = 'URL de imagen es requerida';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  // ✅ Manejar submit
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validate()) return;
    
    setLoading(true);
    
    try {
      await onSave(formData);
      onClose();
    } catch (error) {
      alert('Error al guardar producto');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <Modal onClose={onClose} title={product ? 'Editar Producto' : 'Nuevo Producto'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Nombre */}
        <div>
          <label className="block text-sm font-medium mb-2">Nombre</label>
          <Input
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Ej: Biberón Avent"
            required
          />
          {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name}</p>}
        </div>
        
        {/* Descripción */}
        <div>
          <label className="block text-sm font-medium mb-2">Descripción</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Describe el producto..."
            rows={4}
            className="w-full border rounded px-3 py-2"
            required
          />
          {errors.description && <p className="text-red-500 text-sm mt-1">{errors.description}</p>}
        </div>
        
        {/* Precio y Stock */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Precio ($)</label>
            <Input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              min="0"
              required
            />
            {errors.price && <p className="text-red-500 text-sm mt-1">{errors.price}</p>}
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2">Stock</label>
            <Input
              type="number"
              name="stock"
              value={formData.stock}
              onChange={handleChange}
              min="0"
              required
            />
            {errors.stock && <p className="text-red-500 text-sm mt-1">{errors.stock}</p>}
          </div>
        </div>
        
        {/* Categoría */}
        <div>
          <label className="block text-sm font-medium mb-2">Categoría</label>
          <select
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2"
            required
          >
            <option value="">Selecciona una categoría</option>
            <option value="1">Ropa</option>
            <option value="2">Juguetes</option>
            <option value="3">Alimentación</option>
            <option value="4">Higiene</option>
          </select>
          {errors.categoryId && <p className="text-red-500 text-sm mt-1">{errors.categoryId}</p>}
        </div>
        
        {/* URL de Imagen */}
        <div>
          <label className="block text-sm font-medium mb-2">URL de Imagen</label>
          <Input
            name="imageUrl"
            value={formData.imageUrl}
            onChange={handleChange}
            placeholder="https://example.com/image.jpg"
            required
          />
          {errors.imageUrl && <p className="text-red-500 text-sm mt-1">{errors.imageUrl}</p>}
          {formData.imageUrl && (
            <img
              src={formData.imageUrl}
              alt="Preview"
              className="mt-2 w-32 h-32 object-cover rounded"
              onError={(e) => {
                e.currentTarget.src = 'https://via.placeholder.com/150?text=Error';
              }}
            />
          )}
        </div>
        
        {/* Descuento */}
        <div>
          <label className="block text-sm font-medium mb-2">Descuento (%)</label>
          <Input
            type="number"
            name="discount"
            value={formData.discount}
            onChange={handleChange}
            min="0"
            max="100"
          />
        </div>
        
        {/* Botones */}
        <div className="flex gap-2 justify-end">
          <Button type="button" onClick={onClose} variant="secondary">
            Cancelar
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? '⏳ Guardando...' : 'Guardar'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
```

---

## 📊 AdminProductTable

```tsx
import { Product } from '../../types/product.types';
import { formatPrice } from '../../utils/formatters';
import Button from '../common/Button';

interface AdminProductTableProps {
  products: Product[];
  onEdit: (product: Product) => void;
  onDelete: (id: number) => void;
  loading: boolean;
}

export default function AdminProductTable({
  products,
  onEdit,
  onDelete,
  loading,
}: AdminProductTableProps) {
  if (loading) {
    return <div className="text-center py-12">⏳ Cargando productos...</div>;
  }
  
  if (products.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">No hay productos</p>
      </div>
    );
  }
  
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <table className="w-full">
        <thead className="bg-gray-50 border-b">
          <tr>
            <th className="text-left px-6 py-3 font-semibold">Imagen</th>
            <th className="text-left px-6 py-3 font-semibold">Nombre</th>
            <th className="text-left px-6 py-3 font-semibold">Precio</th>
            <th className="text-left px-6 py-3 font-semibold">Stock</th>
            <th className="text-left px-6 py-3 font-semibold">Categoría</th>
            <th className="text-left px-6 py-3 font-semibold">Descuento</th>
            <th className="text-left px-6 py-3 font-semibold">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id} className="border-b hover:bg-gray-50">
              {/* Imagen */}
              <td className="px-6 py-4">
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  className="w-12 h-12 object-cover rounded"
                />
              </td>
              
              {/* Nombre */}
              <td className="px-6 py-4">
                <p className="font-medium">{product.name}</p>
                <p className="text-sm text-gray-500 line-clamp-1">
                  {product.description}
                </p>
              </td>
              
              {/* Precio */}
              <td className="px-6 py-4">{formatPrice(product.price)}</td>
              
              {/* Stock */}
              <td className="px-6 py-4">
                <span
                  className={`px-2 py-1 rounded text-sm ${
                    product.stock === 0
                      ? 'bg-red-100 text-red-800'
                      : product.stock <= 5
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-green-100 text-green-800'
                  }`}
                >
                  {product.stock}
                </span>
              </td>
              
              {/* Categoría */}
              <td className="px-6 py-4">{product.category.name}</td>
              
              {/* Descuento */}
              <td className="px-6 py-4">
                {product.discount ? `${product.discount}%` : '-'}
              </td>
              
              {/* Acciones */}
              <td className="px-6 py-4">
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(product)}
                    className="text-blue-500 hover:text-blue-700"
                    title="Editar"
                  >
                    ✏️
                  </button>
                  <button
                    onClick={() => onDelete(product.id)}
                    className="text-red-500 hover:text-red-700"
                    title="Eliminar"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

---

## ⚠️ ConfirmDialog

```tsx
// components/common/ConfirmDialog.tsx
import Modal from './Modal';
import Button from './Button';

interface ConfirmDialogProps {
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  confirmText?: string;
  cancelText?: string;
}

export default function ConfirmDialog({
  title,
  message,
  onConfirm,
  onCancel,
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
}: ConfirmDialogProps) {
  return (
    <Modal onClose={onCancel} title={title}>
      <p className="text-gray-700 mb-6">{message}</p>
      
      <div className="flex gap-2 justify-end">
        <Button onClick={onCancel} variant="secondary">
          {cancelText}
        </Button>
        <Button onClick={onConfirm} variant="danger">
          {confirmText}
        </Button>
      </div>
    </Modal>
  );
}
```

---

## 🔄 Uso Completo en AdminProducts

```tsx
import { useState } from 'react';
import { useAdminCrud } from '../../hooks/useAdminCrud';
import { Product } from '../../types/product.types';
import AdminProductTable from './AdminProductTable';
import AdminProductForm from './AdminProductForm';
import ConfirmDialog from '../common/ConfirmDialog';
import Button from '../common/Button';

export default function AdminProducts() {
  const { items: products, loading, create, update, remove } = useAdminCrud<Product>('/products');
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [productToDelete, setProductToDelete] = useState<number | null>(null);
  
  // ✅ Abrir formulario para crear
  const handleCreate = () => {
    setSelectedProduct(null);
    setShowForm(true);
  };
  
  // ✅ Abrir formulario para editar
  const handleEdit = (product: Product) => {
    setSelectedProduct(product);
    setShowForm(true);
  };
  
  // ✅ Confirmar eliminación
  const handleDeleteClick = (id: number) => {
    setProductToDelete(id);
  };
  
  // ✅ Eliminar producto
  const handleDeleteConfirm = async () => {
    if (productToDelete) {
      await remove(productToDelete);
      setProductToDelete(null);
    }
  };
  
  // ✅ Guardar producto (crear o editar)
  const handleSave = async (data: any) => {
    if (selectedProduct) {
      await update(selectedProduct.id, data);
    } else {
      await create(data);
    }
    setShowForm(false);
  };
  
  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold">Productos</h1>
        <Button onClick={handleCreate}>+ Nuevo Producto</Button>
      </div>
      
      <AdminProductTable
        products={products}
        onEdit={handleEdit}
        onDelete={handleDeleteClick}
        loading={loading}
      />
      
      {/* Formulario modal */}
      {showForm && (
        <AdminProductForm
          product={selectedProduct}
          onSave={handleSave}
          onClose={() => setShowForm(false)}
        />
      )}
      
      {/* Diálogo de confirmación */}
      {productToDelete && (
        <ConfirmDialog
          title="Eliminar Producto"
          message="¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer."
          onConfirm={handleDeleteConfirm}
          onCancel={() => setProductToDelete(null)}
          confirmText="Eliminar"
          cancelText="Cancelar"
        />
      )}
    </div>
  );
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo funciona useAdminCrud?"**

> "`useAdminCrud` es un custom hook genérico para CRUD:
> ```tsx
> const { items, loading, create, update, remove } = useAdminCrud<Product>('/products');
> ```
> Internamente:
> - Hace GET al montar para cargar items
> - `create`: POST al endpoint
> - `update`: PUT al endpoint/:id
> - `remove`: DELETE al endpoint/:id
> - Refresca lista después de cada operación
> 
> Es reutilizable para cualquier entidad (productos, órdenes, usuarios)."

---

**2. "¿Por qué usar modal de confirmación?"**

> "Para prevenir eliminaciones accidentales:
> - Usuario hace clic en 🗑️
> - En lugar de eliminar inmediatamente, abre modal
> - Modal pregunta: '¿Estás seguro?'
> - Usuario puede cancelar o confirmar
> - Solo si confirma, se ejecuta `remove()`
> 
> Mejora UX (evita errores) y es best practice."

---

**3. "¿Cómo manejas crear vs editar con un solo formulario?"**

> "Form recibe `product` prop:
> - Si `product === null`, es **crear** (campos vacíos)
> - Si `product` tiene datos, es **editar** (campos pre-llenados)
> 
> ```tsx
> useEffect(() => {
>   if (product) {
>     setFormData({ ...product });
>   }
> }, [product]);
> ```
> 
> En `onSave`:
> - Si `product` existe, llama `update(product.id, data)`
> - Si no existe, llama `create(data)`
> 
> Reutilizar componente evita duplicación."

---

**4. "¿Qué validaciones tiene el formulario?"**

> "Función `validate()` verifica:
> - **Nombre**: Mínimo 3 caracteres
> - **Descripción**: Mínimo 10 caracteres
> - **Precio**: Mayor a 0
> - **Stock**: No negativo
> - **Categoría**: Seleccionada
> - **Imagen**: URL presente
> 
> Si falla alguna, muestra error y previene submit. Esto garantiza datos válidos antes de enviar a backend."

---

## 📝 Checklist de Admin CRUD Components

```
✅ AdminProductForm (crear/editar)
✅ AdminProductTable (lista con acciones)
✅ ConfirmDialog (confirmación eliminación)
✅ useAdminCrud hook genérico
✅ Validaciones en formulario
✅ Preview de imagen
✅ Loading states
✅ Color-coded stock (rojo/amarillo/verde)
✅ Reutilización de form (crear/editar)
✅ Modal overlay
```

---

## 🚀 Conclusión

**Admin CRUD Components:**
- ✅ Sistema completo de administración
- ✅ Reutilizable con useAdminCrud
- ✅ UX segura (confirmación eliminación)
- ✅ Validaciones robustas

**Son componentes críticos para gestionar contenido.**

---

## 🎉 Frontend COMPLETAMENTE Terminado

**Documentación Frontend Completa:**
- ✅ 6 archivos fundamentales (arquitectura, estructura, componentes, hooks, API, patrones)
- ✅ 4 páginas principales (Home, Productos, Carrito, Admin)
- ✅ 4 componentes clave (Navbar, ProductCard, Auth, AdminCrud)

**Total Frontend: 14 archivos de documentación** 📚

---

**Ahora continúa con:** Base de Datos 🗄️
