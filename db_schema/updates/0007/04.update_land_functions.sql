CREATE OR REPLACE FUNCTION land.set_messung_status()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$
DECLARE status_id integer;
    BEGIN
        INSERT INTO land.status_protokoll
            (mst_id, datum, text, messungs_id, status_kombi)
        VALUES ((SELECT mst_id
                     FROM land.probe
                     WHERE id = NEW.probe_id),
                now() AT TIME ZONE 'utc', '', NEW.id, 1)
        RETURNING id into status_id;
        UPDATE land.messung SET status = status_id where id = NEW.id;
        RETURN NEW;
    END;
$BODY$;

CREATE OR REPLACE FUNCTION land.update_letzte_aenderung()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$
BEGIN
        NEW.letzte_aenderung = now() AT TIME ZONE 'utc';
        RETURN NEW;
    END;
$BODY$;

CREATE OR REPLACE FUNCTION land.update_tree_modified()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$
BEGIN
        NEW.tree_modified = now() AT TIME ZONE 'utc';
        RETURN NEW;
    END;
$BODY$;

CREATE OR REPLACE FUNCTION land.update_tree_modified_messung()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$
BEGIN
        NEW.tree_modified = now() AT TIME ZONE 'utc';
        UPDATE land.messwert SET tree_modified = now() AT TIME ZONE 'utc' WHERE messungs_id = NEW.id;
        UPDATE land.status_protokoll SET tree_modified = now() AT TIME ZONE 'utc' WHERE messungs_id = NEW.id;
        RETURN NEW;
    END;
$BODY$;

CREATE OR REPLACE FUNCTION land.update_tree_modified_probe()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$
BEGIN
        NEW.tree_modified = now() AT TIME ZONE 'utc';
        UPDATE land.messung SET tree_modified = now() AT TIME ZONE 'utc' WHERE probe_id = NEW.id;
        UPDATE land.ortszuordnung SET tree_modified = now() AT TIME ZONE 'utc' WHERE probe_id = NEW.id;
        UPDATE land.zusatz_wert SET tree_modified = now() AT TIME ZONE 'utc' WHERE probe_id = NEW.id;
        RETURN NEW;
    END;
$BODY$;

