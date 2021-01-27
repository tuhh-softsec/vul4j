ALTER TABLE stamm.datensatz_erzeuger
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE stamm.messprogramm_kategorie
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE stamm.gemeindeuntergliederung
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE stamm.ort
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE stamm.probenehmer
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

