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
