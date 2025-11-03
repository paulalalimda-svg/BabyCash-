import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

const DevTools = () => {
  const { user, isAuthenticated } = useAuth();
  const [backendStatus, setBackendStatus] = useState<'checking' | 'online' | 'offline'>('checking');

  useEffect(() => {
    const checkBackend = async () => {
      try {
        const response = await fetch('http://localhost:8080/actuator/health');
        if (response.ok) {
          setBackendStatus('online');
        } else {
          setBackendStatus('offline');
        }
      } catch {
        setBackendStatus('offline');
      }
    };

    checkBackend();
    const interval = setInterval(checkBackend, 30000); // Check every 30s

    return () => clearInterval(interval);
  }, []);

  if (import.meta.env.PROD) return null;

  return (
    <div className="fixed bottom-4 right-4 bg-white rounded-lg shadow-xl p-4 text-xs font-mono border border-gray-200 z-50 max-w-xs">
      <h3 className="font-bold text-sm mb-2 text-gray-800">🛠️ Dev Tools</h3>
      
      <div className="space-y-1">
        <div className="flex items-center gap-2">
          <span className="text-gray-600">Backend:</span>
          <span className={`font-semibold ${
            (() => {
              if (backendStatus === 'online') return 'text-green-600';
              if (backendStatus === 'offline') return 'text-red-600';
              return 'text-yellow-600';
            })()
          }`}>
            {(() => {
              if (backendStatus === 'online') return '✓ Online';
              if (backendStatus === 'offline') return '✗ Offline';
              return '⋯ Checking';
            })()}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-gray-600">Auth:</span>
          <span className={`font-semibold ${isAuthenticated ? 'text-green-600' : 'text-gray-400'}`}>
            {isAuthenticated ? '✓ Logged In' : '○ Guest'}
          </span>
        </div>

        {user && (
          <>
            <div className="flex items-center gap-2">
              <span className="text-gray-600">User:</span>
              <span className="font-semibold text-blue-600 truncate">{user.email}</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-gray-600">Role:</span>
              <span className="font-semibold text-purple-600">{user.role}</span>
            </div>
          </>
        )}

        <div className="mt-2 pt-2 border-t border-gray-200">
          <div className="text-gray-500 text-xs">
            Token: {localStorage.getItem('baby-cash-token') ? '✓' : '✗'}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DevTools;
