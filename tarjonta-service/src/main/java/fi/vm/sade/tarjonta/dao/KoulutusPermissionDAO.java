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
import fi.vm.sade.tarjonta.model.KoulutusPermission;

import java.util.Date;
import java.util.List;

public interface KoulutusPermissionDAO extends JpaDAO<KoulutusPermission, Long>  {

    List<KoulutusPermission> getAll();

    List<KoulutusPermission> find(List<String> orgOids, String koodisto, String koodiUri, Date alkuPvm, Date loppuPvm);

    List<KoulutusPermission> find(List<String> orgOids, String koodisto, String koodiUri);

    Long removeAll();

}
