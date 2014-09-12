/**
 2.-asteen valintaperustekuvaukset eivät käytä organisaatio_tyyppi-kenttää, vaan
 sen sijaan niillä on avain-kenttä, joka linkittää kuvauksen koodistosta
 tulevaan valintaperustekuvausryhmään / SORA-ryhmään.
 */
ALTER TABLE "valintaperuste_sora_kuvaus" ADD "avain" character varying(255) NULL;