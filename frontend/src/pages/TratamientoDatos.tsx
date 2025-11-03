import { Link } from 'react-router-dom';

const TratamientoDatos = () => (
  <div className="max-w-2xl mx-auto py-12 px-4 md:px-0 pt-32">
    <div className="bg-white rounded-2xl shadow-xl p-8">
      <h1 className="text-3xl font-bold text-baby-blue mb-4">Política de Tratamiento de Datos</h1>
      <p className="text-gray-700 mb-4">
        En <span className="font-semibold text-baby-blue">Baby Cash</span> nos comprometemos a proteger tu privacidad y tus datos personales. Al aceptar esta política, autorizas el tratamiento de tus datos bajo los siguientes lineamientos:
      </p>
      <ul className="list-disc pl-6 text-gray-700 space-y-2 mb-6">
        <li>Los datos recolectados serán utilizados únicamente para fines comerciales, logísticos y de atención al cliente.</li>
        <li>No compartiremos tu información personal con terceros sin tu consentimiento, salvo obligación legal.</li>
        <li>Puedes solicitar la actualización, corrección o eliminación de tus datos en cualquier momento.</li>
        <li>Implementamos medidas de seguridad para proteger tu información.</li>
        <li>Para ejercer tus derechos, escríbenos a <a href="mailto:admin@babycash.com" className="text-baby-blue underline">admin@babycash.com</a>.</li>
      </ul>
      <Link to="/login" className="text-baby-blue font-medium hover:underline">Volver al inicio de sesión</Link>
    </div>
  </div>
);

export default TratamientoDatos;
