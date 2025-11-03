import { motion, type HTMLMotionProps } from 'framer-motion';
import { Loader2 } from 'lucide-react';
import clsx from 'clsx';
import type { ReactNode } from 'react';

interface ButtonProps extends HTMLMotionProps<'button'> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
  fullWidth?: boolean;
  children: ReactNode;
}

const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  loading = false,
  fullWidth = false,
  disabled,
  children,
  className,
  onClick,
  ...props
}) => {
  const baseClasses =
    'font-inter font-medium rounded-lg transition-all duration-200 transform focus-ring disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2';

  const variantClasses = {
    primary: 'bg-baby-blue hover:bg-blue-400 text-white shadow-md hover:shadow-lg',
    secondary: 'bg-baby-pink hover:bg-pink-400 text-white shadow-md hover:shadow-lg',
    outline: 'border-2 border-baby-blue text-baby-blue hover:bg-baby-blue hover:text-white',
    ghost: 'text-baby-gray hover:bg-gray-100',
    danger: 'bg-red-500 hover:bg-red-600 text-white shadow-md hover:shadow-lg',
  };

  const sizeClasses = {
    sm: 'px-4 py-2 text-sm min-h-[36px]',
    md: 'px-6 py-3 text-base min-h-[44px]',
    lg: 'px-8 py-4 text-lg min-h-[52px]',
  };

  const buttonClasses = clsx(
    baseClasses,
    variantClasses[variant],
    sizeClasses[size],
    fullWidth && 'w-full',
    className
  );

  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    if (!loading && !disabled && onClick) {
      onClick(e);
    }
  };

  return (
    <motion.button
      className={buttonClasses}
      disabled={disabled || loading}
      onClick={handleClick}
      whileHover={!disabled && !loading ? { scale: 1.02 } : undefined}
      whileTap={!disabled && !loading ? { scale: 0.98 } : undefined}
      {...props}
    >
      {loading && (
        <Loader2
          className="animate-spin"
          size={size === 'sm' ? 16 : size === 'lg' ? 20 : 18}
          aria-label="Cargando"
        />
      )}
      {children}
    </motion.button>
  );
};

export default Button;
