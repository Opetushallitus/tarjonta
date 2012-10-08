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
import fi.vm.sade.tarjonta.model.LearningOpportunityObject;
import java.util.List;

/**
 */
public interface KoulutusDAO extends JpaDAO<LearningOpportunityObject, Long> {

    /**
     *
     * @param tila
     * @param startIndex
     * @param pageSize
     * @return
     */
    public List<LearningOpportunityObject> find(String tila, int startIndex, int pageSize);

    /**
     * Returns all existing versions of a LearningOpportunityObject.
     *
     * @param <T> the type of the
     * @param type
     * @param oid
     * @return
     */
    public List<? extends LearningOpportunityObject> findAllVersions(String oid);

    /**
     * Returns a list of Koulutus -objects of type <code>type</code>.
     *
     * @param <T> type of
     * @param type
     * @return
     */
    public <T extends LearningOpportunityObject> List<T> findAll(Class<T> type);

    /**
     * Returns a list of Koulutusmoduulis that are direct children of given <code>oid</code>
     *
     * @param <T>
     * @param type
     * @param oid
     * @return
     */
    public <T extends LearningOpportunityObject> List<T> findAllChildren(Class<T> type, String oid);

    /**
     * Typed version of read to save from casting.
     *
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    public <T extends LearningOpportunityObject> T findByOid(Class<T> type, String id);

    /**
     * Return all LOO objects that match given criteria.
     *
     * @param <T>
     * @param type
     * @param criteria
     * @return
     */
    public <T extends LearningOpportunityObject> List<T> search(SearchCriteria criteria);

    /**
     * Contract and model for passing search criterias to DAO. Another option would be to use an object declared in WSDL but this would
     * imply that any and all changes in WSDL are immediately visible on DAO layer and in worst case might require code changes.
     */
    public static class SearchCriteria {

        private String nimiQuery;

        private Class<? extends LearningOpportunityObject> type;

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

        public void setType(Class<? extends LearningOpportunityObject> type) {
            this.type = type;
        }

        public Class<? extends LearningOpportunityObject> getType() {
            return type;
        }

        public enum GroupBy {

            ORGANISAATIORAKENNE;
        }


    }


}

