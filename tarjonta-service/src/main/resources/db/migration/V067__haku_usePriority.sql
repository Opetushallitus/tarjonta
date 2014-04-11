/*
 * Priority setting to the Haku.
 *
 * Used in KI (koulutus information) and sijoittelu to know if the users
 * application options should be ordered / prioritized.
 */
ALTER TABLE haku ADD COLUMN usePriority BOOL;

UPDATE haku SET usePriority = true;
