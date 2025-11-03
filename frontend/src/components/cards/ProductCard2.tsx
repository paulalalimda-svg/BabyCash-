import React from 'react';
import { motion } from 'framer-motion';
import { ShoppingCart, Eye } from 'lucide-react';
import type { Product } from '../../types';
import { useCart } from '../../contexts/CartContext';
import Button from '../ui/Button';
import Card from '../ui/Card';

interface ProductCardProps {
  product: Product;
  onViewDetails?: () => void;
}

const ProductCard2: React.FC<ProductCardProps> = ({ product, onViewDetails }) => {
  const { addToCart } = useCart();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.stopPropagation();
    addToCart({
      id: product.id_producto,
      name: product.nombre_producto,
      price: product.precio_producto,
      quantity: 1,
      image: product.imagen_producto,
    });
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
          src={product.imagen_producto}
          alt={product.nombre_producto}
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
                aria-label={`Ver detalles de ${product.nombre_producto}`}
              >
                <Eye size={16} />
              </Button>
            )}
          </motion.div>
        </div>

        {/* Badge de categoría */}
        <div className="absolute top-3 left-3">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-baby-blue/90 text-white backdrop-blur-sm">
            {product.categoria_producto}
          </span>
        </div>
      </div>

      {/* Información del producto */}
      <div className="space-y-3">
        <div>
          <h3 className="font-poppins font-semibold text-lg text-baby-gray line-clamp-2">
            {product.nombre_producto}
          </h3>
          <p className="text-sm text-gray-600 mt-1">{product.opciones}</p>
        </div>

        <p className="text-sm text-gray-600 line-clamp-2 leading-relaxed">{product.descripcion_producto}</p>

        {/* Precio y detalle */}
        <div className="space-y-2">
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold text-baby-gray">{formatPrice(product.precio_producto)}</span>
            <span className="text-sm text-gray-500">{product.detalle_precio}</span>
          </div>
        </div>

        {/* Botón de agregar al carrito */}
        <Button
          variant="primary"
          fullWidth
          onClick={handleAddToCart}
          className="mt-4"
          aria-label={`Agregar ${product.nombre_producto} al carrito`}
        >
          <ShoppingCart size={18} />
          Agregar al Carrito
        </Button>
      </div>
    </Card>
  );
};

export default ProductCard2;
