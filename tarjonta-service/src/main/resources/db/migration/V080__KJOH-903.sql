UPDATE "koulutusmoduuli_toteutus"
SET koulutusmoduuli_toteutus_children_id = NULL
WHERE id IN (
  select kt1.id
  from koulutusmoduuli_toteutus as kt1
  left join koulutusmoduuli_toteutus as kt2 on kt1.koulutusmoduuli_toteutus_children_id = kt2.id
  where kt1.koulutusmoduuli_toteutus_children_id > 0
  and kt2.id IS NULL
);

ALTER TABLE "koulutusmoduuli_toteutus"
DROP CONSTRAINT IF EXISTS "koulutusmoduuli_toteutus_koulutusmoduuli_toteutus_children_fkey",
ADD FOREIGN KEY ("koulutusmoduuli_toteutus_children_id") REFERENCES "koulutusmoduuli_toteutus" ("id") ON DELETE CASCADE ON UPDATE CASCADE;
