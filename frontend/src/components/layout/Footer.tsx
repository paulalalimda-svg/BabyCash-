import { Link } from 'react-router-dom';
import { MapPin, Phone, Mail, Clock, Facebook, Instagram, Twitter } from 'lucide-react';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  const footerLinks = {
    navigation: [
      { href: '/', label: 'Inicio' },
      { href: '/nosotros', label: 'Nosotros' },
      { href: '/productos', label: 'Productos' },
      { href: '/blog', label: 'Blog' },
      { href: '/contacto', label: 'Contacto' },
    ],
    legal: [
      { href: '/terminos', label: 'Términos y Condiciones' },
      { href: '/privacidad', label: 'Política de Privacidad' },
      { href: '/devoluciones', label: 'Política de Devoluciones' },
      { href: '/envios', label: 'Información de Envíos' },
    ],
  };

  return (
    <footer className="bg-gradient-to-br from-baby-blue/10 via-baby-pink/10 to-baby-mint/10 border-t border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 lg:gap-12">
          {/* Información de la empresa */}
          <div className="space-y-6">
            {/* Logo y descripción */}
            <div>
              <Link to="/" className="flex items-center space-x-3 mb-4">
                <div className="w-10 h-10 bg-white rounded-full ring-2 ring-baby-blue flex items-center justify-center shadow-md">
                  <img
                    src="/productos/icono-pinguino.png"
                    alt="Baby Cash Logo"
                    className="w-6 h-6 object-contain"
                    onError={(e) => {
                      const target = e.target as HTMLImageElement;
                      target.style.display = 'none';
                      target.nextElementSibling?.classList.remove('hidden');
                    }}
                  />
                  <div className="hidden w-6 h-6 bg-baby-blue rounded-full flex items-center justify-center">
                    <span className="text-white font-bold text-xs">BC</span>
                  </div>
                </div>
                <div>
                  <h3 className="font-poppins font-bold text-lg text-baby-gray">BABY CASH</h3>
                  <p className="font-inter text-xs text-gray-500 -mt-1">
                    Pañalera y variedades {`Soffy's`}
                  </p>
                </div>
              </Link>

              <p className="text-gray-600 text-sm leading-relaxed">
                Tu pañalera de confianza con productos de calidad para bebés y mamás. Comprometidos
                con brindar lo mejor para tu familia.
              </p>
            </div>

            {/* Información de contacto */}
            <div className="space-y-3">
              <div className="flex items-center space-x-3 text-sm text-gray-600">
                <MapPin size={16} className="text-baby-blue flex-shrink-0" />
                <span>Torre Colpatria, Bogotá D.C., Colombia</span>
              </div>

              <div className="flex items-center space-x-3 text-sm text-gray-600">
                <Phone size={16} className="text-baby-blue flex-shrink-0" />
                <a href="tel:+573001234567" className="hover:text-baby-blue transition-colors">
                  +57 (300) 123-4567
                </a>
              </div>

              <div className="flex items-center space-x-3 text-sm text-gray-600">
                <Mail size={16} className="text-baby-blue flex-shrink-0" />
                <a
                  href="mailto:info@babycash.com"
                  className="hover:text-baby-blue transition-colors"
                >
                  info@babycash.com
                </a>
              </div>

              <div className="flex items-center space-x-3 text-sm text-gray-600">
                <Clock size={16} className="text-baby-blue flex-shrink-0" />
                <span>Lunes a Sábado: 8:00 AM - 7:00 PM</span>
              </div>
            </div>
          </div>

          {/* Enlaces de navegación */}
          <div className="space-y-6">
            <h4 className="font-poppins font-semibold text-baby-gray text-lg">Navegación</h4>

            <div className="space-y-2">
              {footerLinks.navigation.map((link) => (
                <Link
                  key={link.href}
                  to={link.href}
                  className="block text-gray-600 hover:text-baby-blue transition-colors text-sm py-1 focus-ring rounded"
                >
                  {link.label}
                </Link>
              ))}
            </div>

            <div className="pt-6">
              <h5 className="font-poppins font-medium text-baby-gray mb-3">Información Legal</h5>
              <div className="space-y-2">
                {footerLinks.legal.map((link) => (
                  <Link
                    key={link.href}
                    to={link.href}
                    className="block text-gray-600 hover:text-baby-blue transition-colors text-sm py-1 focus-ring rounded"
                  >
                    {link.label}
                  </Link>
                ))}
              </div>
            </div>
          </div>

          {/* Redes sociales y newsletter */}
          <div className="space-y-6">
            <h4 className="font-poppins font-semibold text-baby-gray text-lg">Síguenos</h4>

            <div className="flex space-x-4">
              <a
                href="https://www.facebook.com/share/1Zyx71Hjtd/"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 bg-white rounded-full flex items-center justify-center text-baby-blue hover:bg-baby-blue hover:text-white transition-colors shadow-md tap-target"
                aria-label="Síguenos en Facebook"
              >
                <Facebook size={18} />
              </a>

              <a
                href="https://www.facebook.com/share/1Zyx71Hjtd/"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 bg-white rounded-full flex items-center justify-center text-baby-pink hover:bg-baby-pink hover:text-white transition-colors shadow-md tap-target"
                aria-label="Síguenos en Instagram"
              >
                <Instagram size={18} />
              </a>

              <a
                href="https://twitter.com/babycash"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 bg-white rounded-full flex items-center justify-center text-baby-mint hover:bg-green-400 hover:text-white transition-colors shadow-md tap-target"
                aria-label="Síguenos en Twitter"
              >
                <Twitter size={18} />
              </a>
            </div>

            <div>
              <h5 className="font-poppins font-medium text-baby-gray mb-3">Newsletter</h5>
              <p className="text-gray-600 text-sm mb-4">
                Suscríbete para recibir ofertas especiales y consejos para bebés.
              </p>

              <form className="space-y-3" onSubmit={(e) => e.preventDefault()}>
                <input
                  type="email"
                  placeholder="Tu correo electrónico"
                  className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:border-baby-blue focus:ring-2 focus:ring-baby-blue focus:ring-opacity-20 text-sm"
                  aria-label="Correo electrónico para newsletter"
                />
                <button
                  type="submit"
                  className="w-full bg-baby-blue hover:bg-blue-400 text-white font-medium py-2 px-4 rounded-lg transition-colors text-sm tap-target"
                >
                  Suscribirse
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* Línea divisoria */}
        <div className="border-t border-gray-300 mt-12 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
            <p className="text-gray-500 text-sm text-center md:text-left">
              © {currentYear} Baby Cash - Pañalera y variedades {`Soffy's`}. Todos los derechos
              reservados.
            </p>

            <div className="flex items-center space-x-4 text-sm text-gray-500">
              <span>Hecho con ❤️ para las familias colombianas</span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
