--KOMO rename
ALTER TABLE koulutusmoduuli RENAME COLUMN tutkintoohjelmanimi TO tutkinto_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusala TO koulutusala_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN eqfluokitus TO eqf_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN nqfluokitus TO nqf_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusAste TO koulutusaste_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusluokitus_koodi TO koulutus_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusohjelmakoodi TO koulutusohjelma_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN lukiolinja TO lukiolinja_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN laajuusarvo TO opintojen_laajuusarvo_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN laajuusyksikko TO opintojen_laajuusyksikko_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN opintoala TO opintoala_uri;
ALTER TABLE koulutusmoduuli RENAME COLUMN kandi_koulutuskoodi TO kandi_koulutus_uri;

--KOMO other stuff
ALTER TABLE koulutusmoduuli ADD COLUMN koulutustyyppi_uri varchar(255); --for future use
ALTER TABLE koulutusmoduuli DROP COLUMN tutkintonimike; --unused field

--KOMO ROLLBACK
--ALTER TABLE koulutusmoduuli DROP COLUMN koulutustyyppi_uri;
--ALTER TABLE koulutusmoduuli ADD COLUMN tutkintonimike varchar(255);
--ALTER TABLE koulutusmoduuli RENAME COLUMN tutkinto_uri TO tutkintoohjelmanimi;
--ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusala_uri TO koulutusala;
--ALTER TABLE koulutusmoduuli RENAME COLUMN eqf_uri TO eqfluokitus;
--ALTER TABLE koulutusmoduuli RENAME COLUMN nqf_uri TO nqfluokitus;
--ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusaste_uri TO koulutusaste;
--ALTER TABLE koulutusmoduuli RENAME COLUMN koulutus_uri TO koulutusluokitus_koodi;
--ALTER TABLE koulutusmoduuli RENAME COLUMN koulutusohjelma_uri TO koulutusohjelmakoodi;
--ALTER TABLE koulutusmoduuli RENAME COLUMN lukiolinja_uri TO lukiolinja;
--ALTER TABLE koulutusmoduuli RENAME COLUMN opintojen_laajuusarvo_uri TO laajuusarvo;
--ALTER TABLE koulutusmoduuli RENAME COLUMN opintojen_laajuusyksikko_uri TO laajuusyksikko;
--ALTER TABLE koulutusmoduuli RENAME COLUMN opintoala_uri TO opintoala;
--ALTER TABLE koulutusmoduuli RENAME COLUMN kandi_koulutus_uri TO kandi_koulutuskoodi;


--KOMOTO rename
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN opintojen_laajuus_yksikko TO opintojen_laajuusyksikko_uri;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN opintojen_laajuus_arvo TO opintojen_laajuusarvo;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN koulutusaste TO koulutusaste_uri;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN pohjakoulutusvaatimus TO pohjakoulutusvaatimus_uri;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN alkamiskausi TO alkamiskausi_uri;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN suunniteltu_kesto_arvo TO suunniteltukesto_arvo;
ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN suunniteltu_kesto_yksikko TO suunniteltukesto_yksikko_uri;

--KOMOTO other stuff
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN opintojen_laajuusarvo_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN tutkinto_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutusala_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN eqf_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN nqf_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutus_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutusohjelma_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN lukiolinja_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN opintoala_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutustyyppi_uri varchar(255); --for future use

--KOMOTO ROLLBACK
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN opintojen_laajuusyksikko_uri TO opintojen_laajuus_yksikko;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN opintojen_laajuusarvo TO opintojen_laajuus_arvo;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN koulutusaste_uri TO koulutusaste;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN pohjakoulutusvaatimus_uri TO pohjakoulutusvaatimus;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN alkamiskausi_uri TO alkamiskausi;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN suunniteltukesto_arvo TO suunniteltu_kesto_arvo;
--ALTER TABLE koulutusmoduuli_toteutus RENAME COLUMN suunniteltukesto_yksikko_uri TO suunniteltu_kesto_yksikko;

--KOMOTO other stuff
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN opintojen_laajuusarvo_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN tutkinto_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN koulutusala_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN eqf_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN nqf_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN koulutus_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN koulutusohjelma_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN lukiolinja_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN opintoala_uri;
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN koulutustyyppi_uri;
