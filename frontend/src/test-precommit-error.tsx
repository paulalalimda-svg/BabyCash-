// ✅ ARCHIVO CORREGIDO - Sin errores de linting

// Fixed: Variable usada correctamente
const message = 'test message';

// Fixed: Tipo específico en lugar de any
export const goodFunction = (data: string) => {
  return data;
};

// Fixed: Uso de mensaje
export const testComponent = () => {
  return <div>{message}</div>;
};
