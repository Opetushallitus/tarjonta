--FIX DATA, IF ANY
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='AMMATTIKORKEAKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='YLIOPISTOKOULUTUS';


UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_1|koulutustyyppi_4|koulutustyyppi_13|' WHERE koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_3|' WHERE koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_14|' WHERE koulutustyyppi = 'LUKIOKOULUTUS';


ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN tyyppi varchar(255);
UPDATE koulutusmoduuli_toteutus kt set tyyppi='KORKEAKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli_toteutus kt set tyyppi='LUKIOKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'LUKIOKOULUTUS';

ALTER TABLE koulutusmoduuli ADD COLUMN osaamisala_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN osaamisala_uri varchar(255);
UPDATE koulutusmoduuli SET osaamisala_uri='osaamisala' || replace(koulutusohjelma_uri, 'koulutusohjelmaamm', '')  WHERE koulutustyyppi='AMMATILLINEN_PERUSKOULUTUS';

ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutusmoduuli_toteutus_children_id int8;

