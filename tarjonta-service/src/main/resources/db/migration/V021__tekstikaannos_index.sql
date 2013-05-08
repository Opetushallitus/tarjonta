ALTER TABLE teksti_kaannos ADD CONSTRAINT teksti_kaannos_kieli_koodi_teksti_id_key UNIQUE (kieli_koodi, teksti_id);
