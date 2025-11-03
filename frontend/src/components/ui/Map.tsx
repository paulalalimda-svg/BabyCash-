import type { FC } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';

import 'leaflet/dist/leaflet.css';

// 📍 Coordenadas de la tienda (Torre Colpatria, Bogotá)
const STORE_COORDINATES: [number, number] = [4.606734, -74.072235];

/**
 * Componente del mapa de la tienda
 */
const Map: FC = () => {
  // Crea un icono personalizado usando un ícono de Lucide y un div.
  const babyIcon = L.divIcon({
    className: 'custom-map-icon',
    html: `<div style="background-color: #3B82F6; color: white; width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px;"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-baby-carrier"><path d="M18 10c0 1.3-2 2-2 2s-2-.7-2-2a2 2 0 0 1 4 0Z"/><path d="M18 10h-2c0-1.3-2-2-2-2s-.7 2-2 2v2"/><path d="M8 8.8c0-1.5-2-2.3-2-2.3s-2 .8-2 2.3v2.8c0 1.5 2 2.3 2 2.3s2-.8 2-2.3Z"/><path d="M9 13v2a3 3 0 0 1-3 3c-1.6 0-3-2-3-3l-2-2"/><path d="M2 12h18"/></svg></div>`,
    iconSize: [40, 40],
    iconAnchor: [20, 40], // Ancla en la parte inferior del ícono
    popupAnchor: [0, -40], // Posiciona el popup encima del ícono
  });

  return (
    <MapContainer
      center={STORE_COORDINATES}
      zoom={16}
      scrollWheelZoom={false}
      className="w-full h-96 md:h-[500px]"
    >
      <TileLayer
        attribution='© <a href="https://www.openstreetmap.org/">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <Marker position={STORE_COORDINATES} icon={babyIcon}>
        <Popup>
          <div className="text-center">
            <h4 className="font-semibold text-baby-blue">📍 Baby Cash</h4>
            <p className="text-sm text-gray-600">Torre Colpatria, Bogotá D.C.</p>
            <p className="text-xs text-gray-500 mt-1">
              Lun-Sáb: <span className="text-baby-blue">8:00 AM - 7:00 PM</span>
            </p>
            <a
              href={`http://maps.google.com/?q=${STORE_COORDINATES[0]},${STORE_COORDINATES[1]}`}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block mt-2 px-3 py-1 bg-baby-blue hover:bg-blue-400 text-white text-xs font-medium rounded-lg shadow-md transition-colors"
            >
              Ver en Google Maps
            </a>
          </div>
        </Popup>
      </Marker>
    </MapContainer>
  );
};

export default Map;
