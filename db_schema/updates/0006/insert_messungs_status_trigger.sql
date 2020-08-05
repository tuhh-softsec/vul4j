CREATE OR REPLACE FUNCTION land.update_status_messung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        CASE
            WHEN new.status_kombi in (2, 3, 4, 5, 6, 7, 8, 10, 11, 12)
            THEN
                UPDATE land.messung SET fertig = true, status = NEW.id WHERE id = NEW.messungs_id;
            WHEN new.status_kombi in (1, 9, 13)
            THEN
                UPDATE land.messung SET fertig = false, status = NEW.id WHERE id = NEW.messungs_id;
            ELSE
                UPDATE land.messung SET status = NEW.id WHERE id = NEW.messungs_id;
        END CASE;
        RETURN NEW;
    END
$$;

CREATE TRIGGER update_messung_after_status_protokoll_created AFTER INSERT ON land.status_protokoll FOR EACH ROW EXECUTE PROCEDURE land.update_status_messung();