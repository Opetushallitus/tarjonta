UPDATE koulutusmoduuli
SET koulutustyyppi_uri = REPLACE(koulutustyyppi_uri, 'koulutustyyppi_20|', 'koulutustyyppi_21|')
WHERE lukiolinja_uri IS NOT NULL;

UPDATE koulutusmoduuli
SET koulutustyyppi_uri = CONCAT(koulutustyyppi_uri, 'koulutustyyppi_21|')
WHERE koulutustyyppi = 'LUKIOKOULUTUS'
AND koulutustyyppi_uri NOT LIKE '%koulutustyyppi_20|%';