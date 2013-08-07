-- komo
ALTER TABLE koulutusmoduuli ADD COLUMN viimpaivityspvm timestamp;
UPDATE koulutusmoduuli SET viimpaivityspvm=updated;
ALTER TABLE koulutusmoduuli DROP COLUMN updated;

-- komoto
UPDATE koulutusmoduuli_toteutus SET viimpaivityspvm=updated;
ALTER TABLE koulutusmoduuli_toteutus DROP COLUMN updated;
