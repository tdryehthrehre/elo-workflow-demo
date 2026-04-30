-- Full-text search support for Sord using PostgreSQL tsvector/tsquery (German stemming)
ALTER TABLE sord ADD COLUMN search_vector tsvector;

CREATE INDEX idx_sord_search_vector ON sord USING GIN (search_vector);

-- Populate existing rows
UPDATE sord SET search_vector = to_tsvector('german', coalesce(short_description, ''));

-- Keep search_vector current on insert/update
CREATE FUNCTION sord_search_vector_update() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    NEW.search_vector := to_tsvector('german', coalesce(NEW.short_description, ''));
    RETURN NEW;
END;
$$;

CREATE TRIGGER sord_search_vector_trig
    BEFORE INSERT OR UPDATE ON sord
    FOR EACH ROW EXECUTE FUNCTION sord_search_vector_update();
