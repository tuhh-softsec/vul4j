CREATE TABLE lada_schema_version (
    version int PRIMARY KEY,
    update_date timestamp without time zone NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
