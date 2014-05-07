/*
 * Boolean that tells if the haku should use systems own application form.
 */
ALTER TABLE haku ADD COLUMN jarjestelmanHakulomake BOOL;

-- By default no haku uses system forms
UPDATE haku SET jarjestelmanHakulomake = false;

-- Except those that dont have own hakulomake set
UPDATE haku SET jarjestelmanHakulomake = true WHERE hakulomake_url IS NULL;
