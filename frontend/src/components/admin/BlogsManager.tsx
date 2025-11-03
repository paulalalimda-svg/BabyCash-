import { useState } from 'react';
import { motion } from 'framer-motion';
import {
  BookOpen,
  Plus,
  Edit,
  Trash2,
  Star,
  Calendar,
  Tag,
  X,
  Save,
  CheckCircle,
  Clock,
} from 'lucide-react';
import {
  blogService,
  type BlogPost,
  type BlogPostRequest,
  handleApiError,
} from '../../services/api';
import toast from 'react-hot-toast';
import { logger } from '../../utils/logger';
import { useAdminCrud } from '../../hooks/useAdminCrud';
import { PaginationControls } from '../ui/PaginationControls';

type BlogFilter = 'all' | 'published' | 'pending';

export const BlogsManager = () => {
  const {
    items: posts,
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
  } = useAdminCrud<BlogPost, BlogFilter>({
    service: {
      getAll: blogService.getAllPostsAdmin,
      delete: blogService.deletePost,
    },
    pageSize: 10,
    filterFn: (blogs, filterType) => {
      if (filterType === 'published') {
        return blogs.filter(p => p.published);
      } else if (filterType === 'pending') {
        return blogs.filter(p => !p.published);
      }
      return blogs;
    },
    defaultFilter: 'all',
    logName: 'blogs',
  });

  const [showForm, setShowForm] = useState(false);
  const [editingPost, setEditingPost] = useState<BlogPost | null>(null);
  
  const [formData, setFormData] = useState<BlogPostRequest>({
    title: '',
    content: '',
    excerpt: '',
    imageUrl: '',
    tags: [],
    featured: false,
  });
  const [tagInput, setTagInput] = useState('');

  const resetForm = () => {
    setFormData({
      title: '',
      content: '',
      excerpt: '',
      imageUrl: '',
      tags: [],
      featured: false,
    });
    setTagInput('');
    setEditingPost(null);
    setShowForm(false);
  };

  const handleEdit = (post: BlogPost) => {
    setEditingPost(post);
    setFormData({
      title: post.title,
      content: post.content,
      excerpt: post.excerpt,
      imageUrl: post.imageUrl || '',
      tags: post.tags || [],
      featured: post.featured,
    });
    setShowForm(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title.trim() || !formData.content.trim()) {
      toast.error('El título y contenido son obligatorios');
      return;
    }

    try {
      if (editingPost) {
        await blogService.updatePost(editingPost.id, formData);
        toast.success('Blog actualizado correctamente');
      } else {
        await blogService.createPost(formData);
        toast.success('Blog creado correctamente');
      }
      resetForm();
      await loadData(); // Esperar a que recargue los datos
    } catch (error) {
      logger.error('Error al guardar blog:', error);
      toast.error(handleApiError(error));
    }
  };

  // handleDelete is provided by useAdminCrud hook

  const handleToggleFeatured = async (id: number) => {
    try {
      await blogService.toggleFeatured(id);
      toast.success('Estado destacado actualizado');
      loadData();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const handleTogglePublish = async (id: number, currentlyPublished: boolean) => {
    try {
      if (currentlyPublished) {
        await blogService.unpublishPost(id);
        toast.success('Blog despublicado');
      } else {
        await blogService.publishPost(id);
        toast.success('Blog publicado');
      }
      await loadData();
    } catch (error) {
      toast.error(handleApiError(error));
    }
  };

  const addTag = () => {
    if (tagInput.trim() && !formData.tags?.includes(tagInput.trim())) {
      setFormData({
        ...formData,
        tags: [...(formData.tags || []), tagInput.trim()],
      });
      setTagInput('');
    }
  };

  const removeTag = (tag: string) => {
    setFormData({
      ...formData,
      tags: formData.tags?.filter((t) => t !== tag) || [],
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-baby-blue"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold">Gestión de Blogs</h2>
          <p className="text-gray-600">
            Total de publicaciones: {posts.length}
          </p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 bg-baby-blue text-white px-6 py-3 rounded-lg hover:bg-opacity-90 transition-colors"
        >
          {showForm ? <X size={20} /> : <Plus size={20} />}
          {showForm ? 'Cancelar' : 'Nueva Publicación'}
        </button>
      </div>

      {/* Filters */}
      <div className="flex gap-2">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === 'all'
              ? 'bg-baby-blue text-white'
              : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          Todos
        </button>
        <button
          onClick={() => setFilter('published')}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === 'published'
              ? 'bg-green-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          Publicados
        </button>
        <button
          onClick={() => setFilter('pending')}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === 'pending'
              ? 'bg-yellow-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          Pendientes
        </button>
      </div>

      {/* Form */}
      {showForm && (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-lg p-6"
        >
          <h3 className="text-xl font-bold mb-4">
            {editingPost ? 'Editar Publicación' : 'Nueva Publicación'}
          </h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">
                Título *
              </label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) =>
                  setFormData({ ...formData, title: e.target.value })
                }
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                required
                minLength={5}
                maxLength={200}
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">
                Extracto (breve descripción) *
              </label>
              <textarea
                value={formData.excerpt}
                onChange={(e) =>
                  setFormData({ ...formData, excerpt: e.target.value })
                }
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                rows={2}
                required
                minLength={20}
                maxLength={500}
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">
                Contenido *
              </label>
              <textarea
                value={formData.content}
                onChange={(e) =>
                  setFormData({ ...formData, content: e.target.value })
                }
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                rows={10}
                required
                minLength={100}
              />
              <p className="text-xs text-gray-500 mt-1">
                Mínimo 100 caracteres. Actual: {formData.content.length}
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">
                URL de Imagen
              </label>
              <input
                type="url"
                value={formData.imageUrl}
                onChange={(e) =>
                  setFormData({ ...formData, imageUrl: e.target.value })
                }
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                placeholder="https://ejemplo.com/imagen.jpg"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">
                Etiquetas (Tags)
              </label>
              <div className="flex gap-2 mb-2">
                <input
                  type="text"
                  value={tagInput}
                  onChange={(e) => setTagInput(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      addTag();
                    }
                  }}
                  className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                  placeholder="Escribe una etiqueta y presiona Enter"
                />
                <button
                  type="button"
                  onClick={addTag}
                  className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300"
                >
                  Agregar
                </button>
              </div>
              <div className="flex flex-wrap gap-2">
                {formData.tags?.map((tag) => (
                  <span
                    key={tag}
                    className="inline-flex items-center gap-1 bg-baby-blue/10 text-baby-blue px-3 py-1 rounded-full text-sm"
                  >
                    {tag}
                    <button
                      type="button"
                      onClick={() => removeTag(tag)}
                      className="hover:text-red-500"
                    >
                      <X size={14} />
                    </button>
                  </span>
                ))}
              </div>
            </div>

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="featured"
                checked={formData.featured}
                onChange={(e) =>
                  setFormData({ ...formData, featured: e.target.checked })
                }
                className="w-4 h-4 text-baby-blue focus:ring-baby-blue rounded"
              />
              <label htmlFor="featured" className="text-sm font-medium">
                Marcar como destacado (aparecerá en la página de inicio)
              </label>
            </div>

            <div className="flex gap-4">
              <button
                type="submit"
                className="flex items-center gap-2 bg-baby-blue text-white px-6 py-2 rounded-lg hover:bg-opacity-90"
              >
                <Save size={20} />
                {editingPost ? 'Actualizar' : 'Crear'} Publicación
              </button>
              <button
                type="button"
                onClick={resetForm}
                className="px-6 py-2 border rounded-lg hover:bg-gray-50"
              >
                Cancelar
              </button>
            </div>
          </form>
        </motion.div>
      )}

      {/* Posts List */}
      <div className="grid gap-4">
        {posts.map((post) => (
          <motion.div
            key={post.id}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="bg-white rounded-lg shadow-md p-6"
          >
            <div className="flex gap-6">
              {post.imageUrl && (
                <img
                  src={post.imageUrl}
                  alt={post.title}
                  className="w-32 h-32 object-cover rounded-lg"
                />
              )}
              <div className="flex-1">
                <div className="flex items-start justify-between mb-2">
                  <div className="flex-1">
                    <h3 className="text-xl font-bold mb-1">{post.title}</h3>
                    <p className="text-gray-600 text-sm mb-2">{post.excerpt}</p>
                    <div className="flex flex-wrap items-center gap-4 text-sm text-gray-500">
                      <span className="flex items-center gap-1">
                        <Calendar size={14} />
                        {new Date(post.createdAt).toLocaleDateString('es-CO')}
                      </span>
                      {/* Views count removed - not in BlogPost type */}
                      {post.featured && (
                        <span className="flex items-center gap-1 text-yellow-600">
                          <Star size={14} fill="currentColor" />
                          Destacado
                        </span>
                      )}
                      {post.published ? (
                        <span className="flex items-center gap-1 text-green-600">
                          <CheckCircle size={14} />
                          Publicado
                        </span>
                      ) : (
                        <span className="flex items-center gap-1 text-orange-600">
                          <Clock size={14} />
                          Pendiente
                        </span>
                      )}
                    </div>
                    {post.tags && post.tags.length > 0 && (
                      <div className="flex flex-wrap gap-2 mt-2">
                        {post.tags.map((tag) => (
                          <span
                            key={tag}
                            className="inline-flex items-center gap-1 bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs"
                          >
                            <Tag size={12} />
                            {tag}
                          </span>
                        ))}
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex gap-2 mt-4">
                  <button
                    onClick={() => handleTogglePublish(post.id, post.published)}
                    className={`flex items-center gap-1 px-3 py-1.5 rounded text-sm ${
                      post.published
                        ? 'bg-orange-500 text-white hover:bg-orange-600'
                        : 'bg-green-500 text-white hover:bg-green-600'
                    }`}
                  >
                    <CheckCircle size={16} />
                    {post.published ? 'Despublicar' : 'Publicar'}
                  </button>
                  <button
                    onClick={() => handleEdit(post)}
                    className="flex items-center gap-1 px-3 py-1.5 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm"
                  >
                    <Edit size={16} />
                    Editar
                  </button>
                  <button
                    onClick={() => handleToggleFeatured(post.id)}
                    className={`flex items-center gap-1 px-3 py-1.5 rounded text-sm ${
                      post.featured
                        ? 'bg-yellow-500 text-white hover:bg-yellow-600'
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    }`}
                  >
                    <Star size={16} fill={post.featured ? 'currentColor' : 'none'} />
                    {post.featured ? 'Quitar destacado' : 'Destacar'}
                  </button>
                  <button
                    onClick={() => handleDelete(post.id)}
                    className="flex items-center gap-1 px-3 py-1.5 bg-red-500 text-white rounded hover:bg-red-600 text-sm"
                  >
                    <Trash2 size={16} />
                    Eliminar
                  </button>
                </div>
              </div>
            </div>
          </motion.div>
        ))}

        {posts.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            <BookOpen size={48} className="mx-auto mb-4 opacity-50" />
            <p>No hay publicaciones de blog aún.</p>
            <p className="text-sm">Haz clic en "Nueva Publicación" para crear una.</p>
          </div>
        )}

        {/* Pagination Controls */}
        <PaginationControls
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={totalElements}
          itemsCount={posts.length}
          itemName="blogs"
          onPageChange={setCurrentPage}
          onNext={goToNextPage}
          onPrevious={goToPreviousPage}
          canGoNext={canGoNext}
          canGoPrevious={canGoPrevious}
        />
      </div>
    </div>
  );
};
