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
package fi.vm.sade.tarjonta.data.test;

import fi.vm.sade.tarjonta.data.test.modules.HakukohdeGenerator;
import fi.vm.sade.tarjonta.data.test.modules.HakuGenerator;
import fi.vm.sade.tarjonta.data.test.modules.KomotoGenerator;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class DataUploader {

    private static final int MAX_KOMOTOS_PER_ORGANISATION = 5;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private TarjontaAdminService tarjotantaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjotantaPublicService;

    public DataUploader() {
    }

    public void upload() {
        HakuGenerator haku = new HakuGenerator(tarjotantaAdminService);
        final String hakukohdeOid = haku.create();

        OrganisaatioSearchOidType oid = new OrganisaatioSearchOidType();
        oid.setSearchOid("1.2.246.562.10.44513634004");

        //oid.setSearchOid("1.2.246.562.10.00000000001");
        OrganisaatioOidListType result = organisaatioService.findChildrenOidsByOid(oid);
        KomotoGenerator komoto = new KomotoGenerator(tarjotantaAdminService, tarjotantaPublicService);
        HakukohdeGenerator hakukohde = new HakukohdeGenerator(tarjotantaAdminService);

        for (OrganisaatioOidType oidType : result.getOrganisaatioOidList()) {
            for (int i = 0; i < MAX_KOMOTOS_PER_ORGANISATION; i++) {
                final String komotoOid = komoto.create(oidType.getOrganisaatioOid());
                hakukohde.create(hakukohdeOid, komotoOid);
            }
        }
    }
}
