import React from 'react';
import { Star, Quote } from 'lucide-react';
import type { Testimonial } from '../../services/api';
import Card from '../ui/Card';

interface TestimonialCardProps {
  testimonial: Testimonial;
}

const TestimonialCard: React.FC<TestimonialCardProps> = ({ testimonial }) => {
  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, index) => (
      <Star
        key={index}
        size={16}
        className={`${index < rating ? 'text-yellow-400 fill-current' : 'text-gray-300'}`}
      />
    ));
  };

  return (
    <Card className="relative h-full border border-gray-200 shadow-lg hover:shadow-xl transition-all duration-300">
      {/* Icono de comillas */}
      <div className="absolute top-4 right-4 text-baby-blue/20">
        <Quote size={24} />
      </div>

      <div className="flex flex-col h-full">
        {/* Rating */}
        <div className="flex items-center space-x-1 mb-4">{renderStars(testimonial.rating)}</div>

        {/* Mensaje */}
        <blockquote className="flex-1 text-gray-600 leading-relaxed mb-6 break-words overflow-wrap-anywhere">
          {testimonial.message}
        </blockquote>

        {/* Información del cliente */}
        <div className="flex items-center space-x-3">
          <div className="flex-shrink-0">
            {testimonial.avatar ? (
              <img
                src={testimonial.avatar}
                alt={`Avatar de ${testimonial.name}`}
                className="w-12 h-12 rounded-full object-cover"
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.style.display = 'none';
                  target.nextElementSibling?.classList.remove('hidden');
                }}
              />
            ) : null}
            <div
              className={`${testimonial.avatar ? 'hidden' : ''} w-12 h-12 rounded-full bg-gradient-to-br from-baby-blue to-baby-pink flex items-center justify-center`}
            >
              <span className="text-white font-semibold text-lg">
                {testimonial.name.charAt(0).toUpperCase()}
              </span>
            </div>
          </div>

          <div className="flex-1 min-w-0">
            <h4 className="font-poppins font-semibold text-baby-gray truncate">
              {testimonial.name}
            </h4>
            {testimonial.location && (
              <p className="text-sm text-gray-500 truncate">{testimonial.location}</p>
            )}
          </div>
        </div>
      </div>
    </Card>
  );
};

export default React.memo(TestimonialCard);
