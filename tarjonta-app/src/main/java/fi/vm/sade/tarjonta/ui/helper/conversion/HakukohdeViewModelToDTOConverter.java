/*
 *
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

package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.MonikielinenTekstiTyyppi;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tuomas Katva
 */
@Component
public class HakukohdeViewModelToDTOConverter {

    @Autowired(required=true)
    private OIDService oidService;
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HakukohdeViewModelToDTOConverter.class);
    
    public HakukohdeTyyppi convertHakukohdeViewModelToDTO(HakukohdeViewModel hakukohdevm) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        
        hakukohde.setAloituspaikat(hakukohdevm.getAloitusPaikat());
        hakukohde.setHakukelpoisuusVaatimukset(hakukohdevm.getHakukelpoisuusVaatimus());
        if (hakukohdevm.getHakukohdeNimi() != null) {
        hakukohde.setHakukohdeNimi(hakukohdevm.getHakukohdeNimi());
        } else {
            //TODO remove just for testing because koodisto dont work
            hakukohde.setHakukohdeNimi("");
        }
        hakukohde.setHakukohteenHakuOid(hakukohdevm.getHakuOid().getOid());
        hakukohde.setHakukohteenTila(hakukohdevm.getHakukohdeTila());
        if (hakukohdevm.getOid() == null) {
            try {
                hakukohde.setOid(oidService.newOid(NodeClassCode.PALVELUT));
            } catch (ExceptionMessage ex) {
                LOG.warn("UNABLE TO GET OID : " + ex.toString());
            }
        } else {
        hakukohde.setOid(hakukohdevm.getOid());
        }
        
        hakukohde.getHakukohteenKoulutusOidit().addAll(hakukohdevm.getKomotoOids());
        hakukohde.getLisatiedot().addAll(convertTekstis(hakukohdevm.getLisatiedot()));
        hakukohde.getValintaPerusteidenKuvaukset().addAll(convertTekstis(hakukohdevm.getValintaPerusteidenKuvaus()));
        return hakukohde;
    }
    
    private List<MonikielinenTekstiTyyppi> convertTekstis(List<KielikaannosViewModel> kaannokset ) {
        List<MonikielinenTekstiTyyppi> tekstis = new ArrayList<MonikielinenTekstiTyyppi>();
        
        for (KielikaannosViewModel kieli:kaannokset) {
            MonikielinenTekstiTyyppi teksti = new MonikielinenTekstiTyyppi();
            teksti.setTekstinKielikoodi(kieli.getKielikoodi());
            teksti.setTeksti(kieli.getNimi());
            tekstis.add(teksti);
        }
        
        return tekstis;
    }

    /**
     * @return the oidService
     */
    public OIDService getOidService() {
        return oidService;
    }

    /**
     * @param oidService the oidService to set
     */
    public void setOidService(OIDService oidService) {
        this.oidService = oidService;
    }
}
