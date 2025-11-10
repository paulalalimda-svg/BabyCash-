import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { MessageSquare, Star, Users, Heart, ChevronLeft, ChevronRight, Quote } from 'lucide-react';
import { Link } from 'react-router-dom';
import {
  testimonialService,
  type Testimonial,
  type TestimonialRequest,
  handleApiError,
} from '../services/api';
import TestimonialCard from '../components/cards/TestimonialCard';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import toast from 'react-hot-toast';

const Testimonios: React.FC = () => {
  const [currentTestimonial, setCurrentTestimonial] = useState(0);
  const [testimonials, setTestimonials] = useState<Testimonial[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Pagination for "All Testimonials" section
  const [currentPage, setCurrentPage] = useState(0);
  const testimonialsPerPage = 6;

  // Form state
  const [formData, setFormData] = useState<TestimonialRequest>({
    name: '',
    message: '',
    rating: 5,
    avatar: '',
    location: '',
  });

  useEffect(() => {
    loadTestimonials();
  }, []);

  const loadTestimonials = async () => {
    setError(null);
    try {
      const data = await testimonialService.getTestimonials();
      setTestimonials(data);
    } catch (err) {
      setError(handleApiError(err));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await testimonialService.createTestimonial(formData);
      toast.success(
        '¡Gracias por tu testimonio! Está pendiente de aprobación por nuestro equipo.',
        {
          duration: 5000,
          icon: '🎉',
        }
      );
      setShowForm(false);
      setFormData({ name: '', message: '', rating: 5, avatar: '', location: '' });
      // Recargar testimonios después de crear uno
      loadTestimonials();
    } catch (err) {
      const errorMessage = handleApiError(err);
      toast.error(errorMessage);
      setError(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  // Calcular estadísticas
  const totalTestimonials = testimonials.length;
  const averageRating =
    totalTestimonials > 0
      ? testimonials.reduce((acc, t) => acc + t.rating, 0) / totalTestimonials
      : 0;
  const fiveStarCount = testimonials.filter((t) => t.rating === 5).length;
  const satisfactionRate =
    totalTestimonials > 0 ? Math.round((fiveStarCount / totalTestimonials) * 100) : 0;

  const nextTestimonial = () => {
    setCurrentTestimonial((prev) => (prev + 1) % testimonials.length);
  };

  const prevTestimonial = () => {
    setCurrentTestimonial((prev) => (prev - 1 + testimonials.length) % testimonials.length);
  };

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`w-5 h-5 ${i < rating ? 'text-yellow-400 fill-current' : 'text-gray-300'}`}
      />
    ));
  };

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      {/* Formulario Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9 }}
            className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-8">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-bold font-poppins text-baby-gray">
                  Comparte tu Experiencia
                </h2>
                <button
                  onClick={() => setShowForm(false)}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </div>

              {error && (
                <div
                  className={`mb-4 p-4 rounded-lg ${
                    error.includes('Gracias')
                      ? 'bg-green-50 text-green-700'
                      : 'bg-red-50 text-red-700'
                  }`}
                >
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label
                    htmlFor="testimonial-name"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Nombre completo *
                  </label>
                  <input
                    id="testimonial-name"
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-baby-pink focus:border-transparent"
                    placeholder="Tu nombre"
                  />
                </div>

                <div>
                  <label
                    htmlFor="testimonial-location"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Ubicación (opcional)
                  </label>
                  <input
                    id="testimonial-location"
                    type="text"
                    value={formData.location}
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-baby-pink focus:border-transparent"
                    placeholder="Ciudad, País"
                  />
                </div>

                <fieldset>
                  <legend className="block text-sm font-medium text-gray-700 mb-2">
                    Calificación *
                  </legend>
                  <div className="flex gap-2" aria-label="Calificación por estrellas">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={`star-${star}`}
                        type="button"
                        onClick={() => setFormData({ ...formData, rating: star })}
                        className="transition-transform hover:scale-110"
                        aria-label={`${star} estrella${star > 1 ? 's' : ''}`}
                      >
                        <Star
                          className={`w-8 h-8 ${
                            star <= formData.rating
                              ? 'text-yellow-400 fill-current'
                              : 'text-gray-300'
                          }`}
                        />
                      </button>
                    ))}
                  </div>
                </fieldset>

                <div>
                  <label
                    htmlFor="testimonial-message"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Tu experiencia *
                  </label>
                  <textarea
                    id="testimonial-message"
                    required
                    value={formData.message}
                    onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                    rows={5}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-baby-pink focus:border-transparent resize-none"
                    placeholder="Cuéntanos sobre tu experiencia con Baby Cash..."
                  />
                  <p className="mt-2 text-sm text-gray-500">
                    Mínimo 20 caracteres ({formData.message.length}/20)
                  </p>
                </div>

                <div>
                  <label
                    htmlFor="testimonial-avatar"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    URL del avatar (opcional)
                  </label>
                  <input
                    id="testimonial-avatar"
                    type="url"
                    value={formData.avatar}
                    onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-baby-pink focus:border-transparent"
                    placeholder="https://ejemplo.com/tu-foto.jpg"
                  />
                </div>

                <div className="flex gap-4">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setShowForm(false)}
                    className="flex-1"
                  >
                    Cancelar
                  </Button>
                  <Button
                    type="submit"
                    disabled={submitting || formData.message.length < 20}
                    className="flex-1 bg-baby-pink hover:bg-baby-pink/90"
                  >
                    {submitting ? 'Enviando...' : 'Enviar Testimonio'}
                  </Button>
                </div>

                <p className="text-sm text-gray-500 text-center">
                  Tu testimonio será revisado por nuestro equipo antes de ser publicado
                </p>
              </form>
            </div>
          </motion.div>
        </div>
      )}

      {/* Header */}
      <section className="py-16 px-4 bg-gradient-to-r from-baby-blue/10 to-baby-pink/10">
        <div className="max-w-7xl mx-auto text-center">
          <motion.div
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
          >
            <MessageSquare className="w-16 h-16 mx-auto mb-6 text-baby-pink" />
            <h1 className="text-4xl md:text-5xl font-bold font-poppins text-baby-gray mb-6">
              Lo que dicen nuestros <span className="text-baby-pink">Clientes</span>
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto font-inter leading-relaxed mb-6">
              Miles de familias confían en Baby Cash para el cuidado de sus bebés. Estas son sus
              experiencias reales con nuestros productos y servicios.
            </p>
            <div className="flex justify-center">
              <Button
                onClick={() => setShowForm(true)}
                className="bg-baby-pink hover:bg-baby-pink/90 flex items-center"
              >
                <MessageSquare className="w-5 h-5 mr-2" />
                Participar - Comparte tu Experiencia
              </Button>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Statistics */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="grid grid-cols-1 md:grid-cols-3 gap-8"
          >
            <Card className="p-8 text-center bg-gradient-to-br from-baby-blue/5 to-baby-mint/5 border-baby-blue/20">
              <div className="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-baby-blue to-baby-mint rounded-full flex items-center justify-center">
                <Users className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-3xl font-bold font-poppins text-baby-gray mb-2">
                {totalTestimonials}+
              </h3>
              <p className="text-gray-600 font-inter">Familias satisfechas</p>
            </Card>

            <Card className="p-8 text-center bg-gradient-to-br from-baby-pink/5 to-baby-blue/5 border-baby-pink/20">
              <div className="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-baby-pink to-baby-blue rounded-full flex items-center justify-center">
                <Star className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-3xl font-bold font-poppins text-baby-gray mb-2">
                {averageRating.toFixed(1)}
              </h3>
              <p className="text-gray-600 font-inter">Calificación promedio</p>
            </Card>

            <Card className="p-8 text-center bg-gradient-to-br from-baby-mint/5 to-baby-pink/5 border-baby-mint/20">
              <div className="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-baby-mint to-baby-pink rounded-full flex items-center justify-center">
                <Heart className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-3xl font-bold font-poppins text-baby-gray mb-2">
                {satisfactionRate}%
              </h3>
              <p className="text-gray-600 font-inter">Satisfacción total</p>
            </Card>
          </motion.div>
        </div>
      </section>

      {/* Featured Testimonial Carousel */}
      <section className="py-16 px-4 bg-baby-light">
        <div className="max-w-4xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-12"
          >
            <Quote className="w-12 h-12 mx-auto mb-4 text-baby-pink" />
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4">
              Testimonio Destacado
            </h2>
            <p className="text-lg text-gray-600 font-inter">
              Conoce la experiencia de nuestras familias más felices
            </p>
          </motion.div>

          <motion.div
            key={currentTestimonial}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.5 }}
            className="relative"
          >
            {testimonials.length > 0 && (
              <Card className="p-8 md:p-12 bg-gradient-to-br from-white to-baby-blue/5 shadow-xl">
                <div className="text-center">
                  <div className="w-20 h-20 mx-auto mb-6 bg-gradient-to-br from-baby-blue to-baby-pink rounded-full flex items-center justify-center text-2xl font-bold text-white">
                    {testimonials[currentTestimonial].name.charAt(0)}
                  </div>

                  <blockquote className="text-xl md:text-2xl font-inter text-gray-700 mb-6 italic break-words overflow-wrap-anywhere">
                    &ldquo;{testimonials[currentTestimonial].message}&rdquo;
                  </blockquote>

                  <div className="flex justify-center mb-4">
                    {renderStars(testimonials[currentTestimonial].rating)}
                  </div>

                  <h4 className="text-lg font-bold font-poppins text-baby-gray">
                    {testimonials[currentTestimonial].name}
                  </h4>
                  <p className="text-gray-500 font-inter">
                    {testimonials[currentTestimonial].location || 'Cliente verificado'}
                  </p>
                </div>
              </Card>
            )}

            {/* Navigation buttons */}
            <div className="flex justify-center items-center gap-4 mt-8">
              <Button
                onClick={prevTestimonial}
                variant="outline"
                size="sm"
                className="rounded-full w-12 h-12 p-0"
                aria-label="Testimonio anterior"
              >
                <ChevronLeft className="w-5 h-5" />
              </Button>

              <div className="flex gap-2">
                {testimonials.map((testimonial, index) => (
                  <button
                    key={`testimonial-dot-${testimonial.id}-${index}`}
                    onClick={() => setCurrentTestimonial(index)}
                    className={`w-3 h-3 rounded-full transition-all duration-300 ${
                      index === currentTestimonial
                        ? 'bg-baby-pink w-8'
                        : 'bg-gray-300 hover:bg-baby-pink/50'
                    }`}
                    aria-label={`Ir al testimonio ${index + 1}`}
                  />
                ))}
              </div>

              <Button
                onClick={nextTestimonial}
                variant="outline"
                size="sm"
                className="rounded-full w-12 h-12 p-0"
                aria-label="Siguiente testimonio"
              >
                <ChevronRight className="w-5 h-5" />
              </Button>
            </div>
          </motion.div>
        </div>
      </section>

      {/* All Testimonials Grid */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4">
              Todos los Testimonios
            </h2>
            <p className="text-lg text-gray-600 font-inter">
              Lee todas las experiencias de nuestras familias felices
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {testimonials
              .slice(currentPage * testimonialsPerPage, (currentPage + 1) * testimonialsPerPage)
              .map((testimonial, index) => (
                <motion.div
                  key={testimonial.id}
                  initial={{ opacity: 0, y: 30 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.6, delay: index * 0.1 }}
                  whileHover={{ y: -5 }}
                >
                  <TestimonialCard testimonial={testimonial} />
                </motion.div>
              ))}
          </div>

          {/* Pagination Controls */}
          {testimonials.length > testimonialsPerPage && (
            <div className="mt-12 flex items-center justify-center gap-2">
              <button
                onClick={() => {
                  setCurrentPage((prev) => Math.max(0, prev - 1));
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                disabled={currentPage === 0}
                className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                  currentPage === 0
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'bg-baby-blue text-white hover:bg-baby-blue/90'
                }`}
              >
                Anterior
              </button>

              <div className="flex gap-1">
                {Array.from(
                  { length: Math.ceil(testimonials.length / testimonialsPerPage) },
                  (_, i) => (
                    <button
                      key={i}
                      onClick={() => {
                        setCurrentPage(i);
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                      }}
                      className={`w-10 h-10 rounded-lg font-medium transition-colors ${
                        currentPage === i
                          ? 'bg-baby-blue text-white'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                    >
                      {i + 1}
                    </button>
                  )
                )}
              </div>

              <button
                onClick={() => {
                  setCurrentPage((prev) =>
                    Math.min(Math.ceil(testimonials.length / testimonialsPerPage) - 1, prev + 1)
                  );
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                disabled={currentPage === Math.ceil(testimonials.length / testimonialsPerPage) - 1}
                className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                  currentPage === Math.ceil(testimonials.length / testimonialsPerPage) - 1
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'bg-baby-blue text-white hover:bg-baby-blue/90'
                }`}
              >
                Siguiente
              </button>
            </div>
          )}
        </div>
      </section>

      {/* Rating Distribution */}
      <section className="py-16 px-4 bg-baby-light">
        <div className="max-w-4xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4">
              Distribución de Calificaciones
            </h2>
            <p className="text-lg text-gray-600 font-inter">
              Transparencia total en las opiniones de nuestros clientes
            </p>
          </motion.div>

          <Card className="p-8">
            <div className="space-y-4">
              {[5, 4, 3, 2, 1].map((stars) => {
                const count = testimonials.filter((t) => t.rating === stars).length;
                const percentage = totalTestimonials > 0 ? (count / totalTestimonials) * 100 : 0;

                return (
                  <motion.div
                    key={stars}
                    initial={{ opacity: 0, x: -20 }}
                    whileInView={{ opacity: 1, x: 0 }}
                    viewport={{ once: true }}
                    transition={{ duration: 0.4, delay: (5 - stars) * 0.1 }}
                    className="flex items-center gap-4"
                  >
                    <div className="flex items-center gap-1 w-24">
                      <span className="font-medium text-gray-700">{stars}</span>
                      <Star className="w-4 h-4 text-yellow-400 fill-current" />
                    </div>

                    <div className="flex-1 bg-gray-200 rounded-full h-3 overflow-hidden">
                      <motion.div
                        initial={{ width: 0 }}
                        whileInView={{ width: `${percentage}%` }}
                        viewport={{ once: true }}
                        transition={{ duration: 1, delay: (5 - stars) * 0.1 }}
                        className="h-full bg-gradient-to-r from-baby-blue to-baby-pink rounded-full"
                      />
                    </div>

                    <span className="w-16 text-right font-medium text-gray-700">
                      {count} ({percentage.toFixed(0)}%)
                    </span>
                  </motion.div>
                );
              })}
            </div>

            <div className="mt-8 text-center">
              <div className="flex justify-center items-center gap-2 mb-2">
                {renderStars(Math.round(averageRating))}
                <span className="ml-2 text-2xl font-bold text-baby-gray">
                  {averageRating.toFixed(1)}
                </span>
              </div>
              <p className="text-gray-600 font-inter">
                Basado en {totalTestimonials} reseñas verificadas
              </p>
            </div>
          </Card>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 px-4 bg-gradient-to-r from-baby-blue to-baby-pink">
        <div className="max-w-4xl mx-auto text-center">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            <Heart className="w-16 h-16 mx-auto mb-6 text-white" />
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-white mb-6">
              ¿Quieres ser parte de estas historias?
            </h2>
            <p className="text-xl text-white/90 mb-8 font-inter">
              Únete a las miles de familias que confían en Baby Cash para el cuidado de sus bebés
            </p>
            <Link to="/productos" className="inline-block">
              <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                <Button
                  size="lg"
                  className="!bg-white !text-baby-blue font-bold py-4 px-8 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:!bg-gray-50"
                >
                  Explorar Productos
                </Button>
              </motion.div>
            </Link>
          </motion.div>
        </div>
      </section>
    </div>
  );
};

export default Testimonios;
