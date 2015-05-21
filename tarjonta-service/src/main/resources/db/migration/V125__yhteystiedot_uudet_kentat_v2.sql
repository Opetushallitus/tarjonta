ALTER TABLE "yhteystiedot"
ADD "kansainvalinen_osoite" text NULL,
ADD "kansainvalinen_kayntiosoite" text NULL,
ADD "osoitemuoto" character varying(255) NULL;

UPDATE "yhteystiedot"
SET osoitemuoto = 'SUOMALAINEN';

ALTER TABLE "yhteystiedot"
ALTER "osoitemuoto" SET NOT NULL;