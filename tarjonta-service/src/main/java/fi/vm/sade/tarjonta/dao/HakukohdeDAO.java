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

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import java.util.*;

/**
 * DAO interface to retrieve Application Options (Hakukohde's).
 */
public interface HakukohdeDAO extends JpaDAO<Hakukohde, Long> {

    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid);

    //    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely);
    public List<Hakukohde> findOrphanHakukohteet();

    HakukohdeLiite findHakuKohdeLiiteById(String id);

    boolean removeHakuKohdeLiiteById(String id);

    List<HakukohdeLiite> findHakukohdeLiitesByHakukohdeOid(String oid);

    Valintakoe findValintaKoeById(String id);

    List<Valintakoe> findValintakoeByHakukohdeOid(String oid);

    Hakukohde findHakukohdeByOid(String oid);

    List<Hakukohde> findHakukohteetByOids(Set<String> oids);

    Hakukohde findHakukohdeByUniqueExternalId(String uniqueExternalId);

    Hakukohde findExistingHakukohde(HakukohdeV1RDTO dto);

    Hakukohde findHakukohdeById(Long id);

    Hakukohde findHakukohdeByOid(final String oid, final boolean showDeleted);

    void merge(Hakukohde hk);

    /**
     * @deprecated {@link #findHakukohdeByOid(String)}
     */
    @Deprecated
    Hakukohde findHakukohdeWithDepenciesByOid(String oid);

    /**
     * @deprecated {@link #findHakukohdeByOid(String)}
     */
    @Deprecated
    Hakukohde findHakukohdeWithKomotosByOid(String oid);

    void removeValintakoe(Valintakoe valintakoe);

    void removeHakukohdeLiite(HakukohdeLiite hakukohdeLiite);

    void updateValintakoe(List<Valintakoe> valintakoes, String hakukohdeOid);

    void updateSingleValintakoe(Valintakoe valintakoe, String hakukohdeOid);

    void insertLiittees(List<HakukohdeLiite> liites, String hakukohdeOid);

    void updateLiite(HakukohdeLiite hakukohdeLiite, String hakukohdeOid);

    Hakukohde findHakukohdeByUlkoinenTunniste(String ulkoinenTunniste, String tarjoajaOid);

    List<Hakukohde> findByTarjoajaHakuAndNimiUri(Set<String> tarjoajaOid, String hakuOid, String nimiUri);

    List<Hakukohde> findByNameTermAndYear(String name, String term, int year, String providerOid);

    List<Hakukohde> findByTermYearAndProvider(String term, int year, String providerOid);

    List<KoulutusmoduuliToteutus> komotoTest(String term, int year, String providerOid);

    // String getAlkamiskausiUri();

    /**
     * Find list of OIDs with given search specs.
     *
     * @param tila
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return list of oids.
     */
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince, boolean showKK);

    /**
     * Hae hakukohteet jotka liittyvät komotoon
     *
     * @param id komoto id (ei oid!)
     * @return
     */
    List<String> findOidsByKoulutusId(long id);

    /**
     * Returns mapping from haku oids to hakukohde oids (not including ones with POISTETTU state)
     * @return
     */
    Map<String, List<String>> findAllHakuToHakukohde();

    /**
     * @param hakuOid
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return hakukohde OIDs
     */
    public List<String> findByHakuOid(String hakuOid, String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince);

    /**
     * Search application option entities by application system OIDs.
     *
     * @param hakuOids
     * @param requiredStatus
     * @return List<hakukohdeOid>
     */
    List<Long> searchHakukohteetByHakuOid(final Collection<String> hakuOids, final fi.vm.sade.tarjonta.shared.types.TarjontaTila... requiredStatus);

    List<Long> findIdsByoids(Collection<String> oids);

    /**
     * Vaihtaa hakukohteen tilan suoraa poistetuksi/passivoiduksi.
     *
     * @param hakukohdeOid
     * @param userOid
     */
    public void safeDelete(final String hakukohdeOid, final String userOid);

    List<String> findAllOids();

    void setViimIndeksointiPvmToNull(Long hakukohdeId);

    List<String> findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(Long hakuId, Boolean hakuValue);

    Map<Long, List<String>> findAllHakuToHakukohdeWhereYlioppilastutkintoAntaaHakukelpoisuuden(List<Haku> hakus);
}
