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
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.QHakukohde;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QMonikielinenTeksti;
import fi.vm.sade.tarjonta.model.QTekstiKaannos;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class HakukohdeDAOImpl extends AbstractJpaDAOImpl<Hakukohde, Long> implements HakukohdeDAO {

    @Override
    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression oidEq = toteutus.oid.eq(koulutusmoduuliToteutusOid);

        return from(hakukohde).
            join(hakukohde.koulutusmoduuliToteutuseList, toteutus).
            where(oidEq).
            list(hakukohde);

    }

    @Override
    public List<Hakukohde> findHakukohdeWithDepenciesByOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QMonikielinenTeksti qMoniteksti = QMonikielinenTeksti.monikielinenTeksti;
        QTekstiKaannos qTeksti = QTekstiKaannos.tekstiKaannos;

       List<Hakukohde> hakukohdes = from(qHakukohde)
                .leftJoin(qHakukohde.lisatiedot,qMoniteksti)
                .leftJoin(qMoniteksti.tekstis,qTeksti)
                .where(qHakukohde.oid.eq(oid.trim()))
               .list(qHakukohde);
        return hakukohdes;
    }
    
    

    @Override
    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely) {
    	
    	Query query = getEntityManager().createQuery("SELECT h FROM Hakukohde h LEFT JOIN FETCH h.koulutusmoduuliToteutuseList");
    	return query.getResultList();

    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

	@Override
	public List<Hakukohde> findOrphanHakukohteet() {
		QHakukohde hakukohde  = QHakukohde.hakukohde;
		BooleanExpression toteutusesEmpty = hakukohde.koulutusmoduuliToteutuseList.isEmpty();
		return from(hakukohde).where(toteutusesEmpty).list(hakukohde);
	}

}

