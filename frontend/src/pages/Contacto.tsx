import React, { useRef, useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import toast from 'react-hot-toast';
import L, { Map as LeafletMap } from 'leaflet';
import 'leaflet/dist/leaflet.css';
import {
  Phone,
  Mail,
  MessageCircle,
  MapPin,
  Clock,
  Send,
  User,
  MessageSquare,
  ExternalLink,
} from 'lucide-react';

import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';
import customMapIconUrl from '/productos/icono-pinguino.png';
import { contactMessageService, handleApiError } from '../services/api';

const customMapIcon = L.icon({
  iconUrl: customMapIconUrl,
  iconRetinaUrl: customMapIconUrl,
  iconSize: [40, 40],
  iconAnchor: [20, 40],
  popupAnchor: [0, -40],
  shadowUrl: shadowUrl,
  shadowSize: [40, 40],
  shadowAnchor: [15, 40],
});

L.Icon.Default.mergeOptions({
  iconRetinaUrl,
  iconUrl: customMapIconUrl,
  shadowUrl,
});

const contactSchema = z.object({
  name: z
    .string()
    .min(2, 'El nombre debe tener al menos 2 caracteres')
    .max(50, 'El nombre no puede exceder 50 caracteres'),
  email: z.string().email('Ingresa un email válido').min(5, 'El email es muy corto'),
  phone: z
    .string()
    .optional()
    .refine((val) => !val || /^[+]?[\d\s-()]+$/.test(val), 'Formato de teléfono inválido'),
  subject: z
    .string()
    .min(5, 'El asunto debe tener al menos 5 caracteres')
    .max(100, 'El asunto no puede exceder 100 caracteres'),
  message: z
    .string()
    .min(10, 'El mensaje debe tener al menos 10 caracteres')
    .max(500, 'El mensaje no puede exceder 500 caracteres'),
});

type ContactFormData = z.infer<typeof contactSchema>;

const Contacto: React.FC = () => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<LeafletMap | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isValid },
  } = useForm<ContactFormData>({
    resolver: zodResolver(contactSchema),
    mode: 'onChange',
  });

  useEffect(() => {
    const bogotaCoords: [number, number] = [4.606734, -74.072235];

    if (mapContainerRef.current && !mapInstanceRef.current) {
      const map = L.map(mapContainerRef.current).setView(bogotaCoords, 16);
      mapInstanceRef.current = map;

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution:
          '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      }).addTo(map);

      L.marker(bogotaCoords, { icon: customMapIcon })
        .addTo(map)
        .bindPopup("<b>BABY CASH</b><br>Pañalera y variedades Soffy's")
        .openPopup();
    }

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  const contactInfo = [
    {
      icon: Phone,
      label: 'Teléfono',
      value: '+57 3219297605',
      href: 'tel:+57 3219297605',
      description: 'Línea directa para atención inmediata',
    },
    {
      icon: MessageCircle,
      label: 'WhatsApp',
      value: '+57 3219297605',
      href: `https://wa.me/573001234567?text=Hola%20BABY%20CASH,%20necesito%20información`,
      description: 'Chat directo para consultas rápidas',
    },
    {
      icon: Mail,
      label: 'Email',
      value: 'mazoanas09@gmail.com',
      href: 'mazoanas09@gmail.com',
      description: 'Para consultas detalladas y cotizaciones',
    },
    {
      icon: MapPin,
      label: 'Dirección',
      value: 'Calle 2 #8-49 CRUCES',
      href: '#mapa',
      description: 'Visítanos en nuestro punto físico',
    },
  ];

  const schedule = [
    { day: 'Lunes - Viernes', time: '8:00 AM - 6:00 PM' },
    { day: 'Sábados', time: '9:00 AM - 4:00 PM' },
    { day: 'Domingos', time: 'Cerrado' },
  ];

  const onSubmit = async (data: ContactFormData) => {
    setIsSubmitting(true);

    try {
      // Enviar mensaje a través de la API
      await contactMessageService.sendMessage({
        name: data.name,
        email: data.email,
        phone: data.phone,
        subject: data.subject,
        message: data.message,
      });

      toast.success(
        '¡Mensaje enviado exitosamente! Te contactaremos pronto. Revisa tu email para la confirmación.',
        {
          duration: 5000,
          style: {
            background: '#C8F7DC',
            color: '#1F2937',
          },
        }
      );
      reset();
    } catch (error) {
      const errorMessage = handleApiError(error);
      toast.error(`Error al enviar el mensaje: ${errorMessage}`, {
        duration: 4000,
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2,
        delayChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 30 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.6, ease: 'easeOut' },
    },
  };

  return (
    <div className="min-h-screen bg-baby-light pt-24 md:pt-28">
      <section className="py-20 bg-baby-light">
        <div className="container mx-auto px-4 lg:px-8">
          <motion.div
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="text-center max-w-3xl mx-auto"
          >
            <motion.h1
              variants={itemVariants}
              className="text-4xl lg:text-5xl font-poppins font-bold text-baby-gray mb-6"
            >
              Contáctanos
            </motion.h1>
            <motion.p variants={itemVariants} className="text-xl text-baby-gray leading-relaxed">
              Estamos aquí para ayudarte. Escríbenos, llámanos o visítanos. Tu bebé y tu
              tranquilidad son nuestra prioridad.
            </motion.p>
          </motion.div>
        </div>
      </section>

      <div className="container mx-auto px-4 lg:px-8 py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <motion.div
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            variants={containerVariants}
            className="space-y-8"
          >
            <motion.div variants={itemVariants}>
              <h2 className="text-2xl lg:text-3xl font-poppins font-bold text-baby-gray mb-6">
                Información de Contacto
              </h2>
              <p className="text-baby-gray leading-relaxed mb-8">
                Múltiples formas de contactarnos para brindarte la mejor atención personalizada.
              </p>
            </motion.div>

            <div className="space-y-6">
              {contactInfo.map((contact, index) => {
                const Icon = contact.icon;
                return (
                  <motion.a
                    key={index}
                    variants={itemVariants}
                    href={contact.href}
                    target={contact.href.startsWith('http') ? '_blank' : undefined}
                    rel={contact.href.startsWith('http') ? 'noopener noreferrer' : undefined}
                    className="group flex items-start space-x-4 p-4 bg-white rounded-2xl border border-baby-blue hover:border-2 hover:border-baby-blue hover:shadow-lg transition-all duration-300"
                  >
                    <div className="flex-shrink-0 p-3 bg-baby-blue/20 text-baby-blue rounded-xl group-hover:bg-baby-blue/30 transition-colors">
                      <Icon size={24} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-poppins font-semibold text-baby-gray group-hover:text-baby-blue transition-colors">
                        {contact.label}
                      </h3>
                      <p className="text-lg font-medium text-baby-gray mt-1">{contact.value}</p>
                      <p className="text-sm text-baby-gray mt-1">{contact.description}</p>
                    </div>
                    {contact.href.startsWith('http') && (
                      <ExternalLink
                        size={16}
                        className="text-baby-gray group-hover:text-baby-blue transition-colors flex-shrink-0 mt-1"
                      />
                    )}
                  </motion.a>
                );
              })}
            </div>

            <motion.div
              variants={itemVariants}
              className="bg-baby-light rounded-2xl p-6 border border-baby-blue"
            >
              <div className="flex items-center space-x-2 mb-4">
                <Clock className="text-baby-blue" size={24} />
                <h3 className="font-poppins font-semibold text-baby-gray text-lg">
                  Horarios de Atención
                </h3>
              </div>
              <div className="space-y-3">
                {schedule.map((item, index) => (
                  <div key={index} className="flex justify-between items-center">
                    <span className="text-baby-gray font-medium">{item.day}</span>
                    <span className="text-baby-gray">{item.time}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          </motion.div>

          <motion.div
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            variants={containerVariants}
            className="bg-white rounded-2xl shadow-lg border border-baby-blue p-8"
          >
            <motion.div variants={itemVariants} className="mb-8">
              <h2 className="text-2xl font-poppins font-bold text-baby-gray mb-2">
                Envíanos un Mensaje
              </h2>
              <p className="text-baby-gray">
                Completa el formulario y te responderemos en menos de 24 horas.
              </p>
            </motion.div>

            <motion.form
              variants={itemVariants}
              onSubmit={handleSubmit(onSubmit)}
              className="space-y-6"
            >
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-baby-gray mb-2">
                  <User size={16} className="inline mr-1" />
                  Nombre completo *
                </label>
                <input
                  {...register('name')}
                  type="text"
                  id="name"
                  className={`w-full px-4 py-3 rounded-xl border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 ${
                    errors.name
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500/20'
                      : 'border-gray-200 focus:border-baby-blue focus:ring-baby-blue/20'
                  }`}
                  placeholder="Tu nombre completo"
                />
                {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
              </div>

              <div>
                <label htmlFor="email" className="block text-sm font-medium text-baby-gray mb-2">
                  <Mail size={16} className="inline mr-1" />
                  Email *
                </label>
                <input
                  {...register('email')}
                  type="email"
                  id="email"
                  className={`w-full px-4 py-3 rounded-xl border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 ${
                    errors.email
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500/20'
                      : 'border-gray-200 focus:border-baby-blue focus:ring-baby-blue/20'
                  }`}
                  placeholder="tu@email.com"
                />
                {errors.email && (
                  <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="phone" className="block text-sm font-medium text-baby-gray mb-2">
                  <Phone size={16} className="inline mr-1" />
                  Teléfono (opcional)
                </label>
                <input
                  {...register('phone')}
                  type="tel"
                  id="phone"
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-baby-blue focus:ring-2 focus:ring-baby-blue/20 focus:outline-none transition-all duration-200"
                  placeholder="+57 300 123 4567"
                />
                {errors.phone && (
                  <p className="mt-1 text-sm text-red-600">{errors.phone.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="subject" className="block text-sm font-medium text-baby-gray mb-2">
                  <MessageSquare size={16} className="inline mr-1" />
                  Asunto *
                </label>
                <input
                  {...register('subject')}
                  type="text"
                  id="subject"
                  className={`w-full px-4 py-3 rounded-xl border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 ${
                    errors.subject
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500/20'
                      : 'border-gray-200 focus:border-baby-blue focus:ring-baby-blue/20'
                  }`}
                  placeholder="¿En qué podemos ayudarte?"
                />
                {errors.subject && (
                  <p className="mt-1 text-sm text-red-600">{errors.subject.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="message" className="block text-sm font-medium text-baby-gray mb-2">
                  Mensaje *
                </label>
                <textarea
                  {...register('message')}
                  id="message"
                  rows={5}
                  className={`w-full px-4 py-3 rounded-xl border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 resize-none ${
                    errors.message
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-500/20'
                      : 'border-gray-200 focus:border-baby-blue focus:ring-baby-blue/20'
                  }`}
                  placeholder="Escribe tu mensaje aquí. Cuéntanos qué necesitas y cómo podemos ayudarte..."
                />
                {errors.message && (
                  <p className="mt-1 text-sm text-red-600">{errors.message.message}</p>
                )}
              </div>

              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                disabled={!isValid || isSubmitting}
                className={`w-full flex items-center justify-center space-x-2 px-6 py-4 rounded-xl font-semibold text-white transition-all duration-200 focus:outline-none focus:ring-4 focus:ring-offset-2
                  ${
                    isSubmitting
                      ? 'bg-gray-400 cursor-not-allowed'
                      : isValid
                        ? 'bg-gradient-to-r from-baby-blue to-baby-pink hover:from-baby-pink hover:to-baby-blue hover:shadow-lg'
                        : 'bg-gray-400 cursor-not-allowed'
                  }`}
              >
                {isSubmitting ? (
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                ) : (
                  <>
                    <Send size={18} />
                    <span>Enviar Mensaje</span>
                  </>
                )}
              </motion.button>
            </motion.form>
          </motion.div>
        </div>
      </div>

      <section id="mapa" className="py-16 bg-baby-light">
        <div className="container mx-auto px-4 lg:px-8">
          <motion.div
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            variants={containerVariants}
            className="text-center mb-12"
          >
            <motion.h2
              variants={itemVariants}
              className="text-3xl lg:text-4xl font-poppins font-bold text-baby-gray mb-4"
            >
              Nuestra Ubicación
            </motion.h2>
            <motion.p variants={itemVariants} className="text-lg text-baby-gray max-w-2xl mx-auto">
              Encuéntranos en el corazón de Bogotá. Fácil acceso y parqueadero disponible.
            </motion.p>
          </motion.div>

          <motion.div
            variants={itemVariants}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
            className="relative h-96 md:h-[500px] rounded-2xl overflow-hidden shadow-xl border border-baby-blue"
          >
            <div
              ref={mapContainerRef}
              className="absolute inset-0 z-0"
              style={{ width: '100%', height: '100%' }}
            />

            <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm rounded-xl p-4 shadow-lg max-w-sm z-10 border border-baby-blue">
              <h3 className="font-poppins font-bold text-baby-gray mb-1">BABY CASH</h3>
              <p className="text-sm text-baby-gray mb-2">Pañalera y variedades {`Soffy's`}</p>
              <p className="text-xs text-baby-gray">
                <MapPin size={12} className="inline mr-1" />
                Torre Colpatria, Bogotá, Colombia
              </p>
            </div>
          </motion.div>
        </div>
      </section>
    </div>
  );
};

export default Contacto;
