package com.babycash.backend.config;

import com.babycash.backend.model.entity.BlogPost;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.entity.Testimonial;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.BlogPostRepository;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.repository.TestimonialRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Componente que carga datos de prueba en la base de datos al iniciar la aplicación.
 * Incluye productos de ejemplo y usuarios de demostración.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TestimonialRepository testimonialRepository;
    private final BlogPostRepository blogPostRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("🚀 Iniciando carga de datos de prueba...");
        
        // Cargar usuarios primero (necesarios para blog posts)
        if (userRepository.count() == 0) {
            loadUsers();
            log.info("✅ Usuarios cargados: {} usuarios", userRepository.count());
        } else {
            log.info("ℹ️ Usuarios ya existen, omitiendo carga");
        }
        
        // Cargar productos
        if (productRepository.count() == 0) {
            loadProducts();
            log.info("✅ Productos cargados: {} productos", productRepository.count());
        } else {
            log.info("ℹ️ Productos ya existen, omitiendo carga");
        }
        
        // Cargar testimonios
        if (testimonialRepository.count() == 0) {
            loadTestimonials();
            log.info("✅ Testimonios cargados: {} testimonios", testimonialRepository.count());
        } else {
            log.info("ℹ️ Testimonios ya existen, omitiendo carga");
        }
        
        // Cargar blog posts
        if (blogPostRepository.count() == 0) {
            loadBlogPosts();
            log.info("✅ Blog posts cargados: {} posts", blogPostRepository.count());
        } else {
            log.info("ℹ️ Blog posts ya existen, omitiendo carga");
        }
        
        log.info("🎉 Carga de datos completada exitosamente!");
    }

    /**
     * Carga usuarios de demostración (admin y usuario normal)
     */
    private void loadUsers() {
        // Usuario Admin
        User admin = User.builder()
                .email("admin@babycash.com")
                .password(passwordEncoder.encode("Admin123!"))
                .firstName("Administrador")
                .lastName("Sistema")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        // Usuario Demo
        User demo = User.builder()
                .email("demo@babycash.com")
                .password(passwordEncoder.encode("Demo123!"))
                .firstName("Usuario")
                .lastName("Demo")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        // Usuario de prueba adicional
        User testUser = User.builder()
                .email("maria.garcia@example.com")
                .password(passwordEncoder.encode("Maria123!"))
                .firstName("María")
                .lastName("García")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        userRepository.saveAll(List.of(admin, demo, testUser));
        
        log.info("👤 Usuarios creados:");
        log.info("   Admin: admin@babycash.com / Admin123!");
        log.info("   Demo: demo@babycash.com / Demo123!");
        log.info("   Test: maria.garcia@example.com / Maria123!");
    }

    /**
     * Carga productos de ejemplo en todas las categorías
     */
    private void loadProducts() {
        List<Product> products = List.of(
                // ===== ROPA (CLOTHING) =====
                Product.builder()
                        .name("Body de Algodón Manga Corta")
                        .description("Set de 3 bodies de algodón 100% orgánico, suaves y cómodos para la piel del bebé. Disponibles en colores pastel: blanco, rosa y celeste.")
                        .price(new BigDecimal("45000"))
                        .discountPrice(new BigDecimal("38000"))
                        .category(ProductCategory.CLOTHING)
                        .stock(50)
                        .imageUrl("https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Pijama Bebé con Piecitos")
                        .description("Pijama enteriza en algodón suave con pies antideslizantes. Cierre frontal con broches para facilitar el cambio de pañal. Tallas 0-12 meses.")
                        .price(new BigDecimal("52000"))
                        .category(ProductCategory.CLOTHING)
                        .stock(35)
                        .imageUrl("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Conjunto 3 Piezas Recién Nacido")
                        .description("Set incluye: gorro, body y pantalón. Material 100% algodón hipoalergénico. Perfecto para regalo de baby shower. Disponible en varios colores.")
                        .price(new BigDecimal("78000"))
                        .discountPrice(new BigDecimal("65000"))
                        .category(ProductCategory.CLOTHING)
                        .stock(25)
                        .imageUrl("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Calcetines Antideslizantes Pack x6")
                        .description("Pack de 6 pares de calcetines con suela antideslizante. Elástico suave que no aprieta. Diseños divertidos y coloridos. Tallas 0-24 meses.")
                        .price(new BigDecimal("32000"))
                        .category(ProductCategory.CLOTHING)
                        .stock(60)
                        .imageUrl("https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                // ===== JUGUETES (TOYS) =====
                Product.builder()
                        .name("Sonajero Musical Montessori")
                        .description("Sonajero de madera natural con cascabeles. Estimula el desarrollo sensorial y motor. Diseño ergonómico fácil de agarrar. Certificado libre de tóxicos.")
                        .price(new BigDecimal("28000"))
                        .category(ProductCategory.TOYS)
                        .stock(45)
                        .imageUrl("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Gimnasio de Actividades Musical")
                        .description("Gimnasio con arco ajustable, espejo seguro, 5 juguetes colgantes y música. Alfombra acolchada lavable. Estimula el desarrollo motor y visual del bebé.")
                        .price(new BigDecimal("185000"))
                        .discountPrice(new BigDecimal("155000"))
                        .category(ProductCategory.TOYS)
                        .stock(15)
                        .imageUrl("https://images.unsplash.com/photo-1567359781514-3b964e2b04d6?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Cubos Apilables de Tela")
                        .description("Set de 6 cubos blandos de diferentes tamaños y colores. Incluyen números, letras y texturas. Seguros y lavables. Ideales para bebés de 6 meses+.")
                        .price(new BigDecimal("42000"))
                        .category(ProductCategory.TOYS)
                        .stock(30)
                        .imageUrl("https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Peluche Musical Luz Nocturna")
                        .description("Osito de peluche hipoalergénico con proyector de estrellas y 10 melodías relajantes. Temporizador automático. Ayuda al bebé a dormir tranquilo.")
                        .price(new BigDecimal("95000"))
                        .discountPrice(new BigDecimal("78000"))
                        .category(ProductCategory.TOYS)
                        .stock(20)
                        .imageUrl("https://images.unsplash.com/photo-1530325553241-4f6e7690cf36?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                // ===== ALIMENTACIÓN (FOOD) =====
                Product.builder()
                        .name("Papilla Orgánica Manzana y Pera")
                        .description("Pack de 6 frascos de papilla 100% orgánica. Sin azúcares añadidos, sin conservantes. Para bebés de 6+ meses. Frutas cultivadas localmente.")
                        .price(new BigDecimal("38000"))
                        .category(ProductCategory.FOOD)
                        .stock(55)
                        .imageUrl("https://images.unsplash.com/photo-1502301103665-0b95cc738daf?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Cereal Infantil Fortificado")
                        .description("Cereal de arroz fortificado con hierro y vitaminas. Fácil digestión. Sin gluten. Ideal como primer alimento sólido. Caja de 400g.")
                        .price(new BigDecimal("24000"))
                        .category(ProductCategory.FOOD)
                        .stock(40)
                        .imageUrl("https://images.unsplash.com/photo-1505576399279-565b52d4ac71?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Snacks de Manzana Deshidratada")
                        .description("Rodajas de manzana 100% natural deshidratada. Sin azúcar añadido. Fáciles de disolver. Ideales para bebés que están aprendiendo a masticar. Pack 50g.")
                        .price(new BigDecimal("18000"))
                        .category(ProductCategory.FOOD)
                        .stock(70)
                        .imageUrl("https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                // ===== MUEBLES (FURNITURE) =====
                Product.builder()
                        .name("Cuna Convertible 4 en 1")
                        .description("Cuna que crece con tu bebé: cuna tradicional, cuna baja, cama junior y sofá. Madera maciza certificada. Incluye colchón ortopédico. Varios colores.")
                        .price(new BigDecimal("850000"))
                        .discountPrice(new BigDecimal("720000"))
                        .category(ProductCategory.FURNITURE)
                        .stock(8)
                        .imageUrl("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Cambiador de Pañales con Gavetas")
                        .description("Mueble cambiador con 3 gavetas espaciosas. Superficie acolchada impermeable. Barandas de seguridad. Altura ergonómica. Madera resistente al agua.")
                        .price(new BigDecimal("420000"))
                        .category(ProductCategory.FURNITURE)
                        .stock(12)
                        .imageUrl("https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Silla de Comer Ajustable")
                        .description("Silla alta con 7 alturas y 3 posiciones de bandeja. Respaldo reclinable. Bandeja removible apta para lavavajillas. Arnés de 5 puntos. Plegable.")
                        .price(new BigDecimal("320000"))
                        .discountPrice(new BigDecimal("275000"))
                        .category(ProductCategory.FURNITURE)
                        .stock(10)
                        .imageUrl("https://images.unsplash.com/photo-1612428146799-32c1e08fa5cd?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                // ===== ACCESORIOS (ACCESSORIES) =====
                Product.builder()
                        .name("Chupetes Ortodónticos Pack x2")
                        .description("Pack de 2 chupetes de silicona médica. Diseño ortodóntico que respeta el desarrollo bucal. Incluye caja esterilizadora. Sin BPA. 0-6 meses.")
                        .price(new BigDecimal("22000"))
                        .category(ProductCategory.ACCESSORIES)
                        .stock(80)
                        .imageUrl("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Biberones Anticólicos 260ml x3")
                        .description("Set de 3 biberones con sistema anticólico patentado. Tetinas de flujo lento. Libre de BPA. Fácil limpieza. Incluye cepillo limpiador.")
                        .price(new BigDecimal("68000"))
                        .category(ProductCategory.ACCESSORIES)
                        .stock(45)
                        .imageUrl("https://images.unsplash.com/photo-1583003528656-bdee97c40f5e?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Baberos Impermeables Pack x5")
                        .description("Set de 5 baberos con bolsillo recolector. Material impermeable fácil de limpiar. Cierre ajustable. Diseños coloridos y divertidos. Libre de BPA.")
                        .price(new BigDecimal("35000"))
                        .category(ProductCategory.ACCESSORIES)
                        .stock(65)
                        .imageUrl("https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Portabebé Ergonómico Premium")
                        .description("Portabebé ajustable con soporte lumbar. 4 posiciones de carga. Tela transpirable. Distribuye el peso uniformemente. Soporta hasta 20kg. Varios colores.")
                        .price(new BigDecimal("185000"))
                        .discountPrice(new BigDecimal("155000"))
                        .category(ProductCategory.ACCESSORIES)
                        .stock(18)
                        .imageUrl("https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                // ===== SALUD (HEALTHCARE) =====
                Product.builder()
                        .name("Termómetro Digital Infrarrojo")
                        .description("Termómetro sin contacto para frente y objetos. Lectura en 1 segundo. Alarma de fiebre. Pantalla LCD retroiluminada. Memoria de 20 lecturas.")
                        .price(new BigDecimal("75000"))
                        .category(ProductCategory.HEALTHCARE)
                        .stock(30)
                        .imageUrl("https://images.unsplash.com/photo-1584516150693-b5e5c8b4c1f3?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Kit de Aseo Bebé 10 Piezas")
                        .description("Set completo: cortaúñas, lima, cepillo, peine, tijeras punta roma, aspirador nasal, cepillo dental, masajeador encías. Estuche organizador incluido.")
                        .price(new BigDecimal("48000"))
                        .category(ProductCategory.HEALTHCARE)
                        .stock(35)
                        .imageUrl("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Aspirador Nasal Eléctrico")
                        .description("Aspirador nasal suave y efectivo. 3 niveles de succión. Silencioso. Boquillas de silicona lavables. Funciona con baterías. Fácil de limpiar.")
                        .price(new BigDecimal("62000"))
                        .category(ProductCategory.HEALTHCARE)
                        .stock(25)
                        .imageUrl("https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                // ===== LIBROS (BOOKS) =====
                Product.builder()
                        .name("Libro de Tela Animales Crujientes")
                        .description("Libro sensorial de tela con texturas variadas. Sonidos crujientes en cada página. Colores contrastantes. Estimula el desarrollo cognitivo. Lavable.")
                        .price(new BigDecimal("32000"))
                        .category(ProductCategory.BOOKS)
                        .stock(40)
                        .imageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Libros de Baño Pack x3")
                        .description("Set de 3 libros impermeables para la bañera. Ilustraciones coloridas de animales marinos. Fáciles de limpiar. Flotan en el agua. Sin BPA.")
                        .price(new BigDecimal("28000"))
                        .category(ProductCategory.BOOKS)
                        .stock(50)
                        .imageUrl("https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Cuentos Clásicos para Bebés")
                        .description("Libro de cartón grueso con 5 cuentos clásicos adaptados. Ilustraciones grandes y coloridas. Esquinas redondeadas. Perfecto para lectura antes de dormir.")
                        .price(new BigDecimal("45000"))
                        .category(ProductCategory.BOOKS)
                        .stock(35)
                        .imageUrl("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                // ===== OTROS (OTHER) =====
                Product.builder()
                        .name("Monitor de Bebé con Cámara HD")
                        .description("Monitor con video HD, visión nocturna infrarroja, audio bidireccional. Pantalla 5 pulgadas. Canciones de cuna integradas. Alcance 300m. Batería recargable.")
                        .price(new BigDecimal("380000"))
                        .discountPrice(new BigDecimal("320000"))
                        .category(ProductCategory.OTHER)
                        .stock(10)
                        .imageUrl("https://images.unsplash.com/photo-1589986680623-364a2f63f7ac?w=500")
                        .featured(true)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Bañera Plegable con Termómetro")
                        .description("Bañera ergonómica con indicador de temperatura incorporado. Plegable para ahorrar espacio. Base antideslizante. Soporte para recién nacidos incluido.")
                        .price(new BigDecimal("125000"))
                        .category(ProductCategory.OTHER)
                        .stock(15)
                        .imageUrl("https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500")
                        .featured(false)
                        .enabled(true)
                        .build(),

                Product.builder()
                        .name("Coche Travel System 3 en 1")
                        .description("Sistema completo: coche, silla para auto y moisés. Ruedas giratorias 360°. Suspensión independiente. Capota extensible UPF 50+. Plegado compacto.")
                        .price(new BigDecimal("1250000"))
                        .discountPrice(new BigDecimal("1050000"))
                        .category(ProductCategory.OTHER)
                        .stock(5)
                        .imageUrl("https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=500")
                        .featured(true)
                        .enabled(true)
                        .build()
        );

        productRepository.saveAll(products);
        
        log.info("🛍️ Productos creados por categoría:");
        log.info("   CLOTHING (Ropa): 4 productos");
        log.info("   TOYS (Juguetes): 4 productos");
        log.info("   FOOD (Alimentación): 3 productos");
        log.info("   FURNITURE (Muebles): 3 productos");
        log.info("   ACCESSORIES (Accesorios): 4 productos");
        log.info("   HEALTHCARE (Salud): 3 productos");
        log.info("   BOOKS (Libros): 3 productos");
        log.info("   OTHER (Otros): 3 productos");
    }

    /**
     * Carga testimonios de clientes satisfechos
     */
    private void loadTestimonials() {
        List<Testimonial> testimonials = List.of(
                Testimonial.builder()
                        .name("María Rodríguez")
                        .message("Excelente servicio, los productos llegaron en perfecto estado y muy rápido. La calidad de la ropa es excepcional, mi bebé se ve hermoso y cómodo.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=1")
                        .location("Bogotá, Colombia")
                        .approved(true)
                        .featured(true)
                        .build(),

                Testimonial.builder()
                        .name("Carlos Méndez")
                        .message("Los juguetes educativos que compré son increíbles. Mi hija de 1 año está fascinada con el sonajero Montessori. Definitivamente volveré a comprar aquí.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=12")
                        .location("Medellín, Colombia")
                        .approved(true)
                        .featured(true)
                        .build(),

                Testimonial.builder()
                        .name("Ana Patricia López")
                        .message("Me encantó el conjunto de recién nacido que compré para mi baby shower. La calidad es premium y el precio muy justo. Recomendado 100%.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=5")
                        .location("Cali, Colombia")
                        .approved(true)
                        .featured(true)
                        .build(),

                Testimonial.builder()
                        .name("Jorge Andrés Silva")
                        .message("El monitor de bebé con cámara HD superó mis expectativas. La visión nocturna es excelente y la batería dura muchísimo. Muy satisfecho con la compra.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=14")
                        .location("Barranquilla, Colombia")
                        .approved(true)
                        .featured(true)
                        .build(),

                Testimonial.builder()
                        .name("Laura Martínez")
                        .message("Compré la cuna colecho y es perfecta. Fácil de instalar, muy segura y el diseño es hermoso. Mi bebé duerme muy tranquilo.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=9")
                        .location("Cartagena, Colombia")
                        .approved(true)
                        .featured(true)
                        .build(),

                Testimonial.builder()
                        .name("Pedro Sánchez")
                        .message("Excelente atención al cliente. Tuve una consulta sobre tallas y me respondieron súper rápido. Los productos son de muy buena calidad.")
                        .rating(4)
                        .avatar("https://i.pravatar.cc/150?img=13")
                        .location("Bucaramanga, Colombia")
                        .approved(true)
                        .featured(false)
                        .build(),

                Testimonial.builder()
                        .name("Isabella García")
                        .message("El coche travel system 3 en 1 es una maravilla. Muy práctico, fácil de usar y de excelente calidad. Vale cada peso.")
                        .rating(5)
                        .avatar("https://i.pravatar.cc/150?img=10")
                        .location("Santa Marta, Colombia")
                        .approved(true)
                        .featured(false)
                        .build()
        );

        testimonialRepository.saveAll(testimonials);
        
        log.info("💬 Testimonios creados:");
        log.info("   Total: {} testimonios", testimonials.size());
        log.info("   Destacados: {} testimonios", testimonials.stream().filter(Testimonial::isFeatured).count());
    }

    /**
     * Carga posts de blog con contenido educativo para padres
     */
    private void loadBlogPosts() {
        User admin = userRepository.findByEmail("admin@babycash.com")
                .orElseThrow(() -> new IllegalStateException("Admin user not found"));

        List<BlogPost> posts = List.of(
                BlogPost.builder()
                        .title("10 Consejos Esenciales para Padres Primerizos")
                        .slug("10-consejos-esenciales-padres-primerizos")
                        .excerpt("Descubre los mejores consejos que todo padre primerizo debe conocer para cuidar de su bebé con confianza y amor.")
                        .content("""
                                <h2>Bienvenidos al Maravilloso Mundo de la Paternidad</h2>
                                <p>Ser padre por primera vez es una experiencia transformadora llena de alegría, desafíos y aprendizaje continuo. Aquí te compartimos 10 consejos esenciales:</p>
                                
                                <h3>1. Confía en tu Instinto</h3>
                                <p>Los padres tienen una conexión especial con sus bebés. Aprende a confiar en tu intuición mientras observas y conoces a tu hijo.</p>
                                
                                <h3>2. Establece una Rutina</h3>
                                <p>Los bebés se sienten seguros con rutinas predecibles. Establece horarios consistentes para dormir, comer y jugar.</p>
                                
                                <h3>3. No Temas Pedir Ayuda</h3>
                                <p>Criar un bebé requiere apoyo. No dudes en pedir ayuda a familiares, amigos o profesionales cuando la necesites.</p>
                                
                                <h3>4. Cuida tu Propio Bienestar</h3>
                                <p>Recuerda que tu salud física y mental es crucial. Descansa cuando puedas y busca momentos para ti.</p>
                                
                                <h3>5. Documenta los Momentos Especiales</h3>
                                <p>Los bebés crecen increíblemente rápido. Toma fotos y videos para preservar estos preciosos recuerdos.</p>
                                
                                <p>Recuerda, cada bebé es único y cada familia encuentra su propio camino. ¡Disfruta este hermoso viaje!</p>
                                """)
                        .author(admin)
                        .imageUrl("https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=800")
                        .published(true)
                        .featured(true)
                        .tags(List.of("Consejos", "Padres Primerizos", "Crianza"))
                        .build(),

                BlogPost.builder()
                        .title("Guía Completa de Alimentación para Bebés de 0 a 12 Meses")
                        .slug("guia-alimentacion-bebes-0-12-meses")
                        .excerpt("Todo lo que necesitas saber sobre la alimentación de tu bebé durante su primer año de vida, desde la lactancia hasta la alimentación complementaria.")
                        .content("""
                                <h2>Nutrición en el Primer Año de Vida</h2>
                                <p>La alimentación durante el primer año es fundamental para el desarrollo saludable de tu bebé.</p>
                                
                                <h3>0-6 Meses: Lactancia Exclusiva</h3>
                                <p>La leche materna o fórmula proporciona todos los nutrientes necesarios. Se recomienda la lactancia materna exclusiva durante los primeros 6 meses.</p>
                                <ul>
                                  <li>Alimentar a demanda, generalmente cada 2-3 horas</li>
                                  <li>Observar señales de hambre del bebé</li>
                                  <li>Mantener hidratación adecuada</li>
                                </ul>
                                
                                <h3>6-8 Meses: Introducción de Sólidos</h3>
                                <p>Comienza la alimentación complementaria con purés suaves y papillas.</p>
                                <ul>
                                  <li>Introducir un alimento nuevo a la vez</li>
                                  <li>Esperar 3-5 días entre nuevos alimentos</li>
                                  <li>Comenzar con vegetales y frutas</li>
                                </ul>
                                
                                <h3>9-12 Meses: Variedad y Texturas</h3>
                                <p>Aumenta la variedad de alimentos y texturas gradualmente.</p>
                                <p>Consulta siempre con tu pediatra antes de introducir nuevos alimentos, especialmente en casos de alergias familiares.</p>
                                """)
                        .author(admin)
                        .imageUrl("https://images.unsplash.com/photo-1476703993599-0035a21b17a9?w=800")
                        .published(true)
                        .featured(true)
                        .tags(List.of("Alimentación", "Nutrición", "Bebés"))
                        .build(),

                BlogPost.builder()
                        .title("Cómo Crear un Ambiente Seguro para tu Bebé en Casa")
                        .slug("ambiente-seguro-bebe-casa")
                        .excerpt("Aprende a preparar tu hogar para que sea un espacio seguro y estimulante para el desarrollo de tu bebé.")
                        .content("""
                                <h2>Seguridad en el Hogar</h2>
                                <p>Crear un ambiente seguro es esencial para el bienestar de tu bebé y tu tranquilidad como padre.</p>
                                
                                <h3>Habitación del Bebé</h3>
                                <ul>
                                  <li>Cuna con barrotes separados máximo 6 cm</li>
                                  <li>Colchón firme que ajuste perfectamente</li>
                                  <li>Sin almohadas, mantas sueltas o juguetes en la cuna</li>
                                  <li>Monitor de bebé funcionando</li>
                                </ul>
                                
                                <h3>Áreas Comunes</h3>
                                <ul>
                                  <li>Protectores en enchufes</li>
                                  <li>Esquineros en muebles con bordes filosos</li>
                                  <li>Puertas de seguridad en escaleras</li>
                                  <li>Productos de limpieza y medicamentos bajo llave</li>
                                </ul>
                                
                                <h3>Baño</h3>
                                <ul>
                                  <li>Temperatura del agua entre 36-37°C</li>
                                  <li>Nunca dejar al bebé solo</li>
                                  <li>Alfombra antideslizante en bañera</li>
                                </ul>
                                
                                <p>Recuerda: la mejor seguridad es la supervisión constante combinada con un entorno preparado.</p>
                                """)
                        .author(admin)
                        .imageUrl("https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=800")
                        .published(true)
                        .featured(true)
                        .tags(List.of("Seguridad", "Hogar", "Prevención"))
                        .build()
        );

        blogPostRepository.saveAll(posts);
        
        log.info("📝 Blog posts creados:");
        log.info("   Total: {} posts", posts.size());
        log.info("   Publicados: {} posts", posts.stream().filter(BlogPost::getPublished).count());
        log.info("   Destacados: {} posts", posts.stream().filter(BlogPost::getFeatured).count());
    }
}
