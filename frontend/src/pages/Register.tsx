// src/pages/Register.tsx
import { motion } from 'framer-motion';
import { Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { logger } from '../utils/logger';

const Register: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    if (password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres.');
      return;
    }

    if (!acceptedTerms) {
      setError('Debes aceptar los términos y el tratamiento de datos.');
      return;
    }

    setLoading(true);
    try {
      await register({
        email,
        password,
        firstName,
        lastName,
        phone: phone || undefined,
      });

      // Redirigir al home después de registro exitoso
      setTimeout(() => navigate('/'), 100);
    } catch (err: unknown) {
      logger.error('Error en registro:', err);
      // El error ya se muestra con toast en AuthContext
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="from-baby-blue via-baby-pink to-baby-mint flex min-h-screen items-center justify-center bg-gradient-to-br px-4 pb-20 pt-24 md:pb-20 md:pt-28 ">
      <motion.div
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl"
      >
        {/* Logo */}
        <div className="mb-6 flex justify-center">
          <img
            src="/productos/icono-pinguino.png"
            alt="Logo Pingüino"
            className="size-20 object-contain"
          />
        </div>

        {/* Título */}
        <h2 className="mb-6 text-center text-2xl font-bold text-gray-800">
          Regístrate en <span className="text-baby-blue">Baby Cash</span>
        </h2>

        {/* Error */}
        {error && <p className="mb-4 text-center text-sm text-red-500">{error}</p>}

        {/* Formulario */}
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label htmlFor="firstName" className="mb-1 block text-sm font-medium text-gray-700">
              Nombre <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="firstName"
              placeholder="Tu nombre"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
              className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 shadow-sm focus:outline-none focus:ring-2"
            />
          </div>

          <div>
            <label htmlFor="lastName" className="mb-1 block text-sm font-medium text-gray-700">
              Apellido <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="lastName"
              placeholder="Tu apellido"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              required
              className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 shadow-sm focus:outline-none focus:ring-2"
            />
          </div>

          <div>
            <label htmlFor="email" className="mb-1 block text-sm font-medium text-gray-700">
              Correo electrónico <span className="text-red-500">*</span>
            </label>
            <input
              type="email"
              id="email"
              placeholder="ejemplo@correo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 shadow-sm focus:outline-none focus:ring-2"
            />
          </div>

          <div>
            <label htmlFor="phone" className="mb-1 block text-sm font-medium text-gray-700">
              Teléfono (opcional)
            </label>
            <input
              type="tel"
              id="phone"
              placeholder="3001234567"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              pattern="[0-9]{10}"
              className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 shadow-sm focus:outline-none focus:ring-2"
            />
          </div>

          <div>
            <label htmlFor="password" className="mb-1 block text-sm font-medium text-gray-700">
              Contraseña <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                placeholder="********"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 pr-10 shadow-sm focus:outline-none focus:ring-2"
              />
              <button
                type="button"
                tabIndex={-1}
                className="hover:text-baby-blue absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 focus:outline-none"
                onClick={() => setShowPassword((v) => !v)}
                aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          <div>
            <label
              htmlFor="confirmPassword"
              className="mb-1 block text-sm font-medium text-gray-700"
            >
              Confirmar contraseña <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type={showConfirm ? 'text' : 'password'}
                id="confirmPassword"
                placeholder="********"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                className="focus:ring-baby-blue w-full rounded-lg border border-gray-300 px-4 py-2 pr-10 shadow-sm focus:outline-none focus:ring-2"
              />
              <button
                type="button"
                tabIndex={-1}
                className="hover:text-baby-blue absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 focus:outline-none"
                onClick={() => setShowConfirm((v) => !v)}
                aria-label={showConfirm ? 'Ocultar contraseña' : 'Mostrar contraseña'}
              >
                {showConfirm ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          {/* Checkbox */}
          <div className="flex items-start space-x-2">
            <input
              type="checkbox"
              id="terms"
              checked={acceptedTerms}
              onChange={(e) => setAcceptedTerms(e.target.checked)}
              className="mt-1"
            />
            <label htmlFor="terms" className="text-sm text-gray-600">
              Acepto los{' '}
              <Link to="/terminos" className="text-baby-blue font-medium hover:underline">
                términos y condiciones
              </Link>{' '}
              y la{' '}
              <Link to="/tratamiento-datos" className="text-baby-blue font-medium hover:underline">
                política de tratamiento de datos
              </Link>
              .
            </label>
          </div>

          {/* Botón */}
          <motion.button
            whileTap={{ scale: 0.95 }}
            type="submit"
            className="bg-baby-blue hover:bg-baby-pink w-full rounded-lg py-2 font-semibold text-white shadow-md transition disabled:opacity-60"
            disabled={loading}
          >
            {loading ? 'Registrando...' : 'Registrarme'}
          </motion.button>
        </form>

        {/* Links */}
        <div className="mt-6 text-center text-sm text-gray-600">
          <p>
            ¿Ya tienes cuenta?{' '}
            <Link to="/login" className="text-baby-blue font-medium hover:underline">
              Inicia sesión
            </Link>
          </p>
          <p className="mt-2">
            <Link to="/forgot-password" className="text-baby-pink font-medium hover:underline">
              ¿Olvidaste tu contraseña?
            </Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
};

export default Register;
