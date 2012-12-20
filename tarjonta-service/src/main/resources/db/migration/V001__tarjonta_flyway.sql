ALTER TABLE hakukohde ADD COLUMN osoiterivi1 character varying(255), 
ADD COLUMN osoiterivi2 character varying(255),
ADD COLUMN postinumero character varying(255),
ADD COLUMN postitoimipaikka character varying(255),
ADD COLUMN liitteidentoimituspvm timestamp without time zone,
ADD COLUMN sahkoinentoimitusosoite character varying(255),
ADD COLUMN valintojenaloituspaikatlkm integer;
