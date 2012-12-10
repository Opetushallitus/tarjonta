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
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.model.util.CollectionUtils;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class HakukohdeDAOImpl extends AbstractJpaDAOImpl<Hakukohde, Long> implements HakukohdeDAO {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<Hakukohde> findByKoulutusOid(String koulutusmoduuliToteutusOid) {

        QHakukohde hakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus toteutus = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        BooleanExpression oidEq = toteutus.oid.eq(koulutusmoduuliToteutusOid);

        return from(hakukohde).
            join(hakukohde.koulutusmoduuliToteutuses, toteutus).
            where(oidEq).
            list(hakukohde);

    }

    @Override
    public Hakukohde findHakukohdeWithKomotosByOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        Hakukohde hakukohde = from(qHakukohde,qKomoto)
                            .join(qHakukohde.koulutusmoduuliToteutuses,qKomoto)
                            .where(qHakukohde.oid.trim().eq(oid.trim()))
                            .singleResult(qHakukohde);


        return hakukohde;

    }

    @Override
    public List<Hakukohde> findHakukohdeWithDepenciesByOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QHaku qHaku = QHaku.haku;

        List<Hakukohde> hakukohdes = from(qHakukohde,qHaku)
                .join(qHakukohde.haku,qHaku)
                .where(qHakukohde.oid.eq(oid.trim()))
               .list(qHakukohde);

       for (Hakukohde hakukohde:hakukohdes) {
           hakukohde.setLisatiedot(findLisatiedotToHakuKohde(hakukohde));
           try {
           hakukohde.getHaku().getOid();
           } catch (NullPointerException nullPointer) {
                   log.info("HAKUKOHDE HAKU WAS NULL");
           }

       }



        return hakukohdes;
    }

    private MonikielinenTeksti findLisatiedotToHakuKohde(Hakukohde hakukohde) {
        QMonikielinenTeksti qTekstis = QMonikielinenTeksti.monikielinenTeksti;
        QTekstiKaannos qKaannos = QTekstiKaannos.tekstiKaannos;
        QHakukohde qHakukohde = QHakukohde.hakukohde;

        MonikielinenTeksti tekstis = from(qTekstis,qHakukohde)
                .join(qHakukohde.lisatiedot,qTekstis)
                .join(qTekstis.tekstis,qKaannos).fetch()
                .where(qHakukohde.oid.eq(hakukohde.getOid().trim()))
                .singleResult(qTekstis);
        return  tekstis;
    }

    @Override
    public List<Hakukohde> haeHakukohteetJaKoulutukset(HaeHakukohteetKyselyTyyppi kysely) {
    	String searchStr = (kysely.getNimi() != null) ? kysely.getNimi().toLowerCase() : "";
    	QHakukohde qHakukohde = QHakukohde.hakukohde;
    	BooleanExpression criteriaExpr = qHakukohde.hakukohdeKoodistoNimi.toLowerCase().contains(searchStr);

    	List<Hakukohde> hakukohdes = from(qHakukohde)
    			.where(criteriaExpr).
                list(qHakukohde);
    	
    	//Creating grouping such that there is a hakukohde object for each koulutusmoduulitoteutus
    	hakukohdes = createGrouping(hakukohdes);

    	List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
    	//If a list of organisaatio oids is provided only hakukohdes that match
    	//the list are returned
    	if (!kysely.getTarjoajaOids().isEmpty()) {
    		for (Hakukohde curHk : hakukohdes) {
    			if (kysely.getTarjoajaOids().contains(CollectionUtils.singleItem(curHk.getKoulutusmoduuliToteutuses()).getTarjoaja())) {
    				vastaus.add(curHk);
    			}
        	}
    	} else {
    		vastaus = hakukohdes;
    	}
        return vastaus;
    }
    
    /*
     * Creating grouping such that there is a hakukohde object for each koulutusmoduulitoteutus
     */
    private List<Hakukohde> createGrouping (List<Hakukohde> hakukohdes) {
    	List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
    	for (Hakukohde curHakukohde : hakukohdes) {
    		if (curHakukohde.getKoulutusmoduuliToteutuses().size() > 1) {
    			vastaus.addAll(handleKomotos(curHakukohde));
    		} else {
    			vastaus.add(curHakukohde);
    		}
    	}
    	return vastaus;
    }
    
    private List<Hakukohde> handleKomotos(Hakukohde hakukohde) {
    	List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
    	for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
    		Hakukohde newHakukohde = new Hakukohde();
    		newHakukohde.setHakukohdeNimi(hakukohde.getHakukohdeNimi());
            newHakukohde.setTila(hakukohde.getTila());
            newHakukohde.setOid(hakukohde.getOid());
            newHakukohde.addKoulutusmoduuliToteutus(komoto);
            newHakukohde.setHaku(hakukohde.getHaku());
            vastaus.add(newHakukohde);
    	}
    	return vastaus;
    }


    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

	@Override
	public List<Hakukohde> findOrphanHakukohteet() {
		QHakukohde hakukohde  = QHakukohde.hakukohde;
		BooleanExpression toteutusesEmpty = hakukohde.koulutusmoduuliToteutuses.isEmpty();
		return from(hakukohde).where(toteutusesEmpty).list(hakukohde);
	}

}

