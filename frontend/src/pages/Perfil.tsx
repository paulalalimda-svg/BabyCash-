import { motion } from 'framer-motion';
import {
  AlertCircle,
  Calendar,
  CheckCircle,
  DollarSign,
  Edit3,
  Heart,
  LogOut,
  Mail,
  MapPin,
  Package,
  Phone,
  RefreshCw,
  Save,
  Settings,
  ShoppingBag,
  Trash2,
  User,
} from 'lucide-react';
import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Input from '../components/ui/input';
import LinkButton from '../components/ui/LinkButton';
import { useAuth } from '../contexts/AuthContext';
import {
  authService,
  loyaltyService,
  orderService,
  userService,
  type LoyaltyPoints,
  type Order,
  type UpdateProfileData,
  type UserStats,
} from '../services/api';
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

  // Email verification states
  const [verificationCode, setVerificationCode] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [emailVerified, setEmailVerified] = useState(user?.emailVerified || false);

  // Security states
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deletionCode, setDeletionCode] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);
  const [isRequestingDeletion, setIsRequestingDeletion] = useState(false);
  const [isLoggingOutAll, setIsLoggingOutAll] = useState(false);
  const [codeSent, setCodeSent] = useState(false);

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
      } catch (error: unknown) {
        logger.error('Error al cargar órdenes:', error);
        const err = error as { response?: { status?: number } };
        if (err.response?.status !== 404) {
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
      } catch (error: unknown) {
        logger.error('Error al cargar estadísticas:', error);
        setStats({
          totalOrders: orders.length,
          totalProducts: 0,
          totalSpent: orders.reduce((sum, o) => sum + (o.total || 0), 0),
          memberSince: user?.email ? '2024' : '2025',
        });
      } finally {
        setLoadingStats(false);
      }
    };

    fetchStats();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, loadingOrders]);

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
      } catch (error: unknown) {
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

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
    } catch (error: unknown) {
      logger.error('Error al actualizar perfil:', error);
      const err = error as { response?: { data?: { message?: string } } };
      const errorMessage = err.response?.data?.message || 'No se pudo actualizar el perfil';
      toast.error(errorMessage);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (!isAuthenticated) {
    return (
      <div className="bg-baby-light flex min-h-screen items-center justify-center pt-20">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          className="px-4 text-center"
        >
          <Card className="mx-auto max-w-md p-8">
            <User className="mx-auto mb-6 size-20 text-gray-300" />
            <h2 className="font-poppins text-baby-gray mb-4 text-2xl font-bold">Inicia Sesión</h2>
            <p className="font-inter mb-8 text-gray-600">Debes iniciar sesión para ver tu perfil</p>
            <Button
              onClick={() => navigate('/login')}
              size="lg"
              className="from-baby-blue to-baby-pink bg-gradient-to-r text-white"
            >
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
      icon: Package,
    },
    {
      label: 'Total gastado',
      value: loadingStats ? '...' : `$${(stats?.totalSpent || 0).toLocaleString('es-CO')}`,
      icon: DollarSign,
    },
    {
      label: 'Años con nosotros',
      value: loadingStats
        ? '...'
        : String(stats ? new Date().getFullYear() - parseInt(stats.memberSince) : 0),
      icon: Calendar,
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
    <div className="bg-baby-light min-h-screen pt-20">
      <section className="from-baby-blue/10 to-baby-pink/10 bg-gradient-to-r px-4 py-8">
        <div className="mx-auto max-w-7xl">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="flex flex-col sm:flex-row sm:items-center sm:justify-between"
          >
            <div className="mb-4 flex items-center sm:mb-0">
              <div className="from-baby-blue to-baby-pink mr-4 flex size-16 items-center justify-center rounded-full bg-gradient-to-br">
                <User className="size-8 text-white" />
              </div>
              <div>
                <h1 className="font-poppins text-baby-gray text-2xl font-bold md:text-3xl">
                  Mi Perfil
                </h1>
                <p className="font-inter text-gray-600">
                  Bienvenid@ de nuevo, {editData.firstName || user?.firstName}
                </p>
              </div>
            </div>

            <Button
              onClick={handleLogout}
              variant="outline"
              size="sm"
              className="border-red-200 text-red-600 hover:bg-red-50"
            >
              <LogOut className="mr-2 size-4" />
              Cerrar Sesión
            </Button>
          </motion.div>
        </div>
      </section>

      <div className="mx-auto max-w-7xl px-4 py-8">
        <div className="grid grid-cols-1 gap-8 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
            >
              <Card className="mb-8 p-6">
                <div className="mb-6 flex items-center justify-between">
                  <h2 className="font-poppins text-baby-gray text-xl font-bold">
                    Información Personal
                  </h2>
                  <Button
                    onClick={() => (isEditing ? handleSave() : setIsEditing(true))}
                    variant="outline"
                    size="sm"
                  >
                    {isEditing ? (
                      <>
                        <Save className="mr-2 size-4" />
                        Guardar
                      </>
                    ) : (
                      <>
                        <Edit3 className="mr-2 size-4" />
                        Editar
                      </>
                    )}
                  </Button>
                </div>

                <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                  <div>
                    <label className="mb-2 block text-sm font-medium text-gray-700">Nombre</label>
                    {isEditing ? (
                      <Input
                        value={editData.firstName}
                        onChange={(e) => setEditData({ ...editData, firstName: e.target.value })}
                        placeholder="Nombre"
                      />
                    ) : (
                      <div className="flex items-center rounded-lg bg-gray-50 p-3">
                        <User className="mr-3 size-5 text-gray-400" />
                        <span className="font-inter">{editData.firstName}</span>
                      </div>
                    )}
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-medium text-gray-700">Apellido</label>
                    {isEditing ? (
                      <Input
                        value={editData.lastName}
                        onChange={(e) => setEditData({ ...editData, lastName: e.target.value })}
                        placeholder="Apellido"
                      />
                    ) : (
                      <div className="flex items-center rounded-lg bg-gray-50 p-3">
                        <User className="mr-3 size-5 text-gray-400" />
                        <span className="font-inter">{editData.lastName}</span>
                      </div>
                    )}
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-medium text-gray-700">Email</label>
                    <div className="flex items-center rounded-lg bg-gray-100 p-3">
                      <Mail className="mr-3 size-5 text-gray-400" />
                      <span className="font-inter text-gray-600">{user?.email}</span>
                    </div>
                    <p className="mt-1 text-xs text-gray-500">El email no se puede modificar</p>
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-medium text-gray-700">Teléfono</label>
                    {isEditing ? (
                      <Input
                        value={editData.phone}
                        onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                        placeholder="Número de teléfono"
                      />
                    ) : (
                      <div className="flex items-center rounded-lg bg-gray-50 p-3">
                        <Phone className="mr-3 size-5 text-gray-400" />
                        <span className="font-inter">{editData.phone || 'No especificado'}</span>
                      </div>
                    )}
                  </div>

                  <div className="md:col-span-2">
                    <label className="mb-2 block text-sm font-medium text-gray-700">
                      Dirección
                    </label>
                    {isEditing ? (
                      <Input
                        value={editData.address}
                        onChange={(e) => setEditData({ ...editData, address: e.target.value })}
                        placeholder="Dirección completa"
                      />
                    ) : (
                      <div className="flex items-center rounded-lg bg-gray-50 p-3">
                        <MapPin className="mr-3 size-5 text-gray-400" />
                        <span className="font-inter">{editData.address || 'No especificada'}</span>
                      </div>
                    )}
                  </div>
                </div>
              </Card>

              <Card className="p-6">
                <h2 className="font-poppins text-baby-gray mb-6 text-xl font-bold">
                  Historial de Órdenes
                </h2>
                {loadingOrders ? (
                  <div className="flex justify-center py-8">
                    <div className="border-baby-blue size-10 animate-spin rounded-full border-y-2"></div>
                  </div>
                ) : orders.length === 0 ? (
                  <div className="py-8 text-center">
                    <ShoppingBag className="mx-auto mb-4 size-16 text-gray-300" />
                    <p className="mb-4 text-gray-600">No tienes órdenes aún</p>
                    <LinkButton
                      href="/productos"
                      className="bg-baby-blue hover:bg-baby-blue/90 text-white"
                    >
                      Explorar Productos
                    </LinkButton>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {orders.slice(0, 5).map((order) => (
                      <div
                        key={order.id}
                        className="hover:border-baby-blue cursor-pointer rounded-lg border p-4 transition"
                        onClick={() => navigate(`/order-confirmation/${order.id}`)}
                      >
                        <div className="mb-2 flex items-start justify-between">
                          <div>
                            <p className="text-baby-gray font-semibold">Orden #{order.id}</p>
                            <p className="text-sm text-gray-600">
                              {new Date(order.createdAt).toLocaleDateString('es-CO', {
                                year: 'numeric',
                                month: 'short',
                                day: 'numeric',
                              })}
                            </p>
                          </div>
                          <span
                            className={`rounded-full px-3 py-1 text-xs font-semibold ${
                              statusColors[order.status]
                            }`}
                          >
                            {statusLabels[order.status]}
                          </span>
                        </div>
                        <div className="flex items-center justify-between border-t pt-2">
                          <p className="text-sm text-gray-600">
                            {order.items.length} producto{order.items.length !== 1 ? 's' : ''}
                          </p>
                          <p className="text-baby-pink font-bold">
                            ${(order.totalAmount || order.total || 0).toLocaleString('es-CO')}
                          </p>
                        </div>
                      </div>
                    ))}
                    {orders.length > 5 && (
                      <Button variant="outline" className="w-full" onClick={() => {}}>
                        Ver todas las órdenes ({orders.length})
                      </Button>
                    )}
                  </div>
                )}
              </Card>

              <Card className="mt-8 p-6">
                <h2 className="font-poppins text-baby-gray mb-6 text-xl font-bold">
                  Acciones Rápidas
                </h2>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <LinkButton href="/productos" variant="outline" className="justify-center">
                    <Package className="mr-2 size-5" />
                    Ver Productos
                  </LinkButton>
                  <LinkButton href="/carrito" variant="outline" className="justify-center">
                    <Heart className="mr-2 size-5" />
                    Ver Carrito
                  </LinkButton>
                  <LinkButton href="/contacto" variant="outline" className="justify-center">
                    <Mail className="mr-2 size-5" />
                    Contactar Soporte
                  </LinkButton>
                  <Button variant="outline" className="justify-center">
                    <Settings className="mr-2 size-5" />
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
              <Card className="from-baby-blue/5 to-baby-pink/5 border-baby-blue/20 bg-gradient-to-br p-6">
                <h3 className="font-poppins text-baby-gray mb-4 text-lg font-bold">Tu Actividad</h3>
                <div className="space-y-4">
                  {statsData.map((stat, index) => (
                    <motion.div
                      key={stat.label}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ duration: 0.4, delay: index * 0.1 }}
                      className="flex items-center justify-between rounded-lg bg-white p-3"
                    >
                      <div className="flex items-center">
                        <div className="from-baby-blue to-baby-pink mr-3 flex size-10 items-center justify-center rounded-full bg-gradient-to-br">
                          <stat.icon className="size-5 text-white" />
                        </div>
                        <span className="font-inter text-sm text-gray-600">{stat.label}</span>
                      </div>
                      <span className="font-poppins text-baby-pink text-xl font-bold">
                        {stat.value}
                      </span>
                    </motion.div>
                  ))}
                </div>
              </Card>

              <Card className="p-6">
                <h3 className="font-poppins text-baby-gray mb-4 text-lg font-bold">Tu Cuenta</h3>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Tipo de cuenta:</span>
                    <span className="text-baby-pink font-medium">Premium</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Miembro desde:</span>
                    <span className="font-medium">
                      {loadingStats ? '...' : stats?.memberSince || '2024'}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Estado:</span>
                    <span className="font-medium text-green-600">Activo</span>
                  </div>

                  {/* Email Verification Section */}
                  <div className="mt-4 border-t pt-4">
                    <div className="mb-3 flex items-center justify-between">
                      <span className="text-gray-600">Correo verificado:</span>
                      {emailVerified ? (
                        <span className="flex items-center gap-1 font-medium text-green-600">
                          <CheckCircle className="size-4" />
                          Verificado
                        </span>
                      ) : (
                        <span className="flex items-center gap-1 font-medium text-yellow-600">
                          <AlertCircle className="size-4" />
                          Pendiente
                        </span>
                      )}
                    </div>

                    {!emailVerified && (
                      <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4">
                        <p className="mb-3 text-xs text-yellow-800">
                          📧 Te enviamos un código de 6 dígitos a tu correo. Ingrésalo para
                          verificar tu cuenta.
                        </p>
                        <div className="space-y-3">
                          <Input
                            type="text"
                            placeholder="Código de 6 dígitos"
                            value={verificationCode}
                            onChange={(e) => {
                              const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                              setVerificationCode(value);
                            }}
                            className="text-center font-mono text-lg tracking-widest"
                            maxLength={6}
                          />
                          <div className="flex gap-2">
                            <Button
                              onClick={async () => {
                                if (verificationCode.length !== 6) {
                                  toast.error('El código debe tener 6 dígitos');
                                  return;
                                }
                                setIsVerifying(true);
                                try {
                                  await authService.verifyEmail(verificationCode);
                                  setEmailVerified(true);
                                  setVerificationCode('');
                                  toast.success('¡Correo verificado exitosamente!');
                                  // Update localStorage
                                  const storedUser = localStorage.getItem('baby-cash-user');
                                  if (storedUser) {
                                    const userData = JSON.parse(storedUser);
                                    userData.emailVerified = true;
                                    localStorage.setItem(
                                      'baby-cash-user',
                                      JSON.stringify(userData)
                                    );
                                  }
                                } catch (error: unknown) {
                                  const err = error as {
                                    response?: { data?: { message?: string } };
                                  };
                                  toast.error(err.response?.data?.message || 'Código inválido');
                                } finally {
                                  setIsVerifying(false);
                                }
                              }}
                              disabled={isVerifying || verificationCode.length !== 6}
                              size="sm"
                              className="flex-1 bg-green-600 text-white hover:bg-green-700"
                            >
                              {isVerifying ? 'Verificando...' : 'Verificar'}
                            </Button>
                            <Button
                              onClick={async () => {
                                setIsResending(true);
                                try {
                                  await authService.resendVerificationCode(user?.email || '');
                                  toast.success('Código reenviado a tu correo');
                                } catch (error: unknown) {
                                  const err = error as {
                                    response?: { data?: { message?: string } };
                                  };
                                  toast.error(
                                    err.response?.data?.message || 'Error al reenviar código'
                                  );
                                } finally {
                                  setIsResending(false);
                                }
                              }}
                              disabled={isResending}
                              variant="outline"
                              size="sm"
                              className="flex items-center gap-1"
                            >
                              <RefreshCw
                                className={`size-4 ${isResending ? 'animate-spin' : ''}`}
                              />
                              {isResending ? '' : 'Reenviar'}
                            </Button>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Security Section */}
                  <div className="mt-4 border-t pt-4">
                    <h4 className="mb-3 text-sm font-bold text-gray-700">Seguridad</h4>
                    <div className="space-y-2">
                      <Button
                        onClick={async () => {
                          if (
                            confirm(
                              '¿Estás seguro de que quieres cerrar sesión en todos los dispositivos?'
                            )
                          ) {
                            setIsLoggingOutAll(true);
                            try {
                              await authService.logoutAllDevices();
                              toast.success('Sesión cerrada en todos los dispositivos');
                              logout();
                              navigate('/login');
                            } catch (error: unknown) {
                              const err = error as { response?: { data?: { message?: string } } };
                              toast.error(err.response?.data?.message || 'Error al cerrar sesión');
                            } finally {
                              setIsLoggingOutAll(false);
                            }
                          }
                        }}
                        disabled={isLoggingOutAll}
                        variant="outline"
                        size="sm"
                        className="flex w-full items-center justify-center gap-2"
                      >
                        <LogOut className="size-4" />
                        {isLoggingOutAll
                          ? 'Cerrando sesión...'
                          : 'Cerrar sesión en todos los dispositivos'}
                      </Button>

                      <Button
                        onClick={() => setShowDeleteModal(true)}
                        variant="outline"
                        size="sm"
                        className="flex w-full items-center justify-center gap-2 border-red-300 text-red-600 hover:bg-red-50"
                      >
                        <Trash2 className="size-4" />
                        Eliminar cuenta
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>

              <Card className="from-baby-mint/10 to-baby-blue/10 border-baby-mint/20 bg-gradient-to-br p-6">
                <h3 className="font-poppins text-baby-gray mb-4 text-lg font-bold">
                  Programa de Fidelidad
                </h3>
                {loadingLoyalty ? (
                  <div className="flex justify-center py-8">
                    <div className="border-baby-mint size-10 animate-spin rounded-full border-y-2"></div>
                  </div>
                ) : loyaltyPoints ? (
                  <div className="space-y-4">
                    <div className="text-center">
                      <div className="from-baby-mint to-baby-blue mx-auto mb-4 flex size-20 items-center justify-center rounded-full bg-gradient-to-br">
                        <Heart className="size-10 text-white" />
                      </div>
                      <p className="mb-2 text-sm text-gray-600">Puntos disponibles</p>
                      <p className="font-poppins text-baby-mint text-3xl font-bold">
                        {loyaltyPoints.totalPoints.toLocaleString('es-CO')}
                      </p>
                      <div className="mt-2 inline-block rounded-full bg-gradient-to-r from-yellow-400 to-yellow-600 px-3 py-1">
                        <span className="text-xs font-bold text-white">{loyaltyPoints.tier}</span>
                      </div>
                    </div>

                    {/* Descuento disponible */}
                    {loyaltyPoints.availableDiscountPercent > 0 && (
                      <div className="rounded-lg border-2 border-green-400 bg-gradient-to-r from-green-50 to-emerald-50 p-4 text-center">
                        <p className="mb-1 text-xs font-medium text-green-700">
                          🎉 ¡Descuento Disponible!
                        </p>
                        <p className="text-2xl font-bold text-green-600">
                          {loyaltyPoints.availableDiscountPercent}% OFF
                        </p>
                        <p className="mt-1 text-xs text-green-600">En tu próxima compra</p>
                      </div>
                    )}

                    {/* Puntos para siguiente descuento */}
                    {loyaltyPoints.pointsForNextDiscount > 0 && (
                      <div className="rounded-lg border border-blue-200 bg-blue-50 p-3">
                        <p className="text-center text-xs text-blue-800">
                          📈 Te faltan{' '}
                          <span className="font-bold">{loyaltyPoints.pointsForNextDiscount}</span>{' '}
                          puntos para el siguiente 5% de descuento
                        </p>
                      </div>
                    )}

                    <div className="grid grid-cols-2 gap-3 border-t pt-4">
                      <div className="rounded-lg bg-white p-2 text-center">
                        <p className="text-xs text-gray-600">Este mes</p>
                        <p className="text-baby-blue text-lg font-bold">
                          +{loyaltyPoints.earnedThisMonth}
                        </p>
                      </div>
                      <div className="rounded-lg bg-white p-2 text-center">
                        <p className="text-xs text-gray-600">Total ganado</p>
                        <p className="text-baby-pink text-lg font-bold">
                          {loyaltyPoints.earnedTotal}
                        </p>
                      </div>
                    </div>

                    {loyaltyPoints.expiringSoon > 0 && (
                      <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-3">
                        <p className="text-xs text-yellow-800">
                          ⚠️ {loyaltyPoints.expiringSoon} puntos expiran pronto
                        </p>
                      </div>
                    )}

                    <Button
                      variant="outline"
                      size="sm"
                      className="mt-2 w-full"
                      onClick={() => navigate('/loyalty-history')}
                    >
                      Ver Historial
                    </Button>

                    <div className="space-y-1 rounded-lg bg-gray-50 p-3">
                      <p className="text-center text-xs text-gray-500">
                        💰 Gana 1 punto por cada $1,000 en compras
                      </p>
                      <p className="text-center text-xs text-gray-500">
                        🎁 Cada 1,000 puntos = 5% de descuento
                      </p>
                      <p className="text-center text-xs font-semibold text-gray-500">
                        ⭐ Máximo 50% de descuento (10,000 pts)
                      </p>
                    </div>
                  </div>
                ) : (
                  <div className="py-6 text-center">
                    <p className="text-sm text-gray-600">No se pudieron cargar los puntos</p>
                  </div>
                )}
              </Card>
            </motion.div>
          </div>
        </div>

        {/* Delete Account Modal */}
        {showDeleteModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl"
            >
              <div className="mb-6 text-center">
                <div className="mx-auto mb-4 flex size-16 items-center justify-center rounded-full bg-red-100">
                  <AlertCircle className="size-8 text-red-600" />
                </div>
                <h3 className="mb-2 text-2xl font-bold text-gray-900">Eliminar Cuenta</h3>
                <p className="text-sm text-gray-600">
                  ⚠️ Esta acción es <strong>permanente e irreversible</strong>
                </p>
              </div>

              <div className="space-y-4">
                {!codeSent ? (
                  <>
                    <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4">
                      <p className="text-sm text-yellow-800">
                        Se eliminarán todos tus datos, pedidos, puntos y no podrás recuperar tu
                        cuenta.
                      </p>
                    </div>
                    <Button
                      onClick={async () => {
                        setIsRequestingDeletion(true);
                        try {
                          await authService.requestAccountDeletion();
                          toast.success(
                            'Código enviado a tu correo. Revisa tu bandeja de entrada.'
                          );
                          setCodeSent(true);
                        } catch (error: unknown) {
                          const err = error as { response?: { data?: { message?: string } } };
                          toast.error(err.response?.data?.message || 'Error al solicitar código');
                        } finally {
                          setIsRequestingDeletion(false);
                        }
                      }}
                      disabled={isRequestingDeletion}
                      className="w-full bg-red-600 hover:bg-red-700"
                    >
                      {isRequestingDeletion
                        ? 'Enviando código...'
                        : 'Enviar código de confirmación'}
                    </Button>
                  </>
                ) : (
                  <>
                    <Input
                      type="text"
                      placeholder="Código de 6 dígitos"
                      value={deletionCode}
                      onChange={(e) =>
                        setDeletionCode(e.target.value.replace(/\D/g, '').slice(0, 6))
                      }
                      className="text-center font-mono text-lg tracking-widest"
                      maxLength={6}
                    />
                    <Input
                      type="password"
                      placeholder="Confirma tu contraseña"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                    <Button
                      onClick={async () => {
                        if (deletionCode.length !== 6) {
                          toast.error('El código debe tener 6 dígitos');
                          return;
                        }
                        if (!confirmPassword) {
                          toast.error('Debes confirmar tu contraseña');
                          return;
                        }
                        if (
                          confirm('¿Estás COMPLETAMENTE SEGURO? Esta acción NO se puede deshacer.')
                        ) {
                          setIsDeletingAccount(true);
                          try {
                            await authService.deleteAccount(deletionCode, confirmPassword);
                            toast.success('Cuenta eliminada exitosamente');
                            logout();
                            navigate('/');
                          } catch (error: unknown) {
                            const err = error as { response?: { data?: { message?: string } } };
                            toast.error(err.response?.data?.message || 'Error al eliminar cuenta');
                          } finally {
                            setIsDeletingAccount(false);
                          }
                        }
                      }}
                      disabled={isDeletingAccount || deletionCode.length !== 6 || !confirmPassword}
                      className="w-full bg-red-600 hover:bg-red-700"
                    >
                      {isDeletingAccount ? 'Eliminando...' : 'Confirmar eliminación'}
                    </Button>
                  </>
                )}

                <Button
                  onClick={() => {
                    setShowDeleteModal(false);
                    setDeletionCode('');
                    setConfirmPassword('');
                    setCodeSent(false);
                  }}
                  variant="outline"
                  className="w-full"
                >
                  Cancelar
                </Button>
              </div>
            </motion.div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Perfil;
