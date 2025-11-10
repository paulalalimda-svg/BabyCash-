import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  MessageCircle,
  Send,
  Trash2,
  Edit2,
  Check,
  X,
  Loader2,
  AlertCircle,
  User,
} from 'lucide-react';
import { commentService, handleApiError } from '../../services/api';
import type { BlogComment, CommentRequest } from '../../services/api';
import Button from '../ui/Button';
import { useAuth } from '../../contexts/AuthContext';

interface CommentSectionProps {
  postId: number;
}

const CommentSection: React.FC<CommentSectionProps> = ({ postId }) => {
  const { user } = useAuth();
  const [comments, setComments] = useState<BlogComment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newComment, setNewComment] = useState('');
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadComments();
  }, [postId]);

  const loadComments = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await commentService.getComments(postId);
      setComments(data);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim() || !user) return;

    setSubmitting(true);
    setError(null);
    try {
      const request: CommentRequest = { content: newComment.trim() };
      await commentService.createComment(postId, request);
      setNewComment('');
      setError('¡Comentario enviado! Está pendiente de aprobación.');
      setTimeout(() => setError(null), 3000);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmitReply = async (parentId: number) => {
    if (!replyContent.trim() || !user) return;

    setSubmitting(true);
    setError(null);
    try {
      const request: CommentRequest = {
        content: replyContent.trim(),
        parentCommentId: parentId,
      };
      await commentService.createComment(postId, request);
      setReplyContent('');
      setReplyingTo(null);
      setError('¡Respuesta enviada! Está pendiente de aprobación.');
      setTimeout(() => setError(null), 3000);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdateComment = async (commentId: number) => {
    if (!editContent.trim()) return;

    setSubmitting(true);
    setError(null);
    try {
      const request: CommentRequest = { content: editContent.trim() };
      await commentService.updateComment(postId, commentId, request);
      setEditingId(null);
      setEditContent('');
      loadComments();
      setError('¡Comentario actualizado! Está pendiente de aprobación.');
      setTimeout(() => setError(null), 3000);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    if (!confirm('¿Estás seguro de eliminar este comentario?')) return;

    setSubmitting(true);
    setError(null);
    try {
      await commentService.deleteComment(postId, commentId);
      loadComments();
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const startEditing = (comment: BlogComment) => {
    setEditingId(comment.id);
    setEditContent(comment.content);
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditContent('');
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Ahora mismo';
    if (diffMins < 60) return `Hace ${diffMins} min`;
    if (diffHours < 24) return `Hace ${diffHours} h`;
    if (diffDays < 7) return `Hace ${diffDays} días`;

    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const renderComment = (comment: BlogComment, isReply = false) => {
    const isOwner = user?.id === comment.user.id;
    const isEditing = editingId === comment.id;

    return (
      <motion.div
        key={comment.id}
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -10 }}
        className={`${isReply ? 'ml-8 md:ml-12' : ''} mb-4`}
      >
        <div className="bg-white rounded-lg p-4 shadow-sm border border-gray-100">
          <div className="flex items-start gap-3">
            <div className="flex-shrink-0">
              <div className="w-10 h-10 rounded-full bg-baby-pink/20 flex items-center justify-center">
                <User className="w-5 h-5 text-baby-pink" />
              </div>
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-1">
                <span className="font-semibold text-baby-gray text-sm">
                  {comment.user.firstName} {comment.user.lastName}
                </span>
                <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
                {!comment.approved && isOwner && (
                  <span className="text-xs px-2 py-0.5 bg-yellow-100 text-yellow-700 rounded">
                    Pendiente
                  </span>
                )}
              </div>

              {isEditing ? (
                <div className="mt-2">
                  <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-baby-blue focus:border-transparent"
                    rows={3}
                    disabled={submitting}
                  />
                  <div className="flex gap-2 mt-2">
                    <Button
                      size="sm"
                      onClick={() => handleUpdateComment(comment.id)}
                      disabled={submitting || !editContent.trim()}
                    >
                      <Check className="w-4 h-4 mr-1" />
                      Guardar
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={cancelEditing}
                      disabled={submitting}
                    >
                      <X className="w-4 h-4 mr-1" />
                      Cancelar
                    </Button>
                  </div>
                </div>
              ) : (
                <>
                  <p className="text-gray-700 text-sm leading-relaxed whitespace-pre-wrap">
                    {comment.content}
                  </p>

                  <div className="flex items-center gap-3 mt-2">
                    {user && !isReply && (
                      <button
                        onClick={() => setReplyingTo(comment.id)}
                        className="text-xs text-baby-blue hover:text-blue-600 font-medium"
                      >
                        Responder
                      </button>
                    )}
                    {isOwner && (
                      <>
                        <button
                          onClick={() => startEditing(comment)}
                          className="text-xs text-gray-600 hover:text-baby-blue flex items-center gap-1"
                        >
                          <Edit2 className="w-3 h-3" />
                          Editar
                        </button>
                        <button
                          onClick={() => handleDeleteComment(comment.id)}
                          className="text-xs text-red-600 hover:text-red-700 flex items-center gap-1"
                        >
                          <Trash2 className="w-3 h-3" />
                          Eliminar
                        </button>
                      </>
                    )}
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Reply Form */}
          {replyingTo === comment.id && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="mt-4 ml-12"
            >
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleSubmitReply(comment.id);
                }}
              >
                <textarea
                  value={replyContent}
                  onChange={(e) => setReplyContent(e.target.value)}
                  placeholder="Escribe tu respuesta..."
                  className="w-full p-3 border border-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-baby-blue focus:border-transparent"
                  rows={3}
                  disabled={submitting}
                />
                <div className="flex gap-2 mt-2">
                  <Button type="submit" size="sm" disabled={submitting || !replyContent.trim()}>
                    {submitting ? (
                      <Loader2 className="w-4 h-4 animate-spin" />
                    ) : (
                      <Send className="w-4 h-4" />
                    )}
                    <span className="ml-1">Responder</span>
                  </Button>
                  <Button
                    type="button"
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setReplyingTo(null);
                      setReplyContent('');
                    }}
                    disabled={submitting}
                  >
                    Cancelar
                  </Button>
                </div>
              </form>
            </motion.div>
          )}
        </div>

        {/* Render Replies */}
        {comment.replies && comment.replies.length > 0 && (
          <div className="mt-2">{comment.replies.map((reply) => renderComment(reply, true))}</div>
        )}
      </motion.div>
    );
  };

  return (
    <section className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 md:p-8">
      <div className="flex items-center gap-2 mb-6">
        <MessageCircle className="w-6 h-6 text-baby-pink" />
        <h2 className="text-2xl font-bold font-poppins text-baby-gray">
          Comentarios ({comments.length})
        </h2>
      </div>

      {/* Error/Success Message */}
      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className={`mb-4 p-4 rounded-lg flex items-center gap-2 ${
              error.includes('enviado') || error.includes('actualizado')
                ? 'bg-green-50 text-green-700 border border-green-200'
                : 'bg-red-50 text-red-700 border border-red-200'
            }`}
          >
            <AlertCircle className="w-5 h-5 flex-shrink-0" />
            <p className="text-sm">{error}</p>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Comment Form */}
      {user ? (
        <form onSubmit={handleSubmitComment} className="mb-8">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Escribe tu comentario..."
            className="w-full p-4 border border-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-baby-blue focus:border-transparent"
            rows={4}
            disabled={submitting}
          />
          <div className="flex justify-end mt-3">
            <Button type="submit" disabled={submitting || !newComment.trim()}>
              {submitting ? (
                <Loader2 className="w-4 h-4 animate-spin mr-2" />
              ) : (
                <Send className="w-4 h-4 mr-2" />
              )}
              Enviar comentario
            </Button>
          </div>
        </form>
      ) : (
        <div className="mb-8 p-4 bg-baby-blue/5 rounded-lg border border-baby-blue/20 text-center">
          <p className="text-gray-600">
            <a href="/login" className="text-baby-blue font-semibold hover:underline">
              Inicia sesión
            </a>{' '}
            para dejar un comentario
          </p>
        </div>
      )}

      {/* Comments List */}
      {(() => {
        if (loading) {
          return (
            <div className="text-center py-8">
              <Loader2 className="w-8 h-8 animate-spin mx-auto text-baby-pink" />
              <p className="mt-3 text-gray-600">Cargando comentarios...</p>
            </div>
          );
        }

        if (comments.length === 0) {
          return (
            <div className="text-center py-12">
              <MessageCircle className="w-12 h-12 mx-auto text-gray-300 mb-3" />
              <p className="text-gray-500">Sé el primero en comentar</p>
            </div>
          );
        }

        return (
          <AnimatePresence>
            <div className="space-y-4">{comments.map((comment) => renderComment(comment))}</div>
          </AnimatePresence>
        );
      })()}
    </section>
  );
};

export default CommentSection;
