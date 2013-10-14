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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.List;

import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;

/**
 *
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 */
public interface PerustiedotView {

    public void initForm();

    public  void reloadLisatiedot(List<KielikaannosViewModel> lisatiedot);

    public HakuaikaViewModel getSelectedHakuaika();

    public void addItemsToHakuCombobox(List<HakuViewModel> haut);

    public void setSelectedHaku(HakuViewModel haku);

    public void setTunnisteKoodi(String tunnistekoodi);

    public void addNewOppiaineRow(PainotettavaOppiaineViewModel oppiaine);

    public void removeOppiaineRow(PainotettavaOppiaineViewModel painotettava);
    
    public void refreshOppiaineet();
    
    public List<KielikaannosViewModel> getLisatiedot();

    public List<KielikaannosViewModel> getValintaperusteet();

    public void reloadValintaperusteet(List<KielikaannosViewModel> valintaperusteet);
}
