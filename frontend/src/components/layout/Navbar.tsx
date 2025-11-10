import { AnimatePresence, motion } from 'framer-motion';
import { Menu, ShoppingCart, User, X } from 'lucide-react';
import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useCart } from '../../contexts/CartContext';
import Button from '../ui/Button';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const location = useLocation();
  const { getTotalItems } = useCart();
  const { user, isAuthenticated } = useAuth();
  const totalItems = getTotalItems();

  const navLinks = [
    { href: '/', label: 'Inicio' },
    { href: '/nosotros', label: 'Nosotros' },
    { href: '/productos', label: 'Productos' },
    { href: '/blog', label: 'Blog' },
    { href: '/contacto', label: 'Contacto' },
    ...(user?.role === 'ADMIN' || user?.role === 'MODERATOR'
      ? [{ href: '/admin', label: 'Admin' }]
      : []),
  ];

  const isActiveLink = (href: string) => {
    return location.pathname === href;
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          {/* Logo */}
          <Link
            to="/"
            className="flex items-center space-x-2 sm:space-x-3 focus-ring rounded-lg p-2"
            aria-label="Ir al inicio - Baby Cash"
          >
            <div className="w-10 h-10 sm:w-12 sm:h-12 bg-white rounded-full ring-2 ring-baby-blue flex items-center justify-center shadow-md flex-shrink-0">
              <img
                src="/productos/icono-pinguino.png"
                alt="Baby Cash Logo"
                className="w-6 h-6 sm:w-8 sm:h-8 object-contain"
                onError={(e) => {
                  // Fallback si no existe la imagen
                  const target = e.target as HTMLImageElement;
                  target.style.display = 'none';
                  target.nextElementSibling?.classList.remove('hidden');
                }}
              />
              <div className="hidden w-6 h-6 sm:w-8 sm:h-8 bg-baby-blue rounded-full items-center justify-center">
                <span className="text-white font-bold text-xs">BC</span>
              </div>
            </div>
            <div>
              <h1 className="font-poppins font-bold text-base sm:text-xl text-baby-gray">
                BABY CASH
              </h1>
              <p className="font-inter text-xs text-gray-500 -mt-1 hidden sm:block">
                Pañalera y variedades {`Soffy's`}
              </p>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            {navLinks.map((link) => (
              <Link
                key={link.href}
                to={link.href}
                className={`font-inter text-sm font-medium transition-colors duration-200 hover:text-baby-blue focus-ring rounded px-3 py-2 ${
                  isActiveLink(link.href) ? 'text-baby-blue' : 'text-gray-700'
                }`}
              >
                {link.label}
              </Link>
            ))}
          </div>

          {/* Actions */}
          <div className="flex items-center space-x-4">
            {/* Cart */}
            <Link
              to="/carrito"
              className="relative p-2 text-gray-700 hover:text-baby-blue transition-colors focus-ring rounded-lg tap-target"
              aria-label={`Ver carrito - ${totalItems} productos`}
            >
              <ShoppingCart size={24} />
              {totalItems > 0 && (
                <motion.span
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  className="absolute -top-2 -right-2 bg-baby-pink text-white text-xs font-bold rounded-full w-6 h-6 flex items-center justify-center"
                >
                  {totalItems > 99 ? '99+' : totalItems}
                </motion.span>
              )}
            </Link>

            {/* User Menu */}
            {isAuthenticated ? (
              <Link
                to="/perfil"
                className="flex items-center space-x-2 p-2 text-gray-700 hover:text-baby-blue transition-colors focus-ring rounded-lg"
                aria-label={`Perfil de ${user?.firstName}`}
              >
                <User size={24} />
                <span className="hidden lg:inline font-inter text-sm">{user?.firstName}</span>
              </Link>
            ) : (
              <Link to="/login">
                <Button variant="outline" size="sm">
                  Iniciar Sesión
                </Button>
              </Link>
            )}

            {/* Mobile menu button */}
            <button
              onClick={toggleMenu}
              className="md:hidden p-2 text-gray-700 hover:text-baby-blue transition-colors focus-ring rounded-lg tap-target"
              aria-label="Abrir menú de navegación"
              aria-expanded={isMenuOpen}
            >
              {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      <AnimatePresence>
        {isMenuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="md:hidden bg-white border-t border-gray-200 shadow-lg"
          >
            <div className="px-4 py-6 space-y-4">
              {navLinks.map((link) => (
                <Link
                  key={link.href}
                  to={link.href}
                  onClick={() => setIsMenuOpen(false)}
                  className={`block px-3 py-2 rounded-lg font-inter text-base transition-colors focus-ring tap-target ${
                    isActiveLink(link.href)
                      ? 'text-baby-blue bg-baby-blue/10'
                      : 'text-gray-700 hover:text-baby-blue hover:bg-gray-50'
                  }`}
                >
                  {link.label}
                </Link>
              ))}

              {/* Mobile Auth */}
              {!isAuthenticated && (
                <div className="pt-4 border-t border-gray-200">
                  <Link to="/login" onClick={() => setIsMenuOpen(false)}>
                    <Button variant="primary" fullWidth>
                      Iniciar Sesión
                    </Button>
                  </Link>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  );
};

export default Navbar;
