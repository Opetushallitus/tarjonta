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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QHakukohde;
import fi.vm.sade.tarjonta.model.QKoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.QKoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.QMonikielinenTeksti;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 *
 * @author Marko Lyly
 */
@Repository
public class KoulutusmoduuliDAOImpl extends AbstractJpaDAOImpl<Koulutusmoduuli, Long> implements KoulutusmoduuliDAO {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliDAO.class);

    @Autowired
    private OidService oidService;

    @Override
    public Koulutusmoduuli findByOid(String oid) {
        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression oidEq = moduuli.oid.eq(oid);

        return from(moduuli).where(oidEq).singleResult(moduuli);
    }

    @Override
    public List<Koulutusmoduuli> find(String tila, int startIndex, int pageSize) {
        return findBy(Koulutusmoduuli.TILA_COLUMN_NAME, tila, startIndex, pageSize);
    }

    public List<KoulutusmoduuliToteutus> findKomotoByHakukohde(Hakukohde hakukohde) {
        QHakukohde qHakukohde = QHakukohde.hakukohde;
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;

        return from(qHakukohde, qKomoto)
                .join(qKomoto.hakukohdes, qHakukohde)
                .where(qHakukohde.oid.eq(hakukohde.getOid()))
                .list(qKomoto);
    }

    private BooleanBuilder bb(Predicate initial) {
        return new BooleanBuilder(initial);
    }

    @Override
    public List<KoulutusmoduuliToteutus> findActiveKomotosByKomoOid(String komoOid) {
        QKoulutusmoduuliToteutus qKomoto = QKoulutusmoduuliToteutus.koulutusmoduuliToteutus;
        return from(qKomoto)
                .where(qKomoto.koulutusmoduuli.oid.eq(komoOid).and(bb(qKomoto.tila.notIn(TarjontaTila.POISTETTU))))
                .list(qKomoto);
    }

    @Override
    public List<Koulutusmoduuli> getAlamoduuliList(String oid) {

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        QKoulutusmoduuli ylamoduuli = new QKoulutusmoduuli("ylamoduuli");
        QKoulutusSisaltyvyys sisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;
        BooleanExpression oidEq = ylamoduuli.oid.eq(oid);

        return (List<Koulutusmoduuli>) from(moduuli).
                join(moduuli.sisaltyvyysList, sisaltyvyys).
                join(sisaltyvyys.ylamoduuli, ylamoduuli).
                where(oidEq).
                list(moduuli);

    }

    @Override
    public List<Koulutusmoduuli> search(SearchCriteria criteria) {

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression whereExpr = null;
        QMonikielinenTeksti nimi = QMonikielinenTeksti.monikielinenTeksti;

        // todo: are we searching for LOI or LOS - that group by e.g. per organisaatio is
        // take from different attribute
        //Expression groupBy = groupBy(criteria);
        if (criteria.getNimiQuery() != null) {
            // todo: FIX THIS
            //whereExpr = and(whereExpr, moduuli.nimi.tekstis.like("%" + criteria.getNimiQuery() + "%"));
        }

        if (criteria.getKoulutusKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.eq(criteria.getKoulutusKoodi()));
        }

        if (criteria.getKoulutusohjelmaKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaUri.eq(criteria.getKoulutusohjelmaKoodi()));
        }

        if (criteria.getLukiolinjaKoodiUri() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.lukiolinjaUri.eq(criteria.getLukiolinjaKoodiUri()));
        }

        if (criteria.getLikeKoulutusohjelmaKoodiUriWithoutVersion() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaUri.like(criteria.getLikeKoulutusohjelmaKoodiUriWithoutVersion() + "%"));
        }

        if (criteria.getLikeLukiolinjaKoodiUriUriWithoutVersion() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.lukiolinjaUri.like(criteria.getLikeLukiolinjaKoodiUriUriWithoutVersion() + "%"));
        }

        if (criteria.getLikeKoulutusKoodiUriWithoutVersion() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.like("%" + criteria.getLikeKoulutusKoodiUriWithoutVersion() + "%"));
        }

        //Like search by given uri from koulutusohjelma, osaamisala, lukiolinja
        if (criteria.getLikeOhjelmaKoodiUriWithoutVersion() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaUri.like("%" + criteria.getLikeOhjelmaKoodiUriWithoutVersion() + "%")
                    .or(moduuli.osaamisalaUri.like("%" + criteria.getLikeOhjelmaKoodiUriWithoutVersion() + "%"))
                    .or(moduuli.lukiolinjaUri.like("%" + criteria.getLikeOhjelmaKoodiUriWithoutVersion() + "%")));
        }

        if (criteria.getModuulityyppi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutustyyppiEnum.eq(criteria.getModuulityyppi()));
        }

        if (criteria.getOppilaitostyyppis() != null && !criteria.getOppilaitostyyppis().isEmpty()) {
            BooleanExpression ors = null;

            for (String oppilaitostyyppi : criteria.getOppilaitostyyppis()) {
                final BooleanExpression like = moduuli.oppilaitostyyppi.like("%|" + oppilaitostyyppi + "|%");
                ors = (ors != null) ? ors.or(like) : like;
            }
            whereExpr = QuerydslUtils.and(whereExpr, ors);
        }

        if (!criteria.getKoulutustyyppiUris().isEmpty()) {
            BooleanExpression ors = null;

            for (String koulutustyyppi : criteria.getKoulutustyyppiUris()) {
                final BooleanExpression like = moduuli.koulutustyyppiUri.like("%|" + koulutustyyppi + "|%");
                ors = (ors != null) ? ors.or(like) : like;
            }
            whereExpr = QuerydslUtils.and(whereExpr, ors);
        }

        if (criteria.getToteutustyyppiEnum() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutustyyppiUri.like("%|" + criteria.getToteutustyyppiEnum().uri() + "|%"));
        }

        if (criteria.getTila() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.tila.eq(criteria.getTila()));
        }

        if (criteria.getKoulutusmoduuliTyyppi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.moduuliTyyppi.eq(criteria.getKoulutusmoduuliTyyppi()));
        }

        if (whereExpr == null) {
            return from(moduuli).
                    leftJoin(moduuli.nimi, nimi).fetch().
                    list(moduuli);
        } else {
            return from(moduuli).
                    where(whereExpr).
                    leftJoin(moduuli.nimi, nimi).fetch().
                    list(moduuli);
        }

    }

    @Override
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri) {
        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression whereExpr = null;

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKoulutusKoodi(koulutusLuokitusUri);
        criteria.setKoulutusohjelmaKoodi(koulutusOhjelmaUri);

        if (criteria.getKoulutusKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(criteria.getKoulutusKoodi()) + "%"));
        } else {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.isNull().or(moduuli.koulutusUri.isEmpty()));
        }

        if (criteria.getKoulutusohjelmaKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(criteria.getKoulutusohjelmaKoodi()) + "%"));
        } else {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusohjelmaUri.isEmpty().or(moduuli.koulutusohjelmaUri.isNull()));
        }

        return from(moduuli).
                where(whereExpr).
                singleResult(moduuli);

    }

    @Override
    public List<Koulutusmoduuli> findAllKomos() {
        /*QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
         QMonikielinenTeksti kr = new QMonikielinenTeksti("koulutuksenrakenne");
         QMonikielinenTeksti jatko = new QMonikielinenTeksti("jatkoopintomahdollisuudet");
         QMonikielinenTeksti t = new QMonikielinenTeksti("tavoitteet");
         return from(moduuli).
         leftJoin(moduuli.koulutuksenRakenne, kr).fetch().leftJoin(kr.tekstis).fetch().
         leftJoin(moduuli.jatkoOpintoMahdollisuudet, jatko).fetch().leftJoin(jatko.tekstis).fetch().
         leftJoin(moduuli.tavoitteet, t).fetch().leftJoin(t.tekstis).fetch().
         list(moduuli);*/
        return findAll();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public Koulutusmoduuli findParentKomo(Koulutusmoduuli komo) {
        QKoulutusSisaltyvyys sisaltyvyys = QKoulutusSisaltyvyys.koulutusSisaltyvyys;

        List<KoulutusSisaltyvyys> parents = from(sisaltyvyys).
                join(sisaltyvyys.alamoduuliList).fetch().
                where(sisaltyvyys.alamoduuliList.contains(komo)).
                list(sisaltyvyys);

        if (parents == null || parents.isEmpty()) {
            return null;
        }

        String oid = parents.get(0).getYlamoduuli().getOid();
        return findByOid(oid);
    }

    @Override
    public Koulutusmoduuli findLukiolinja(String koulutusLuokitusUri, String lukiolinjaUriUri) {
        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;
        BooleanExpression whereExpr = null;

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKoulutusKoodi(koulutusLuokitusUri);
        criteria.setLukiolinjaKoodiUri(lukiolinjaUriUri);

        if (criteria.getKoulutusKoodi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(criteria.getKoulutusKoodi()) + "%"));
        } else {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.koulutusUri.isNull().or(moduuli.koulutusUri.isEmpty()));
        }

        if (criteria.getLukiolinjaKoodiUri() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.lukiolinjaUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(criteria.getLukiolinjaKoodiUri()) + "%"));
        } else {
            whereExpr = QuerydslUtils.and(whereExpr, moduuli.lukiolinjaUri.isEmpty().or(moduuli.lukiolinjaUri.isNull()));
        }

        whereExpr.and(moduuli.koulutustyyppiEnum.eq(ModuulityyppiEnum.LUKIOKOULUTUS));

        return from(moduuli).
                where(whereExpr).
                singleResult(moduuli);
    }

    @Override
    public List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedAfter) {

        QKoulutusmoduuli komo = QKoulutusmoduuli.koulutusmoduuli;

        BooleanExpression whereExpr = null;

        if (tila != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komo.tila.eq(tila));
        }
        if (lastModifiedBefore != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komo.updated.before(lastModifiedBefore));
        }
        if (lastModifiedAfter != null) {
            whereExpr = QuerydslUtils.and(whereExpr, komo.updated.after(lastModifiedAfter));
        }
        List<ModuulityyppiEnum> baseKoulutustyyppi = new ArrayList<ModuulityyppiEnum>();

        baseKoulutustyyppi.add(ModuulityyppiEnum.KORKEAKOULUTUS);

        whereExpr = QuerydslUtils.and(whereExpr, komo.koulutustyyppiEnum.notIn(baseKoulutustyyppi));

        JPAQuery q = from(komo);
        if (whereExpr != null) {
            q = q.where(whereExpr);
        }

        if (count > 0) {
            q = q.limit(count);
        }

        if (startIndex > 0) {
            q.offset(startIndex);
        }

        return q.list(komo.oid);
    }

    @Override
    public Koulutusmoduuli createKomoKorkeakoulu(KoulutusmoduuliKoosteTyyppi tyyppi) {
        Preconditions.checkNotNull(tyyppi, "KoulutusmoduuliKoosteTyyppi object cannot be null!");
        Koulutusmoduuli komo = EntityUtils.copyFieldsToKoulutusmoduuli(tyyppi);
        try {
            komo.setOid(oidService.get(TarjontaOidType.KOMO));
        } catch (OIDCreationException ex) {
            throw new TarjontaBusinessException("OID service unavailable.", ex);
        }

        return komo;
    }

    @Override
    public Koulutusmoduuli findKoulutus(String koulutusLuokitusUri) {
        return findTutkintoOhjelma(koulutusLuokitusUri, null);
    }

    @Override
    public void safeDelete(final String komoOid, final String userOid) {
        Preconditions.checkNotNull(komoOid, "Komo OID string object cannot be null.");
        List<String> oids = Lists.<String>newArrayList();
        oids.add(komoOid);
        Koulutusmoduuli findByOid = findByOid(komoOid);
        Preconditions.checkArgument(findByOid != null, "Delete failed, entity not found.");
        findByOid.setTila(TarjontaTila.POISTETTU);
        //TODO: add field for the entity 
        //findByOid.setLastUpdatedByOid(userOid);
    }

    /**
     * 24.06.2014, a method for searching 'tutkinto' or 'tutkinto-ohjelma'
     * modules. All version information from query parameters will be removed.
     *
     * @param tyyppi
     * @param koulutusUri
     * @param likeKoulutusohjelmaUri
     * @param likeOsaamisalaUri
     * @param likeLukiolinjaUri
     * @return
     */
    @Override
    public Koulutusmoduuli findModule(final KoulutusmoduuliTyyppi tyyppi, final String koulutusUri,
            final String likeKoulutusohjelmaUri,
            final String likeOsaamisalaUri,
            final String likeLukiolinjaUri) {
        Preconditions.checkNotNull(tyyppi, "KoulutusmoduuliTyyppi cannot be null!");
        Preconditions.checkNotNull(koulutusUri, "Koulutus uri cannot be null!");

        QKoulutusmoduuli moduuli = QKoulutusmoduuli.koulutusmoduuli;

        BooleanExpression like = null;

        if (likeKoulutusohjelmaUri != null) {
            like = moduuli.koulutusohjelmaUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(likeKoulutusohjelmaUri) + "#%");
        }

        if (likeOsaamisalaUri != null) {
            like = QuerydslUtils.or(like, moduuli.osaamisalaUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(likeOsaamisalaUri) + "#%"));
        }

        if (likeLukiolinjaUri != null) {
            like = moduuli.lukiolinjaUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(likeLukiolinjaUri) + "#%");
        }

        SearchResults<Koulutusmoduuli> modules = from(moduuli).where(
                moduuli.moduuliTyyppi.eq(tyyppi)
                .and(moduuli.koulutusUri.like(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(koulutusUri) + "#%"))
                .and(like)).listResults(moduuli);

        //result of the query should aways be unique, or there is somekind of data error. 
        if (modules.getTotal() > 1) {
            for (Koulutusmoduuli m : modules.getResults()) {
                log.error("Error in module oid : ", m.getOid());
            }

            throw new RuntimeException("Possible data error - result contains too many modules, koulutus uri : '" + koulutusUri + "'." + " ohjelma : '" + likeKoulutusohjelmaUri + "|" + likeOsaamisalaUri + "|" + likeLukiolinjaUri + "'");
        }

        return !modules.isEmpty() ? modules.getResults().get(0) : null;
    }
}
