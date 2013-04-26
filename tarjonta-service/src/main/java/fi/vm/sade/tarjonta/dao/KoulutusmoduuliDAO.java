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
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

/**
 */
public interface KoulutusmoduuliDAO extends JpaDAO<Koulutusmoduuli, Long> {

    /**
     *
     * @param tila
     * @param startIndex
     * @param pageSize
     * @return
     */
    public List<Koulutusmoduuli> find(String tila, int startIndex, int pageSize);

    /*
     * Returns all koulutusmoduulitoteutukses for given hakukohde
     *
     * @param hakukohde
     * @return List<KoulutusmoduuliToteutus>
     */
    List<KoulutusmoduuliToteutus> findKomotoByHakukohde(Hakukohde hakukohde);

    /**
     * Returns a list of Koulutusmoduulis that are direct children of given
     * <code>oid</code>
     *
     * @param <T>
     * @param type
     * @param oid
     * @return
     */
    public List<Koulutusmoduuli> getAlamoduuliList(String oid);

    /**
     * Typed version of read to save from casting.
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    public Koulutusmoduuli findByOid(String id);

    /**
     * Return all LOO objects that match given criteria.
     *
     * @param <T>
     * @param type
     * @param criteria
     * @return
     */
    public List<Koulutusmoduuli> search(SearchCriteria criteria);

    /**
     *
     * @param koulutusLuokitusUri
     * @param koulutusOhjelmaUri
     * @return
     */
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri);

    /**
     * Get full KOMO objects with language text data.
     *
     * @return
     */
    public List<Koulutusmoduuli> findAllKomos();


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
     * Contract and model for passing search criterias to DAO. Another option
     * would be to use an object declared in WSDL but this would imply that any
     * and all changes in WSDL are immediately visible on DAO layer and in worst
     * case might require code changes.
     */
    public static class SearchCriteria {

        private String nimiQuery;
        private String koulutusKoodiUri;
        private String koulutusohjelmaKoodiUri;
        private String lukiolinjaKoodiUri;
        private List<String> oppilaitostyyppis;
        private KoulutusasteTyyppi koulutustyyppi;
        private Class<? extends BaseKoulutusmoduuli> type;
        private GroupBy groupBy = GroupBy.ORGANISAATIORAKENNE;

        public void setNimiQuery(String nimiQuery) {
            this.nimiQuery = nimiQuery;
        }

        public String getNimiQuery() {
            return nimiQuery;
        }

        public void setGroupBy(GroupBy groupBy) {
            this.groupBy = groupBy;
        }

        public GroupBy getGroupBy() {
            return groupBy;
        }

        public void setType(Class<? extends BaseKoulutusmoduuli> type) {
            this.type = type;
        }

        public Class<? extends BaseKoulutusmoduuli> getType() {
            return type;
        }

        public String getKoulutusKoodi() {
            return koulutusKoodiUri;
        }

        public void setKoulutusKoodi(String koulutusKoodi) {
            this.koulutusKoodiUri = koulutusKoodi;
        }

        public String getKoulutusohjelmaKoodi() {
            return koulutusohjelmaKoodiUri;
        }

        public void setKoulutusohjelmaKoodi(String koulutusohjelmaKoodi) {
            this.koulutusohjelmaKoodiUri = koulutusohjelmaKoodi;
        }

        /**
         * @return the koulutustyyppi
         */
        public KoulutusasteTyyppi getKoulutustyyppi() {
            return koulutustyyppi;
        }

        /**
         * @param koulutustyyppi the koulutustyyppi to set
         */
        public void setKoulutustyyppi(KoulutusasteTyyppi koulutustyyppi) {
            this.koulutustyyppi = koulutustyyppi;
        }

        /**
         * @return the lukiolinjaKoodiUri
         */
        public String getLukiolinjaKoodiUri() {
            return lukiolinjaKoodiUri;
        }

        /**
         * @param lukiolinjaKoodiUri the lukiolinjaKoodiUri to set
         */
        public void setLukiolinjaKoodiUri(String lukiolinjaKoodiUri) {
            this.lukiolinjaKoodiUri = lukiolinjaKoodiUri;
        }

        /**
         * @return the oppilaitostyyppis
         */
        public List<String> getOppilaitostyyppis() {
            if (oppilaitostyyppis == null) {
                oppilaitostyyppis = new ArrayList<String>();
            }
            return oppilaitostyyppis;
        }

        /**
         * @param oppilaitostyyppis the oppilaitostyyppis to set
         */
        public void setOppilaitostyyppis(List<String> oppilaitostyyppis) {
            this.oppilaitostyyppis = oppilaitostyyppis;
        }

        public enum GroupBy {

            ORGANISAATIORAKENNE;
        }
    }

    /**
     * Returns the parent of the koulutusmoduuli given as parameter
     *
     * @param komo - the koulutusmoduuli the parent of which we are searching.
     * @return - the parent koulutusmoduuli.
     */
    public Koulutusmoduuli findParentKomo(Koulutusmoduuli komo);

    public Koulutusmoduuli findLukiolinja(String uri, String uri2);
}
