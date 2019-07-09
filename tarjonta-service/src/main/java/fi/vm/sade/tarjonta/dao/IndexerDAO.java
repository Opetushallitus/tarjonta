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

import fi.vm.sade.tarjonta.model.Haku;

import java.util.Date;
import java.util.List;

public interface IndexerDAO {

    List<Long> findAllHakukohdeIds();

    List<Long> findAllKoulutusIds();

    List<Long> findUnindexedHakukohdeIds();

    List<Long> findUnindexedKoulutusIds();

    Long setKoulutusViimindeksointiPvmToNull();

    Long setHakukohdeViimindeksointiPvmToNull(Haku haku);

    Long setHakukohdeViimindeksointiPvmToNull();

    void updateHakukohteesIndexed(List<Long> ids, Date time);

    void updateKoulutuksesIndexed(List<Long> ids, Date time);

    void updateHakukohdeIndexed(Long id, Date time);

    void updateKoulutusIndexed(Long id, Date time);

    void reindexOrganizationChanges(List<String> organizationOids);

}
