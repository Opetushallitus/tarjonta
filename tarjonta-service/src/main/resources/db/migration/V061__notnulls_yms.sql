/* *** refaktorointi *** */
/* hakukohdeliite */
UPDATE hakukohdeliite SET hakukohde_liite_nimi = liitteentyyppikoodistonimi WHERE hakukohde_liite_nimi IS NULL;
ALTER TABLE hakukohdeliite DROP COLUMN liitteenTyyppiKoodistoNimi;

/* pisteraja */
ALTER TABLE pisteraja ADD COLUMN valintakoe_id int8;
UPDATE pisteraja SET valintakoe_id = (SELECT valintakoe_id FROM valintakoe_pisteraja WHERE valintakoe_pisteraja.pisterajat_id = pisteraja.id);

/* roskat pois */
DELETE FROM pisteraja WHERE valintakoe_id IS NULL;

ALTER TABLE pisteraja ALTER COLUMN valintakoe_id SET NOT NULL;
ALTER TABLE pisteraja 
    ADD CONSTRAINT FK86F4FB1C5B6C9A9 
    FOREIGN KEY (valintakoe_id) 
    REFERENCES valintakoe;

ALTER TABLE valintakoe_pisteraja DROP CONSTRAINT FK63376A60BFB79B19;
ALTER TABLE valintakoe_pisteraja DROP CONSTRAINT FK63376A60C5B6C9A9;
DROP TABLE valintakoe_pisteraja;

/* *** muut roskat pois *** */
DELETE FROM hakukohdeliite WHERE hakukohde_id IS NULL;
DELETE FROM valintakoe WHERE hakukohde_id IS NULL;

/* *** not-nullit *** */

/* hakukohde */
ALTER TABLE hakukohde ALTER COLUMN aloituspaikat_lkm SET NOT NULL;
ALTER TABLE hakukohde ALTER COLUMN kaksoisTutkinto SET NOT NULL;
ALTER TABLE hakukohde ALTER COLUMN kaytetaanJarjestelmanValintapalvelua SET NOT NULL;
ALTER TABLE hakukohde ALTER COLUMN valintojenAloituspaikatLkm SET NOT NULL;
ALTER TABLE hakukohde ALTER COLUMN haku_id SET NOT NULL;

/* hakukohdeliite */
ALTER TABLE hakukohdeliite ALTER COLUMN erapaiva SET NOT NULL;
ALTER TABLE hakukohdeliite ALTER COLUMN hakukohde_liite_nimi SET NOT NULL;
ALTER TABLE hakukohdeliite ALTER COLUMN hakukohde_id SET NOT NULL;

/* pisteraja */
ALTER TABLE pisteraja ALTER COLUMN alinhyvaksyttypistemaara SET NOT NULL;
ALTER TABLE pisteraja ALTER COLUMN alinpistemaara SET NOT NULL;
ALTER TABLE pisteraja ALTER COLUMN valinnanpisterajatyyppi SET NOT NULL;
ALTER TABLE pisteraja ALTER COLUMN ylinpistemaara SET NOT NULL;

/* valintakoe */
ALTER TABLE valintakoe ALTER COLUMN hakukohde_id SET NOT NULL;

/* valintakoe_ajankohta */
ALTER TABLE valintakoe_ajankohta ALTER COLUMN alkamisaika SET NOT NULL;
ALTER TABLE valintakoe_ajankohta ALTER COLUMN paattymisaika SET NOT NULL;
ALTER TABLE valintakoe_ajankohta ALTER COLUMN valintakoe_id SET NOT NULL;
