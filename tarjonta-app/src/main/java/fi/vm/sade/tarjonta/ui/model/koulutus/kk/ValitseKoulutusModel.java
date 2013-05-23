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
package fi.vm.sade.tarjonta.ui.model.koulutus.kk;

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class ValitseKoulutusModel extends BaseUIViewModel {

    private static final long serialVersionUID = 5947767945761972546L;
    private String searchWord;
    private String koulutusala;
    private Set<KoulutuskoodiRowModel> searchResultRows;

    public ValitseKoulutusModel() {
        clear();
    }

    public void clear() {
        setSearchWord("");
        searchResultRows = Sets.<KoulutuskoodiRowModel>newHashSet();
    }

    /**
     * @return the searchResultRows
     */
    public Set<KoulutuskoodiRowModel> getSearchResultRows() {
        return searchResultRows;
    }

    /**
     * @param searchResultRows the searchResultRows to set
     */
    public void setSearchResultRows(Set<KoulutuskoodiRowModel> searchResultRows) {
        this.searchResultRows = searchResultRows;
    }

    /**
     * @return the koulutusala
     */
    public String getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(String koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the searchWord
     */
    public String getSearchWord() {
        return searchWord;
    }

    /**
     * @param searchWord the searchWord to set
     */
    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }
}
