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
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 */
public interface KoulutusmoduuliToteutusDAO extends JpaDAO<KoulutusmoduuliToteutus, Long> {

    public KoulutusmoduuliToteutus findByOid(String oid);

    public KoulutusmoduuliToteutus findKomotoByOid(String oid);

    public List<KoulutusmoduuliToteutus> findByCriteria(
            List<String> tarjoajaOids, String nimi, int koulutusAlkuVuosi, List<Integer> koulutusAlkuKuukaudet);

    public KoulutusmoduuliToteutus findKomotoWithYhteyshenkilosByOid(String oid);

    public List<KoulutusmoduuliToteutus> findKoulutusModuuliToteutusesByOids(List<String> oids);

    /**
     * Return all koulutumoduulitoteutuses in oid list with Hakukohde depencies
     *
     * @param komotoOids
     * @return List<KoulutusmoduuliToteutus>
     */
    public List<KoulutusmoduuliToteutus> findKoulutusModuulisWithHakukohdesByOids(List<String> komotoOids);

    public List<KoulutusmoduuliToteutus> findKoulutusModuuliWithPohjakoulutusAndTarjoaja(String tarjoaja, String pohjakoulutus,
            String koulutusluokitus, String koulutusohjelma,
            List<String> opetuskielis, List<String> koulutuslajis);

    public List<KoulutusmoduuliToteutus> findKomotosByKomoTarjoajaPohjakoulutus(
            Koulutusmoduuli parentKomo, String tarjoaja, String pohjakoulutusvaatimusUri);

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
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedAfter);

    /**
     * Find list of komoto oids by hakukohdeid
     *
     * @param id
     * @return
     */
    public List<String> findOidsByHakukohdeId(long id);

    /**
     * Finds KOMOTO OIDs by KOMO oid.
     *
     * @param oid
     * @param count
     * @param startIndex
     * @return
     */
    public List<String> findOidsByKomoOid(String oid, int count, int startIndex);

    /**
     * Find an image by KOMOTO OID and language URI.
     *
     * @param komotoOid
     * @param kieliUri
     * @return
     */
    public BinaryData findKuvaByKomotoOidAndKieliUri(final String komotoOid, final String kieliUri);
    
    /**
     * Find komoto ids by hakukohdeids
     * @param hakukohdeIds
     * @param requiredStatus
     * @return
     */
    List<Long> searchKomotoIdsByHakukohdesId(final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus);

    
    /**
     * Find komoto oids by hakukohdeids
     * @param hakukohdeIds
     * @param requiredStatus
     * @return
     */
    List<String> searchKomotoOIDsByHakukohdesId(final Collection<Long> hakukohdeIds, final TarjontaTila... requiredStatus);

    public List<Long> findIdsByoids(Collection<String> oids);
}
