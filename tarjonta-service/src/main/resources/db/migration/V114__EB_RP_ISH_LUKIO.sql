UPDATE koulutusmoduuli
SET koulutustyyppi_uri = CONCAT(koulutustyyppi_uri, 'koulutustyyppi_20|')
WHERE lukiolinja_uri IS NOT NULL;