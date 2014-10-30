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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;

/**
 * DAO for indexer.
 */
public interface IndexerDAO {

    /**
     * Find all hakukohteet for indexer. Used for bulk indexing.
     */
    List<HakukohdeIndexEntity> findAllHakukohteet();

    /**
     * Find hakukohde. Used for indexing single hakukohde.
     * @param hakukohdeId hakukohdeId
     * @return
     */
    HakukohdeIndexEntity findHakukohdeById(Long hakukohdeId);

    /**
     * Find koulutusmoduulitoteutuses by hakukohde id. Used to find koulutukset that are attached to hakukohde.
     * @param hakukohdeId
     * @return
     */
    List<KoulutusIndexEntity> findKoulutusmoduuliToteutusesByHakukohdeId(Long hakukohdeId);

    /**
     * Find all hakukohde ids
     * @return
     */
    List<Long> findAllHakukohdeIds();


    /**
     * Find all koulutus (koulutusmoduulitoteutus) ids
     */
    List<Long> findAllKoulutusIds();

    /**
     * Find hakuajat for haku. Used to find hakuajat for hakukohde.
     * @param id
     * @return
     */
    List<HakuAikaIndexEntity> findHakuajatForHaku(Long id);

    /**
     * Find all koulutukset for indexer. Used for bulk indexing.
     * @return
     */
    List<KoulutusIndexEntity> findAllKoulutukset();

    /**
     * Find koulutus by id
     * @param koulutusmoduuliId
     * @return
     */
    KoulutusIndexEntity findKoulutusById(Long koulutusmoduulitoteutusId);


    /**
     * Find hakukohteet for koulutusmoduulitoteutus.
     */
    List<HakukohdeIndexEntity> findhakukohteetByKoulutusmoduuliToteutusId(Long id);

    /**
     * Find koulutuslajis for koulutus
     * @param koulutusmoduuliToteutusId
     * @return
     */
    List<String> findKoulutusLajisForKoulutus(Long koulutusmoduuliToteutusId);

    /**
     * Find komo.nimi (koulutuksen nimi) for komoto, used in kk koulutukset
     * @param id komoto id
     * @return
     */
    MonikielinenTeksti getKomoNimi(Long id);

    /**
     * Find nimi for hakukohde, used in kk hakukohdes
     * @param hakukohdeId hakukohteen id
     * @return
     */
    MonikielinenTeksti getNimiForHakukohde(Long hakukohdeId);

    /**
     * Etsi hakukohteet joita ei ole indeksoitu.
     * @return
     */
    List<Long> findUnindexedHakukohdeIds();

    /**
     * Etsi koulutukset joita ei ole indeksoitu.
     * @return
     */
    List<Long> findUnindexedKoulutusIds();

    /**
     * Päivitä viimIndeksointi pvm
     * @param id
     * @param time
     */
    void updateHakukohdeIndexed(Long id, Date time);

    /**
     * Päivitä viimIndeksointi pvm
     * @param id
     * @param time
     */
    void updateKoulutusIndexed(Long id, Date time);

    /**
     * Palauta nimi komotosta (käytetään erikoistapauksissa valmentava)
     * @param koulutusId komoto id
     * @return
     */
    MonikielinenTeksti getKomotoNimi(Long koulutusId);

    Set<String> getOwners(String oid, String type);

}
