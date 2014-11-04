ALTER TABLE "hakukohde"
ADD FOREIGN KEY ("valintaperustekuvaustunniste") REFERENCES "valintaperuste_sora_kuvaus" ("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "hakukohde"
ADD FOREIGN KEY ("sorakuvaustunniste") REFERENCES "valintaperuste_sora_kuvaus" ("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "valintaperuste_sora_kuvaus"
ADD "tila" character varying(255) NULL;

CREATE INDEX "valintaperuste_sora_kuvaus_tila" ON "valintaperuste_sora_kuvaus" ("tila");

/* Laita oletuksena kaikkiin olemassa oleviin tilaksi valmis */
update valintaperuste_sora_kuvaus set tila = 'VALMIS';
