/*
 * KJOH-744 organisation (tarjoaja) for Haku
 * - more space for oids
 */
ALTER TABLE haku ALTER COLUMN organisationOids TYPE text;
