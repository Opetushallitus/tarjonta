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

import com.google.common.base.Preconditions;
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

 
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private TarjontaAdminService tarjotantaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjotantaPublicService;

    public DataUploader() {
    }

    public void upload(final String organisationOid, final int loiItemCountPerOrganisation) {
        Preconditions.checkNotNull(organisationOid, "Organisation OID cannot be null.");
        HakuGenerator haku = new HakuGenerator(tarjotantaAdminService);
        final String hakukohdeOid = haku.create();

        OrganisaatioSearchOidType oid = new OrganisaatioSearchOidType();
        oid.setSearchOid(organisationOid);
        OrganisaatioOidListType result = organisaatioService.findChildrenOidsByOid(oid);
        KomotoGenerator komoto = new KomotoGenerator(tarjotantaAdminService, tarjotantaPublicService);
        HakukohdeGenerator hakukohde = new HakukohdeGenerator(tarjotantaAdminService);

        for (OrganisaatioOidType oidType : result.getOrganisaatioOidList()) {
            for (int i = 0; i < loiItemCountPerOrganisation; i++) {
                final String komotoOid = komoto.create(oidType.getOrganisaatioOid());

                hakukohde.create(hakukohdeOid, komotoOid);
            }
        }
    }
}
