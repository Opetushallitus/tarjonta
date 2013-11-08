ALTER TABLE hakukohde DROP COLUMN IF EXISTS  kaksoisTutkinto;

ALTER TABLE hakukohde ADD COLUMN kaksoisTutkinto boolean not null DEFAULT FALSE;