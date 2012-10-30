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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;

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

    /**
     * Returns a list of Koulutusmoduulis that are direct children of given <code>oid</code>
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
     * Contract and model for passing search criterias to DAO. Another option would be to use an object declared in WSDL but this would
     * imply that any and all changes in WSDL are immediately visible on DAO layer and in worst case might require code changes.
     */
    public static class SearchCriteria {



		private String nimiQuery;
        private String koulutusKoodiUri;
        private String koulutusohjelmaKoodiUri;

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

        public enum GroupBy {

            ORGANISAATIORAKENNE;
        }
        
        


    }


}

