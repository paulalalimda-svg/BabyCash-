import { motion } from 'framer-motion';
import {
  AlertCircle,
  CheckCircle,
  Clock,
  Eye,
  EyeOff,
  MapPin,
  Star,
  Trash2,
  XCircle,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAdminCrud } from '../../hooks/useAdminCrud';
import {
  handleApiError,
  testimonialService,
  type Testimonial,
  type TestimonialStats,
} from '../../services/api';
import { PaginationControls } from '../ui/PaginationControls';

type TestimonialFilter = 'all' | 'approved' | 'pending';

export const TestimonialsManager = () => {
  const {
    items: testimonials,
    loading,
    filter,
    setFilter,
    currentPage,
    totalPages,
    totalElements,
    setCurrentPage,
    loadData,
    handleDelete,
    goToNextPage,
    goToPreviousPage,
    canGoNext,
    canGoPrevious,
  } = useAdminCrud<Testimonial, TestimonialFilter>({
    service: {
      getAll: testimonialService.getAllTestimonialsPaged,
      delete: testimonialService.deleteTestimonial,
    },
    pageSize: 10,
    filterFn: (items, filterType) => {
      if (filterType === 'approved') {
        return items.filter((t) => t.approved);
      } else if (filterType === 'pending') {
        return items.filter((t) => !t.approved);
      }
      return items;
    },
    defaultFilter: 'all',
    logName: 'testimonios',
  });

  const [stats, setStats] = useState<TestimonialStats | null>(null);

  // Load stats when component mounts or page changes
  useEffect(() => {
    const loadStats = async () => {
      try {
        const statsData = await testimonialService.getStats();
        setStats(statsData);
      } catch (error) {
        toast.error(handleApiError(error));
      }
    };
    loadStats();
  }, [currentPage]); // Solo depende de currentPage, no de testimonials

  const handleApprove = async (id: number) => {
    try {
      await testimonialService.approveTestimonial(id);
      toast.success('Testimonio aprobado');
      loadData();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const handleReject = async (id: number) => {
    try {
      await testimonialService.rejectTestimonial(id);
      toast.success('Testimonio rechazado');
      loadData();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const handleToggleFeatured = async (id: number) => {
    try {
      await testimonialService.toggleFeatured(id);
      toast.success('Estado destacado actualizado');
      loadData();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  // handleDelete is provided by useAdminCrud hook

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }).map((_, i) => (
      <Star
        key={`star-${rating}-${i}`}
        size={16}
        className={i < rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
      />
    ));
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-baby-blue"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Estadísticas */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white rounded-xl p-6 border border-baby-blue">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total</p>
                <p className="text-2xl font-bold text-baby-gray">{stats.total}</p>
              </div>
              <Star className="text-baby-blue" size={32} />
            </div>
          </div>

          <div className="bg-white rounded-xl p-6 border border-green-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Aprobados</p>
                <p className="text-2xl font-bold text-green-600">{stats.totalApproved}</p>
              </div>
              <CheckCircle className="text-green-500" size={32} />
            </div>
          </div>

          <div className="bg-white rounded-xl p-6 border border-orange-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Pendientes</p>
                <p className="text-2xl font-bold text-orange-600">{stats.totalPending}</p>
              </div>
              <Clock className="text-orange-500" size={32} />
            </div>
          </div>

          <div className="bg-white rounded-xl p-6 border border-purple-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Destacados</p>
                <p className="text-2xl font-bold text-purple-600">{stats.totalFeatured}</p>
              </div>
              <Eye className="text-purple-500" size={32} />
            </div>
          </div>
        </div>
      )}

      {/* Filtros */}
      <div className="flex space-x-2">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'all'
              ? 'bg-baby-blue text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Todos ({stats?.total || 0})
        </button>
        <button
          onClick={() => setFilter('approved')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'approved'
              ? 'bg-green-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Aprobados ({stats?.totalApproved || 0})
        </button>
        <button
          onClick={() => setFilter('pending')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'pending'
              ? 'bg-orange-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Pendientes ({stats?.totalPending || 0})
        </button>
      </div>

      {/* Lista de Testimonios */}
      <div className="space-y-4">
        {testimonials.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-xl border border-gray-200">
            <AlertCircle className="mx-auto text-gray-400 mb-4" size={48} />
            <p className="text-gray-500">No hay testimonios en esta categoría</p>
          </div>
        ) : (
          testimonials.map((testimonial) => (
            <motion.div
              key={testimonial.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white rounded-xl p-6 border border-gray-200 hover:shadow-lg transition-shadow"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center space-x-3 mb-2">
                    <div className="w-12 h-12 rounded-full bg-gradient-to-r from-baby-blue to-baby-pink flex items-center justify-center text-white font-bold">
                      {testimonial.name.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="font-semibold text-baby-gray">{testimonial.name}</h3>
                      {testimonial.location && (
                        <p className="text-sm text-gray-500 flex items-center">
                          <MapPin size={12} className="mr-1" />
                          {testimonial.location}
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center space-x-1 mb-2">
                    {renderStars(testimonial.rating)}
                  </div>

                  <p className="text-gray-700 mb-3">{testimonial.message}</p>

                  <div className="flex flex-wrap gap-2">
                    {testimonial.approved ? (
                      <span className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm">
                        ✓ Aprobado
                      </span>
                    ) : (
                      <span className="px-3 py-1 bg-orange-100 text-orange-700 rounded-full text-sm">
                        ⏳ Pendiente
                      </span>
                    )}

                    {testimonial.featured && (
                      <span className="px-3 py-1 bg-purple-100 text-purple-700 rounded-full text-sm">
                        ⭐ Destacado
                      </span>
                    )}
                  </div>
                </div>

                {/* Acciones */}
                <div className="flex flex-col space-y-2 ml-4">
                  {!testimonial.approved && (
                    <button
                      onClick={() => handleApprove(testimonial.id)}
                      className="p-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
                      title="Aprobar"
                    >
                      <CheckCircle size={20} />
                    </button>
                  )}

                  {testimonial.approved && (
                    <>
                      <button
                        onClick={() => handleReject(testimonial.id)}
                        className="p-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
                        title="Rechazar"
                      >
                        <XCircle size={20} />
                      </button>

                      <button
                        onClick={() => handleToggleFeatured(testimonial.id)}
                        className={`p-2 rounded-lg transition-colors ${
                          testimonial.featured
                            ? 'bg-purple-500 text-white hover:bg-purple-600'
                            : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
                        }`}
                        title={testimonial.featured ? 'Quitar de destacados' : 'Destacar'}
                      >
                        {testimonial.featured ? <EyeOff size={20} /> : <Eye size={20} />}
                      </button>
                    </>
                  )}

                  <button
                    onClick={() => handleDelete(testimonial.id)}
                    className="p-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
                    title="Eliminar"
                  >
                    <Trash2 size={20} />
                  </button>
                </div>
              </div>

              <div className="mt-4 text-xs text-gray-500">
                Creado: {new Date(testimonial.createdAt).toLocaleString('es-ES')}
              </div>
            </motion.div>
          ))
        )}
      </div>

      {/* Pagination Controls */}
      <PaginationControls
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsCount={testimonials.length}
        itemName="testimonios"
        onPageChange={setCurrentPage}
        onNext={goToNextPage}
        onPrevious={goToPreviousPage}
        canGoNext={canGoNext}
        canGoPrevious={canGoPrevious}
      />
    </div>
  );
};
