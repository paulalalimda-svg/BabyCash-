import { motion } from 'framer-motion';
import clsx from 'clsx';
import type { ReactNode } from 'react';

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  padding?: 'sm' | 'md' | 'lg';
  onClick?: () => void;
}

const Card: React.FC<CardProps> = ({
  children,
  className,
  hover = false,
  padding = 'md',
  onClick,
}) => {
  const paddingClasses = {
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  const cardClasses = clsx('card', paddingClasses[padding], onClick && 'cursor-pointer', className);

  const CardComponent = onClick ? motion.button : motion.div;

  return (
    <CardComponent
      className={cardClasses}
      onClick={onClick}
      whileHover={undefined}
      transition={{ duration: 0 }}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
    >
      {children}
    </CardComponent>
  );
};

export default Card;
