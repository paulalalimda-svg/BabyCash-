import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, Package, Truck, MapPin, Calendar, CreditCard } from 'lucide-react';
import { orderService, type Order } from '../services/api';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

const OrderConfirmation = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOrder = async () => {
      if (!orderId) {
        toast.error('ID de orden no válido');
        navigate('/perfil');
        return;
      }

      try {
        const orderData = await orderService.getOrderById(Number(orderId));
        setOrder(orderData);
        
        // Scroll to top when order loads
        window.scrollTo({ top: 0, behavior: 'smooth' });
      } catch (error) {
        logger.error('Error al cargar orden:', error);
        toast.error('No se pudo cargar la orden');
        // No redirigir automáticamente, dejar que el usuario vea el mensaje
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId, navigate]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-baby-blue/10 via-baby-cream to-baby-pink/10 flex items-center justify-center">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="text-center"
        >
          <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-baby-blue mx-auto mb-4"></div>
          <p className="text-gray-600 font-semibold">Cargando tu orden...</p>
        </motion.div>
      </div>
    );
  }

  if (!order) {
    return null;
  }

  const statusColors = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    PROCESSING: 'bg-blue-100 text-blue-800',
    SHIPPED: 'bg-indigo-100 text-indigo-800',
    DELIVERED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
  };

  const statusLabels = {
    PENDING: 'Pendiente',
    PROCESSING: 'Procesando',
    SHIPPED: 'Enviada',
    DELIVERED: 'Entregada',
    CANCELLED: 'Cancelada',
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-baby-blue/10 via-baby-cream to-baby-pink/10 py-12">
      <div className="container mx-auto px-4 max-w-4xl">
        {/* Success Message with Animation */}
        <motion.div
          initial={{ scale: 0.9, opacity: 0, y: 20 }}
          animate={{ scale: 1, opacity: 1, y: 0 }}
          transition={{ duration: 0.6, type: "spring" }}
          className="bg-gradient-to-br from-white to-green-50 rounded-2xl shadow-2xl p-8 mb-6 text-center border border-green-100"
        >
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.3, type: "spring", stiffness: 200 }}
            className="w-24 h-24 bg-gradient-to-br from-green-400 to-green-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg"
          >
            <CheckCircle className="w-14 h-14 text-white" strokeWidth={2.5} />
          </motion.div>
          
          <motion.h1
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-baby-blue to-green-600 mb-3"
          >
            ¡Orden Confirmada!
          </motion.h1>
          
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.5 }}
            className="text-gray-700 text-lg mb-6 max-w-xl mx-auto"
          >
            🎉 ¡Gracias por tu compra! Tu orden ha sido recibida exitosamente y está siendo procesada.
          </motion.p>
          
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.6 }}
            className="inline-block bg-gradient-to-r from-baby-blue to-baby-pink p-1 rounded-xl"
          >
            <div className="bg-white px-8 py-4 rounded-lg">
              <p className="text-sm text-gray-600 mb-1">Número de Orden</p>
              <p className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-baby-blue to-baby-pink">
                #{order.id}
              </p>
            </div>
          </motion.div>
        </motion.div>

        {/* Order Details */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.7 }}
          className="bg-white rounded-2xl shadow-xl p-8 mb-6 border border-gray-100"
        >
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-baby-blue/10 rounded-xl flex items-center justify-center">
                <Package className="w-6 h-6 text-baby-blue" />
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-800">Detalles de la Orden</h2>
                <p className="text-sm text-gray-500">Información completa de tu pedido</p>
              </div>
            </div>
            <span
              className={`px-6 py-2 rounded-full text-sm font-bold shadow-md ${
                statusColors[order.status]
              }`}
            >
              {statusLabels[order.status]}
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 border-b pb-6 mb-6">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center flex-shrink-0">
                <Calendar className="w-5 h-5 text-purple-600" />
              </div>
              <div>
                <p className="text-sm text-gray-600 font-medium">Fecha de Orden</p>
                <p className="font-semibold text-gray-800">
                  {new Date(order.createdAt).toLocaleDateString('es-CO', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </p>
              </div>
            </div>
            
            {order.shippingAddress && (
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <MapPin className="w-5 h-5 text-blue-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-600 font-medium">Dirección de Envío</p>
                  <p className="font-semibold text-gray-800 text-sm">
                    {order.shippingAddress}
                  </p>
                </div>
              </div>
            )}
          </div>

          {/* Order Items */}
          <div>
            <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
              <Package className="w-5 h-5 text-baby-blue" />
              Productos en tu Orden
            </h3>
            {order.items && order.items.length > 0 ? (
              <div className="space-y-3">
                {order.items.map((item, index) => (
                  <div
                    key={`${item.productId}-${index}`}
                    className="flex justify-between items-center bg-gray-50 rounded-xl p-4 hover:bg-gray-100 transition"
                  >
                    <div className="flex-1">
                      <p className="font-semibold text-gray-800">
                        {item.productName}
                      </p>
                      <p className="text-sm text-gray-600">
                        Cantidad: {item.quantity} × ${item.unitPrice.toLocaleString('es-CO')}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-lg text-baby-pink">
                        ${item.subtotal.toLocaleString('es-CO')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="bg-gray-50 rounded-xl p-6 text-center">
                <p className="text-gray-500">No hay productos en esta orden</p>
              </div>
            )}
          </div>

          {/* Total */}
          <div className="mt-8 pt-6 border-t-2 border-gray-200">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-gray-600 text-sm mb-1">Total de tu Orden</p>
                <p className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-baby-blue to-baby-pink">
                  ${(order.totalAmount || order.total || 0).toLocaleString('es-CO')}
                </p>
              </div>
              <div className="w-16 h-16 bg-gradient-to-br from-baby-blue to-baby-pink rounded-2xl flex items-center justify-center shadow-lg">
                <CreditCard className="w-8 h-8 text-white" />
              </div>
            </div>
          </div>
        </motion.div>

        {/* Order Timeline */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
          className="bg-white rounded-2xl shadow-xl p-8 mb-6 border border-gray-100"
        >
          <div className="flex items-center gap-3 mb-6">
            <div className="w-12 h-12 bg-indigo-100 rounded-xl flex items-center justify-center">
              <Truck className="w-6 h-6 text-indigo-600" />
            </div>
            <div>
              <h3 className="font-bold text-xl text-gray-800">Estado de tu Pedido</h3>
              <p className="text-sm text-gray-500">Seguimiento en tiempo real</p>
            </div>
          </div>
          
          <div className="space-y-6">
            {['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED'].map((status, index) => {
              const statusKeys = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
              const currentIndex = statusKeys.indexOf(order.status);
              const isPast = currentIndex >= index;
              const isCurrent = order.status === status;
              
              return (
                <motion.div
                  key={status}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.9 + index * 0.1 }}
                  className="flex items-center gap-4"
                >
                  <div className="relative">
                    <div
                      className={`w-12 h-12 rounded-full flex items-center justify-center font-bold transition-all duration-300 ${
                        isPast
                          ? 'bg-gradient-to-br from-baby-blue to-baby-pink text-white shadow-lg scale-110'
                          : 'bg-gray-200 text-gray-500'
                      }`}
                    >
                      {isPast ? '✓' : index + 1}
                    </div>
                    {index < 3 && (
                      <div
                        className={`absolute left-1/2 top-12 w-0.5 h-8 -ml-px transition-all duration-300 ${
                          isPast ? 'bg-gradient-to-b from-baby-blue to-baby-pink' : 'bg-gray-300'
                        }`}
                      />
                    )}
                  </div>
                  <div className="flex-1">
                    <p
                      className={`font-bold text-lg transition-colors ${
                        (() => {
                          if (isCurrent) return 'text-baby-blue';
                          if (isPast) return 'text-gray-800';
                          return 'text-gray-400';
                        })()
                      }`}
                    >
                      {statusLabels[status as keyof typeof statusLabels]}
                    </p>
                    {isCurrent && (
                      <p className="text-sm text-baby-blue font-medium">Estado actual</p>
                    )}
                  </div>
                </motion.div>
              );
            })}
          </div>
        </motion.div>

        {/* Actions */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.9 }}
          className="flex flex-col sm:flex-row gap-4"
        >
          <Link
            to="/productos"
            className="flex-1 bg-gradient-to-r from-baby-blue to-baby-pink text-white py-4 rounded-xl font-bold text-center hover:shadow-xl transition-all transform hover:-translate-y-1"
          >
            🛍️ Seguir Comprando
          </Link>
          <Link
            to="/perfil"
            className="flex-1 border-2 border-baby-blue text-baby-blue py-4 rounded-xl font-bold text-center hover:bg-baby-blue hover:text-white transition-all transform hover:-translate-y-1"
          >
            📦 Ver Mis Órdenes
          </Link>
        </motion.div>

        {/* Contact Info */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1 }}
          className="mt-8 text-center bg-white rounded-xl p-6 shadow-lg border border-gray-100"
        >
          <p className="text-gray-700 font-medium mb-2">¿Necesitas ayuda con tu orden?</p>
          <p className="text-gray-600">
            Contáctanos en{' '}
            <a
              href="mailto:soporte@babycash.com"
              className="text-baby-blue hover:text-baby-pink font-semibold hover:underline transition"
            >
              soporte@babycash.com
            </a>
          </p>
        </motion.div>
      </div>
    </div>
  );
};

export default OrderConfirmation;
