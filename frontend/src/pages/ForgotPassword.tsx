// src/pages/ForgotPassword.tsx
import { useState } from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { authService } from '../services/api';

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setError('');
    setIsLoading(true);

    try {
      await authService.forgotPassword(email);
      setEmailSent(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al enviar el email');
    } finally {
      setIsLoading(false);
    }
  };

  if (emailSent) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-baby-light to-baby-pink p-6 pt-20">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          className="bg-white/80 backdrop-blur-lg shadow-2xl rounded-3xl p-8 w-full max-w-md text-center"
        >
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg
              className="w-10 h-10 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-baby-dark mb-4">¡Correo enviado!</h2>
          <p className="text-gray-600 mb-6">
            Hemos enviado las instrucciones para restablecer tu contraseña a <strong>{email}</strong>.
            Revisa tu bandeja de entrada y sigue los pasos indicados.
          </p>
          <Link
            to="/login"
            className="inline-block w-full bg-baby-blue text-white py-3 rounded-xl font-bold shadow-lg hover:bg-baby-blue/90 transition-colors"
          >
            Volver a iniciar sesión
          </Link>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-baby-light to-baby-pink p-6 pt-20">
      <motion.div
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="bg-white/80 backdrop-blur-lg shadow-2xl rounded-3xl p-8 w-full max-w-md"
      >
        {/* Encabezado */}
        <motion.h2
          initial={{ opacity: 0, x: -40 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
          className="text-3xl font-bold text-center text-baby-dark mb-2"
        >
          ¿Olvidaste tu contraseña?
        </motion.h2>
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4 }}
          className="text-center text-gray-600 mb-6"
        >
          No te preocupes, ingresa tu correo electrónico y te enviaremos instrucciones para
          restablecerla.
        </motion.p>

        {/* Formulario */}
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Error message */}
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-red-50 border-l-4 border-red-400 p-4 rounded-lg"
            >
              <p className="text-sm text-red-700">{error}</p>
            </motion.div>
          )}

          {/* Email */}
          <motion.div
            initial={{ opacity: 0, x: -40 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.6 }}
          >
            <label htmlFor="email" className="block text-gray-700 font-semibold mb-2">
              Correo electrónico
            </label>
            <input
              type="email"
              id="email"
              placeholder="ejemplo@correo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-baby-dark focus:outline-none transition duration-300"
              required
              disabled={isLoading}
            />
          </motion.div>

          {/* Botón */}
          <motion.button
            type="submit"
            whileHover={{ scale: isLoading ? 1 : 1.05 }}
            whileTap={{ scale: isLoading ? 1 : 0.95 }}
            disabled={isLoading}
            className="w-full bg-baby-pink text-white py-3 rounded-xl font-bold shadow-lg transition-colors duration-300 hover:bg-baby-pink/90 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Enviando...' : 'Enviar instrucciones'}
          </motion.button>
        </form>

        {/* Enlaces */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
          className="text-center mt-6 space-y-2"
        >
          <p className="text-gray-600">
            ¿Recordaste tu contraseña?{' '}
            <Link to="/login" className="text-baby-blue font-semibold hover:underline">
              Inicia sesión
            </Link>
          </p>
          <p className="text-gray-600">
            ¿No tienes cuenta aún?{' '}
            <Link to="/register" className="text-baby-blue font-semibold hover:underline">
              Regístrate
            </Link>
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default ForgotPassword;
