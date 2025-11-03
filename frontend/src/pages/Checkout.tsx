import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import { orderService } from '../services/api';
import toast from 'react-hot-toast';
import { CreditCard, Banknote, Building2, Wallet, Package } from 'lucide-react';
import { logger } from '../utils/logger';

interface ShippingInfo {
  fullName: string;
  phone: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  notes?: string;
}

type PaymentMethod = 'credit_card' | 'debit_card' | 'pse' | 'cash_on_delivery';

interface ValidationErrors {
  [key: string]: string;
}

const Checkout = () => {
  const navigate = useNavigate();
  const { items, getTotalPrice, clearCart } = useCart();
  const { isAuthenticated } = useAuth();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const orderCreated = useRef(false); // Flag para evitar alert de carrito vacío

  const [shippingInfo, setShippingInfo] = useState<ShippingInfo>({
    fullName: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    notes: '',
  });

  const [errors, setErrors] = useState<ValidationErrors>({});
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('credit_card');

  useEffect(() => {
    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para realizar una compra');
      navigate('/login');
      return;
    }

    // Solo mostrar alerta si el carrito está vacío Y NO se acaba de crear una orden
    if (items.length === 0 && !orderCreated.current) {
      toast.error('Tu carrito está vacío');
      navigate('/productos');
    }
  }, [isAuthenticated, items, navigate]);

  const handleShippingChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setShippingInfo((prev) => ({ ...prev, [name]: value }));
    
    // Validar en tiempo real
    if (name !== 'notes') { // 'notes' es opcional
      const error = validateField(name, value);
      setErrors((prev) => ({ ...prev, [name]: error }));
    }
  };

  // Validadores por campo (reduce complejidad cognitiva)
  const fieldValidators: Record<string, (value: string) => string> = {
    fullName: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (value.trim().length < 3) return 'El nombre debe tener al menos 3 caracteres';
      if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(value)) return 'Solo se permiten letras';
      return '';
    },
    phone: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (!/^\+?[\d\s\-()]{7,}$/.test(value)) return 'Formato de teléfono inválido';
      return '';
    },
    address: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (value.trim().length < 10) return 'La dirección debe ser más específica';
      return '';
    },
    city: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (value.trim().length < 3) return 'El nombre de la ciudad es inválido';
      if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(value)) return 'Solo se permiten letras';
      return '';
    },
    state: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (value.trim().length < 3) return 'El nombre del departamento es inválido';
      return '';
    },
    zipCode: (value) => {
      if (!value.trim()) return 'Este campo es obligatorio';
      if (!/^\d{4,6}$/.test(value)) return 'Código postal inválido (4-6 dígitos)';
      return '';
    },
  };

  const validateField = (name: string, value: string): string => {
    const validator = fieldValidators[name];
    return validator ? validator(value) : '';
  };

  const validateShipping = (): boolean => {
    const required = ['fullName', 'phone', 'address', 'city', 'state', 'zipCode'];
    const newErrors: ValidationErrors = {};
    let isValid = true;

    for (const field of required) {
      const value = shippingInfo[field as keyof ShippingInfo] || '';
      const error = validateField(field, value);
      if (error) {
        newErrors[field] = error;
        isValid = false;
      }
    }

    setErrors(newErrors);
    
    if (!isValid) {
      toast.error('Por favor corrige los errores en el formulario');
    }
    
    return isValid;
  };

  const handleNextStep = () => {
    // Solo validar envío cuando estamos en el paso 2 (información de envío)
    if (step === 2 && !validateShipping()) return;
    setStep((prev) => prev + 1);
  };

  const handlePreviousStep = () => {
    setStep((prev) => prev - 1);
  };

  const handlePlaceOrder = async () => {
    try {
      setLoading(true);
      
      // Validar información de envío antes de proceder
      if (!validateShipping()) {
        setLoading(false);
        return;
      }
      
      // Formatear la dirección de envío como un string para el backend
      const formattedAddress = `${shippingInfo.fullName} | Tel: ${shippingInfo.phone} | ${shippingInfo.address}, ${shippingInfo.city}, ${shippingInfo.state}, CP: ${shippingInfo.zipCode}`;
      
      // Crear la orden en el backend con la información completa
      const order = await orderService.createOrder({
        shippingAddress: formattedAddress,
        notes: shippingInfo.notes || `Método de pago: ${paymentMethod}`,
        items: items.map(item => ({
          productId: Number(item.id),
          quantity: item.quantity
        }))
      });
      
      toast.success('¡Orden creada exitosamente!');
      
      // Marcar que se creó una orden para evitar alert de carrito vacío
      orderCreated.current = true;
      
      // Limpiar carrito DESPUÉS de guardar el orderId
      const orderId = order.id;
      clearCart();
      
      // Redirigir a la página de confirmación
      navigate(`/order-confirmation/${orderId}`);
    } catch (error: any) {
      logger.error('Error al crear orden:', error);
      toast.error(error.response?.data?.message || 'Error al procesar la orden');
    } finally {
      setLoading(false);
    }
  };

  const totalPrice = getTotalPrice();
  const shippingCost = totalPrice > 100000 ? 0 : 10000;
  const tax = totalPrice * 0.19; // IVA 19%
  const finalTotal = totalPrice + shippingCost + tax;

  if (items.length === 0) return null;

  return (
    <div className="min-h-screen bg-baby-cream py-8">
      <div className="container mx-auto px-4 max-w-6xl">
        <h1 className="text-4xl font-bold text-baby-blue mb-8">Checkout</h1>

        {/* Progress Steps */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            {[
              { num: 1, label: 'Carrito' },
              { num: 2, label: 'Envío' },
              { num: 3, label: 'Pago' },
              { num: 4, label: 'Confirmar' }
            ].map((s, idx) => (
              <div key={s.num} className="flex flex-col items-center flex-1">
                <div className="flex items-center w-full">
                  {idx > 0 && (
                    <div
                      className={`flex-1 h-1 ${
                        s.num <= step ? 'bg-baby-blue' : 'bg-gray-300'
                      }`}
                    />
                  )}
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${
                      s.num <= step
                        ? 'bg-baby-blue text-white'
                        : 'bg-gray-300 text-gray-600'
                    }`}
                  >
                    {s.num}
                  </div>
                  {idx < 3 && (
                    <div
                      className={`flex-1 h-1 ${
                        s.num < step ? 'bg-baby-blue' : 'bg-gray-300'
                      }`}
                    />
                  )}
                </div>
                <span className={`mt-2 text-sm ${step === s.num ? 'font-bold text-baby-blue' : 'text-gray-600'}`}>
                  {s.label}
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-xl shadow-lg p-6">
              {/* Step 1: Review Cart */}
              {step === 1 && (
                <div>
                  <h2 className="text-2xl font-bold mb-4">Revisa tu carrito</h2>
                  <div className="space-y-4">
                    {items.map((item) => (
                      <div
                        key={item.id}
                        className="flex items-center gap-4 border-b pb-4"
                      >
                        {item.image && (
                          <img
                            src={item.image}
                            alt={item.name}
                            className="w-20 h-20 object-cover rounded"
                          />
                        )}
                        <div className="flex-1">
                          <h3 className="font-semibold">{item.name}</h3>
                          <p className="text-sm text-gray-600">Cantidad: {item.quantity}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-bold text-baby-pink">
                            ${(item.price * item.quantity).toLocaleString('es-CO')}
                          </p>
                        </div>
                      </div>
                    ))}
                  </div>
                  <button
                    onClick={handleNextStep}
                    className="w-full mt-6 bg-baby-blue text-white py-3 rounded-lg font-semibold hover:bg-baby-blue/90 transition"
                  >
                    Continuar al envío
                  </button>
                </div>
              )}

              {/* Step 2: Shipping Info */}
              {step === 2 && (
                <div>
                  <h2 className="text-2xl font-bold mb-4">Información de envío</h2>
                  <form className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label htmlFor="fullName" className="block text-sm font-medium mb-1">
                          Nombre completo *
                        </label>
                        <input
                          id="fullName"
                          type="text"
                          name="fullName"
                          value={shippingInfo.fullName}
                          onChange={handleShippingChange}
                          className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                            errors.fullName 
                              ? 'border-red-500 focus:ring-red-500' 
                              : 'focus:ring-baby-blue'
                          }`}
                          required
                        />
                        {errors.fullName && (
                          <p className="text-red-500 text-xs mt-1">{errors.fullName}</p>
                        )}
                      </div>
                      <div>
                        <label htmlFor="phone" className="block text-sm font-medium mb-1">
                          Teléfono *
                        </label>
                        <input
                          id="phone"
                          type="tel"
                          name="phone"
                          value={shippingInfo.phone}
                          onChange={handleShippingChange}
                          placeholder="+57 300 1234567"
                          className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                            errors.phone 
                              ? 'border-red-500 focus:ring-red-500' 
                              : 'focus:ring-baby-blue'
                          }`}
                          required
                        />
                        {errors.phone && (
                          <p className="text-red-500 text-xs mt-1">{errors.phone}</p>
                        )}
                      </div>
                    </div>
                    <div>
                      <label htmlFor="address" className="block text-sm font-medium mb-1">
                        Dirección *
                      </label>
                      <input
                        id="address"
                        type="text"
                        name="address"
                        value={shippingInfo.address}
                        onChange={handleShippingChange}
                        placeholder="Calle 123 #45-67, Apto 101"
                        className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                          errors.address 
                            ? 'border-red-500 focus:ring-red-500' 
                            : 'focus:ring-baby-blue'
                        }`}
                        required
                      />
                      {errors.address && (
                        <p className="text-red-500 text-xs mt-1">{errors.address}</p>
                      )}
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div>
                        <label htmlFor="city" className="block text-sm font-medium mb-1">
                          Ciudad *
                        </label>
                        <input
                          id="city"
                          type="text"
                          name="city"
                          value={shippingInfo.city}
                          onChange={handleShippingChange}
                          placeholder="Bogotá"
                          className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                            errors.city 
                              ? 'border-red-500 focus:ring-red-500' 
                              : 'focus:ring-baby-blue'
                          }`}
                          required
                        />
                        {errors.city && (
                          <p className="text-red-500 text-xs mt-1">{errors.city}</p>
                        )}
                      </div>
                      <div>
                        <label htmlFor="state" className="block text-sm font-medium mb-1">
                          Departamento *
                        </label>
                        <input
                          id="state"
                          type="text"
                          name="state"
                          value={shippingInfo.state}
                          onChange={handleShippingChange}
                          placeholder="Cundinamarca"
                          className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                            errors.state 
                              ? 'border-red-500 focus:ring-red-500' 
                              : 'focus:ring-baby-blue'
                          }`}
                          required
                        />
                        {errors.state && (
                          <p className="text-red-500 text-xs mt-1">{errors.state}</p>
                        )}
                      </div>
                      <div>
                        <label htmlFor="zipCode" className="block text-sm font-medium mb-1">
                          Código Postal *
                        </label>
                        <input
                          id="zipCode"
                          type="text"
                          name="zipCode"
                          value={shippingInfo.zipCode}
                          onChange={handleShippingChange}
                          placeholder="110111"
                          className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 ${
                            errors.zipCode 
                              ? 'border-red-500 focus:ring-red-500' 
                              : 'focus:ring-baby-blue'
                          }`}
                          required
                        />
                        {errors.zipCode && (
                          <p className="text-red-500 text-xs mt-1">{errors.zipCode}</p>
                        )}
                      </div>
                    </div>
                    <div>
                      <label htmlFor="notes" className="block text-sm font-medium mb-1">
                        Notas de entrega (opcional)
                      </label>
                      <textarea
                        id="notes"
                        name="notes"
                        value={shippingInfo.notes}
                        onChange={handleShippingChange}
                        rows={3}
                        className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                      />
                    </div>
                  </form>
                  <div className="flex gap-4 mt-6">
                    <button
                      onClick={handlePreviousStep}
                      className="flex-1 border-2 border-baby-blue text-baby-blue py-3 rounded-lg font-semibold hover:bg-baby-blue/10 transition"
                    >
                      Volver
                    </button>
                    <button
                      onClick={handleNextStep}
                      className="flex-1 bg-baby-blue text-white py-3 rounded-lg font-semibold hover:bg-baby-blue/90 transition"
                    >
                      Continuar al pago
                    </button>
                  </div>
                </div>
              )}

              {/* Step 3: Payment Method */}
              {step === 3 && (
                <div>
                  <h2 className="text-2xl font-bold mb-4">Método de pago</h2>
                  <div className="space-y-4">
                    {[
                      { 
                        id: 'credit_card', 
                        label: 'Tarjeta de Crédito', 
                        icon: CreditCard,
                        color: 'text-blue-600'
                      },
                      { 
                        id: 'debit_card', 
                        label: 'Tarjeta Débito', 
                        icon: Banknote,
                        color: 'text-green-600'
                      },
                      { 
                        id: 'pse', 
                        label: 'PSE', 
                        icon: Building2,
                        color: 'text-purple-600'
                      },
                      { 
                        id: 'cash_on_delivery', 
                        label: 'Pago Contra Entrega', 
                        icon: Wallet,
                        color: 'text-orange-600'
                      },
                    ].map((method) => {
                      const Icon = method.icon;
                      return (
                        <label
                          key={method.id}
                          className={`flex items-center gap-4 p-4 border-2 rounded-lg cursor-pointer transition ${
                            paymentMethod === method.id
                              ? 'border-baby-blue bg-baby-blue/10'
                              : 'border-gray-200 hover:border-baby-blue/50'
                          }`}
                        >
                          <input
                            type="radio"
                            name="paymentMethod"
                            value={method.id}
                            checked={paymentMethod === method.id}
                            onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                            className="w-5 h-5"
                          />
                          <Icon className={`w-6 h-6 ${method.color}`} />
                          <span className="font-semibold">{method.label}</span>
                        </label>
                      );
                    })}
                  </div>
                  <div className="flex gap-4 mt-6">
                    <button
                      onClick={handlePreviousStep}
                      className="flex-1 border-2 border-baby-blue text-baby-blue py-3 rounded-lg font-semibold hover:bg-baby-blue/10 transition"
                    >
                      Volver
                    </button>
                    <button
                      onClick={handleNextStep}
                      className="flex-1 bg-baby-blue text-white py-3 rounded-lg font-semibold hover:bg-baby-blue/90 transition"
                    >
                      Revisar orden
                    </button>
                  </div>
                </div>
              )}

              {/* Step 4: Confirmation */}
              {step === 4 && (
                <div>
                  <h2 className="text-2xl font-bold mb-4">Confirma tu orden</h2>
                  
                  <div className="space-y-6">
                    {/* Shipping Info Summary */}
                    <div className="border-b pb-4">
                      <h3 className="font-semibold mb-2 flex items-center gap-2">
                        <Package className="w-5 h-5 text-baby-blue" />
                        Envío a:
                      </h3>
                      <p>{shippingInfo.fullName}</p>
                      <p>{shippingInfo.address}</p>
                      <p>
                        {shippingInfo.city}, {shippingInfo.state} - {shippingInfo.zipCode}
                      </p>
                      <p>{shippingInfo.phone}</p>
                      {shippingInfo.notes && (
                        <p className="text-sm text-gray-600 mt-2">
                          Notas: {shippingInfo.notes}
                        </p>
                      )}
                    </div>

                    {/* Payment Method Summary */}
                    <div className="border-b pb-4">
                      <h3 className="font-semibold mb-2 flex items-center gap-2">
                        <CreditCard className="w-5 h-5 text-baby-blue" />
                        Método de pago:
                      </h3>
                      <p>
                        {paymentMethod === 'credit_card' && 'Tarjeta de Crédito'}
                        {paymentMethod === 'debit_card' && 'Tarjeta Débito'}
                        {paymentMethod === 'pse' && 'PSE'}
                        {paymentMethod === 'cash_on_delivery' && 'Pago Contra Entrega'}
                      </p>
                    </div>

                    {/* Items Summary */}
                    <div>
                      <h3 className="font-semibold mb-2">🛍️ Productos:</h3>
                      <div className="space-y-2">
                        {items.map((item) => (
                          <div
                            key={item.id}
                            className="flex justify-between text-sm"
                          >
                            <span>
                              {item.name} x{item.quantity}
                            </span>
                            <span className="font-medium">
                              ${(item.price * item.quantity).toLocaleString('es-CO')}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>

                  <div className="flex gap-4 mt-6">
                    <button
                      onClick={handlePreviousStep}
                      className="flex-1 border-2 border-baby-blue text-baby-blue py-3 rounded-lg font-semibold hover:bg-baby-blue/10 transition"
                    >
                      Volver
                    </button>
                    <button
                      onClick={handlePlaceOrder}
                      disabled={loading}
                      className="flex-1 bg-baby-pink text-white py-3 rounded-lg font-semibold hover:bg-baby-pink/90 transition disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {loading ? 'Procesando...' : 'Confirmar y pagar'}
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Order Summary Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-lg p-6 sticky top-4">
              <h3 className="text-xl font-bold mb-4">Resumen de orden</h3>
              <div className="space-y-3 border-b pb-4 mb-4">
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span className="font-semibold">
                    ${totalPrice.toLocaleString('es-CO')}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>Envío</span>
                  <span className="font-semibold">
                    {shippingCost === 0 ? (
                      <span className="text-green-600">¡Gratis!</span>
                    ) : (
                      `$${shippingCost.toLocaleString('es-CO')}`
                    )}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>IVA (19%)</span>
                  <span className="font-semibold">
                    ${tax.toLocaleString('es-CO')}
                  </span>
                </div>
              </div>
              <div className="flex justify-between text-xl font-bold">
                <span>Total</span>
                <span className="text-baby-pink">
                  ${finalTotal.toLocaleString('es-CO')}
                </span>
              </div>
              {totalPrice < 100000 && (
                <p className="text-sm text-gray-600 mt-4">
                  💡 ¡Agrega ${(100000 - totalPrice).toLocaleString('es-CO')} más para envío
                  gratis!
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
