import React from 'react';
import { motion } from 'framer-motion';
import { Home, ArrowLeft, Search, Baby, MapPin } from 'lucide-react';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import LinkButton from '../components/ui/LinkButton';

const NotFound: React.FC = () => {
  const suggestedLinks = [
    { icon: Home, label: 'Inicio', href: '/', description: 'Volver a la página principal' },
    {
      icon: Baby,
      label: 'Productos',
      href: '/productos',
      description: 'Ver todos nuestros productos',
    },
    { icon: Search, label: 'Blog', href: '/blog', description: 'Consejos y tips para tu bebé' },
    {
      icon: MapPin,
      label: 'Contacto',
      href: '/contacto',
      description: 'Ponte en contacto con nosotros',
    },
  ];

  return (
    <div className="pt-24 md:pt-28 pb-20 min-h-screen bg-baby-light flex items-center justify-center">
      <div className="max-w-4xl mx-auto px-4 text-center">
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          className="mb-12"
        >
          {/* 404 Animation */}
          <div className="relative mb-8">
            <motion.div
              animate={{
                y: [0, -10, 0],
                rotate: [0, 5, -5, 0],
              }}
              transition={{
                duration: 4,
                repeat: Infinity,
                ease: 'easeInOut',
              }}
              className="text-8xl md:text-9xl font-bold font-poppins text-baby-pink/30 select-none"
            >
              404
            </motion.div>

            {/* Floating Baby Icons */}
            <motion.div
              animate={{ y: [0, -15, 0], x: [0, 10, 0] }}
              transition={{
                duration: 3,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: 0.5,
              }}
              className="absolute top-4 right-8 text-baby-blue"
            >
              <Baby className="w-12 h-12" />
            </motion.div>

            <motion.div
              animate={{ y: [0, -20, 0], x: [0, -8, 0] }}
              transition={{
                duration: 2.5,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: 1,
              }}
              className="absolute bottom-8 left-12 text-baby-mint"
            >
              <Baby className="w-8 h-8" />
            </motion.div>
          </div>

          <motion.h1
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4"
          >
            ¡Ups! Página no encontrada
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.4 }}
            className="text-xl text-gray-600 font-inter mb-8 max-w-2xl mx-auto"
          >
            Lo sentimos, la página que buscas no existe o ha sido movida. Pero no te preocupes,
            tenemos muchas otras opciones increíbles para ti y tu bebé.
          </motion.p>

          {/* Quick Actions */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.6 }}
            className="flex flex-col sm:flex-row gap-4 justify-center mb-12"
          >
            <LinkButton
              href="/"
              size="lg"
              className="bg-gradient-to-r from-baby-blue to-baby-pink text-white"
            >
              <Home className="w-5 h-5 mr-2" />
              Volver al Inicio
            </LinkButton>

            <Button onClick={() => window.history.back()} variant="outline" size="lg">
              <ArrowLeft className="w-5 h-5 mr-2" />
              Página Anterior
            </Button>
          </motion.div>
        </motion.div>

        {/* Suggested Links */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.8 }}
        >
          <h2 className="text-2xl font-bold font-poppins text-baby-gray mb-8">
            ¿Qué tal si exploramos estas opciones?
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {suggestedLinks.map((link, index) => (
              <motion.a
                key={link.href}
                href={link.href}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.9 + index * 0.1 }}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="group block"
              >
                <Card className="p-6 h-full border border-gray-200 hover:shadow-xl transition-all duration-300 hover:border-baby-blue/50 group-hover:bg-gradient-to-br group-hover:from-baby-blue/5 group-hover:to-baby-pink/5">
                  <div className="text-center">
                    <div className="w-12 h-12 mx-auto mb-4 bg-gradient-to-br from-baby-blue to-baby-pink rounded-full flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                      <link.icon className="w-6 h-6 text-white" />
                    </div>
                    <h3 className="font-bold font-poppins text-baby-gray mb-2 group-hover:text-baby-pink transition-colors duration-300">
                      {link.label}
                    </h3>
                    <p className="text-gray-600 font-inter text-sm">{link.description}</p>
                  </div>
                </Card>
              </motion.a>
            ))}
          </div>
        </motion.div>

        {/* Fun Facts */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8, delay: 1.2 }}
          className="mt-16 p-8 bg-gradient-to-r from-baby-mint/10 to-baby-blue/10 rounded-2xl"
        >
          <h3 className="text-lg font-bold font-poppins text-baby-gray mb-4">Dato Curioso</h3>
          <p className="text-gray-600 font-inter">
            Mientras encuentras lo que buscas, ¿sabías que los bebés pueden reconocer la voz de su
            mamá desde las primeras horas de vida? En Baby Cash creemos que cada detalle cuenta para
            crear esos momentos especiales.
          </p>
        </motion.div>

        {/* Contact Support */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8, delay: 1.4 }}
          className="mt-8 text-center"
        >
          <p className="text-gray-600 font-inter mb-4">
            ¿Necesitas ayuda para encontrar algo específico?
          </p>

          <LinkButton
            href="/"
            size="lg"
            className="bg-gradient-to-r from-baby-blue to-baby-pink text-white"
          >
            <Home className="w-5 h-5 mr-2" />
            Volver al Inicio
          </LinkButton>
        </motion.div>
      </div>
    </div>
  );
};

export default NotFound;
