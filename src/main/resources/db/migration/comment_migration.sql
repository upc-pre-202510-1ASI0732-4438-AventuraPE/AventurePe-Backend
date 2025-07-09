-- Si los campos no existen, añadirlos
ALTER TABLE comments ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_comments_publication_deleted ON comments(publication_id, deleted);
CREATE INDEX IF NOT EXISTS idx_comments_deleted ON comments(deleted);