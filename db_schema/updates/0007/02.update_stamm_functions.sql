CREATE OR REPLACE FUNCTION stamm.update_letzte_aenderung()
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

