// Lazy loading de páginas

import { motion } from 'framer-motion';
import React, { Suspense } from 'react';
import { Route, Routes } from 'react-router-dom';
import Footer from '../components/layout/Footer';
import Navbar from '../components/layout/Navbar';
import ScrollToTop from '../components/ScrollToTop';
import Preloader from '../components/ui/Preloader';

// Lazy loading de páginas
const Home = React.lazy(() => import('../pages/Home'));
const Nosotros = React.lazy(() => import('../pages/Nosotros'));
const Productos = React.lazy(() => import('../pages/Productos'));
const Carrito = React.lazy(() => import('../pages/Carrito'));
const Perfil = React.lazy(() => import('../pages/Perfil'));
const Contacto = React.lazy(() => import('../pages/Contacto'));
const Blog = React.lazy(() => import('../pages/Blog'));
const BlogPostDetail = React.lazy(() => import('../pages/BlogPostDetail'));
const CrearBlog = React.lazy(() => import('../pages/CrearBlog'));
const Testimonios = React.lazy(() => import('../pages/Testimonios'));
const NotFound = React.lazy(() => import('../pages/NotFound'));
const Terminos = React.lazy(() => import('../pages/Terminos'));
const TratamientoDatos = React.lazy(() => import('../pages/TratamientoDatos'));

// Checkout pages
const Checkout = React.lazy(() => import('../pages/Checkout'));
const OrderConfirmation = React.lazy(() => import('../pages/OrderConfirmation'));

// Admin page
const AdminPanel = React.lazy(() => import('../pages/AdminPanel'));

// Auth pages
const Login = React.lazy(() => import('../pages/Login'));
const Register = React.lazy(() => import('../pages/Register'));
const ForgotPassword = React.lazy(() => import('../pages/ForgotPassword'));
const ResetPassword = React.lazy(() => import('../pages/ResetPassword'));

const AppRouter: React.FC = () => {
  return (
    <div className="bg-baby-light flex min-h-screen flex-col">
      {/* Skip link for keyboard navigation - WCAG 2.4.1 */}
      <a
        href="#main-content"
        className="focus:bg-baby-blue sr-only shadow-lg focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-50 focus:rounded-lg focus:px-4 focus:py-2 focus:text-white focus:ring-2 focus:ring-white"
      >
        Saltar al contenido principal
      </a>

      <Navbar />
      <ScrollToTop />

      <motion.main
        id="main-content"
        className="flex-1 "
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.6 }}
      >
        <Suspense fallback={<Preloader />}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/nosotros" element={<Nosotros />} />
            <Route path="/productos" element={<Productos />} />
            <Route path="/carrito" element={<Carrito />} />
            <Route path="/perfil" element={<Perfil />} />
            <Route path="/contacto" element={<Contacto />} />
            <Route path="/blog" element={<Blog />} />
            <Route path="/blog/:slug" element={<BlogPostDetail />} />
            <Route path="/crear-blog" element={<CrearBlog />} />
            <Route path="/testimonios" element={<Testimonios />} />
            {/* Rutas de autenticación */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
            {/* Checkout */}
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/order-confirmation/:orderId" element={<OrderConfirmation />} />
            {/* Legales */}
            <Route path="/terminos" element={<Terminos />} />
            <Route path="/tratamiento-datos" element={<TratamientoDatos />} />
            {/* Panel de administrador */}
            <Route path="/admin" element={<AdminPanel />} />
            {/* NotFound */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </motion.main>

      <Footer />
    </div>
  );
};

export default AppRouter;
