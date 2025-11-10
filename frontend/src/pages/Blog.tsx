import { motion } from 'framer-motion';
import {
  AlertCircle,
  BookOpen,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Search,
  Tag,
} from 'lucide-react';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BlogCard from '../components/cards/BlogCard';
import Button from '../components/ui/Button';
import Input from '../components/ui/input';
import type { BlogPost as ApiBlogPost } from '../services/api';
import { blogService, handleApiError } from '../services/api';
import type { BlogPost as LegacyBlogPost } from '../types';

const Blog: React.FC = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTag, setSelectedTag] = useState<string>('');
  const [posts, setPosts] = useState<LegacyBlogPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [allTags, setAllTags] = useState<string[]>([]);

  // Convert API BlogPost to Legacy BlogPost for BlogCard compatibility
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
    const loadPosts = async () => {
      setLoading(true);
      setError(null);
      try {
        let response;
        if (searchTerm) {
          response = await blogService.searchPosts(searchTerm, page, 6);
        } else if (selectedTag) {
          response = await blogService.getPostsByTag(selectedTag, page, 6);
        } else {
          response = await blogService.getPosts(page, 6);
        }

        setPosts(response.content.map(convertToLegacyPost));
        setTotalPages(response.totalPages);

        // Extract all unique tags
        const tags = new Set<string>();
        response.content.forEach((post) => {
          post.tags.forEach((tag) => tags.add(tag));
        });
        setAllTags(Array.from(tags));
      } catch (err) {
        setError(handleApiError(err));
      } finally {
        setLoading(false);
      }
    };

    loadPosts();
  }, [searchTerm, selectedTag, page]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
  };

  const handleTagClick = (tag: string) => {
    setSelectedTag(tag === selectedTag ? '' : tag);
    setSearchTerm('');
    setPage(0);
  };

  const handlePostClick = (postId: string) => {
    navigate(`/blog/${postId}`);
  };

  const handleRetry = () => {
    setError(null);
    setLoading(true);
    // Trigger reload by changing page
    setPage(0);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCreatePost = () => {
    navigate('/crear-blog');
  };

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      {/* Header Section */}
      <section className="py-16 px-4 bg-gradient-to-r from-baby-blue/10 to-baby-pink/10">
        <div className="max-w-7xl mx-auto text-center">
          <motion.div
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
          >
            <BookOpen className="w-16 h-16 mx-auto mb-6 text-baby-pink" />
            <h1 className="text-4xl md:text-5xl font-bold font-poppins text-baby-gray mb-6">
              Nuestro <span className="text-baby-pink">Blog</span>
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto font-inter leading-relaxed mb-6">
              Consejos, tips y artículos sobre el cuidado de tu bebé.
            </p>
            <Button
              onClick={handleCreatePost}
              className="bg-baby-pink hover:bg-baby-pink/90 mx-auto"
            >
              <BookOpen className="w-5 h-5 mr-2" />
              Participar - Escribe un Artículo
            </Button>
          </motion.div>
        </div>
      </section>

      {/* Search and Filter Section */}
      <section className="py-8 px-4 bg-baby-light">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row gap-4 mb-8">
            {/* Search Bar */}
            <form onSubmit={handleSearch} className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <Input
                  type="text"
                  placeholder="Buscar artículos..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 w-full"
                />
              </div>
            </form>
            <Button type="submit" onClick={handleSearch}>
              Buscar
            </Button>
          </div>

          {/* Tags Filter */}
          {allTags.length > 0 && (
            <div className="mb-8">
              <div className="flex items-center mb-4">
                <Tag className="w-5 h-5 text-baby-blue mr-2" />
                <h3 className="font-semibold text-baby-gray">Filtrar por etiqueta:</h3>
              </div>
              <div className="flex flex-wrap gap-2">
                {allTags.map((tag) => (
                  <button
                    key={tag}
                    onClick={() => handleTagClick(tag)}
                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                      selectedTag === tag
                        ? 'bg-baby-pink text-white'
                        : 'bg-white text-gray-700 hover:bg-baby-pink/10 border border-gray-200'
                    }`}
                  >
                    {tag}
                  </button>
                ))}
                {selectedTag && (
                  <button
                    onClick={() => setSelectedTag('')}
                    className="px-4 py-2 rounded-full text-sm font-medium bg-gray-200 text-gray-700 hover:bg-gray-300"
                  >
                    Limpiar filtro
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Main Content */}
      <section className="py-12 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          {/* Loading State */}
          {loading && (
            <div className="text-center py-16">
              <Loader2 className="w-12 h-12 animate-spin mx-auto text-baby-pink" />
              <p className="mt-4 text-gray-600 font-inter">Cargando artículos...</p>
            </div>
          )}

          {/* Error State */}
          {error && !loading && (
            <div className="text-center py-16">
              <AlertCircle className="w-12 h-12 mx-auto text-red-500 mb-4" />
              <p className="text-red-600 font-inter mb-4">{error}</p>
              <Button onClick={handleRetry}>Reintentar</Button>
            </div>
          )}

          {/* Empty State */}
          {!loading && !error && posts.length === 0 && (
            <div className="text-center py-16">
              <Search className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <h3 className="text-xl font-bold text-baby-gray mb-2">No se encontraron artículos</h3>
              <p className="text-gray-600">
                {searchTerm
                  ? `No hay resultados para "${searchTerm}"`
                  : selectedTag
                    ? `No hay artículos con la etiqueta "${selectedTag}"`
                    : 'No hay artículos disponibles en este momento'}
              </p>
            </div>
          )}

          {/* Posts Grid */}
          {!loading && !error && posts.length > 0 && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {posts.map((post) => (
                  <BlogCard key={post.id} post={post} onReadMore={() => handlePostClick(post.id)} />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="mt-12 flex items-center justify-center gap-2">
                  <Button
                    variant="outline"
                    onClick={() => handlePageChange(page - 1)}
                    disabled={page === 0}
                    className="flex items-center gap-1"
                  >
                    <ChevronLeft className="w-4 h-4" />
                    Anterior
                  </Button>

                  <div className="flex gap-1">
                    {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => {
                      let pageNum = i;
                      if (totalPages > 5) {
                        if (page > 2) {
                          pageNum = page - 2 + i;
                        }
                        if (pageNum >= totalPages) {
                          pageNum = totalPages - 5 + i;
                        }
                      }
                      return (
                        <Button
                          key={pageNum}
                          variant={page === pageNum ? 'primary' : 'outline'}
                          onClick={() => handlePageChange(pageNum)}
                          className="w-10 h-10 p-0"
                        >
                          {pageNum + 1}
                        </Button>
                      );
                    })}
                  </div>

                  <Button
                    variant="outline"
                    onClick={() => handlePageChange(page + 1)}
                    disabled={page === totalPages - 1}
                    className="flex items-center gap-1"
                  >
                    Siguiente
                    <ChevronRight className="w-4 h-4" />
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      </section>
    </div>
  );
};

export default Blog;
