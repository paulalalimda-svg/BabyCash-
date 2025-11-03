import React from 'react';
import { motion } from 'framer-motion';
import Card from '../components/ui/Card';
import { Heart, Users, Target, Clock, CheckCircle, Star } from 'lucide-react';

const Nosotros: React.FC = () => {
  const values = [
    {
      icon: Heart,
      title: 'Amor por los Bebés',
      description:
        'Cada producto es seleccionado pensando en el bienestar y la felicidad de tu pequeño tesoro.',
    },
    {
      icon: Users,
      title: 'Familia',
      description:
        'Entendemos las necesidades de las familias porque también somos padres y madres.',
    },
    {
      icon: Target,
      title: 'Calidad',
      description:
        'Solo ofrecemos productos de las mejores marcas con estándares internacionales de calidad.',
    },
    {
      icon: Star,
      title: 'Confianza',
      description: 'Más de 10 años siendo el aliado de confianza de miles de familias en Colombia.',
    },
  ];

  const timeline = [
    {
      step: '01',
      title: 'Consulta Personalizada',
      description:
        'Te asesoramos sobre los mejores productos según la edad y necesidades de tu bebé.',
    },
    {
      step: '02',
      title: 'Selección Cuidadosa',
      description: 'Elegimos cada artículo con amor, verificando calidad y seguridad.',
    },
    {
      step: '03',
      title: 'Entrega Segura',
      description: 'Empacamos con cuidado especial y entregamos en tiempo record.',
    },
    {
      step: '04',
      title: 'Seguimiento Total',
      description: 'Te acompañamos después de la compra para asegurar tu satisfacción.',
    },
  ];

  return (
    <div className="min-h-screen bg-baby-light pt-20">
      {/* Hero Section */}
      <section className="relative py-16 px-4 overflow-hidden">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-center mb-16"
          >
            <h1 className="text-4xl md:text-5xl font-bold font-poppins text-baby-gray mb-6">
              Conoce a <span className="text-baby-pink">Baby Cash</span>
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto font-inter leading-relaxed">
              Somos Pañalera y Variedades {`Soffy's`}, tu aliado de confianza en el cuidado de los
              más pequeños. Con más de una década de experiencia, nos dedicamos a brindar productos
              de calidad superior para que cada momento con tu bebé sea especial.
            </p>
          </motion.div>

          {/* Hero Image */}
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="relative mb-16"
          >
            <div className="aspect-video w-full max-w-6xl mx-auto rounded-3xl flex items-center justify-center shadow-2xl bg-cover bg-center bg-[url('https://cdn.pixabay.com/photo/2015/04/11/20/17/feet-718146_640.jpg')]">
              <div className="text-center text-white ">
                <Heart className="w-20 h-20 mx-auto mb-4" />
                <h2 className="text-3xl font-bold font-poppins">Tu familia es nuestra prioridad</h2>
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Mission, Vision, Values */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4">
              Nuestros Valores
            </h2>
            <p className="text-lg text-gray-600 font-inter">
              Los principios que nos guían cada día
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, index) => (
              <motion.div
                key={value.title}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
                whileHover={{ scale: 1.05 }}
                className="group"
              >
                <Card className="h-full p-6 text-center hover:shadow-xl transition-all duration-300 border-2 border-baby-purple/20 hover:border-baby-purple">
                  <div className="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-baby-purple to-baby-pink rounded-full flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                    <value.icon className="w-8 h-8 text-white" />
                  </div>
                  <h3 className="text-xl font-bold font-poppins text-baby-gray mb-3">
                    {value.title}
                  </h3>
                  <p className="text-gray-600 font-inter leading-relaxed">{value.description}</p>
                </Card>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Mission & Vision */}
      <section className="py-16 px-4 bg-baby-light">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
            <motion.div
              initial={{ opacity: 0, x: -50 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.8 }}
            >
              <Card className="h-full p-8 bg-gradient-to-br from-baby-blue/10 to-baby-mint/10 border-baby-blue/20">
                <Target className="w-12 h-12 text-baby-blue mb-6" />
                <h3 className="text-2xl font-bold font-poppins text-baby-gray mb-4">
                  Nuestra Misión
                </h3>
                <p className="text-gray-600 font-inter leading-relaxed text-lg">
                  Brindar a las familias colombianas productos de alta calidad para el cuidado y
                  bienestar de sus bebés, con un servicio personalizado, precios justos y la
                  confianza de más de una década de experiencia en el mercado.
                </p>
              </Card>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, x: 50 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.8 }}
            >
              <Card className="h-full p-8 bg-gradient-to-br from-baby-pink/10 to-baby-mint/10 border-baby-pink/20">
                <Heart className="w-12 h-12 text-baby-pink mb-6" />
                <h3 className="text-2xl font-bold font-poppins text-baby-gray mb-4">
                  Nuestra Visión
                </h3>
                <p className="text-gray-600 font-inter leading-relaxed text-lg">
                  Ser la pañalera líder en Colombia, reconocida por la excelencia en nuestros
                  productos y servicios, expandiendo nuestra presencia a nivel nacional y siendo el
                  primer aliado que las familias elijan para el cuidado de sus bebés.
                </p>
              </Card>
            </motion.div>
          </div>
        </div>
      </section>

      {/* Customer Service Timeline */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-12"
          >
            <Clock className="w-16 h-16 mx-auto mb-4 text-baby-pink" />
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-baby-gray mb-4">
              Nuestro Proceso de Atención
            </h2>
            <p className="text-lg text-gray-600 font-inter max-w-2xl mx-auto">
              Cada paso está diseñado para brindarte la mejor experiencia de compra
            </p>
          </motion.div>

          <div className="relative">
            {/* Timeline Line */}
            <div className="absolute left-1/2 top-0 bottom-0 w-0.5 bg-gradient-to-b from-baby-blue to-baby-pink transform -translate-x-1/2 hidden lg:block" />

            <div className="space-y-12 lg:space-y-16">
              {timeline.map((item, index) => (
                <motion.div
                  key={item.step}
                  initial={{ opacity: 0, y: 50 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.6, delay: index * 0.2 }}
                  className={`relative flex flex-col lg:flex-row items-center ${
                    index % 2 === 0 ? 'lg:flex-row-reverse' : ''
                  }`}
                >
                  {/* Step Number */}
                  <div className="relative z-10 w-16 h-16 bg-gradient-to-br from-baby-blue to-baby-pink rounded-full flex items-center justify-center shadow-lg mb-4 lg:mb-0 lg:mx-8">
                    <span className="text-white font-bold font-poppins text-lg">{item.step}</span>
                  </div>

                  {/* Content */}
                  <div className={`flex-1 max-w-md ${index % 2 === 0 ? 'lg:text-right' : ''}`}>
                    <Card className="p-6">
                      <h3 className="text-xl font-bold font-poppins text-baby-gray mb-3">
                        {item.title}
                      </h3>
                      <p className="text-gray-600 font-inter leading-relaxed">{item.description}</p>
                    </Card>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 px-4 bg-gradient-to-r from-baby-blue to-baby-pink">
        <div className="max-w-4xl mx-auto text-center">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            <CheckCircle className="w-16 h-16 mx-auto mb-6 text-white" />
            <h2 className="text-3xl md:text-4xl font-bold font-poppins text-white mb-6">
              ¿Listo para conocer nuestros productos?
            </h2>
            <p className="text-xl text-white/90 mb-8 font-inter">
              Descubre nuestra amplia gama de productos diseñados especialmente para tu bebé
            </p>
            <motion.a
              href="/productos"
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="inline-block bg-white text-baby-gray font-bold font-poppins py-4 px-8 rounded-full shadow-lg hover:shadow-xl transition-all duration-300"
            >
              Ver Productos
            </motion.a>
          </motion.div>
        </div>
      </section>
    </div>
  );
};

export default Nosotros;
