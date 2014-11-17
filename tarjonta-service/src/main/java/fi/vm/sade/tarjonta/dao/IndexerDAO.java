/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IndexerDAO {

    List<HakukohdeIndexEntity> findAllHakukohteet();

    HakukohdeIndexEntity findHakukohdeById(Long hakukohdeId);

    List<KoulutusIndexEntity> findKoulutusmoduuliToteutusesByHakukohdeId(Long hakukohdeId);

    List<Long> findAllHakukohdeIds();

    List<Long> findAllKoulutusIds();

    List<HakuAikaIndexEntity> findHakuajatForHaku(Long id);

    List<KoulutusIndexEntity> findAllKoulutukset();

    KoulutusIndexEntity findKoulutusById(Long koulutusmoduulitoteutusId);

    List<HakukohdeIndexEntity> findhakukohteetByKoulutusmoduuliToteutusId(Long id);

    List<String> findKoulutusLajisForKoulutus(Long koulutusmoduuliToteutusId);

    MonikielinenTeksti getKomoNimi(Long id);

    MonikielinenTeksti getNimiForHakukohde(Long hakukohdeId);

    List<Long> findUnindexedHakukohdeIds();

    List<Long> findUnindexedKoulutusIds();

    void updateHakukohdeIndexed(Long id, Date time);

    void updateKoulutusIndexed(Long id, Date time);

    MonikielinenTeksti getKomotoNimi(Long koulutusId);

    Set<String> getOwners(String oid, String type);

    MonikielinenTeksti getAloituspaikatKuvausForHakukohde(long hakukohdeId);
}
