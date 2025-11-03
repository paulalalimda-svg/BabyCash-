# HOME PAGE - PÁGINA PRINCIPAL

## 🎯 Visión General

La **Home Page** es la página principal de Baby Cash donde los usuarios ven:
- Hero section con mensaje de bienvenida
- Productos destacados
- Categorías principales
- Testimonios
- Call-to-action

---

## 📁 Ubicación

```
frontend/src/pages/Home.tsx
```

---

## 🏗️ Estructura del Componente

```tsx
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { productsAPI } from '../services/productsAPI';
import { Product } from '../types/product.types';
import ProductCard from '../components/products/ProductCard';
import Loader from '../components/common/Loader';
import MainLayout from '../components/layout/MainLayout';

export default function Home() {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  
  // ✅ Cargar productos destacados al montar
  useEffect(() => {
    productsAPI.getAll()
      .then(response => {
        // Tomar solo primeros 4 productos
        setFeaturedProducts(response.data.slice(0, 4));
      })
      .finally(() => setLoading(false));
  }, []);
  
  return (
    <MainLayout>
      {/* Hero Section */}
      <HeroSection />
      
      {/* Featured Products */}
      <section className="py-12">
        <h2 className="text-3xl font-bold text-center mb-8">
          Productos Destacados
        </h2>
        {loading ? (
          <Loader />
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {featuredProducts.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
        <div className="text-center mt-8">
          <Link
            to="/productos"
            className="btn btn-primary"
          >
            Ver Todos los Productos
          </Link>
        </div>
      </section>
      
      {/* Categories */}
      <CategoriesSection />
      
      {/* Testimonials */}
      <TestimonialsSection />
      
      {/* CTA */}
      <CallToAction />
    </MainLayout>
  );
}
```

---

## 🎨 Secciones Principales

### 1️⃣ Hero Section

```tsx
function HeroSection() {
  return (
    <section className="bg-gradient-to-r from-baby-pink to-baby-blue py-20">
      <div className="container mx-auto px-4 text-center">
        <h1 className="text-5xl font-baby font-bold text-white mb-4">
          Bienvenido a Baby Cash 👶
        </h1>
        <p className="text-xl text-white mb-8">
          Todo lo que tu bebé necesita en un solo lugar
        </p>
        <Link
          to="/productos"
          className="bg-white text-baby-pink px-8 py-3 rounded-full font-semibold hover:shadow-lg transition"
        >
          Explorar Productos
        </Link>
      </div>
    </section>
  );
}
```

**Características:**
- ✅ Gradiente con colores baby
- ✅ Título grande con emoji
- ✅ Descripción corta
- ✅ Botón CTA a productos

---

### 2️⃣ Featured Products Section

```tsx
function FeaturedProductsSection() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    productsAPI.getAll()
      .then(response => setProducts(response.data.slice(0, 4)))
      .finally(() => setLoading(false));
  }, []);
  
  return (
    <section className="py-12 bg-gray-50">
      <div className="container mx-auto px-4">
        <h2 className="text-3xl font-bold text-center mb-2">
          Productos Destacados
        </h2>
        <p className="text-gray-600 text-center mb-8">
          Los productos más populares de nuestra tienda
        </p>
        
        {loading ? (
          <Loader />
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {products.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
        
        <div className="text-center mt-8">
          <Link
            to="/productos"
            className="text-baby-pink font-semibold hover:underline"
          >
            Ver todos los productos →
          </Link>
        </div>
      </div>
    </section>
  );
}
```

**Características:**
- ✅ Muestra 4 productos destacados
- ✅ Grid responsive
- ✅ Loading state
- ✅ Link a catálogo completo

---

### 3️⃣ Categories Section

```tsx
function CategoriesSection() {
  const categories = [
    {
      id: 1,
      name: 'Ropa',
      icon: '👕',
      description: 'Ropa cómoda y adorable',
      slug: 'ropa',
    },
    {
      id: 2,
      name: 'Juguetes',
      icon: '🧸',
      description: 'Juguetes seguros y educativos',
      slug: 'juguetes',
    },
    {
      id: 3,
      name: 'Alimentación',
      icon: '🍼',
      description: 'Biberones y accesorios',
      slug: 'alimentacion',
    },
    {
      id: 4,
      name: 'Higiene',
      icon: '🛁',
      description: 'Productos de cuidado',
      slug: 'higiene',
    },
  ];
  
  return (
    <section className="py-12">
      <div className="container mx-auto px-4">
        <h2 className="text-3xl font-bold text-center mb-8">
          Categorías
        </h2>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {categories.map(category => (
            <Link
              key={category.id}
              to={`/productos?category=${category.slug}`}
              className="bg-white rounded-lg shadow-md p-6 text-center hover:shadow-xl transition"
            >
              <div className="text-6xl mb-4">{category.icon}</div>
              <h3 className="text-xl font-semibold mb-2">{category.name}</h3>
              <p className="text-gray-600 text-sm">{category.description}</p>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}
```

