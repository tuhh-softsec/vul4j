ALTER TABLE land.tagzuordnung ADD COLUMN messung_id INTEGER;
ALTER TABLE land.tagzuordnung ADD CONSTRAINT tagzuordnung_messung_fkey FOREIGN KEY (messung_id)
        REFERENCES land.messung (id) MATCH SIMPLE
        ON DELETE CASCADE;
ALTER TABLE land.tagzuordnung ADD CONSTRAINT messung_id_unique UNIQUE (messung_id, tag_id);