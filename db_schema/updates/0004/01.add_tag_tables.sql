CREATE TABLE stamm.tag (
    id serial PRIMARY KEY,
    tag text COLLATE pg_catalog."default",
    mst_id character varying REFERENCES stamm.mess_stelle(id)
);

CREATE TABLE land.tagzuordnung
(
    id serial PRIMARY KEY,
    probe_id integer,
    tag_id integer,
    datum timestamp without time zone DEFAULT now(),
    CONSTRAINT tagzuordnung_tag_fkey FOREIGN KEY (tag_id)
        REFERENCES stamm.tag (id) MATCH SIMPLE
        ON DELETE CASCADE,
    CONSTRAINT tagzuordnung_probe_fkey FOREIGN KEY (probe_id)
        REFERENCES land.probe (id) MATCH SIMPLE
        ON DELETE CASCADE,
    UNIQUE (probe_id, tag_id)
);

GRANT USAGE
    ON ALL SEQUENCES IN SCHEMA stamm, land TO lada;
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
    ON ALL TABLES IN SCHEMA stamm, land TO lada;