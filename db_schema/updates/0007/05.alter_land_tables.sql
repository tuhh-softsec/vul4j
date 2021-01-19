ALTER TABLE land.messprogramm
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.messprogramm_mmt
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.probe
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.probe
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.kommentar_p
    ALTER COLUMN datum SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.ortszuordnung
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.ortszuordnung
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.ortszuordnung_mp
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.ortszuordnung_mp
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.zusatz_wert
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.zusatz_wert
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.messung
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.messung
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.kommentar_m
    ALTER COLUMN datum SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.messwert
    ALTER COLUMN letzte_aenderung SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.messwert
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.status_protokoll
    ALTER COLUMN datum SET DEFAULT (now() AT TIME ZONE 'utc');
ALTER TABLE land.status_protokoll
    ALTER COLUMN tree_modified SET DEFAULT (now() AT TIME ZONE 'utc');

ALTER TABLE land.tagzuordnung
    ALTER COLUMN datum SET DEFAULT (now() AT TIME ZONE 'utc');

