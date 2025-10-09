-- Create user_collections table
CREATE TABLE IF NOT EXISTS user_collections (
    id BIGSERIAL PRIMARY KEY,
    collection_name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    schema JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_collections_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_collection UNIQUE (user_id, collection_name)
);

-- Create collection_documents table
CREATE TABLE IF NOT EXISTS collection_documents (
    id BIGSERIAL PRIMARY KEY,
    collection_id BIGINT NOT NULL,
    document_data JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_collection_documents_collection FOREIGN KEY (collection_id) REFERENCES user_collections(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_collections_user_id ON user_collections(user_id);
CREATE INDEX IF NOT EXISTS idx_user_collections_collection_name ON user_collections(collection_name);
CREATE INDEX IF NOT EXISTS idx_collection_documents_collection_id ON collection_documents(collection_id);
CREATE INDEX IF NOT EXISTS idx_collection_documents_data ON collection_documents USING GIN (document_data);

-- Add comments for documentation
COMMENT ON TABLE user_collections IS 'Stores user-defined collections with their schemas';
COMMENT ON TABLE collection_documents IS 'Stores JSON documents belonging to collections';
COMMENT ON COLUMN user_collections.schema IS 'JSONB schema definition for the collection';
COMMENT ON COLUMN collection_documents.document_data IS 'JSONB document data conforming to collection schema';

