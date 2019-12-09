ALTER TABLE stamm.mass_einheit_umrechnung ALTER COLUMN meh_id_von SET NOT NULL;
ALTER TABLE stamm.mass_einheit_umrechnung ALTER COLUMN meh_id_zu SET NOT NULL;
ALTER TABLE stamm.mass_einheit_umrechnung ALTER COLUMN faktor SET NOT NULL;
ALTER TABLE stamm.mass_einheit_umrechnung ADD FOREIGN KEY (meh_id_von) REFERENCES stamm.mess_einheit;
ALTER TABLE stamm.mass_einheit_umrechnung ADD FOREIGN KEY (meh_id_zu) REFERENCES stamm.mess_einheit;
ALTER TABLE stamm.mass_einheit_umrechnung ADD CONSTRAINT mass_einheit_umrechnung_unique UNIQUE(meh_id_von, meh_id_zu);