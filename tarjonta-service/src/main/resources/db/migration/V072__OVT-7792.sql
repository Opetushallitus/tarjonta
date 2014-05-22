UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_1|koulutustyyppi_4|koulutustyyppi_13|' WHERE koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_3|' WHERE koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_14|' WHERE koulutustyyppi = 'LUKIOKOULUTUS';


ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN tyyppi varchar(255);
UPDATE koulutusmoduuli_toteutus kt set tyyppi='KORKEAKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli_toteutus kt set tyyppi='LUKIOKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'LUKIOKOULUTUS';

