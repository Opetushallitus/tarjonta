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
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.*;

public interface KoulutusmoduuliToteutusDAO extends JpaDAO<KoulutusmoduuliToteutus, Long> {

    KoulutusmoduuliToteutus findByOid(String oid);

    KoulutusmoduuliToteutus findKomotoByOid(String oid);

    KoulutusmoduuliToteutus findKomotoByKoulutusId(KoulutusIdentification id);

    KoulutusmoduuliToteutus findFirstByKomoOid(String komoOid);

    List<KoulutusmoduuliToteutus> findByCriteria(
            List<String> tarjoajaOids, String nimi, int koulutusAlkuVuosi, List<Integer> koulutusAlkuKuukaudet);

    KoulutusmoduuliToteutus findKomotoWithYhteyshenkilosByOid(String oid);

    List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids);

    List<KoulutusmoduuliToteutus> findFutureKoulutukset(
            List<ToteutustyyppiEnum> toteutustyyppis,
            int offset,
            int limit
    );

    /**
     * Return all koulutumoduulitoteutuses in oid list with Hakukohde depencies
     *
     * @param komotoOids
     * @return List<KoulutusmoduuliToteutus>
     */
    List<KoulutusmoduuliToteutus> findKoulutusModuulisWithHakukohdesByOids(List<String> komotoOids);

    List<KoulutusmoduuliToteutus> findSiblingKomotos(KoulutusmoduuliToteutus komoto);

    List<KoulutusmoduuliToteutus> findKoulutusModuuliWithPohjakoulutusAndTarjoaja(String tarjoaja, String pohjakoulutus,
                                                                                  String koulutusluokitus, String koulutusohjelma,
                                                                                  List<String> opetuskielis, List<String> koulutuslajis);

    List<KoulutusmoduuliToteutus> findKomotosByKomoTarjoajaPohjakoulutus(
            Koulutusmoduuli parentKomo, String tarjoaja, String pohjakoulutusvaatimusUri);

    List<KoulutusmoduuliToteutus> findKomotosByTarjoajanKoulutusOid(String oid);

    public List<KoulutusmoduuliToteutus> findKomotosSharingCommonFields(KoulutusmoduuliToteutus komoto);

    /**
     * Find list of oid's matching.
     *
     * @param tila
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedAfter
     * @return
     */
    List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedAfter);

    /**
     * Find list of komoto oids by hakukohdeid
     *
     * @param id
     * @return
     */
    List<String> findOidsByHakukohdeId(long id);

    /**
     * Finds KOMOTO OIDs by KOMO oid.
     *
     * @param oid
     * @param count
     * @param startIndex
     * @return
     */
    List<String> findOidsByKomoOid(String oid, int count, int startIndex);

    List<String> findOidsByKomoOids(Set<String> komoOids);

    /**
     * Find an image by KOMOTO OID and language URI.
     *
     * @param komotoOid
     * @param kieliUri
     * @return
     */
    BinaryData findKuvaByKomotoOidAndKieliUri(final String komotoOid, final String kieliUri);

    Map<String, BinaryData> findAllImagesByKomotoOid(final String komotoOid);

    /**
     * Find komoto ids by hakukohdeids
     *
     * @param hakukohdeIds
     * @param requiredStatus
     * @return
     */
    List<Long> searchKomotoIdsByHakukohdesId(final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus);

    /**
     * Find komoto oids by hakukohdeids
     *
     * @param hakukohdeIds
     * @param requiredStatus
     * @return
     */
    List<String> searchKomotoOIDsByHakukohdesId(final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus);

    List<Long> findIdsByoids(Collection<String> oids);

    /**
     * Vaihtaa koulutusmoduulin toteutuksen tilan suoraa
     * poistetuksi/passivoiduksi.
     *
     * @param komotoOid
     * @param userOid
     */
    void safeDelete(final String komotoOid, final String userOid);

    void setViimIndeksointiPvmToNull(Long id);
}
