CREATE TABLE binarydata(
  id int8 NOT NULL,
  version int8 NOT NULL,
  data oid,
  filename character varying(255),
  mimetype character varying(255),
  CONSTRAINT binarydata_pkey PRIMARY KEY (id)
);

CREATE TABLE koulutusmoduuli_toteutus_kuvat(
  koulutusmoduuli_toteutus_id int8 NOT NULL,
  binary_data_id int8 NOT NULL,
  kieli_uri character varying(255) NOT NULL,
  CONSTRAINT koulutusmoduuli_toteutus_kuvat_pkey PRIMARY KEY (koulutusmoduuli_toteutus_id, kieli_uri),
  CONSTRAINT fk59d7f6812566ebfa FOREIGN KEY (koulutusmoduuli_toteutus_id)
      REFERENCES koulutusmoduuli_toteutus (id),
  CONSTRAINT fk59d7f681d81ff18c FOREIGN KEY (binary_data_id)
      REFERENCES binarydata (id),
  CONSTRAINT koulutusmoduuli_toteutus_kuvat_binary_data_id_key UNIQUE (binary_data_id)
);
