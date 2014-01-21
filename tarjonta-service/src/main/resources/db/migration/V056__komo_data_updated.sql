--OVT-6066 : update laajuusarvo numeric values to koodi uris
UPDATE koulutusmoduuli SET laajuusarvo='opintojenlaajuus_' || laajuusarvo || '#1' WHERE laajuusarvo is not null AND koulutustyyppi != 'Korkeakoulutus';

--OVT-6066 : laajuus is a text, not a value 
UPDATE koulutusmoduuli SET laajuusarvo='opintojenlaajuus_ibdp#1' WHERE koulutusluokitus_koodi like 'koulutus_301102%' AND moduulityyppi='TUTKINTO' AND koulutustyyppi='Lukiokoulutus' AND laajuusarvo is null;

--OVT-6068
UPDATE koulutusmoduuli SET laajuusarvo='opintojenlaajuus_44#1' WHERE koulutusluokitus_koodi like 'koulutus_301101%' AND moduulityyppi='TUTKINTO_OHJELMA' AND koulutustyyppi='Lukiokoulutus' AND lukiolinja='lukiolinjat_0086#1';