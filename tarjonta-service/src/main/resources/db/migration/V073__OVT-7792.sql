--merge old types to korkeakoulutus, if any.
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='AMMATTIKORKEAKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi='KORKEAKOULUTUS' WHERE koulutustyyppi='YLIOPISTOKOULUTUS';

--update koulutustyyppi uris
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_1|koulutustyyppi_4|koulutustyyppi_13|' WHERE koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_3|' WHERE koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli SET koulutustyyppi_uri='|koulutustyyppi_14|' WHERE koulutustyyppi = 'LUKIOKOULUTUS';

--add column for toteutustyyppi enum
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN toteutustyyppi varchar(255);

--update missing data
UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='KORKEAKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'KORKEAKOULUTUS';
UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='LUKIOKOULUTUS' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'LUKIOKOULUTUS';
UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='AMMATILLINEN_PERUSTUTKINTO' FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='MAAHANM_AMM_VALMISTAVA_KOULUTUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='MAAHANM_LUKIO_VALMISTAVA_KOULUTUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='PERUSOPETUKSEN_LISAOPETUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'PERUSOPETUKSEN_LISAOPETUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='PERUSOPETUKSEN_LISAOPETUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'PERUSOPETUKSEN_LISAOPETUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='VAPAAN_SIVISTYSTYON_KOULUTUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'VAPAAN_SIVISTYSTYON_KOULUTUS';

UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='VALMENTAVA_JA_KUNTOUTTAVA_OPETUS' 
FROM koulutusmoduuli m WHERE m.id=kt.koulutusmoduuli_id AND koulutustyyppi = 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS';

--update correct type for aikuislukio
UPDATE koulutusmoduuli_toteutus kt set toteutustyyppi='LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA' from koulutusmoduuli_toteutus_koulutuslaji ktk, koulutusmoduuli m 
WHERE ktk.koodi_uri like 'koulutuslaji_a%'
AND ktk.koulutusmoduuli_toteutus_id = kt.id
AND kt.koulutusmoduuli_id=m.id
AND m.koulutustyyppi = 'LUKIOKOULUTUS';

--add column for osaamisala uris and generate the uris from koulutusohjelma column
ALTER TABLE koulutusmoduuli ADD COLUMN osaamisala_uri varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN osaamisala_uri varchar(255);
UPDATE koulutusmoduuli SET osaamisala_uri='osaamisala' || replace(koulutusohjelma_uri, 'koulutusohjelmaamm', '')  WHERE koulutustyyppi='AMMATILLINEN_PERUSKOULUTUS';

--add join column 
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN koulutusmoduuli_toteutus_children_id int8;
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN jarjesteja varchar(255);




