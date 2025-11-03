import { z } from 'zod';

/**
 * Schema de validación para Product
 */
export const ProductSchema = z.object({
  id: z.number(),
  name: z.string().min(1),
  description: z.string(),
  price: z.number().positive(),
  stock: z.number().int().nonnegative(),
  category: z.string().min(1),
  imageUrl: z.string().url().optional().nullable(),
  featured: z.boolean(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime(),
});

/**
 * Schema de validación para BlogPost
 */
export const BlogPostSchema = z.object({
  id: z.number(),
  title: z.string().min(5).max(200),
  content: z.string().min(100),
  excerpt: z.string().min(20).max(500),
  imageUrl: z.string().url().optional().nullable(),
  tags: z.array(z.string()).default([]),
  featured: z.boolean().default(false),
  published: z.boolean().default(false),
  publishedAt: z.string().datetime().optional().nullable(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime(),
  author: z.object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    email: z.string().email(),
  }),
});

/**
 * Schema de validación para Testimonial
 */
export const TestimonialSchema = z.object({
  id: z.number(),
  name: z.string().min(2).max(100),
  message: z.string().min(10).max(1000),
  rating: z.number().int().min(1).max(5),
  location: z.string().max(100).optional().nullable(),
  approved: z.boolean().default(false),
  featured: z.boolean().default(false),
  createdAt: z.string().datetime(),
});

/**
 * Schema de validación para ContactMessage
 */
export const ContactMessageSchema = z.object({
  id: z.number(),
  name: z.string().min(2).max(100),
  email: z.string().email(),
  phone: z.string().max(20).optional().nullable(),
  subject: z.string().min(3).max(200),
  message: z.string().min(10).max(2000),
  status: z.enum(['NEW', 'READ', 'REPLIED', 'ARCHIVED']).default('NEW'),
  ipAddress: z.string().optional().nullable(),
  createdAt: z.string().datetime(),
});

/**
 * Schema de validación para User
 */
export const UserSchema = z.object({
  id: z.number(),
  firstName: z.string().min(2).max(50),
  lastName: z.string().min(2).max(50),
  email: z.string().email(),
  role: z.enum(['USER', 'ADMIN']).default('USER'),
  createdAt: z.string().datetime(),
});

/**
 * Schema de validación para Order
 */
export const OrderSchema = z.object({
  id: z.number(),
  userId: z.number(),
  status: z.enum(['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED']),
  total: z.number().positive(),
  shippingAddress: z.string().min(10),
  paymentMethod: z.string(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime(),
});

/**
 * Schema de validación para PagedResponse
 */
export const PagedResponseSchema = <T extends z.ZodTypeAny>(itemSchema: T) =>
  z.object({
    content: z.array(itemSchema),
    totalPages: z.number().int().nonnegative(),
    totalElements: z.number().int().nonnegative(),
    size: z.number().int().positive(),
    number: z.number().int().nonnegative(),
    first: z.boolean(),
    last: z.boolean(),
    empty: z.boolean(),
  });

/**
 * Helper function para validar datos de API
 */
export function validateApiData<T>(schema: z.ZodSchema<T>, data: unknown): T {
  try {
    return schema.parse(data);
  } catch (error) {
    if (error instanceof z.ZodError) {
      console.error('❌ Validation error:', error.errors);
      throw new Error(`Datos inválidos recibidos de la API: ${error.errors.map(e => e.message).join(', ')}`);
    }
    throw error;
  }
}

/**
 * Helper function para validación segura (no lanza errores)
 */
export function safeValidateApiData<T>(schema: z.ZodSchema<T>, data: unknown): { success: true; data: T } | { success: false; error: z.ZodError } {
  const result = schema.safeParse(data);
  if (result.success) {
    return { success: true, data: result.data };
  }
  return { success: false, error: result.error };
}

// Export types inferidos de los schemas
export type ValidatedProduct = z.infer<typeof ProductSchema>;
export type ValidatedBlogPost = z.infer<typeof BlogPostSchema>;
export type ValidatedTestimonial = z.infer<typeof TestimonialSchema>;
export type ValidatedContactMessage = z.infer<typeof ContactMessageSchema>;
export type ValidatedUser = z.infer<typeof UserSchema>;
export type ValidatedOrder = z.infer<typeof OrderSchema>;
