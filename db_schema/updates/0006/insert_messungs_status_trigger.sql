CREATE OR REPLACE FUNCTION update_status_messung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE
        status_kombi_record stamm.status_kombi;
        status_wert INTEGER;
    BEGIN
        UPDATE land.messung SET status = NEW.id WHERE id = NEW.messungs_id;
        SELECT * FROM stamm.status_kombi into status_kombi_record WHERE ID = NEW.status_kombi;
        status_wert := status_kombi_record.wert_id;

        CASE
            WHEN status_wert = 1 OR status_wert = 2 OR status_wert = 3 OR status_wert = 7
            THEN
                UPDATE land.messung SET fertig = true WHERE id = NEW.messungs_id;
            WHEN status_wert = 4
            THEN
                UPDATE land.messung SET fertig = false WHERE id = NEW.messungs_id;
            ELSE
                -- Skip
        END CASE;
        RETURN NEW;
    END
$$;

CREATE TRIGGER update_messung_after_status_protokoll_created AFTER INSERT ON land.status_protokoll FOR EACH ROW EXECUTE PROCEDURE update_status_messung();