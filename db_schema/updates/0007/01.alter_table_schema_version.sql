ALTER TABLE public.lada_schema_version
    ALTER COLUMN update_date SET DEFAULT (now() AT TIME ZONE 'utc');
