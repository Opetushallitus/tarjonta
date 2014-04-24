--rename
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

--other stuff
ALTER TABLE koulutusmoduuli ADD COLUMN koulutustyyppi_uri varchar(255); --for future use
ALTER TABLE koulutusmoduuli DROP COLUMN tutkintonimike; --unused field

--ROLLBACK
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