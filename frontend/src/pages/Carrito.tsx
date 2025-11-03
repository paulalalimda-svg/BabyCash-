import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { ShoppingCart, Plus, Minus, Trash2, ShoppingBag, CreditCard } from 'lucide-react';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import LinkButton from '../components/ui/LinkButton';
import toast from 'react-hot-toast';

const Carrito: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { items, updateQuantity, removeFromCart, clearCart, getTotalPrice, getTotalItems } =
    useCart();

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const handleCheckout = () => {
    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para continuar con la compra');
      navigate('/login');
      return;
    }
    navigate('/checkout');
  };

  if (items.length === 0) {
    return (
      <div className="min-h-screen bg-baby-light pt-20 flex items-center justify-center">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          className="text-center px-4"
        >
          <Card className="max-w-md mx-auto p-8">
            <ShoppingBag className="w-20 h-20 mx-auto mb-6 text-gray-300" />
            <h2 className="text-2xl font-bold font-poppins text-baby-gray mb-4">
              Tu carrito está vacío
            </h2>
            <p className="text-gray-600 font-inter mb-8">
              Agrega algunos productos increíbles para tu bebé y verás que aparecen aquí
            </p>
            <LinkButton href="/productos" size="lg" variant="primary" className="flex items-center">
              <ShoppingBag className="w-5 h-5 mr-2" />
              Explorar Productos
            </LinkButton>
          </Card>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      {/* Header */}
      <section className="py-8 px-4 bg-white shadow-sm">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="flex flex-col sm:flex-row sm:items-center sm:justify-between"
          >
            <div className="flex items-center mb-4 sm:mb-0">
              <h1 className="text-2xl md:text-3xl font-bold font-poppins text-baby-gray flex items-center">
                <ShoppingCart className="inline w-8 h-8 mr-3 text-baby-pink" />
                Carrito de Compras
              </h1>
              <p className="text-gray-600 font-inter ml-11">
                {getTotalItems()} artículo{getTotalItems() !== 1 ? 's' : ''} en tu carrito
              </p>
            </div>

            <div className="flex items-center gap-4">
              <LinkButton
                href="/productos"
                size="lg"
                variant="primary"
                className="flex items-center"
              >
                <ShoppingBag className="w-5 h-5 mr-2" />
                Seguir Comprando
              </LinkButton>

              {items.length > 0 && (
                <Button
                  onClick={clearCart}
                  variant="outline"
                  size="sm"
                  className="text-red-600 border-red-200 hover:bg-red-50"
                >
                  <Trash2 className="w-4 h-4 mr-2" />
                  Vaciar carrito
                </Button>
              )}
            </div>
          </motion.div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart Items */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((item, index) => (
              <motion.div
                key={item.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: index * 0.1 }}
                layout
              >
                <Card className="p-6 border border-gray-200 hover:shadow-lg transition-shadow duration-300">
                  <div className="flex flex-col sm:flex-row gap-4">
                    {/* Product Image */}
                    <div className="flex-shrink-0">
                      <div className="w-24 h-24 bg-gradient-to-br from-baby-blue/20 to-baby-pink/20 rounded-lg flex items-center justify-center">
                        <ShoppingBag className="w-12 h-12 text-baby-pink" />
                      </div>
                    </div>

                    {/* Product Info */}
                    <div className="flex-1">
                      <h3 className="text-lg font-bold font-poppins text-baby-gray mb-1">
                        {item.name}
                      </h3>
                      <p className="text-sm text-gray-500 mb-2 capitalize">
                        {item.category?.replace('-', ' ') || 'Sin categoría'}
                      </p>
                      <p className="text-baby-pink font-bold font-inter text-lg">
                        {formatPrice(item.price)}
                      </p>
                    </div>

                    {/* Quantity Controls */}
                    <div className="flex flex-col sm:items-end gap-3">
                      <div className="flex items-center gap-3 bg-gray-50 rounded-lg p-1">
                        <Button
                          onClick={() => updateQuantity(item.id, item.quantity - 1)}
                          variant="ghost"
                          size="sm"
                          className="w-8 h-8 p-0 hover:bg-gray-200"
                          disabled={item.quantity <= 1}
                          aria-label="Disminuir cantidad"
                        >
                          <Minus className="w-4 h-4" />
                        </Button>

                        <span className="w-8 text-center font-bold font-inter">
                          {item.quantity}
                        </span>

                        <Button
                          onClick={() => updateQuantity(item.id, item.quantity + 1)}
                          variant="ghost"
                          size="sm"
                          className="w-8 h-8 p-0 hover:bg-gray-200"
                          aria-label="Aumentar cantidad"
                        >
                          <Plus className="w-4 h-4" />
                        </Button>
                      </div>

                      {/* Subtotal */}
                      <p className="font-bold font-inter text-baby-gray">
                        {formatPrice(item.price * item.quantity)}
                      </p>

                      {/* Remove Button */}
                      <Button
                        onClick={() => removeFromCart(item.id)}
                        variant="ghost"
                        size="sm"
                        className="text-red-600 hover:bg-red-50 p-2"
                        aria-label="Eliminar del carrito"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </Card>
              </motion.div>
            ))}
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.6 }}
              className="sticky top-24"
            >
              <Card className="p-6 bg-gradient-to-br from-baby-blue/5 to-baby-pink/5 border border-baby-blue/20">
                <h2 className="text-xl font-bold font-poppins text-baby-gray mb-6">
                  Resumen del Pedido
                </h2>

                <div className="space-y-4 mb-6">
                  <div className="flex justify-between items-center pb-2 border-b border-gray-200">
                    <span className="font-inter text-gray-600">
                      Subtotal ({getTotalItems()} artículo{getTotalItems() !== 1 ? 's' : ''})
                    </span>
                    <span className="font-bold font-inter text-baby-gray">
                      {formatPrice(getTotalPrice())}
                    </span>
                  </div>

                  <div className="flex justify-between items-center pb-2 border-b border-gray-200">
                    <span className="font-inter text-gray-600">Envío</span>
                    <span className="font-inter text-green-600">Gratis</span>
                  </div>

                  <div className="flex justify-between items-center text-lg pt-2">
                    <span className="font-bold font-poppins text-baby-gray">Total</span>
                    <span className="font-bold font-poppins text-baby-pink text-xl">
                      {formatPrice(getTotalPrice())}
                    </span>
                  </div>
                </div>

                <Button
                  onClick={handleCheckout}
                  size="lg"
                  className="w-full bg-gradient-to-r from-baby-blue to-baby-pink text-white font-bold py-4 rounded-lg shadow-lg hover:shadow-xl transition-all duration-300"
                >
                  <CreditCard className="w-5 h-5 mr-2" />
                  Proceder al Pago
                </Button>

                <div className="mt-4 text-center">
                  <p className="text-xs text-gray-500 font-inter">
                    Envío gratis en pedidos superiores a $50.000
                  </p>
                </div>
              </Card>

              {/* Security Features */}
              <Card className="p-4 mt-4">
                <div className="text-center">
                  <h3 className="font-bold font-poppins text-baby-gray text-sm mb-2">
                    Compra Segura
                  </h3>
                  <div className="flex justify-center items-center gap-2 text-xs text-gray-600">
                    <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                    <span>Datos protegidos</span>
                    <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                    <span>Pago seguro</span>
                  </div>
                </div>
              </Card>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Carrito;
