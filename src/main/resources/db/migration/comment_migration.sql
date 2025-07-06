-- Si los campos no existen, añadirlos
ALTER TABLE comment ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE comment ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_comment_publication_deleted ON comment(publication_id, deleted);
CREATE INDEX IF NOT EXISTS idx_comment_deleted ON comment(deleted);