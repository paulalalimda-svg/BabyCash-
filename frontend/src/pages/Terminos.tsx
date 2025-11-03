import { Link } from 'react-router-dom';

const Terminos = () => (
  <div className="max-w-2xl mx-auto py-12 px-4 md:px-0 pt-32">
    <div className="bg-white rounded-2xl shadow-xl p-8">
      <h1 className="text-3xl font-bold text-baby-blue mb-4">Términos y Condiciones</h1>
      <p className="text-gray-700 mb-4">
        Bienvenido a <span className="font-semibold text-baby-blue">Baby Cash</span>. Al utilizar nuestro sitio web y servicios, aceptas los siguientes términos y condiciones:
      </p>
      <ul className="list-disc pl-6 text-gray-700 space-y-2 mb-6">
        <li>El uso de la plataforma es solo para mayores de edad o bajo supervisión de un adulto responsable.</li>
        <li>La información proporcionada debe ser verídica y actualizada.</li>
        <li>Nos reservamos el derecho de modificar productos, precios y condiciones sin previo aviso.</li>
        <li>El usuario es responsable de mantener la confidencialidad de sus credenciales de acceso.</li>
        <li>El uso indebido de la plataforma puede resultar en la suspensión o eliminación de la cuenta.</li>
        <li>Para más información, puedes contactarnos en <a href="mailto:admin@babycash.com" className="text-baby-blue underline">admin@babycash.com</a>.</li>
      </ul>
      <Link to="/login" className="text-baby-blue font-medium hover:underline">Volver al inicio de sesión</Link>
    </div>
  </div>
);

export default Terminos;
