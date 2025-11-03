import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import {
  User,
  Edit3,
  Save,
  Mail,
  Phone,
  MapPin,
  Calendar,
  Package,
  Heart,
  Settings,
  LogOut,
  ShoppingBag,
  DollarSign,
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { orderService, userService, loyaltyService, type Order, type UserStats, type UpdateProfileData, type LoyaltyPoints } from '../services/api';
import Button from '../components/ui/Button';
import Input from '../components/ui/input';
import Card from '../components/ui/Card';
import LinkButton from '../components/ui/LinkButton';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

const Perfil: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [stats, setStats] = useState<UserStats | null>(null);
  const [loadingStats, setLoadingStats] = useState(true);
  const [loyaltyPoints, setLoyaltyPoints] = useState<LoyaltyPoints | null>(null);
  const [loadingLoyalty, setLoadingLoyalty] = useState(true);
  
  const [editData, setEditData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    phone: user?.phone || '',
    address: '',
  });

  useEffect(() => {
    const fetchOrders = async () => {
      if (!isAuthenticated) {
        setLoadingOrders(false);
        return;
      }

      try {
        setLoadingOrders(true);
        const response = await orderService.getMyOrders();
        setOrders(response.content || []);
      } catch (error: any) {
        logger.error('Error al cargar órdenes:', error);
        if (error.response?.status !== 404) {
          toast.error('No se pudieron cargar tus órdenes');
        }
        setOrders([]);
      } finally {
        setLoadingOrders(false);
      }
    };

    fetchOrders();
  }, [isAuthenticated]);

  useEffect(() => {
    const fetchStats = async () => {
      if (!isAuthenticated || loadingOrders) {
        setLoadingStats(false);
        return;
      }

      try {
        setLoadingStats(true);
        const userStats = await userService.getStats();
        setStats(userStats);
      } catch (error: any) {
        logger.error('Error al cargar estadísticas:', error);
        setStats({
          totalOrders: orders.length,
          totalProducts: 0,
          totalSpent: orders.reduce((sum, o) => sum + (o.total || 0), 0),
          memberSince: user?.email ? '2024' : '2025'
        });
      } finally {
        setLoadingStats(false);
      }
    };

    fetchStats();
  }, [isAuthenticated, loadingOrders]); // ✅ Depende de loadingOrders, no de orders directamente

  useEffect(() => {
    const fetchLoyalty = async () => {
      if (!isAuthenticated) {
        setLoadingLoyalty(false);
        return;
      }

      try {
        setLoadingLoyalty(true);
        const points = await loyaltyService.getPoints();
        setLoyaltyPoints(points);
      } catch (error: any) {
        logger.error('Error al cargar puntos de fidelidad:', error);
        // Establecer valores por defecto en caso de error
        setLoyaltyPoints({
          totalPoints: 0,
          earnedThisMonth: 0,
          earnedTotal: 0,
          redeemedTotal: 0,
          expiringSoon: 0,
          memberSince: user?.email ? '2024' : '2025',
          tier: 'BRONZE',
          availableDiscountPercent: 0,
          pointsForNextDiscount: 100,
        });
      } finally {
        setLoadingLoyalty(false);
      }
    };

    fetchLoyalty();
  }, [isAuthenticated]); // ✅ Solo isAuthenticated - user no cambia frecuentemente

  const handleSave = async () => {
    try {
      // Validar teléfono si se proporciona
      if (editData.phone && editData.phone.length !== 10) {
        toast.error('El teléfono debe tener exactamente 10 dígitos');
        return;
      }

      const profileData: UpdateProfileData = {
        firstName: editData.firstName,
        lastName: editData.lastName,
        phone: editData.phone || undefined, // Solo enviar si tiene valor
        address: editData.address || undefined,
      };

      const updatedUser = await userService.updateProfile(profileData);
      setIsEditing(false);
      toast.success('Perfil actualizado correctamente');
      
      // Update localStorage with the response from backend
      localStorage.setItem('baby-cash-user', JSON.stringify(updatedUser));
      
      // Force a page reload to update AuthContext
      window.location.reload();
    } catch (error: any) {
      logger.error('Error al actualizar perfil:', error);
      const errorMessage = error.response?.data?.message || 'No se pudo actualizar el perfil';
      toast.error(errorMessage);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-baby-light pt-20 flex items-center justify-center">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          className="text-center px-4"
        >
          <Card className="max-w-md mx-auto p-8">
            <User className="w-20 h-20 mx-auto mb-6 text-gray-300" />
            <h2 className="text-2xl font-bold font-poppins text-baby-gray mb-4">Inicia Sesión</h2>
            <p className="text-gray-600 font-inter mb-8">Debes iniciar sesión para ver tu perfil</p>
            <Button onClick={() => navigate('/login')} size="lg" className="bg-gradient-to-r from-baby-blue to-baby-pink text-white">
              Iniciar Sesión
            </Button>
          </Card>
        </motion.div>
      </div>
    );
  }

  const statsData = [
    { 
      label: 'Pedidos realizados', 
      value: loadingStats ? '...' : String(stats?.totalOrders || orders.length), 
      icon: Package 
    },
    { 
      label: 'Total gastado', 
      value: loadingStats ? '...' : `$${(stats?.totalSpent || 0).toLocaleString('es-CO')}`, 
      icon: DollarSign 
    },
    { 
      label: 'Años con nosotros', 
      value: loadingStats ? '...' : String(stats ? new Date().getFullYear() - parseInt(stats.memberSince) : 0), 
      icon: Calendar 
    },
  ];

  const statusColors: Record<string, string> = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    PAID: 'bg-green-100 text-green-800',
    SHIPPED: 'bg-blue-100 text-blue-800',
    DELIVERED: 'bg-purple-100 text-purple-800',
    CANCELLED: 'bg-red-100 text-red-800',
  };

  const statusLabels: Record<string, string> = {
    PENDING: 'Pendiente',
    PAID: 'Pagada',
    SHIPPED: 'Enviada',
    DELIVERED: 'Entregada',
    CANCELLED: 'Cancelada',
  };

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      <section className="py-8 px-4 bg-gradient-to-r from-baby-blue/10 to-baby-pink/10">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="flex flex-col sm:flex-row sm:items-center sm:justify-between"
          >
            <div className="flex items-center mb-4 sm:mb-0">
              <div className="w-16 h-16 bg-gradient-to-br from-baby-blue to-baby-pink rounded-full flex items-center justify-center mr-4">
                <User className="w-8 h-8 text-white" />
              </div>
              <div>
                <h1 className="text-2xl md:text-3xl font-bold font-poppins text-baby-gray">
                  Mi Perfil
                </h1>
                <p className="text-gray-600 font-inter">
                  Bienvenid@ de nuevo, {editData.firstName || user?.firstName}
                </p>
              </div>
            </div>

            <Button
              onClick={handleLogout}
              variant="outline"
              size="sm"
              className="text-red-600 border-red-200 hover:bg-red-50"
            >
              <LogOut className="w-4 h-4 mr-2" />
              Cerrar Sesión
            </Button>
          </motion.div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
            >
              <Card className="p-6 mb-8">
                <div className="flex items-center justify-between mb-6">
                  <h2 className="text-xl font-bold font-poppins text-baby-gray">
                    Información Personal
                  </h2>
                  <Button
                    onClick={() => (isEditing ? handleSave() : setIsEditing(true))}
                    variant="outline"
                    size="sm"
                  >
                    {isEditing ? (
                      <>
                        <Save className="w-4 h-4 mr-2" />
                        Guardar
                      </>
                    ) : (
                      <>
                        <Edit3 className="w-4 h-4 mr-2" />
                        Editar
                      </>
                    )}
                  </Button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nombre
                    </label>
                    {isEditing ? (
                      <Input
                        value={editData.firstName}
                        onChange={(e) => setEditData({ ...editData, firstName: e.target.value })}
                        placeholder="Nombre"
                      />
                    ) : (
                      <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                        <User className="w-5 h-5 text-gray-400 mr-3" />
                        <span className="font-inter">{editData.firstName}</span>
                      </div>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Apellido
                    </label>
                    {isEditing ? (
                      <Input
                        value={editData.lastName}
                        onChange={(e) => setEditData({ ...editData, lastName: e.target.value })}
                        placeholder="Apellido"
                      />
                    ) : (
                      <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                        <User className="w-5 h-5 text-gray-400 mr-3" />
                        <span className="font-inter">{editData.lastName}</span>
                      </div>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                    <div className="flex items-center p-3 bg-gray-100 rounded-lg">
                      <Mail className="w-5 h-5 text-gray-400 mr-3" />
                      <span className="font-inter text-gray-600">{user?.email}</span>
                    </div>
                    <p className="text-xs text-gray-500 mt-1">El email no se puede modificar</p>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Teléfono</label>
                    {isEditing ? (
                      <Input
                        value={editData.phone}
                        onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                        placeholder="Número de teléfono"
                      />
                    ) : (
                      <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                        <Phone className="w-5 h-5 text-gray-400 mr-3" />
                        <span className="font-inter">{editData.phone || 'No especificado'}</span>
                      </div>
                    )}
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Dirección
                    </label>
                    {isEditing ? (
                      <Input
                        value={editData.address}
                        onChange={(e) => setEditData({ ...editData, address: e.target.value })}
                        placeholder="Dirección completa"
                      />
                    ) : (
                      <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                        <MapPin className="w-5 h-5 text-gray-400 mr-3" />
                        <span className="font-inter">{editData.address || 'No especificada'}</span>
                      </div>
                    )}
                  </div>
                </div>
              </Card>

              <Card className="p-6">
                <h2 className="text-xl font-bold font-poppins text-baby-gray mb-6">
                  Historial de Órdenes
                </h2>
                {loadingOrders ? (
                  <div className="flex justify-center py-8">
                    <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-baby-blue"></div>
                  </div>
                ) : orders.length === 0 ? (
                  <div className="text-center py-8">
                    <ShoppingBag className="w-16 h-16 mx-auto mb-4 text-gray-300" />
                    <p className="text-gray-600 mb-4">No tienes órdenes aún</p>
                    <LinkButton
                      href="/productos"
                      className="bg-baby-blue text-white hover:bg-baby-blue/90"
                    >
                      Explorar Productos
                    </LinkButton>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {orders.slice(0, 5).map((order) => (
                      <div
                        key={order.id}
                        className="border rounded-lg p-4 hover:border-baby-blue transition cursor-pointer"
                        onClick={() => navigate(`/order-confirmation/${order.id}`)}
                      >
                        <div className="flex justify-between items-start mb-2">
                          <div>
                            <p className="font-semibold text-baby-gray">
                              Orden #{order.id}
                            </p>
                            <p className="text-sm text-gray-600">
                              {new Date(order.createdAt).toLocaleDateString('es-CO', {
                                year: 'numeric',
                                month: 'short',
                                day: 'numeric',
                              })}
                            </p>
                          </div>
                          <span
                            className={`px-3 py-1 rounded-full text-xs font-semibold ${
                              statusColors[order.status]
                            }`}
                          >
                            {statusLabels[order.status]}
                          </span>
                        </div>
                        <div className="flex justify-between items-center pt-2 border-t">
                          <p className="text-sm text-gray-600">
                            {order.items.length} producto{order.items.length !== 1 ? 's' : ''}
                          </p>
                          <p className="font-bold text-baby-pink">
                            ${(order.totalAmount || order.total || 0).toLocaleString('es-CO')}
                          </p>
                        </div>
                      </div>
                    ))}
                    {orders.length > 5 && (
                      <Button
                        variant="outline"
                        className="w-full"
                        onClick={() => {}}
                      >
                        Ver todas las órdenes ({orders.length})
                      </Button>
                    )}
                  </div>
                )}
              </Card>

              <Card className="p-6 mt-8">
                <h2 className="text-xl font-bold font-poppins text-baby-gray mb-6">
                  Acciones Rápidas
                </h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <LinkButton
                    href="/productos"
                    variant="outline"
                    className="justify-center"
                  >
                    <Package className="w-5 h-5 mr-2" />
                    Ver Productos
                  </LinkButton>
                  <LinkButton
                    href="/carrito"
                    variant="outline"
                    className="justify-center"
                  >
                    <Heart className="w-5 h-5 mr-2" />
                    Ver Carrito
                  </LinkButton>
                  <LinkButton
                    href="/contacto"
                    variant="outline"
                    className="justify-center"
                  >
                    <Mail className="w-5 h-5 mr-2" />
                    Contactar Soporte
                  </LinkButton>
                  <Button variant="outline" className="justify-center">
                    <Settings className="w-5 h-5 mr-2" />
                    Configuración
                  </Button>
                </div>
              </Card>
            </motion.div>
          </div>

          <div className="lg:col-span-1">
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.6 }}
              className="space-y-6"
            >
              <Card className="p-6 bg-gradient-to-br from-baby-blue/5 to-baby-pink/5 border-baby-blue/20">
                <h3 className="text-lg font-bold font-poppins text-baby-gray mb-4">Tu Actividad</h3>
                <div className="space-y-4">
                  {statsData.map((stat, index) => (
                    <motion.div
                      key={stat.label}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ duration: 0.4, delay: index * 0.1 }}
                      className="flex items-center justify-between p-3 bg-white rounded-lg"
                    >
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-gradient-to-br from-baby-blue to-baby-pink rounded-full flex items-center justify-center mr-3">
                          <stat.icon className="w-5 h-5 text-white" />
                        </div>
                        <span className="font-inter text-gray-600 text-sm">{stat.label}</span>
                      </div>
                      <span className="font-bold font-poppins text-baby-pink text-xl">
                        {stat.value}
                      </span>
                    </motion.div>
                  ))}
                </div>
              </Card>

              <Card className="p-6">
                <h3 className="text-lg font-bold font-poppins text-baby-gray mb-4">Tu Cuenta</h3>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Tipo de cuenta:</span>
                    <span className="font-medium text-baby-pink">Premium</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Miembro desde:</span>
                    <span className="font-medium">
                      {loadingStats ? '...' : (stats?.memberSince || '2024')}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Estado:</span>
                    <span className="font-medium text-green-600">Activo</span>
                  </div>
                </div>
              </Card>

              <Card className="p-6 bg-gradient-to-br from-baby-mint/10 to-baby-blue/10 border-baby-mint/20">
                <h3 className="text-lg font-bold font-poppins text-baby-gray mb-4">
                  Programa de Fidelidad
                </h3>
                {loadingLoyalty ? (
                  <div className="flex justify-center py-8">
                    <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-baby-mint"></div>
                  </div>
                ) : loyaltyPoints ? (
                  <div className="space-y-4">
                    <div className="text-center">
                      <div className="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-baby-mint to-baby-blue rounded-full flex items-center justify-center">
                        <Heart className="w-10 h-10 text-white" />
                      </div>
                      <p className="text-sm text-gray-600 mb-2">Puntos disponibles</p>
                      <p className="text-3xl font-bold font-poppins text-baby-mint">
                        {loyaltyPoints.totalPoints.toLocaleString('es-CO')}
                      </p>
                      <div className="inline-block mt-2 px-3 py-1 bg-gradient-to-r from-yellow-400 to-yellow-600 rounded-full">
                        <span className="text-xs font-bold text-white">
                          {loyaltyPoints.tier}
                        </span>
                      </div>
                    </div>

                    {/* Descuento disponible */}
                    {loyaltyPoints.availableDiscountPercent > 0 && (
                      <div className="bg-gradient-to-r from-green-50 to-emerald-50 border-2 border-green-400 rounded-lg p-4 text-center">
                        <p className="text-xs text-green-700 font-medium mb-1">
                          🎉 ¡Descuento Disponible!
                        </p>
                        <p className="text-2xl font-bold text-green-600">
                          {loyaltyPoints.availableDiscountPercent}% OFF
                        </p>
                        <p className="text-xs text-green-600 mt-1">
                          En tu próxima compra
                        </p>
                      </div>
                    )}

                    {/* Puntos para siguiente descuento */}
                    {loyaltyPoints.pointsForNextDiscount > 0 && (
                      <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                        <p className="text-xs text-blue-800 text-center">
                          📈 Te faltan <span className="font-bold">{loyaltyPoints.pointsForNextDiscount}</span> puntos para el siguiente 5% de descuento
                        </p>
                      </div>
                    )}

                    <div className="grid grid-cols-2 gap-3 pt-4 border-t">
                      <div className="text-center p-2 bg-white rounded-lg">
                        <p className="text-xs text-gray-600">Este mes</p>
                        <p className="text-lg font-bold text-baby-blue">
                          +{loyaltyPoints.earnedThisMonth}
                        </p>
                      </div>
                      <div className="text-center p-2 bg-white rounded-lg">
                        <p className="text-xs text-gray-600">Total ganado</p>
                        <p className="text-lg font-bold text-baby-pink">
                          {loyaltyPoints.earnedTotal}
                        </p>
                      </div>
                    </div>

                    {loyaltyPoints.expiringSoon > 0 && (
                      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
                        <p className="text-xs text-yellow-800">
                          ⚠️ {loyaltyPoints.expiringSoon} puntos expiran pronto
                        </p>
                      </div>
                    )}

                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full mt-2"
                      onClick={() => navigate('/loyalty-history')}
                    >
                      Ver Historial
                    </Button>

                    <div className="bg-gray-50 rounded-lg p-3 space-y-1">
                      <p className="text-xs text-gray-500 text-center">
                        💰 Gana 1 punto por cada $1,000 en compras
                      </p>
                      <p className="text-xs text-gray-500 text-center">
                        🎁 Cada 1,000 puntos = 5% de descuento
                      </p>
                      <p className="text-xs text-gray-500 text-center font-semibold">
                        ⭐ Máximo 50% de descuento (10,000 pts)
                      </p>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-6">
                    <p className="text-sm text-gray-600">
                      No se pudieron cargar los puntos
                    </p>
                  </div>
                )}
              </Card>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Perfil;
