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
package fi.vm.sade.tarjonta.service.search;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ORGANISAATIORYHMAOID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;
import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

@Component
public class KoulutusToSolrDocument implements Function<Long, List<SolrInputDocument>> {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Autowired
    private TarjontaKoodistoHelper koodistoHelper;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private OppilaitostyyppiResolver oppilaitostyyppiResolver;

    @Override
    public List<SolrInputDocument> apply(final Long koulutusId) {
        KoulutusmoduuliToteutus koulutusmoduuliToteutus = koulutusmoduuliToteutusDAO.findBy("id", koulutusId).get(0);

        final List<OrganisaatioPerustieto> orgs = getTarjoajat(koulutusmoduuliToteutus);

        if (orgs.isEmpty()) {
            logger.warn("No org found for komoto: " + koulutusmoduuliToteutus.getOid() + " skipping indexing!");
            return Lists.newArrayList();
        }

        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        final SolrInputDocument komotoDoc = new SolrInputDocument();

        // Varmista, että se organisaatio joka luo koulutuksen indeksoidaan ensimmäisenä
        String firstOwner = koulutusmoduuliToteutus.getTarjoaja();
        addFirstOwner(komotoDoc, firstOwner);

        List<OrganisaatioPerustieto> organisaatiotiedot = organisaatioSearchService.findByOidSet(koulutusmoduuliToteutus.getOwnerOids());

        addOid(komotoDoc, koulutusmoduuliToteutus);
        addTyypit(komotoDoc, koulutusmoduuliToteutus);
        addOrganisaatiotiedot(komotoDoc, orgs, firstOwner);
        addTyyppiDependentData(komotoDoc, koulutusmoduuliToteutus);
        addKoulutuskoodiTiedot(komotoDoc, koulutusmoduuliToteutus);
        addVuosikoodi(komotoDoc, koulutusmoduuliToteutus);
        addTila(komotoDoc, koulutusmoduuliToteutus);
        addKomoOid(komotoDoc, koulutusmoduuliToteutus);
        addKoulutusastetyyppi(komotoDoc, koulutusmoduuliToteutus);
        addPohjakoulutusvaatimukset(komotoDoc, koulutusmoduuliToteutus);
        addKoulutuslajit(komotoDoc, koulutusmoduuliToteutus.getKoulutuslajiKoodiUris());
        addHakukohdeOids(komotoDoc, koulutusmoduuliToteutus);
        addHakuOids(komotoDoc, koulutusmoduuliToteutus);
        addHakukohderyhmat(komotoDoc, koulutusmoduuliToteutus);
        addKoulutusAlkamisPvm(komotoDoc, koulutusmoduuliToteutus);
        addOppilaitostyypit(komotoDoc, organisaatiotiedot);
        addKunnat(komotoDoc, organisaatiotiedot);
        addOpetuskielet(komotoDoc, koulutusmoduuliToteutus);
        addKoulutusmoduuliTyyppi(komotoDoc, koulutusmoduuliToteutus);
        addDataFromHakukohde(komotoDoc, koulutusmoduuliToteutus);
        addTekstihaku(komotoDoc);
        addKoulutusKoodis(komotoDoc, koulutusmoduuliToteutus);
        addParentKomoOid(komotoDoc, koulutusmoduuliToteutus);
        addSiblingKomotos(komotoDoc, koulutusmoduuliToteutus);

        if (koulutusmoduuliToteutus.getToteutustyyppi().equals(ToteutustyyppiEnum.KORKEAKOULUOPINTO)
                && !koulutusmoduuliToteutus.getJarjestajaOids().isEmpty()) {
            addJarjestajatiedot(komotoDoc, getJarjestajat(koulutusmoduuliToteutus));
        }

        docs.add(komotoDoc);

        return docs;
    }

    private void addHakukohderyhmat(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus komoto) {
        for (Hakukohde hakukohde : komoto.getHakukohdes()) {
            for (Ryhmaliitos ryhmaliitos : hakukohde.getRyhmaliitokset()) {
                komotoDoc.addField(ORGANISAATIORYHMAOID, ryhmaliitos.getRyhmaOid());
            }
        }
    }

    private void addKoulutusmoduuliTyyppi(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        KoulutusmoduuliTyyppi moduuliTyyppi = koulutusmoduuliToteutus.getKoulutusmoduuli().getModuuliTyyppi();
        if (moduuliTyyppi != null) {
            add(komotoDoc, KOULUTUSMODUULITYYPPI_ENUM, moduuliTyyppi);
        }
    }

