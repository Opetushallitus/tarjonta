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
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.Date;
import java.util.List;

/**
 * DAO interface to retrieve Application Options (Hakukohde's).
 */
public interface HakukohdeDAO extends JpaDAO<Hakukohde, Long> {

    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid);

//    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely);

    public List<Hakukohde> findOrphanHakukohteet();

    HakukohdeLiite findHakuKohdeLiiteById(String id);

    List<HakukohdeLiite> findHakukohdeLiitesByHakukohdeOid(String oid);

    Valintakoe findValintaKoeById(String id);

    List<Valintakoe> findValintakoeByHakukohdeOid(String oid);

    Hakukohde findHakukohdeByOid(String oid);

    /**
     * @deprecated {@link #findHakukohdeByOid(String)}
     */
    Hakukohde findHakukohdeWithDepenciesByOid(String oid);

    /**
     * @deprecated {@link #findHakukohdeByOid(String)}
     */
    Hakukohde findHakukohdeWithKomotosByOid(String oid);

    void removeValintakoe(Valintakoe valintakoe);

    void updateValintakoe(List<Valintakoe> valintakoes, String hakukohdeOid);

    void updateLiittees(List<HakukohdeLiite> liites, String hakukohdeOid);

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
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince);

    /**
     * Hae hakukohteet jotka liittyvät komotoon
     * @param id komoto id (ei oid!)
     * @return
     */
    List<String> findOidsByKoulutusId(long id);

    /**
     *
     * @param hakuOid
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return
     */
    public List<String> findByHakuOid(String hakuOid, String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince);

}

