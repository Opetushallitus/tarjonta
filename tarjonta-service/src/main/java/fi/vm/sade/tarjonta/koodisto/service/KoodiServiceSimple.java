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
package fi.vm.sade.tarjonta.koodisto.service;

import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.koodisto.model.Koodi;
import fi.vm.sade.tarjonta.koodisto.util.ConversionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jukka Raanamo
 */
public class KoodiServiceSimple implements KoodiService {

    private KoodiBusinessService koodiService;

    @Override
    public List<KoodiType> listKoodiByRelation(String koodi, boolean onAlaKoodi, SuhteenTyyppiType suhdeTyyppi) throws GenericFault {
        throw new UnsupportedOperationException("listKoodiByRelation is not supported");
    }

    @Override
    public List<KoodiType> searchKoodis(SearchKoodisCriteriaType searchCriteria) throws GenericFault {

        List<Koodi> koodis = koodiService.searchKoodis(searchCriteria);
        List<KoodiType> result = new ArrayList<KoodiType>(koodis.size());

        for (Koodi fromKoodi : koodis) {
            KoodiType toKoodi = new KoodiType();
            ConversionUtils.copy(fromKoodi, toKoodi);
            result.add(toKoodi);
        }

        return result;

    }

    @Override
    public List<KoodiType> searchKoodisByKoodisto(SearchKoodisByKoodistoCriteriaType searchCriteria) throws GenericFault {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void checkNotSupported(SearchKoodisCriteriaType criteria) throws GenericFault {

        if (criteria.getKoodiTilas() != null) {
            throwNotSupported("koodiTilas");
        }

    }

    private void throwNotSupported(String attribute) throws GenericFault {
        throw new GenericFault("search criteria not supported: " + attribute);
    }

}

