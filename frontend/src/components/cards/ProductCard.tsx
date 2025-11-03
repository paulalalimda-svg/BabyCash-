import React from 'react';
import { motion } from 'framer-motion';
import { ShoppingCart, Eye } from 'lucide-react';
import type { Product } from '../../services/api';
import { useCart } from '../../contexts/CartContext';
import Button from '../ui/Button';
import Card from '../ui/Card';
import toast from 'react-hot-toast';

interface ProductCardProps {
  product: Product;
  onViewDetails?: () => void;
}

const ProductCard: React.FC<ProductCardProps> = ({ product, onViewDetails }) => {
  const { addToCart } = useCart();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.stopPropagation();
    
    if (product.stock <= 0) {
      toast.error('Producto agotado');
      return;
    }
    
    addToCart({
      id: String(product.id),
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.imageUrl,
    });
    
    // CartContext ya maneja el toast, no necesitamos uno aquí
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  return (
    <Card
      className="group overflow-hidden rounded-xl border border-gray-200 hover:shadow-md transition-all duration-300"
      hover={true}
    >
      {/* Imagen del producto */}
      <div className="relative mb-4 overflow-hidden rounded-lg bg-gray-100 aspect-square">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
          onError={(e) => {
            const target = e.target as HTMLImageElement;
            target.src = '/images/productos/placeholder.jpg';
          }}
        />

        {/* Overlay con acciones */}
        <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors duration-300 flex items-center justify-center">
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            whileHover={{ opacity: 1, scale: 1 }}
            className="opacity-0 group-hover:opacity-100 transition-opacity duration-300 space-x-2"
          >
            {onViewDetails && (
              <Button
                variant="secondary"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  onViewDetails();
                }}
                className="shadow-lg"
                aria-label={`Ver detalles de ${product.name}`}
              >
                <Eye size={16} />
              </Button>
            )}
          </motion.div>
        </div>

        {/* Badge de categoría */}
        <div className="absolute top-3 left-3">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-baby-blue/90 text-white backdrop-blur-sm">
            {product.category}
          </span>
        </div>
        
        {/* Badge de stock bajo */}
        {product.stock > 0 && product.stock < 10 && (
          <div className="absolute top-3 right-3">
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-500/90 text-white backdrop-blur-sm">
              ¡Solo {product.stock}!
            </span>
          </div>
        )}
        
        {/* Badge de agotado */}
        {product.stock <= 0 && (
          <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
            <span className="text-white font-bold text-lg">AGOTADO</span>
          </div>
        )}
      </div>

      {/* Información del producto */}
      <div className="space-y-3">
        <div>
          <h3 className="font-poppins font-semibold text-lg text-baby-gray line-clamp-2">
            {product.name}
          </h3>
        </div>

        <p className="text-sm text-gray-600 line-clamp-2 leading-relaxed">
          {product.description}
        </p>

        {/* Precio y detalle */}
        <div className="space-y-2">
          <div className="flex items-baseline justify-between">
            {product.discountPrice ? (
              <>
                <div className="flex flex-col">
                  <span className="text-sm text-gray-400 line-through">
                    {formatPrice(product.price)}
                  </span>
                  <span className="text-2xl font-bold text-baby-pink">
                    {formatPrice(product.discountPrice)}
                  </span>
                </div>
                <span className="text-sm font-semibold text-green-600 bg-green-50 px-2 py-1 rounded">
                  {Math.round((1 - product.discountPrice / product.price) * 100)}% OFF
                </span>
              </>
            ) : (
              <span className="text-2xl font-bold text-baby-gray">
                {formatPrice(product.price)}
              </span>
            )}
          </div>
        </div>

        {/* Botón de agregar al carrito */}
        <Button
          variant="primary"
          fullWidth
          onClick={handleAddToCart}
          disabled={product.stock <= 0}
          className="transition-all duration-300"
          aria-label={`Agregar ${product.name} al carrito`}
        >
          <ShoppingCart size={16} className="mr-2" />
          {product.stock <= 0 ? 'Agotado' : 'Agregar al Carrito'}
        </Button>
      </div>
    </Card>
  );
};

export default React.memo(ProductCard);
