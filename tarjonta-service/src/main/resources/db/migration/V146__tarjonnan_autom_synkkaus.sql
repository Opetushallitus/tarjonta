ALTER TABLE "haku"
ADD "autosync_tarjonta" boolean NOT NULL DEFAULT 'false';

ALTER TABLE "haku"
ADD "autosync_tarjonta_from" date NULL;

ALTER TABLE "haku"
ADD "autosync_tarjonta_to" date NULL;