**Características:**
- ✅ 4 categorías principales con iconos
- ✅ Grid responsive
- ✅ Links a productos filtrados
- ✅ Hover effects

---

### 4️⃣ Testimonials Section

```tsx
function TestimonialsSection() {
  const testimonials = [
    {
      id: 1,
      name: 'María González',
      rating: 5,
      comment: '¡Excelente servicio! Los productos llegaron en perfecto estado.',
      avatar: '👩',
    },
    {
      id: 2,
      name: 'Carlos Rodríguez',
      rating: 5,
      comment: 'Gran variedad de productos para bebés. Muy recomendado.',
      avatar: '👨',
    },
    {
      id: 3,
      name: 'Ana Martínez',
      rating: 5,
      comment: 'Precios justos y entrega rápida. Volveré a comprar.',
      avatar: '👩',
    },
  ];
  
  return (
    <section className="py-12 bg-baby-pink bg-opacity-10">
      <div className="container mx-auto px-4">
        <h2 className="text-3xl font-bold text-center mb-8">
          Lo Que Dicen Nuestros Clientes
        </h2>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {testimonials.map(testimonial => (
            <div
              key={testimonial.id}
              className="bg-white rounded-lg shadow-md p-6"
            >
              <div className="flex items-center mb-4">
                <div className="text-4xl mr-3">{testimonial.avatar}</div>
                <div>
                  <h4 className="font-semibold">{testimonial.name}</h4>
                  <div className="text-yellow-400">
                    {'⭐'.repeat(testimonial.rating)}
                  </div>
                </div>
              </div>
              <p className="text-gray-600 italic">"{testimonial.comment}"</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
```

**Características:**
- ✅ 3 testimonios de clientes
- ✅ Rating con estrellas
- ✅ Avatares con emojis
- ✅ Grid responsive

---

### 5️⃣ Call to Action

```tsx
function CallToAction() {
  return (
    <section className="py-16 bg-gradient-to-r from-baby-blue to-baby-purple">
      <div className="container mx-auto px-4 text-center">
        <h2 className="text-4xl font-bold text-white mb-4">
          ¿Listo para Comprar?
        </h2>
        <p className="text-xl text-white mb-8">
          Descubre los mejores productos para tu bebé
        </p>
        <Link
          to="/productos"
          className="bg-white text-baby-blue px-8 py-3 rounded-full font-semibold hover:shadow-lg transition inline-block"
        >
          Ver Catálogo Completo
        </Link>
      </div>
    </section>
  );
}
```

**Características:**
- ✅ Gradiente llamativo
- ✅ Mensaje claro
- ✅ Botón CTA grande
- ✅ Link a productos

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué hace la Home Page?"**

> "La Home Page es la página principal que da la bienvenida a usuarios. Muestra:
> - Hero section con mensaje y CTA
> - 4 productos destacados
> - 4 categorías principales
> - 3 testimonios de clientes
> - Call-to-action final
> 
> El objetivo es captar atención y guiar al usuario hacia productos."

---

**2. "¿Cómo cargas los productos destacados?"**

> "Uso `useEffect` con `[]` para cargar productos al montar el componente:
> ```tsx
> useEffect(() => {
>   productsAPI.getAll()
>     .then(response => setFeaturedProducts(response.data.slice(0, 4)));
> }, []);
> ```
> Tomo solo los primeros 4 con `.slice(0, 4)` para no sobrecargar la página."

---

**3. "¿Por qué usar MainLayout?"**

> "`MainLayout` es un componente wrapper que agrega Navbar y Footer automáticamente:
> ```tsx
> <MainLayout>
>   {/* Contenido de la página */}
> </MainLayout>
> ```
> Esto evita repetir Navbar y Footer en cada página. Es el patrón Layout Component."

---

## 📝 Checklist de Home Page

```
✅ Hero section atractivo
✅ Productos destacados (4)
✅ Categorías con iconos (4)
✅ Testimonios (3)
✅ Call-to-action final
✅ Loading states
✅ Responsive design
✅ Links a otras páginas
✅ MainLayout wrapper
```

---

## 🚀 Conclusión

**Home Page:**
- ✅ Primera impresión del sitio
- ✅ Muestra valor rápidamente
- ✅ Guía al usuario con CTAs
- ✅ Responsive y atractiva

**Es la página más importante para conversión.**

---

**Ahora lee:** `PRODUCTOS-PAGE.md` para catálogo. 🚀
