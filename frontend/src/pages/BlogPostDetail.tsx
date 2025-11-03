import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Calendar,
  User,
  Tag,
  ArrowLeft,
  Loader2,
  AlertCircle,
  Share2,
  Clock,
} from 'lucide-react';
import { blogService, handleApiError } from '../services/api';
import type { BlogPost as ApiBlogPost } from '../services/api';
import type { BlogPost as LegacyBlogPost } from '../types';
import BlogCard from '../components/cards/BlogCard';
import Button from '../components/ui/Button';
import CommentSection from '../components/blog/CommentSection';
import { logger } from '../utils/logger';

const BlogPostDetail: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const [post, setPost] = useState<ApiBlogPost | null>(null);
  const [relatedPosts, setRelatedPosts] = useState<LegacyBlogPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const convertToLegacyPost = (post: ApiBlogPost): LegacyBlogPost => ({
    id: post.id.toString(),
    title: post.title,
    excerpt: post.excerpt || post.content.substring(0, 150) + '...',
    content: post.content,
    image: post.imageUrl || '/images/blog/placeholder.jpg',
    date: post.publishedAt || post.createdAt,
    author: `${post.author.firstName} ${post.author.lastName}`,
    category: post.tags[0] || 'General',
  });

  useEffect(() => {
    const loadPost = async () => {
      if (!slug) return;

      setLoading(true);
      setError(null);

      try {
        const postData = await blogService.getPostBySlug(slug);
        setPost(postData);

        // Load related posts (posts with same tags)
        if (postData.tags.length > 0) {
          const related = await blogService.getPostsByTag(postData.tags[0], 0, 3);
          const filtered = related.content
            .filter((p) => p.id !== postData.id)
            .map(convertToLegacyPost);
          setRelatedPosts(filtered.slice(0, 3));
        }
      } catch (err) {
        setError(handleApiError(err));
      } finally {
        setLoading(false);
      }
    };

    loadPost();
  }, [slug]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const handleShare = async () => {
    if (navigator.share && post) {
      try {
        await navigator.share({
          title: post.title,
          text: post.excerpt || post.title,
          url: window.location.href,
        });
      } catch (err) {
        logger.error('Error sharing:', err);
      }
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(window.location.href);
      alert('¡Enlace copiado al portapapeles!');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-baby-light pt-20 flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-12 h-12 animate-spin mx-auto text-baby-pink mb-4" />
          <p className="text-gray-600 font-inter">Cargando artículo...</p>
        </div>
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="min-h-screen bg-baby-light pt-20 flex items-center justify-center">
        <div className="text-center max-w-md mx-auto px-4">
          <AlertCircle className="w-16 h-16 mx-auto text-red-500 mb-4" />
          <h2 className="text-2xl font-bold text-baby-gray mb-2">
            {error || 'Artículo no encontrado'}
          </h2>
          <p className="text-gray-600 mb-6">
            El artículo que buscas no existe o ha sido eliminado.
          </p>
          <Button onClick={() => navigate('/blog')}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Volver al Blog
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      {/* Back Button */}
      <div className="max-w-4xl mx-auto px-4 py-6">
        <Button
          variant="ghost"
          onClick={() => navigate('/blog')}
          className="flex items-center gap-2 text-baby-gray hover:text-baby-pink"
        >
          <ArrowLeft className="w-4 h-4" />
          Volver al Blog
        </Button>
      </div>

      {/* Article Header */}
      <article className="max-w-4xl mx-auto px-4 pb-12">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          {/* Featured Image */}
          {post.imageUrl && (
            <div className="mb-8 rounded-2xl overflow-hidden shadow-lg">
              <img
                src={post.imageUrl}
                alt={post.title}
                className="w-full h-auto aspect-video object-cover"
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.src = '/images/blog/placeholder.jpg';
                }}
              />
            </div>
          )}

          {/* Tags */}
          {post.tags.length > 0 && (
            <div className="flex flex-wrap gap-2 mb-4">
              {post.tags.map((tag) => (
                <span
                  key={tag}
                  className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-baby-pink/10 text-baby-pink"
                >
                  <Tag className="w-3 h-3 mr-1" />
                  {tag}
                </span>
              ))}
            </div>
          )}

          {/* Title */}
          <h1 className="text-4xl md:text-5xl font-bold font-poppins text-baby-gray mb-6 leading-tight">
            {post.title}
          </h1>

          {/* Meta Information */}
          <div className="flex flex-wrap items-center gap-4 text-gray-600 mb-8 pb-8 border-b border-gray-200">
            <div className="flex items-center gap-2">
              <User className="w-5 h-5" />
              <span className="font-medium">
                {post.author.firstName} {post.author.lastName}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <Calendar className="w-5 h-5" />
              <span>{formatDate(post.publishedAt || post.createdAt)}</span>
            </div>
            <div className="flex items-center gap-2">
              <Clock className="w-5 h-5" />
              <span>{Math.ceil(post.content.length / 1000)} min de lectura</span>
            </div>
            <button
              onClick={handleShare}
              className="flex items-center gap-2 ml-auto text-baby-pink hover:text-baby-blue transition-colors"
              aria-label="Compartir artículo"
            >
              <Share2 className="w-5 h-5" />
              <span className="font-medium">Compartir</span>
            </button>
          </div>

          {/* Excerpt */}
          {post.excerpt && (
            <div className="text-xl text-gray-700 font-inter leading-relaxed mb-8 p-6 bg-baby-blue/5 rounded-xl border-l-4 border-baby-blue">
              {post.excerpt}
            </div>
          )}

          {/* Content */}
          <div
            className="prose prose-lg max-w-none font-inter text-gray-700 leading-relaxed"
            dangerouslySetInnerHTML={{ __html: post.content }}
          />
        </motion.div>

        {/* Share Section Bottom */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
          className="mt-12 pt-8 border-t border-gray-200"
        >
          <div className="flex items-center justify-between">
            <p className="text-gray-600 font-medium">¿Te gustó este artículo?</p>
            <Button onClick={handleShare} className="flex items-center gap-2">
              <Share2 className="w-4 h-4" />
              Compartir
            </Button>
          </div>
        </motion.div>
      </article>

      {/* Comments Section */}
      {post && (
        <div className="max-w-4xl mx-auto px-4 pb-12">
          <CommentSection postId={post.id} />
        </div>
      )}

      {/* Related Posts */}
      {relatedPosts.length > 0 && (
        <section className="bg-white py-16 px-4">
          <div className="max-w-7xl mx-auto">
            <h2 className="text-3xl font-bold font-poppins text-baby-gray mb-8">
              Artículos Relacionados
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {relatedPosts.map((relatedPost) => (
                <BlogCard
                  key={relatedPost.id}
                  post={relatedPost}
                  onReadMore={() => navigate(`/blog/${relatedPost.id}`)}
                />
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

export default BlogPostDetail;
