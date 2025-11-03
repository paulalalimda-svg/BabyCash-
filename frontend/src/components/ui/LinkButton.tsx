import { motion } from 'framer-motion';
import React from 'react';

interface LinkButtonProps extends React.ComponentProps<typeof motion.a> {
  size?: 'sm' | 'md' | 'lg';
  variant?: 'primary' | 'outline';
  children: React.ReactNode;
}

const sizeClasses = {
  sm: 'px-3 py-1 text-sm',
  md: 'px-4 py-2 text-base',
  lg: 'px-6 py-3 text-lg',
};

const variantClasses = {
  primary: 'bg-baby-blue text-white hover:bg-baby-pink',
  outline: 'border border-baby-blue text-baby-blue hover:bg-baby-blue hover:text-white',
};

export const LinkButton: React.FC<LinkButtonProps> = ({
  size = 'md',
  variant = 'primary',
  children,
  className = '',
  ...props
}) => {
  return (
    <motion.a
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
      {...props}
      className={`inline-flex items-center justify-center rounded-lg font-poppins font-semibold transition-all duration-300 ${sizeClasses[size]} ${variantClasses[variant]} ${className}`}
    >
      {children}
    </motion.a>
  );
};

export default LinkButton;
