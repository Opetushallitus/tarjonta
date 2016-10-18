/*
 * tunnistusKaytossa setting to the Haku.
 *
 * Ohjataanko haussa tunnistukseen
 */
ALTER TABLE "haku"
ADD "tunnistuskaytossa" boolean NOT NULL DEFAULT 'false';
