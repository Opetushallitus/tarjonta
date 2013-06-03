alter table yhteyshenkilo add column tyyppi character varying(32);
UPDATE yhteyshenkilo SET tyyppi='Yhteyshenkilo';

-- ROLLBACK
--ALTER TABLE yhteyshenkilo DROP COLUMN tyyppi;

alter table koulutusmoduuli_toteutus add column hinta numeric(19,2);

CREATE TABLE koulutusmoduuli_toteutus_pohjakoulutusvaatimus(
  koulutusmoduuli_toteutus_id bigint NOT NULL,
  koodi_uri character varying(255) NOT NULL,
  CONSTRAINT koulutusmoduuli_toteutus_pohjakoulutusvaatimus_pkey PRIMARY KEY (koulutusmoduuli_toteutus_id , koodi_uri ),
  CONSTRAINT fk182ad11a2566ebfa FOREIGN KEY (koulutusmoduuli_toteutus_id)
      REFERENCES koulutusmoduuli_toteutus (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
