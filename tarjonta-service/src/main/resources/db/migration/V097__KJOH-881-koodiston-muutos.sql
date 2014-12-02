/* Koodistojen muutokset KJOH-881 tikettiin liitettyn excel tiedoston mukaisesti */

-- Opetuspaikka
INSERT INTO koulutusmoduuli_toteutus_opetuspaikka (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_e#1', 'opetuspaikkakk_2#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetuspaikka AS opetuspaikka ON
        (opetuspaikka.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetuspaikka.koodi_uri = 'opetuspaikkakk_2#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_e#1' AND opetuspaikka.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetuspaikka (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_l#1', 'opetuspaikkakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetuspaikka AS opetuspaikka ON
        (opetuspaikka.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetuspaikka.koodi_uri = 'opetuspaikkakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_l#1' AND opetuspaikka.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetuspaikka (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_p#1', 'opetuspaikkakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetuspaikka AS opetuspaikka ON
        (opetuspaikka.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetuspaikka.koodi_uri = 'opetuspaikkakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_p#1' AND opetuspaikka.koulutusmoduuli_toteutus_id IS NULL
);


-- Opetusaika
INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_i#1', 'opetusaikakk_2#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_2#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_i#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_l#1', 'opetusaikakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_l#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_p#1', 'opetusaikakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_p#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_im#1', 'opetusaikakk_2#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_2#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_im#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_pi#1', 'opetusaikakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_pi#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_pi#1', 'opetusaikakk_2#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_2#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_pi#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusaika (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_pm#1', 'opetusaikakk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusaika AS opetusaika ON
        (opetusaika.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusaika.koodi_uri = 'opetusaikakk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_pm#1' AND opetusaika.koulutusmoduuli_toteutus_id IS NULL
);


-- Opetusmuoto
INSERT INTO koulutusmoduuli_toteutus_opetusmuoto (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_l#1', 'opetusmuotokk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusmuoto AS opetusmuoto ON
        (opetusmuoto.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusmuoto.koodi_uri = 'opetusmuotokk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_l#1' AND opetusmuoto.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusmuoto (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_m#1', 'opetusmuotokk_3#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusmuoto AS opetusmuoto ON
        (opetusmuoto.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusmuoto.koodi_uri = 'opetusmuotokk_3#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_m#1' AND opetusmuoto.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusmuoto (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_p#1', 'opetusmuotokk_1#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusmuoto AS opetusmuoto ON
        (opetusmuoto.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusmuoto.koodi_uri = 'opetusmuotokk_1#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_p#1' AND opetusmuoto.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusmuoto (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_im#1', 'opetusmuotokk_3#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusmuoto AS opetusmuoto ON
        (opetusmuoto.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusmuoto.koodi_uri = 'opetusmuotokk_3#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_im#1' AND opetusmuoto.koulutusmoduuli_toteutus_id IS NULL
);

INSERT INTO koulutusmoduuli_toteutus_opetusmuoto (
    SELECT muoto.koulutusmoduuli_toteutus_id, replace(muoto.koodi_uri, 'opetusmuoto_pm#1', 'opetusmuotokk_3#1') as koodi_uri
    FROM koulutusmoduuli_toteutus_opetusmuoto AS muoto
    LEFT JOIN koulutusmoduuli_toteutus_opetusmuoto AS opetusmuoto ON
        (opetusmuoto.koulutusmoduuli_toteutus_id = muoto.koulutusmoduuli_toteutus_id AND opetusmuoto.koodi_uri = 'opetusmuotokk_3#1')
    WHERE muoto.koodi_uri = 'opetusmuoto_pm#1' AND opetusmuoto.koulutusmoduuli_toteutus_id IS NULL
);
