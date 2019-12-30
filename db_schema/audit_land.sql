-- Based on https://github.com/xdimedrolx/audit-trigger/
--
-- which is licensed by "The PostgreSQL License", effectively equivalent to the BSD
-- license.

SET search_path TO land;
CREATE TABLE audit_trail(
      id bigserial primary key,
      table_name varchar(50) not null,
      tstamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
      action varchar(1) NOT NULL CHECK (action IN ('I','D','U', 'T')),
      object_id integer not null,
      row_data JSONB,
      changed_fields JSONB
);

CREATE OR REPLACE FUNCTION jsonb_delete_left(a jsonb, b jsonb)
  RETURNS jsonb AS
  $BODY$
       SELECT COALESCE(
              (
              SELECT ('{' || string_agg(to_json(key) || ':' || value, ',') || '}')
              FROM jsonb_each(a)
              WHERE NOT ('{' || to_json(key) || ':' || value || '}')::jsonb <@ b
              )
       , '{}')::jsonb;
       $BODY$
LANGUAGE sql IMMUTABLE STRICT;
COMMENT ON FUNCTION jsonb_delete_left(jsonb, jsonb) IS 'delete matching pairs in second argument from first argument';
DROP OPERATOR IF EXISTS - (jsonb, jsonb);
CREATE OPERATOR - ( PROCEDURE = jsonb_delete_left, LEFTARG = jsonb, RIGHTARG = jsonb);
COMMENT ON OPERATOR - (jsonb, jsonb) IS 'delete matching pairs from left operand';

CREATE OR REPLACE FUNCTION if_modified_func() RETURNS TRIGGER AS $body$
DECLARE
    audit_row land.audit_trail;
    include_values boolean;
    log_diffs boolean;
    h_old jsonb;
    h_new jsonb;
    excluded_cols text[] = ARRAY[]::text[];
BEGIN
    IF TG_WHEN <> 'AFTER' THEN
        RAISE EXCEPTION 'land.if_modified_func() may only run as an AFTER trigger';
    END IF;

    -- Do nothing on delete.
    IF (TG_OP = 'DELETE') THEN
        RETURN NULL;
    END IF;

    audit_row = ROW(
        nextval('land.audit_trail_id_seq'), -- id
        TG_TABLE_NAME::varchar,             -- table_name
        current_timestamp,                  -- tstamp
        substring(TG_OP,1,1),               -- action
        NEW.id,                             -- object_id
        NULL, NULL                          -- row_data, changed_fields
        );

    IF TG_ARGV[1] IS NOT NULL THEN
        excluded_cols = TG_ARGV[1]::text[];
    END IF;

    IF (TG_OP = 'UPDATE' AND TG_LEVEL = 'ROW') THEN
        audit_row.row_data = row_to_json(OLD)::JSONB;
        audit_row.changed_fields = row_to_json(NEW)::JSONB - audit_row.row_data - excluded_cols;
        IF audit_row.changed_fields = '{}'::jsonb THEN
            -- All changed fields are ignored. Skip this update.
            RETURN NULL;
        END IF;
    ELSIF (TG_OP = 'INSERT' AND TG_LEVEL = 'ROW') THEN
        audit_row.row_data = row_to_json(NEW)::JSONB;
        audit_row.changed_fields = jsonb_strip_nulls(row_to_json(NEW)::JSONB - excluded_cols);
    ELSE
        RAISE EXCEPTION '[land.if_modified_func] - Trigger func added as trigger for unhandled case: %, %',TG_OP, TG_LEVEL;
        RETURN NULL;
    END IF;
    INSERT INTO land.audit_trail VALUES (audit_row.*);
    RETURN NULL;
END;
$body$
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = land, public;

CREATE INDEX probe_id_ndx ON audit_trail(cast("row_data"->>'probe_id' AS int));
CREATE INDEX messung_id_ndx ON audit_trail(cast("row_data"->>'messung_id' AS int));

-- View for probe audit trail
CREATE OR REPLACE VIEW land.audit_trail_probe AS
SELECT
    land_audit.id,
    land_audit.table_name,
    land_audit.action,
    land_audit.object_id,
    land_audit.tstamp,
    cast(row_data ->> 'messungs_id' AS integer) AS messungs_id,
    coalesce(cast(row_data ->> 'probe_id' AS integer),
        (SELECT probe_id FROM land.messung WHERE id = cast(
            row_data ->> 'messungs_id' AS integer))) AS probe_id,
    land_audit.row_data,
    land_audit.changed_fields,
    null as ort_id
FROM land.audit_trail as land_audit
UNION
SELECT stamm_audit.id,
    stamm_audit.table_name,
    stamm_audit.action,
    stamm_audit.object_id,
    stamm_audit.tstamp,
    null as messungs_id,
    null as probe_id,
    stamm_audit.row_data,
    stamm_audit.changed_fields,
    cast(row_data ->> 'id' AS integer) AS ort_id
FROM stamm.audit_trail as stamm_audit;


-- View for messung audit trail
CREATE OR REPLACE VIEW audit_trail_messung AS
SELECT audit_trail.id,
    audit_trail.table_name,
    audit_trail.tstamp,
    audit_trail.action,
    audit_trail.object_id,
    audit_trail.row_data,
    audit_trail.changed_fields,
    cast(row_data ->> 'messungs_id' AS int) AS messungs_id
FROM audit_trail;


SELECT stamm.audit_table('probe', true, false, '{id, tree_modified, letzte_aenderung}'::text[]);
SELECT stamm.audit_table('messung', true, false, '{id, probe_id, tree_modified, letzte_aenderung, status}'::text[]);
SELECT stamm.audit_table('messwert', true, false, '{id, messungs_id, tree_modified, letzte_aenderung}'::text[]);
SELECT stamm.audit_table('kommentar_p', true, false, '{id, probe_id, tree_modified, letzte_aenderung}'::text[]);
SELECT stamm.audit_table('kommentar_m', true, false, '{id, messungs_id, tree_modified, letzte_aenderung}'::text[]);
SELECT stamm.audit_table('zusatz_wert', true, false, '{id, probe_id, tree_modified, letzte_aenderung}'::text[]);
SELECT stamm.audit_table('ortszuordnung', true, false, '{id, probe_id, tree_modified, letzte_aenderung}'::text[]);

SET search_path TO public;
