ALTER TABLE "yhteyshenkilo"
ADD "nimi" character varying(255) NULL;

update yhteyshenkilo
set nimi = TRIM(CONCAT(etunimis, ' ', sukunimi));

ALTER TABLE "yhteyshenkilo"
DROP "etunimis",
DROP "sukunimi";