    private void addOpetuskielet(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
            add(komotoDoc, OPETUSKIELI_URIS, getKoodiURIFromVersionedUri(koodistoUri.getKoodiUri()));
        }
    }

    private void addKunnat(SolrInputDocument komotoDoc, List<OrganisaatioPerustieto> organisaatiotiedot) {
        Set<String> kuntas = new HashSet<String>();

        for (OrganisaatioPerustieto organisaatioPerustieto : organisaatiotiedot) {
            kuntas.add(getKoodiURIFromVersionedUri(organisaatioPerustieto.getKotipaikkaUri()));
        }

        for (String kunta : kuntas) {
            add(komotoDoc, KUNTA_URIS, kunta);
        }
    }

    private void addOppilaitostyypit(SolrInputDocument komotoDoc, List<OrganisaatioPerustieto> organisaatiotiedot) {
        Set<String> oppilaitostyypit = new HashSet<String>();

        for (OrganisaatioPerustieto organisaatioPerustieto : organisaatiotiedot) {
            String oppilaitostyyppi = oppilaitostyyppiResolver.resolve(organisaatioPerustieto);
            if (oppilaitostyyppi != null) {
                oppilaitostyypit.add(oppilaitostyyppi);
            }
        }

        for (String oppilaitostyyppi : oppilaitostyypit) {
            add(komotoDoc, OPPILAITOSTYYPPI_URIS, oppilaitostyyppi);
        }
    }

    private void addDataFromHakukohde(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        addHakutavat(komotoDoc, koulutusmoduuliToteutus);
        addHakutyypit(komotoDoc, koulutusmoduuliToteutus);
        addKohdejoukot(komotoDoc, koulutusmoduuliToteutus);
    }

    private void addKohdejoukot(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        Set<String> kohdejoukkos = new HashSet<String>();
        for (Hakukohde hakukohde : koulutusmoduuliToteutus.getHakukohdes()) {
            kohdejoukkos.add(getKoodiURIFromVersionedUri(hakukohde.getHaku().getKohdejoukkoUri()));
        }

        for (String kohdejoukko : kohdejoukkos) {
            add(komotoDoc, KOHDEJOUKKO_URIS, kohdejoukko);
        }
    }

    private void addHakutyypit(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        Set<String> hakutyyppis = new HashSet<String>();
        for (Hakukohde hakukohde : koulutusmoduuliToteutus.getHakukohdes()) {
            hakutyyppis.add(getKoodiURIFromVersionedUri(hakukohde.getHaku().getHakutyyppiUri()));
        }

        for (String hakutyyppi : hakutyyppis) {
            add(komotoDoc, HAKUTYYPPI_URIS, hakutyyppi);
        }
    }

    private void addHakutavat(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        Set<String> hakutapas = new HashSet<String>();
        for (Hakukohde hakukohde : koulutusmoduuliToteutus.getHakukohdes()) {
            hakutapas.add(getKoodiURIFromVersionedUri(hakukohde.getHaku().getHakutapaUri()));
        }

        for (String hakutapa : hakutapas) {
            add(komotoDoc, HAKUTAPA_URIS, hakutapa);
        }
    }

    private void addPohjakoulutusvaatimukset(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        IndexDataUtils.addKoodiLyhytnimiTiedot(komotoDoc,
                koulutusmoduuliToteutus.getPohjakoulutusvaatimusUri(),
                koodistoHelper,
                POHJAKOULUTUSVAATIMUS_URI,
                POHJAKOULUTUSVAATIMUS_FI,
                POHJAKOULUTUSVAATIMUS_SV,
                POHJAKOULUTUSVAATIMUS_EN);
    }

    private void addKoulutusastetyyppi(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, KOULUTUSASTETYYPPI_ENUM, koulutusmoduuliToteutus.getKoulutusmoduuli().getKoulutustyyppiEnum().getKoulutusasteTyyppi().value());
    }

    private void addKomoOid(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, KOULUTUSMODUULI_OID, koulutusmoduuliToteutus.getKoulutusmoduuli().getOid());
    }

    private void addParentKomoOid(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus komoto) {
        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komoto.getKoulutusmoduuli());

        if (parentKomo != null) {
            add(komotoDoc, PARENT_KOULUTUSMODUULI_OID, parentKomo.getOid());
        }
    }

    private void addSiblingKomotos(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus komoto) {
        List<KoulutusmoduuliToteutus> siblingKomotos = koulutusmoduuliToteutusDAO.findSiblingKomotos(komoto);

        if (siblingKomotos != null) {
            for (KoulutusmoduuliToteutus siblingKomoto : siblingKomotos) {
                add(komotoDoc, SIBLING_KOMOTOS, siblingKomoto.getOid());
            }
        }
    }

    private void addTila(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, TILA, koulutusmoduuliToteutus.getTila());
    }

    private void addVuosikoodi(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        Date alkamisPvm = koulutusmoduuliToteutus.getMinAlkamisPvm();

        if (alkamisPvm == null) {
            IndexDataUtils.addKausikoodiTiedot(komotoDoc, koulutusmoduuliToteutus.getAlkamiskausiUri(), koodistoHelper);
            add(komotoDoc, VUOSI_KOODI, koulutusmoduuliToteutus.getAlkamisVuosi());
        } else {
            IndexDataUtils.addKausikoodiTiedot(komotoDoc, IndexDataUtils.parseKausiKoodi(alkamisPvm), koodistoHelper);
            add(komotoDoc, VUOSI_KOODI, IndexDataUtils.parseYear(alkamisPvm));
        }
    }

    private void addTarjoajanKoulutus(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, KOULUTUKSEN_TARJOAJA_KOMOTO, koulutusmoduuliToteutus.getTarjoajanKoulutus().getOid());
    }

    private void addTyyppiDependentData(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        if (koulutusmoduuliToteutus.getToteutustyyppi() != null) {
            switch (koulutusmoduuliToteutus.getToteutustyyppi()) {
                case LUKIOKOULUTUS:
                case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                    addKoulutusohjelmaTiedot(komotoDoc, koulutusmoduuliToteutus.getKoulutusmoduuli().getLukiolinjaUri());
                    break;

                case KORKEAKOULUTUS:
                case KORKEAKOULUOPINTO:
                    MonikielinenTeksti nimi = koulutusmoduuliToteutus.getNimi();
                    if (nimi == null) {
                        nimi = koulutusmoduuliToteutus.getKoulutusmoduuli().getNimi();
                    }

                    if (nimi == null) {
                        nimi = new MonikielinenTeksti();
                    }

                    addMonikielinenNimi(komotoDoc, nimi);

                    if (koulutusmoduuliToteutus.getTarjoajanKoulutus() != null) {
                        addTarjoajanKoulutus(komotoDoc, koulutusmoduuliToteutus);
                    }
                    break;

                case VAPAAN_SIVISTYSTYON_KOULUTUS:
                case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
                    addKoulutusohjelmaTiedot(komotoDoc, getKoulutusohjelmaOrOsaamisalaUri(koulutusmoduuliToteutus));

                    nimi = koulutusmoduuliToteutus.getNimi();

                    if (nimi == null) {
                        nimi = new MonikielinenTeksti();
                    }

                    addMonikielinenNimi(komotoDoc, nimi);
                    break;

                default:
                    if (ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO
                            .equals(koulutusmoduuliToteutus.getToteutustyyppi())
                        || ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA
                            .equals(koulutusmoduuliToteutus.getToteutustyyppi())) {
                        addKoulutusohjelmaTiedot(komotoDoc, getKoulutusohjelmaOrOsaamisalaUri(koulutusmoduuliToteutus));
                    } else {
                        if (koulutusmoduuliToteutus.getOsaamisalaUri() != null) {
                            addKoulutusohjelmaTiedot(komotoDoc, koulutusmoduuliToteutus.getOsaamisalaUri());
                        } else if (koulutusmoduuliToteutus.getKoulutusmoduuli().getKoulutusohjelmaUri() != null) {
                            addKoulutusohjelmaTiedot(komotoDoc, koulutusmoduuliToteutus.getKoulutusmoduuli().getKoulutusohjelmaUri());
                        }
                    }
                    break;
            }
        }
    }

    private String getKoulutusohjelmaOrOsaamisalaUri(KoulutusmoduuliToteutus komoto) {
        String koulutusohjelmaOrOsaamisalaUri = null;
        if (komoto.isSyksy2015OrLater()) {
            koulutusohjelmaOrOsaamisalaUri = komoto.getOsaamisalaUri() != null ?
                    komoto.getOsaamisalaUri() :
                    komoto.getKoulutusmoduuli().getOsaamisalaUri();
        }
        // Fallback
        if (koulutusohjelmaOrOsaamisalaUri == null) {
            koulutusohjelmaOrOsaamisalaUri = komoto.getKoulutusohjelmaUri() != null ?
                    komoto.getKoulutusohjelmaUri() :
                    komoto.getKoulutusmoduuli().getKoulutusohjelmaUri();
        }
        return koulutusohjelmaOrOsaamisalaUri;
    }

    private void addMonikielinenNimi(SolrInputDocument komotoDoc, MonikielinenTeksti nimi) {
        for (TekstiKaannos tekstikaannos : nimi.getTekstiKaannos()) {
            Preconditions.checkNotNull(koodistoHelper);
            KoodiType type = koodistoHelper.getKoodiByUri(tekstikaannos.getKieliKoodi());

            if (type != null) {
                add(komotoDoc, NIMET, tekstikaannos.getArvo());
                add(komotoDoc, NIMIEN_KIELET, type.getKoodiArvo().toLowerCase());
            }
        }
    }

    private void addOrganisaatiotiedot(SolrInputDocument komotoDoc, List<OrganisaatioPerustieto> orgs, String firstOwner) {
        for (OrganisaatioPerustieto org : orgs) {
            if (!org.getOid().equals(firstOwner)) {
                addOrganisaatioTiedot(komotoDoc, org);
            }

            ArrayList<String> oidPath = getReversedParentOrgOids(org);

            for (String path : oidPath) {
                add(komotoDoc, ORG_PATH, path);
            }
        }
    }

    private void addJarjestajatiedot(SolrInputDocument komotoDoc, List<OrganisaatioPerustieto> orgs) {
        for (OrganisaatioPerustieto org : orgs) {
            ArrayList<String> oidPath = getReversedParentOrgOids(org);

            for (String path : oidPath) {
                add(komotoDoc, JARJESTAJA_PATH, path);
            }
        }
    }

    private ArrayList<String> getReversedParentOrgOids(OrganisaatioPerustieto org) {
        ArrayList<String> oids = getParentOrgOids(org);
        Collections.reverse(oids);
        return oids;
    }

    private ArrayList<String> getParentOrgOids(OrganisaatioPerustieto org) {
        ArrayList<String> oids = Lists.newArrayList();

        if (org.getParentOidPath() == null) {
            return oids;
        }

        Iterables.addAll(oids, Splitter.on("/").omitEmptyStrings().split(org.getParentOidPath()));
        return oids;
    }

    private void addFirstOwner(SolrInputDocument komotoDoc, String firstOwner) {
        if (firstOwner != null) {
            add(komotoDoc, ORG_OID, firstOwner);
        }
    }

    private List<OrganisaatioPerustieto> getTarjoajat(KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        return organisaatioSearchService.findByOidSet(koulutusmoduuliToteutus.getTarjoajaOids());
    }

    private List<OrganisaatioPerustieto> getJarjestajat(KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        return organisaatioSearchService.findByOidSet(koulutusmoduuliToteutus.getJarjestajaOids());
    }

    private void addTyypit(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, KOULUTUSTYYPPI_URI, koulutusmoduuliToteutus.getToteutustyyppi().uri());
        add(komotoDoc, TOTEUTUSTYYPPI_ENUM, koulutusmoduuliToteutus.getToteutustyyppi());
    }

    private void addOid(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, OID, koulutusmoduuliToteutus.getOid());
    }

    private void addKoulutuslajit(SolrInputDocument doc, List<String> koodistoUris) {
        if (koodistoUris == null) {
            return;
        }

        for (String uri : koodistoUris) {
            //käännökset, vain yksi käännös tallennetaan
            KoodiType koodi = koodistoHelper.getKoodiByUri(uri);

            if (koodi != null) {
                add(doc, KOULUTUSLAJI_URIS, koodi.getKoodiUri()); // no '#'-characters!
                KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));

                add(doc, KOULUTUSLAJI_FI, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
                add(doc, KOULUTUSLAJI_SV, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
                add(doc, KOULUTUSLAJI_EN, metadata.getNimi());
            } else {
                add(doc, KOULUTUSLAJI_URIS, getKoodiURIFromVersionedUri(uri)); // no '#'-characters!
            }
        }
    }

    private void addHakukohdeOids(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {

        for (Hakukohde hakukohde : koulutusmoduuliToteutus.getHakukohdes()) {
            add(komotoDoc, HAKUKOHDE_OIDS, hakukohde.getOid());
        }
    }

    private void addHakuOids(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus komoto) {
        Set<String> hakuOids = Sets.newHashSet();
        for (Hakukohde hakukohde : komoto.getHakukohdes()) {
            hakuOids.add(hakukohde.getHaku().getOid());
        }
        for (String hakuOid : hakuOids) {
            add(komotoDoc, HAKU_OIDS, hakuOid);
        }
    }

    private void addTekstihaku(SolrInputDocument komotoDoc) {
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSKOODI_FI));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSKOODI_SV));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSKOODI_EN));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KAUSI_URI));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(VUOSI_KOODI));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSOHJELMA_FI));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSOHJELMA_SV));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(KOULUTUSOHJELMA_EN));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(TUTKINTONIMIKE_FI));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(TUTKINTONIMIKE_SV));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValue(TUTKINTONIMIKE_EN));
        add(komotoDoc, TEKSTIHAKU, komotoDoc.getFieldValues(NIMET));
    }

    private void addKoulutuskoodiTiedot(SolrInputDocument doc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        String koulutusUri = koulutusmoduuliToteutus.getKoulutusUri() != null
                ? koulutusmoduuliToteutus.getKoulutusUri()
                : koulutusmoduuliToteutus.getKoulutusmoduuli().getKoulutusUri();

        if (koulutusUri == null) {
            logger.error("Data error - koulutus URI missing by KOMOTO OID '{}'", koulutusmoduuliToteutus.getOid());
            return;
        }

        KoodiType koodi = koodistoHelper.getKoodiByUri(koulutusUri);

        KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
        if (metadata != null) {
            add(doc, KOULUTUSKOODI_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSKOODI_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSKOODI_EN, metadata.getNimi());
            add(doc, KOULUTUSKOODI_URI, koulutusUri);
        }
    }

    public static void addKoulutusKoodis(SolrInputDocument doc, KoulutusmoduuliToteutus komoto) {
        String opintoalaUri = komoto.getOpintoalaUri() != null ?
                komoto.getOpintoalaUri() :
                komoto.getKoulutusmoduuli().getOpintoalaUri();
        String koulutusalaUri = komoto.getKoulutusalaUri() != null ?
                komoto.getKoulutusalaUri() :
                komoto.getKoulutusmoduuli().getKoulutusalaUri();
        if (opintoalaUri != null) {
            doc.addField(OPINTOALA_URI, opintoalaUri);
        }
        if (koulutusalaUri != null) {
            doc.addField(KOULUTUSALA_URI, koulutusalaUri);
        }
    }

    private void addKoulutusohjelmaTiedot(SolrInputDocument doc, String koulutusohjelmaKoodi) {
        if (koulutusohjelmaKoodi != null) {

            KoodiType koodi = koodistoHelper.getKoodiByUri(koulutusohjelmaKoodi);

            if (koodi != null) {
                KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
                add(doc, KOULUTUSOHJELMA_FI, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
                add(doc, KOULUTUSOHJELMA_SV, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
                add(doc, KOULUTUSOHJELMA_EN, metadata.getNimi());
                add(doc, KOULUTUSOHJELMA_URI, koulutusohjelmaKoodi);
            } else {
                add(doc, KOULUTUSOHJELMA_FI, koulutusohjelmaKoodi);
                add(doc, KOULUTUSOHJELMA_URI, koulutusohjelmaKoodi);
            }
        }
    }

    private void addOrganisaatioTiedot(SolrInputDocument doc, OrganisaatioPerustieto org) {
        if (org == null) {
            return;
        }
        add(doc, ORG_OID, org.getOid());
    }

    private void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }

    private void addKoulutusAlkamisPvm(SolrInputDocument komotoDoc, KoulutusmoduuliToteutus koulutusmoduuliToteutus) {
        add(komotoDoc, KOULUTUALKAMISPVM_MIN, koulutusmoduuliToteutus.getMinAlkamisPvm());
        add(komotoDoc, KOULUTUALKAMISPVM_MAX, koulutusmoduuliToteutus.getMaxAlkamisPvm());
    }
}
