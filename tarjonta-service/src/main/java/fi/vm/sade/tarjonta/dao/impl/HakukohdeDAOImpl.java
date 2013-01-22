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
import java.util.Calendar;
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
    public List<Valintakoe> findValintakoeByHakukohdeOid(String oid) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QValintakoe qValintakoe = QValintakoe.valintakoe;
        return from(qHakukohde,qValintakoe)
                .join(qHakukohde.valintakoes,qValintakoe)
                .where(qHakukohde.oid.eq(oid))
                .list(qValintakoe);
    }

    @Override
    public HakukohdeLiite findHakuKohdeLiiteById(String id) {
        QHakukohdeLiite liite = QHakukohdeLiite.hakukohdeLiite;
        Long idLong = new Long(id);
        return  from(liite).where(liite.id.eq(idLong)).singleResult(liite);
    }

    @Override
    public Valintakoe findValintaKoeById(String id) {
        QValintakoe qValintakoe = QValintakoe.valintakoe;
        Long idLong = new Long(id);
        return from(qValintakoe).where(qValintakoe.id.eq(idLong)).singleResult(qValintakoe);

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
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        List<Hakukohde> hakukohdes = from(qHakukohde,qHaku,qKomoto)
                .join(qHakukohde.haku,qHaku)
                .leftJoin(qHakukohde.koulutusmoduuliToteutuses,qKomoto)
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
    	hakukohdes = createGrouping(hakukohdes, kysely);

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
    private List<Hakukohde> createGrouping (List<Hakukohde> hakukohdes, HaeHakukohteetKyselyTyyppi kysely) {
    	List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
    	for (Hakukohde curHakukohde : hakukohdes) {
    	    List<String> tarjoajat = new ArrayList<String>();
    		if (curHakukohde.getKoulutusmoduuliToteutuses().size() > 1) {
    			vastaus.addAll(handleKomotos(curHakukohde, kysely, tarjoajat));
    		} else if (isHakukohdeMatch(curHakukohde, kysely, tarjoajat)) {
    			vastaus.add(curHakukohde);
    			tarjoajat.add(curHakukohde.getKoulutusmoduuliToteutuses().iterator().next().getTarjoaja());
    		}
    	}
    	return vastaus;
    }
    
    private List<Hakukohde> handleKomotos(Hakukohde hakukohde, HaeHakukohteetKyselyTyyppi kysely, List<String> tarjoajat) {
    	List<Hakukohde> vastaus = new ArrayList<Hakukohde>();
    	for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
    	    if (isKomotoMatch(komoto, kysely) && !tarjoajat.contains(komoto.getTarjoaja())) {
    	        Hakukohde newHakukohde = new Hakukohde();
    	        newHakukohde.setHakukohdeNimi(hakukohde.getHakukohdeNimi());
    	        newHakukohde.setTila(hakukohde.getTila());
    	        newHakukohde.setOid(hakukohde.getOid());
    	        newHakukohde.addKoulutusmoduuliToteutus(komoto);
    	        newHakukohde.setHaku(hakukohde.getHaku());
    	        vastaus.add(newHakukohde);
    	        tarjoajat.add(komoto.getTarjoaja());
    	    }
    	}
    	return vastaus;
    }
    
    private boolean isHakukohdeMatch(Hakukohde hakukohde, HaeHakukohteetKyselyTyyppi kysely, List<String> tarjoajat) {
        KoulutusmoduuliToteutus komoto = !hakukohde.getKoulutusmoduuliToteutuses().isEmpty() ? hakukohde.getKoulutusmoduuliToteutuses().iterator().next() : null;
        return isKomotoMatch(komoto, kysely) && !tarjoajat.contains(komoto.getTarjoaja());
    }

    private boolean isKomotoMatch(KoulutusmoduuliToteutus komoto, HaeHakukohteetKyselyTyyppi kysely) {  
        if (komoto == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(komoto.getKoulutuksenAlkamisPvm());
        return  isYearMatch(cal, kysely) && isKausiMatch(cal, kysely);
    }
    
    private boolean isYearMatch(Calendar cal, HaeHakukohteetKyselyTyyppi kysely) {
        if (kysely.getKoulutuksenAlkamisvuosi() == null || kysely.getKoulutuksenAlkamisvuosi() <= 0) {
            return true;
        }
        return cal.get(Calendar.YEAR) == kysely.getKoulutuksenAlkamisvuosi().intValue();
    }
    
    private boolean isKausiMatch(Calendar cal, HaeHakukohteetKyselyTyyppi kysely) {
        if (kysely.getKoulutuksenAlkamiskausi() == null || kysely.getKoulutuksenAlkamiskausi().isEmpty()) {
            return true;
        }
        if (kysely.getKoulutuksenAlkamiskausi().contains("uri: Syksy")) {
            return cal.get(Calendar.MONTH) >= 6;
        }
        return cal.get(Calendar.MONTH) < 6;
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

