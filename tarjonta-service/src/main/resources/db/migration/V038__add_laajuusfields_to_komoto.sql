ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN IF EXISTS opintojen_laajuus_arvo;
ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN IF EXISTS opintojen_laajuus_yksikko IF EXISTS;
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN opintojen_laajuus_arvo varchar(255);
ALTER TABLE koulutusmoduuli_toteutus ADD COLUMN opintojen_laajuus_yksikko varchar(255);