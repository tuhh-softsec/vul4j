-- Based on https://github.com/xdimedrolx/audit-trigger/
--
-- which is licensed by "The PostgreSQL License", effectively equivalent to the BSD
-- license.

SET search_path TO stamm;
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
    audit_row stamm.audit_trail;
    include_values boolean;
    log_diffs boolean;
    h_old jsonb;
    h_new jsonb;
    excluded_cols text[] = ARRAY[]::text[];
BEGIN
    IF TG_WHEN <> 'AFTER' THEN
        RAISE EXCEPTION 'stamm.if_modified_func() may only run as an AFTER trigger';
    END IF;

    -- Do nothing on delete.
    IF (TG_OP = 'DELETE') THEN
        RETURN NULL;
    END IF;

    audit_row = ROW(
        nextval('stamm.audit_trail_id_seq'), -- id
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
        RAISE EXCEPTION '[stamm.if_modified_func] - Trigger func added as trigger for unhandled case: %, %',TG_OP, TG_LEVEL;
        RETURN NULL;
    END IF;
    INSERT INTO stamm.audit_trail VALUES (audit_row.*);
    RETURN NULL;
END;
$body$
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = stamm, public;

CREATE OR REPLACE FUNCTION audit_table(
    target_table regclass,
    audit_rows boolean,
    audit_query_text boolean,
    ignored_cols text[]
) RETURNS void AS
$body$
DECLARE
  stm_targets text = 'INSERT OR UPDATE OR DELETE OR TRUNCATE';
  _q_txt text;
  _ignored_cols_snip text = '';
BEGIN
    EXECUTE 'DROP TRIGGER IF EXISTS audit_trigger_row ON ' || quote_ident(target_table::TEXT);
    EXECUTE 'DROP TRIGGER IF EXISTS audit_trigger_stm ON ' || quote_ident(target_table::TEXT);

    IF audit_rows THEN
        IF array_length(ignored_cols,1) > 0 THEN
            _ignored_cols_snip = ', ' || quote_literal(ignored_cols);
        END IF;
        _q_txt = 'CREATE TRIGGER audit_trigger_row AFTER INSERT OR UPDATE OR DELETE ON ' ||
                 quote_ident(target_table::TEXT) ||
                 ' FOR EACH ROW EXECUTE PROCEDURE stamm.if_modified_func(' ||
                 quote_literal(audit_query_text) || _ignored_cols_snip || ');';
        RAISE NOTICE '%',_q_txt;
        EXECUTE _q_txt;
        stm_targets = 'TRUNCATE';
    ELSE
    END IF;

    _q_txt = 'CREATE TRIGGER audit_trigger_stm AFTER ' || stm_targets || ' ON ' ||
             target_table ||
             ' FOR EACH STATEMENT EXECUTE PROCEDURE stamm.if_modified_func('||
             quote_literal(audit_query_text) || ');';
    RAISE NOTICE '%',_q_txt;
    EXECUTE _q_txt;

END;
$body$
language 'plpgsql';

COMMENT ON FUNCTION audit_table(regclass, boolean, boolean, text[]) IS $body$
Add auditing support to a table.

Arguments:
   target_table:     Table name, schema qualified if not on search_path
   audit_rows:       Record each row change, or only audit at a statement level
   audit_query_text: Record the text of the client query that triggered the audit event?
   ignored_cols:     Columns to exclude from update diffs, ignore updates that change only ignored cols.
$body$;

-- Pg doesn't allow variadic calls with 0 params, so provide a wrapper
CREATE OR REPLACE FUNCTION audit_table(target_table regclass, audit_rows boolean, audit_query_text boolean) RETURNS void AS $body$
SELECT audit_table($1, $2, $3, ARRAY[]::text[]);
$body$ LANGUAGE SQL;

-- And provide a convenience call wrapper for the simplest case
-- of row-level logging with no excluded cols and query logging enabled.
--
CREATE OR REPLACE FUNCTION audit_table(target_table regclass) RETURNS void AS $body$
SELECT audit_table($1, BOOLEAN 't', BOOLEAN 't');
$body$ LANGUAGE 'sql';

COMMENT ON FUNCTION audit_table(regclass) IS $body$
Add auditing support to the given table. Row-level changes will be logged with full client query text. No cols are ignored.
$body$;

CREATE INDEX probe_id_ndx ON audit_trail(cast("row_data"->>'ort_id' AS varchar));

-- View for ort audit trail
CREATE OR REPLACE VIEW audit_trail_ort AS
SELECT audit_trail.id,
    audit_trail.table_name,
    audit_trail.tstamp,
    audit_trail.action,
    audit_trail.object_id,
    audit_trail.row_data,
    audit_trail.changed_fields,
    cast(row_data ->> 'id' AS varchar) AS ort_id
FROM audit_trail;

SELECT audit_table('ort', true, false, '{id, ort_id, tree_modified, letzte_aenderung}'::text[]);

SET search_path TO public;
