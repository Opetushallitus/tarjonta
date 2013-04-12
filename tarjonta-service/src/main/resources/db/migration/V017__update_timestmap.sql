ALTER TABLE hakukohde ADD COLUMN viimPaivittajaOid character varying(50);

ALTER TABLE hakukohde ADD COLUMN viimPaivitysPvm timestamp;

ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN viimPaivittajaOid character varying(50);

ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN viimPaivitysPvm timestamp;

ALTER TABLE valintakoe ADD COLUMN viimPaivittajaOid character varying(50);

ALTER TABLE valintakoe ADD COLUMN viimPaivitysPvm timestamp;

ALTER TABLE hakukohdeliite ADD COLUMN viimPaivittajaOid character varying(50);

ALTER TABLE hakukohdeliite ADD COLUMN viimPaivitysPvm timestamp;
