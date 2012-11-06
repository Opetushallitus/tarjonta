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
package fi.vm.sade.tarjonta.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QYhteyshenkilo;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;

import java.util.List;
import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class YhteyshenkiloDAOImpl extends AbstractJpaDAOImpl<Yhteyshenkilo, Long> implements YhteyshenkiloDAO {

    @Override
    public Yhteyshenkilo findByOid(String oid) {

        List<Yhteyshenkilo> list = findBy(Yhteyshenkilo.ID_COLUMN_NAME, oid);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new IllegalStateException("multiple results for oid: " + oid);
        }
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
}
