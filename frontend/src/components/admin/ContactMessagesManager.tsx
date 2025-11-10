import { motion } from 'framer-motion';
import {
  AlertCircle,
  Archive,
  CheckCheck,
  Clock,
  Mail,
  MailOpen,
  MessageSquare,
  Phone,
  Trash2,
  User,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAdminCrud } from '../../hooks/useAdminCrud';
import { contactMessageService, handleApiError, type ContactMessage } from '../../services/api';
import { PaginationControls } from '../ui/PaginationControls';

type MessageFilter = 'all' | 'new' | 'read' | 'replied' | 'archived';

export const ContactMessagesManager = () => {
  const {
    items: messages,
    loading,
    filter,
    setFilter,
    currentPage,
    totalPages,
    totalElements,
    setCurrentPage,
    loadData,
    handleDelete,
    goToNextPage,
    goToPreviousPage,
    canGoNext,
    canGoPrevious,
  } = useAdminCrud<ContactMessage, MessageFilter>({
    service: {
      getAll: contactMessageService.getAllMessagesPaged,
      delete: contactMessageService.deleteMessage,
    },
    pageSize: 10,
    filterFn: (items, filterType) => {
      if (filterType === 'new') {
        return items.filter((m) => m.status === 'NEW');
      } else if (filterType === 'read') {
        return items.filter((m) => m.status === 'READ');
      } else if (filterType === 'replied') {
        return items.filter((m) => m.status === 'REPLIED');
      } else if (filterType === 'archived') {
        return items.filter((m) => m.status === 'ARCHIVED');
      }
      return items;
    },
    defaultFilter: 'all',
    logName: 'mensajes',
  });

  const [newCount, setNewCount] = useState(0);

  // Load new message count when component mounts or loadData is called
  useEffect(() => {
    const loadCount = async () => {
      try {
        const count = await contactMessageService.countNewMessages();
        setNewCount(count);
      } catch (error) {
        toast.error(handleApiError(error));
      }
    };
    loadCount();
  }, [currentPage]); // Solo depende de currentPage, no de messages

  const handleMarkAsRead = async (id: number) => {
    try {
      await contactMessageService.markAsRead(id);
      toast.success('Marcado como leído');
      loadData();
    } catch (error: unknown) {
      // Si es un 403, mostrar mensaje de permisos más claro
      if ((error as { response?: { status?: number } })?.response?.status === 403) {
        toast.error(
          'No tienes permisos para realizar esta acción. Intenta iniciar sesión de nuevo.'
        );
      } else {
        toast.error(handleApiError(error));
      }
    }
  };

  const handleMarkAsReplied = async (id: number) => {
    try {
      await contactMessageService.markAsReplied(id);
      toast.success('Marcado como respondido');
      loadData();
    } catch (error: unknown) {
      if ((error as { response?: { status?: number } })?.response?.status === 403) {
        toast.error(
          'No tienes permisos para realizar esta acción. Intenta iniciar sesión de nuevo.'
        );
      } else {
        toast.error(handleApiError(error));
      }
    }
  };

  const handleArchive = async (id: number) => {
    try {
      await contactMessageService.archiveMessage(id);
      toast.success('Mensaje archivado');
      loadData();
    } catch (error: unknown) {
      if ((error as { response?: { status?: number } })?.response?.status === 403) {
        toast.error(
          'No tienes permisos para realizar esta acción. Intenta iniciar sesión de nuevo.'
        );
      } else {
        toast.error(handleApiError(error));
      }
    }
  };

  const handleUnarchive = async (id: number) => {
    try {
      await contactMessageService.unarchiveMessage(id);
      toast.success('Mensaje desarchivado');
      loadData();
    } catch (error: unknown) {
      if ((error as { response?: { status?: number } })?.response?.status === 403) {
        toast.error(
          'No tienes permisos para realizar esta acción. Intenta iniciar sesión de nuevo.'
        );
      } else {
        toast.error(handleApiError(error));
      }
    }
  };

  // handleDelete is provided by useAdminCrud hook

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'NEW':
        return 'bg-blue-100 text-blue-700';
      case 'READ':
        return 'bg-yellow-100 text-yellow-700';
      case 'REPLIED':
        return 'bg-green-100 text-green-700';
      case 'ARCHIVED':
        return 'bg-gray-100 text-gray-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'NEW':
        return <Mail size={16} />;
      case 'READ':
        return <MailOpen size={16} />;
      case 'REPLIED':
        return <CheckCheck size={16} />;
      case 'ARCHIVED':
        return <Archive size={16} />;
      default:
        return <Mail size={16} />;
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'NEW':
        return 'Nuevo';
      case 'READ':
        return 'Leído';
      case 'REPLIED':
        return 'Respondido';
      case 'ARCHIVED':
        return 'Archivado';
      default:
        return status;
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-baby-blue"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Estadísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-xl p-6 border border-baby-blue">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total</p>
              <p className="text-2xl font-bold text-baby-gray">{totalElements}</p>
            </div>
            <MessageSquare className="text-baby-blue" size={32} />
          </div>
        </div>

        <div className="bg-white rounded-xl p-6 border border-blue-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Nuevos</p>
              <p className="text-2xl font-bold text-blue-600">{newCount}</p>
            </div>
            <Mail className="text-blue-500" size={32} />
          </div>
        </div>

        <div className="bg-white rounded-xl p-6 border border-yellow-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Leídos</p>
              <p className="text-2xl font-bold text-yellow-600">
                {messages.filter((m) => m.status === 'READ').length}
              </p>
            </div>
            <MailOpen className="text-yellow-500" size={32} />
          </div>
        </div>

        <div className="bg-white rounded-xl p-6 border border-green-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Respondidos</p>
              <p className="text-2xl font-bold text-green-600">
                {messages.filter((m) => m.status === 'REPLIED').length}
              </p>
            </div>
            <CheckCheck className="text-green-500" size={32} />
          </div>
        </div>

        <div className="bg-white rounded-xl p-6 shadow-md border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Archivados</p>
              <p className="text-2xl font-bold text-gray-600">
                {messages.filter((m) => m.status === 'ARCHIVED').length}
              </p>
            </div>
            <Archive className="text-gray-500" size={32} />
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="flex space-x-2">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'all'
              ? 'bg-baby-blue text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Todos
        </button>
        <button
          onClick={() => setFilter('new')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'new'
              ? 'bg-blue-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Nuevos ({newCount})
        </button>
        <button
          onClick={() => setFilter('read')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'read'
              ? 'bg-yellow-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Leídos
        </button>
        <button
          onClick={() => setFilter('replied')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'replied'
              ? 'bg-green-500 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Respondidos
        </button>
        <button
          onClick={() => setFilter('archived')}
          className={`px-4 py-2 rounded-lg ${
            filter === 'archived'
              ? 'bg-gray-600 text-white'
              : 'bg-white text-gray-600 border border-gray-300'
          }`}
        >
          Archivados
        </button>
      </div>

      {/* Lista de Mensajes */}
      <div className="space-y-4">
        {messages.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-xl border border-gray-200">
            <AlertCircle className="mx-auto text-gray-400 mb-4" size={48} />
            <p className="text-gray-500">No hay mensajes en esta categoría</p>
          </div>
        ) : (
          messages.map((message) => (
            <motion.div
              key={message.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className={`bg-white rounded-xl p-6 border ${
                message.status === 'NEW' ? 'border-blue-300 shadow-md' : 'border-gray-200'
              } hover:shadow-lg transition-shadow`}
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  {/* Header */}
                  <div className="flex items-center space-x-3 mb-3">
                    <div className="flex items-center space-x-2">
                      <User size={20} className="text-baby-blue" />
                      <h3 className="font-semibold text-baby-gray">{message.name}</h3>
                    </div>
                    <span
                      className={`px-3 py-1 rounded-full text-sm flex items-center space-x-1 ${getStatusColor(message.status)}`}
                    >
                      {getStatusIcon(message.status)}
                      <span>{getStatusText(message.status)}</span>
                    </span>
                  </div>

                  {/* Información de contacto */}
                  <div className="space-y-2 mb-4 text-sm text-gray-600">
                    <div className="flex items-center space-x-2">
                      <Mail size={16} />
                      <a
                        href={`mailto:${message.email}`}
                        className="text-baby-blue hover:underline"
                      >
                        {message.email}
                      </a>
                    </div>
                    {message.phone && (
                      <div className="flex items-center space-x-2">
                        <Phone size={16} />
                        <a href={`tel:${message.phone}`} className="text-baby-blue hover:underline">
                          {message.phone}
                        </a>
                      </div>
                    )}
                  </div>

                  {/* Asunto */}
                  <div className="mb-2">
                    <span className="font-semibold text-gray-700">Asunto:</span>{' '}
                    <span className="text-gray-600">{message.subject}</span>
                  </div>

                  {/* Mensaje: limitar altura para evitar overflow del layout */}
                  <div className="bg-gray-50 rounded-lg p-4 mb-3 max-h-48 overflow-auto">
                    <p className="text-gray-700 whitespace-pre-wrap">{message.message}</p>
                  </div>

                  {/* Metadata */}
                  <div className="flex items-center space-x-4 text-xs text-gray-500">
                    <div className="flex items-center space-x-1">
                      <Clock size={14} />
                      <span>{new Date(message.createdAt).toLocaleString('es-ES')}</span>
                    </div>
                    {message.ipAddress && (
                      <span className="flex items-center space-x-1">
                        <span>IP:</span>
                        <code className="bg-gray-200 px-2 py-1 rounded">{message.ipAddress}</code>
                      </span>
                    )}
                  </div>
                </div>

                {/* Acciones */}
                <div className="flex flex-col space-y-2 ml-4">
                  {message.status === 'NEW' && (
                    <button
                      onClick={() => handleMarkAsRead(message.id)}
                      className="p-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors"
                      title="Marcar como leído"
                    >
                      <MailOpen size={20} />
                    </button>
                  )}

                  {(message.status === 'NEW' || message.status === 'READ') && (
                    <button
                      onClick={() => handleMarkAsReplied(message.id)}
                      className="p-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
                      title="Marcar como respondido"
                    >
                      <CheckCheck size={20} />
                    </button>
                  )}

                  {message.status === 'ARCHIVED' ? (
                    <button
                      onClick={() => handleUnarchive(message.id)}
                      className="p-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                      title="Desarchivar"
                    >
                      <Archive size={20} />
                    </button>
                  ) : (
                    <button
                      onClick={() => handleArchive(message.id)}
                      className="p-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                      title="Archivar"
                    >
                      <Archive size={20} />
                    </button>
                  )}

                  <button
                    onClick={() => handleDelete(message.id)}
                    className="p-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
                    title="Eliminar"
                  >
                    <Trash2 size={20} />
                  </button>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>

      {/* Pagination Controls */}
      <PaginationControls
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        itemsCount={messages.length}
        itemName="mensajes"
        onPageChange={setCurrentPage}
        onNext={goToNextPage}
        onPrevious={goToPreviousPage}
        canGoNext={canGoNext}
        canGoPrevious={canGoPrevious}
      />
    </div>
  );
};
