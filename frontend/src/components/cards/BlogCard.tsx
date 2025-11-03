import React from 'react';
import { motion } from 'framer-motion';
import { Calendar, User, ArrowRight } from 'lucide-react';
import type { BlogPost } from '../../types';
import Card from '../ui/Card';
import Button from '../ui/Button';

interface BlogCardProps {
  post: BlogPost;
  onReadMore?: () => void;
}

const BlogCard: React.FC<BlogCardProps> = ({ post, onReadMore }) => {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <Card className="group overflow-hidden h-full border border-gray-200 hover:border-baby-blue transition-colors duration-300">
      {/* Imagen del artículo */}
      <div className="relative mb-4 overflow-hidden rounded-lg bg-gray-100 aspect-video">
        <img
          src={post.image}
          alt={post.title}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
          onError={(e) => {
            const target = e.target as HTMLImageElement;
            target.src = '/images/blog/placeholder.jpg';
          }}
        />

        {/* Overlay sutil */}
        <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
      </div>

      <div className="flex flex-col h-full">
        {/* Metadatos */}
        <div className="flex items-center space-x-4 text-sm text-gray-500 mb-3">
          <div className="flex items-center space-x-1">
            <Calendar size={14} />
            <span>{formatDate(post.date)}</span>
          </div>
          <div className="flex items-center space-x-1">
            <User size={14} />
            <span>{post.author}</span>
          </div>
        </div>

        {/* Título */}
        <h3 className="font-poppins font-semibold text-xl text-baby-gray mb-3 line-clamp-2 group-hover:text-baby-blue transition-colors">
          {post.title}
        </h3>

        {/* Extracto */}
        <p className="text-gray-600 leading-relaxed mb-6 flex-1 line-clamp-3">{post.excerpt}</p>

        {/* Botón de leer más */}
        <div className="mt-auto">
          <Button
            variant="ghost"
            onClick={onReadMore}
            className="p-0 h-auto text-baby-blue hover:text-blue-600 font-medium"
            aria-label={`Leer más sobre ${post.title}`}
          >
            Leer más
            <motion.div whileHover={{ x: 5 }} transition={{ duration: 0.2 }}>
              <ArrowRight size={16} />
            </motion.div>
          </Button>
        </div>
      </div>
    </Card>
  );
};

export default React.memo(BlogCard);
