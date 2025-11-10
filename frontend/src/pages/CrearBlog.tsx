import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { BookOpen, X, Save, Tag } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { blogService, type BlogPostRequest, handleApiError } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import Button from '../components/ui/Button';
import Input from '../components/ui/input';
import toast from 'react-hot-toast';
import { logger } from '../utils/logger';

const CrearBlog: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [submitting, setSubmitting] = useState(false);
  const [tagInput, setTagInput] = useState('');
  const [formData, setFormData] = useState<BlogPostRequest>({
    title: '',
    content: '',
    excerpt: '',
    imageUrl: '',
    tags: [],
    featured: false,
  });

  React.useEffect(() => {
    if (!isAuthenticated) {
      toast.error('Debes iniciar sesión para crear un artículo');
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  const addTag = () => {
    const tags = formData.tags || [];
    if (tagInput.trim() && !tags.includes(tagInput.trim())) {
      setFormData({
        ...formData,
        tags: [...tags, tagInput.trim()],
      });
      setTagInput('');
    }
  };

  const removeTag = (tag: string) => {
    setFormData({
      ...formData,
      tags: (formData.tags || []).filter((t) => t !== tag),
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.title.trim() || !formData.content.trim() || !(formData.excerpt || '').trim()) {
      toast.error('El título, extracto y contenido son obligatorios');
      return;
    }

    if (formData.content.length < 100) {
      toast.error('El contenido debe tener al menos 100 caracteres');
      return;
    }

    setSubmitting(true);
    try {
      await blogService.createPost(formData);
      toast.success('¡Artículo creado exitosamente! Será revisado por nuestro equipo.');
      navigate('/blog');
    } catch (error) {
      logger.error('Error al crear blog:', error);
      toast.error(handleApiError(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-baby-light pt-20 pb-12">
      <div className="max-w-4xl mx-auto px-4">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-8"
        >
          <BookOpen className="w-16 h-16 mx-auto mb-4 text-baby-pink" />
          <h1 className="text-4xl font-bold font-poppins text-baby-gray mb-4">
            Crear Nuevo Artículo
          </h1>
          <p className="text-gray-600 font-inter">
            Comparte tus conocimientos y experiencias con nuestra comunidad
          </p>
        </motion.div>

        {/* Form */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-xl shadow-lg p-8"
        >
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title */}
            <div>
              <label htmlFor="blog-title" className="block text-sm font-medium text-gray-700 mb-2">
                Título del Artículo *
              </label>
              <Input
                id="blog-title"
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                placeholder="Ej: Consejos para el sueño del bebé"
                required
                minLength={5}
                maxLength={200}
                className="w-full"
              />
              <p className="text-xs text-gray-500 mt-1">{formData.title.length}/200 caracteres</p>
            </div>

            {/* Excerpt */}
            <div>
              <label
                htmlFor="blog-excerpt"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Extracto o Resumen *
              </label>
              <textarea
                id="blog-excerpt"
                value={formData.excerpt || ''}
                onChange={(e) => setFormData({ ...formData, excerpt: e.target.value })}
                placeholder="Una breve descripción de tu artículo (aparecerá en la lista de blogs)"
                required
                minLength={20}
                maxLength={500}
                rows={3}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
              />
              <p className="text-xs text-gray-500 mt-1">
                {(formData.excerpt || '').length}/500 caracteres (mínimo 20)
              </p>
            </div>

            {/* Content */}
            <div>
              <label
                htmlFor="blog-content"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Contenido del Artículo *
              </label>
              <textarea
                id="blog-content"
                value={formData.content}
                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                placeholder="Escribe aquí el contenido completo de tu artículo..."
                required
                minLength={100}
                rows={15}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue font-inter"
              />
              <p className="text-xs text-gray-500 mt-1">
                {formData.content.length} caracteres (mínimo 100)
              </p>
            </div>

            {/* Image URL */}
            <div>
              <label htmlFor="blog-image" className="block text-sm font-medium text-gray-700 mb-2">
                URL de Imagen (opcional)
              </label>
              <Input
                id="blog-image"
                type="url"
                value={formData.imageUrl}
                onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                placeholder="https://ejemplo.com/imagen.jpg"
                className="w-full"
              />
              <p className="text-xs text-gray-500 mt-1">
                Puedes usar servicios como Unsplash, Pexels, o subirar a Imgur
              </p>
            </div>

            {/* Tags */}
            <div>
              <label htmlFor="blog-tags" className="block text-sm font-medium text-gray-700 mb-2">
                Etiquetas (Tags)
              </label>
              <div className="flex gap-2 mb-2">
                <Input
                  id="blog-tags"
                  type="text"
                  value={tagInput}
                  onChange={(e) => setTagInput(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      addTag();
                    }
                  }}
                  placeholder="Ej: bebé, sueño, consejos"
                  className="flex-1"
                />
                <Button type="button" onClick={addTag} variant="outline" className="shrink-0">
                  Agregar
                </Button>
              </div>
              <div className="flex flex-wrap gap-2">
                {(formData.tags || []).map((tag) => (
                  <span
                    key={`tag-${tag}`}
                    className="inline-flex items-center gap-1 bg-baby-blue/10 text-baby-blue px-3 py-1 rounded-full text-sm"
                  >
                    <Tag size={14} />
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
              <p className="text-xs text-gray-500 mt-1">
                Agrega etiquetas para ayudar a otros a encontrar tu artículo
              </p>
            </div>

            {/* Buttons */}
            <div className="flex gap-4 pt-4">
              <Button
                type="submit"
                disabled={submitting}
                className="flex-1 bg-baby-blue hover:bg-baby-blue/90"
              >
                {submitting ? (
                  <>Publicando...</>
                ) : (
                  <>
                    <Save className="w-5 h-5 mr-2" />
                    Publicar Artículo
                  </>
                )}
              </Button>
              <Button
                type="button"
                onClick={() => navigate('/blog')}
                variant="outline"
                className="flex-1"
              >
                Cancelar
              </Button>
            </div>

            <div className="bg-baby-light rounded-lg p-4 mt-4">
              <p className="text-sm text-gray-600">
                <strong>Nota:</strong> Tu artículo será revisado por nuestro equipo antes de ser
                publicado. Te notificaremos una vez que esté disponible.
              </p>
            </div>
          </form>
        </motion.div>
      </div>
    </div>
  );
};

export default CrearBlog;
