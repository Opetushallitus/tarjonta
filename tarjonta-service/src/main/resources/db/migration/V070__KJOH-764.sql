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
ALTER TABLE koulutusmoduuli ALTER COLUMN koulutustyyppi TYPE varchar(64);

UPDATE koulutusmoduuli SET koulutustyyppi='AMMATILLINEN_PERUSKOULUTUS' WHERE koulutustyyppi='AmmatillinenPeruskoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='LUKIOKOULUTUS' WHERE koulutustyyppi='Lukiokoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='Korkeakoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='Ammattikorkeakoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='Yliopistokoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='PERUSOPETUKSEN_LISAOPETUS' WHERE koulutustyyppi='PerusopetuksenLisaopetus';
UPDATE koulutusmoduuli SET koulutustyyppi='VALMENTAVA_JA_KUNTOUTTAVA_OPETUS' WHERE koulutustyyppi='ValmentavaJaKuntouttavaOpetus';
UPDATE koulutusmoduuli SET koulutustyyppi='AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS' WHERE koulutustyyppi='AmmOhjaavaJaValmistavaKoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='MAAHANM_AMM_VALMISTAVA_KOULUTUS' WHERE koulutustyyppi='MaahanmAmmValmistavaKoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='MAAHANM_LUKIO_VALMISTAVA_KOULUTUS' WHERE koulutustyyppi='MaahanmLukioValmistavaKoulutus';
UPDATE koulutusmoduuli SET koulutustyyppi='VAPAAN_SIVISTYSTYON_KOULUTUS' WHERE koulutustyyppi='VapaanSivistystyonKoulutus';

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
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN kandi_koulutus_uri varchar(255); 

create table koulutusmoduuli_toteutus_tutkintonimike (
	koulutusmoduuli_toteutus_id int8 not null,
	koodi_uri varchar(255) not null,
	primary key (koulutusmoduuli_toteutus_id, koodi_uri)
);

alter table koulutusmoduuli_toteutus_tutkintonimike 
        add constraint FK1FE24A872566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;



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
--ALTER TABLE koulutusmoduuli DROP COLUMN row_type;

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
--ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN kandi_koulutus_uri;

--DROP TABLE koulutusmoduuli_toteutus_tutkintonimike CASCADE;
