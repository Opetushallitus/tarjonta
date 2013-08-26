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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.util.List;

import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus.KoulutusTulos;

/**
 *
 * @author mlyly
 */
public interface ListHakukohdeView {

    /**
     * Reloads the haku search result list.
     */
    public void reload();

    /**
     * Appends the koulutukset that are related to the hakukohde given as a
     * paremeter.
     *
     * @param hakukohde
     */
    public void showKoulutuksetForHakukohde(List<KoulutusTulos> koulutukset, HakukohdeTulos hakukohde);

    void showErrorMessage(String msg);

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems();

    public void closeDialog();
}
