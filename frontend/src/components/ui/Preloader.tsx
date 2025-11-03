import { motion } from 'framer-motion';

const Preloader = () => {
  return (
    <div className="fixed inset-0 bg-gradient-to-br from-baby-blue via-baby-pink to-baby-mint flex items-center justify-center z-50">
      <div className="text-center">
        {/* Logo animado */}
        <motion.div
          initial={{ scale: 0, rotate: -180 }}
          animate={{ scale: 1, rotate: 0 }}
          transition={{ duration: 0.8, ease: 'easeInOut' }}
          className="mb-8"
        >
          <motion.div
            className="w-32 h-32 mx-auto"
            animate={{ y: [0, -20, 0], rotate: [0, 5, -5, 0] }}
            transition={{ duration: 2.5, repeat: Infinity, ease: 'easeInOut' }}
          >
            <img
              src="/productos/icono-pinguino.png"
              alt="Mascota Pingüino"
              className="w-full h-auto object-contain"
            />
          </motion.div>
        </motion.div>

        {/* Texto animado */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.3 }}
        >
          <h1 className="text-4xl font-poppins font-bold text-white mb-2">
            <motion.span
              animate={{
                textShadow: [
                  '0px 0px 0px rgba(255,255,255,0)',
                  '0px 0px 20px rgba(255,255,255,0.8)',
                  '0px 0px 0px rgba(255,255,255,0)',
                ],
              }}
              transition={{ duration: 2, repeat: Infinity }}
            >
              BABY CASH
            </motion.span>
          </h1>
          <motion.p
            className="text-white/90 font-inter text-lg"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.6 }}
          >
            Pañalera y variedades {`Soffy's`}
          </motion.p>
        </motion.div>

        {/* Indicador de carga */}
        <motion.div
          className="mt-8 flex justify-center space-x-2"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.8 }}
        >
          {[0, 1, 2].map((index) => (
            <motion.div
              key={index}
              className="w-3 h-3 bg-white rounded-full"
              animate={{
                scale: [1, 1.5, 1],
                opacity: [0.5, 1, 0.5],
              }}
              transition={{
                duration: 1.5,
                repeat: Infinity,
                delay: index * 0.2,
              }}
            />
          ))}
        </motion.div>

        {/* Mensaje de carga */}
        <motion.p
          className="mt-4 text-white/80 text-sm font-inter"
          animate={{ opacity: [0.5, 1, 0.5] }}
          transition={{ duration: 2, repeat: Infinity }}
        >
          Preparando tu experiencia...
        </motion.p>
      </div>
    </div>
  );
};

export default Preloader;
