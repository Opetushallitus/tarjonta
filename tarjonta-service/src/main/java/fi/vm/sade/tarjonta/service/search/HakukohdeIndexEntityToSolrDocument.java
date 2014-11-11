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
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutusTarjoajatiedot;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSTYYPPI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TOTEUTUSTYYPPI_ENUM;

/**
 * Convert "Hakukohde" to {@link SolrInputDocument} so that it can be indexed.
 */
@Component
public class HakukohdeIndexEntityToSolrDocument implements Function<HakukohdeIndexEntity, List<SolrInputDocument>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Override
    public List<SolrInputDocument> apply(final HakukohdeIndexEntity hakukohde) {
        Preconditions.checkNotNull(hakukohde);
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        final SolrInputDocument hakukohdeDoc = new SolrInputDocument();
        if (hakukohde.getOid() == null) {
            logger.warn("There is a hakukohde without oid???" + hakukohde.toString());
            return Collections.EMPTY_LIST;
        }

        List<KoulutusIndexEntity> koulutuses = indexerDao.findKoulutusmoduuliToteutusesByHakukohdeId(hakukohde.getId());
        if (koulutuses.size() == 0) {
            logger.warn("There is a hakukohde without komotos???" + hakukohde.toString());
            return Collections.EMPTY_LIST;
        }

        add(hakukohdeDoc, OID, hakukohde.getOid());
        IndexDataUtils.addKausikoodiTiedot(hakukohdeDoc, koulutuses.get(0).getKausi(), koodiService);
        add(hakukohdeDoc, VUOSI_KOODI, koulutuses.get(0).getVuosi());
        addHakutapaTiedot(hakukohdeDoc, hakukohde.getHakutapaUri());
        addAloituspaikkatiedot(hakukohdeDoc, hakukohde);
        add(hakukohdeDoc, TILA, hakukohde.getTila());
        addNimitiedot(hakukohdeDoc, hakukohde.getHakukohdeNimi(), hakukohde.getId(), koulutuses);
        addHakuTiedot(hakukohdeDoc, getHakuajat(hakukohde.getHakuId()));
        addTekstihaku(hakukohdeDoc);
        add(hakukohdeDoc, HAUN_OID, hakukohde.getHakuOid());
        addRyhmat(hakukohdeDoc, hakukohde.getRyhmaOidit());

        addKomotoOids(hakukohdeDoc, koulutuses);
        addKoulutuslajit(hakukohdeDoc, koulutuses);
        addKoulutusAsteTyyppi(hakukohdeDoc, koulutuses);
        addToteutustyyppi(hakukohdeDoc, koulutuses);


        addPohjakoulutusvaatimus(hakukohdeDoc, koulutuses);

        add(hakukohdeDoc, HAKUTYYPPI_URI, hakukohde.getHakutyyppiUri());

        docs.add(hakukohdeDoc);

        boolean orgFound = false;
        String tarjoaja = "not initialized";

        // KJOH-778 koulutuksella/hakukohteella monta tarjoajaa
        Hakukohde hakukohdeDao = hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid());
        Map<String, KoulutusmoduuliToteutusTarjoajatiedot> koulutusmoduuliToteutusTarjoajatiedot
                = hakukohdeDao.getKoulutusmoduuliToteutusTarjoajatiedot();

        // Jos ei ole monta tarjoajaa -> indeksoi kuten ennen KJOH-778
        if (koulutusmoduuliToteutusTarjoajatiedot.isEmpty()) {
            if (koulutuses.size() > 0) {
                tarjoaja = koulutuses.get(0).getTarjoaja();
                orgFound = addOrganisaatioTiedot(hakukohdeDoc, docs, tarjoaja);
            } else {
                logger.warn("No koulutuses found, this should not be possible!");
            }
        }

        // Monta tarjoajaa
        else {
            // Loop all komotos attached to hakukohde
            for (KoulutusmoduuliToteutusTarjoajatiedot tarjoajaTiedot : koulutusmoduuliToteutusTarjoajatiedot.values()) {
                // Loop all tarjoajas attached to this komoto and this hakukohde
                for (String tarjoajaOid : tarjoajaTiedot.getTarjoajaOids()) {
                    tarjoaja = tarjoajaOid;
                    orgFound = addOrganisaatioTiedot(hakukohdeDoc, docs, tarjoaja);
                }
            }
        }

        if (!orgFound) {
            logger.warn("Skipping hakukohde:" + hakukohde.getOid()
                    + " no organisation found with oid " + tarjoaja);
            return Lists.newArrayList();
        }

        return docs;
    }

    private void addAloituspaikkatiedot(SolrInputDocument doc, HakukohdeIndexEntity hakukohde) {
        add(doc, ALOITUSPAIKAT, hakukohde.getAloituspaikatLkm());

        MonikielinenTeksti aloituspaikatKuvaus = indexerDao.getAloituspaikatKuvausForHakukohde(hakukohde.getId());

        if (aloituspaikatKuvaus != null) {

            for (TekstiKaannos tekstikaannos : aloituspaikatKuvaus.getTekstiKaannos()) {

                KoodiType type = IndexDataUtils.getKoodiByUriWithVersion(tekstikaannos.getKieliKoodi(), koodiService);

                if (type != null) {
                    add(doc, ALOITUSPAIKAT_KUVAUKSET, tekstikaannos.getArvo());
                    add(doc, ALOITUSPAIKAT_KIELET, type.getKoodiArvo().toLowerCase());
                    add(doc, TEKSTIHAKU, tekstikaannos.getArvo());
                }
            }
        }
    }

    private void addRyhmat(SolrInputDocument hakukohdeDoc, String ryhmaOidit) {
        if (ryhmaOidit == null) {
            return;
        }
        for (String oid : ryhmaOidit.split(",")) {
            hakukohdeDoc.addField(ORGANISAATIORYHMAOID, oid);
        }
    }

    private void addKoulutusAsteTyyppi(SolrInputDocument hakukohdeDoc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses != null && koulutuses.size() > 0) {
            String koulutusastetyyppi = koulutuses.get(0).getBaseKoulutustyyppiEnum().getKoulutusasteTyyppi().value();
            hakukohdeDoc.addField(KOULUTUSASTETYYPPI, koulutusastetyyppi);
        }
    }

    private void addToteutustyyppi(SolrInputDocument hakukohdeDoc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses != null && koulutuses.size() > 0) {
            final KoulutusIndexEntity koulutus = koulutuses.get(0);
            if (koulutus.getSubKoulutustyyppiEnum() != null) {
                add(hakukohdeDoc, KOULUTUSTYYPPI_URI, koulutus.getSubKoulutustyyppiEnum().uri());
                add(hakukohdeDoc, TOTEUTUSTYYPPI_ENUM, koulutus.getSubKoulutustyyppiEnum());
            }
        }
    }

    private void addPohjakoulutusvaatimus(SolrInputDocument hakukohdeDoc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses == null) {
            return;
        }
        for (KoulutusIndexEntity komoto : koulutuses) {
            IndexDataUtils.addKoodiLyhytnimiTiedot(hakukohdeDoc, komoto.getPohjakoulutusvaatimus(), koodiService, POHJAKOULUTUSVAATIMUS_URI, POHJAKOULUTUSVAATIMUS_FI, POHJAKOULUTUSVAATIMUS_SV, POHJAKOULUTUSVAATIMUS_EN);
            return;
        }
    }

    private List<HakuAikaIndexEntity> getHakuajat(Long hakuId) {
        return indexerDao.findHakuajatForHaku(hakuId);
    }

    private void addKomotoOids(SolrInputDocument hakukohdeDoc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses == null) {
            return;
        }
        for (KoulutusIndexEntity komoto : koulutuses) {
            add(hakukohdeDoc, KOULUTUS_OIDS, komoto.getOid());
        }
    }

    private void addTekstihaku(SolrInputDocument hakukohdeDoc) {
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_FI));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_SV));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_EN));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(KAUSI_FI));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(KAUSI_SV));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(KAUSI_EN));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(VUOSI_KOODI));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUTAPA_FI));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUTAPA_SV));
        add(hakukohdeDoc, TEKSTIHAKU, hakukohdeDoc.getFieldValue(HAKUTAPA_EN));
    }

    private void addHakuTiedot(SolrInputDocument hakukohdeDoc, List<HakuAikaIndexEntity> hakuajat) {
        add(hakukohdeDoc, HAUN_ALKAMISPVM, getStartDateStr(hakuajat));
        add(hakukohdeDoc, HAUN_PAATTYMISPVM, getEndDateStr(hakuajat));
    }

    private void addNimitiedot(SolrInputDocument doc, String hakukohdeNimi, long id, List<KoulutusIndexEntity> koulutuses) {
        if (hakukohdeNimi == null) {
            // kk? nimi monikielisenä tekstinä
            MonikielinenTeksti nimi = indexerDao.getNimiForHakukohde(id);
            for (TekstiKaannos tekstikaannos : nimi.getTekstiKaannos()) {

                Preconditions.checkNotNull(koodiService);
                KoodiType type = IndexDataUtils.getKoodiByUriWithVersion(tekstikaannos.getKieliKoodi(), koodiService);

                if (type != null) {
                    add(doc, NIMET, tekstikaannos.getArvo());
                    add(doc, NIMIEN_KIELET, type.getKoodiArvo().toLowerCase());
                    add(doc, TEKSTIHAKU, tekstikaannos.getArvo());
                }
            }

            return;
        }

        //Vapaan sivistyon koulutus has an edited name, not a koodiuri
        if (!koulutuses.isEmpty()
                && !koulutuses.get(0).getBaseKoulutustyyppiEnum().equals(ModuulityyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS)) {

            KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(hakukohdeNimi, koodiService);

            if (koodi != null) {
                KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
                add(doc, HAKUKOHTEEN_NIMI_FI, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
                add(doc, HAKUKOHTEEN_NIMI_SV, metadata.getNimi());
                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
                add(doc, HAKUKOHTEEN_NIMI_EN, metadata.getNimi());
                add(doc, HAKUKOHTEEN_NIMI_URI, hakukohdeNimi);
            }
        } else {
            add(doc, HAKUKOHTEEN_NIMI_FI, hakukohdeNimi);
            add(doc, HAKUKOHTEEN_NIMI_SV, hakukohdeNimi);
            add(doc, HAKUKOHTEEN_NIMI_EN, hakukohdeNimi);
            add(doc, HAKUKOHTEEN_NIMI_URI, hakukohdeNimi);
        }
    }

    private void addHakutapaTiedot(SolrInputDocument doc, String hakutapaUri) {
        if (hakutapaUri == null) {
            return;
        }
        KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(hakutapaUri, koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, HAKUTAPA_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, HAKUTAPA_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, HAKUTAPA_EN, metadata.getNimi());
            add(doc, HAKUTAPA_URI, hakutapaUri);
        }
    }


    private void addKoulutuslajit(SolrInputDocument doc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses == null || koulutuses.size() == 0) {
            return;
        }

        KoulutusIndexEntity koulutus = koulutuses.get(0);

        if (koulutus.getKoulutuslaji() == null) {
            return;
        }

        KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(koulutus.getKoulutuslaji(), koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSLAJI_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSLAJI_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSLAJI_EN, metadata.getNimi());
        }
    }

    private void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }

    private boolean addOrganisaatioTiedot(SolrInputDocument hakukohdeDoc, List<SolrInputDocument> docs, String tarjoaja) {
        return handleOrganisaatio(tarjoaja, hakukohdeDoc, docs);
    }

    private boolean handleOrganisaatio(String tarjoaja, SolrInputDocument hakukohdeDoc, List<SolrInputDocument> docs) {
        final List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(Sets.newHashSet(tarjoaja));
        if (orgs.size() == 0) {
            return false;
        }

        final OrganisaatioPerustieto perus = orgs.get(0);

        add(hakukohdeDoc, ORG_OID, perus.getOid());
        ArrayList<String> oidPath = Lists.newArrayList();

        if (perus.getParentOidPath() != null) {
            Iterables.addAll(oidPath, Splitter.on("/").omitEmptyStrings().split(perus.getParentOidPath()));
            Collections.reverse(oidPath);

            for (String path : oidPath) {
                add(hakukohdeDoc, ORG_PATH, path);
            }
        }
        return true;
    }

    private Date getStartDate(List<HakuAikaIndexEntity> hakuaikas) {
        Date startDate = null;
        for (HakuAikaIndexEntity aika : hakuaikas) {
            if (startDate == null) {
                startDate = aika.getAlkamisPvm();
            } else if (aika.getAlkamisPvm().before(startDate)) {
                startDate = aika.getAlkamisPvm();
            }
        }
        return startDate;
    }

    private String getStartDateStr(List<HakuAikaIndexEntity> hakuaikas) {
        Date startDate = getStartDate(hakuaikas);
        if (startDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(startDate);
        }
        return "";
    }

    private Date getEndDate(List<HakuAikaIndexEntity> hakuaikas) {
        Date endDate = null;
        for (HakuAikaIndexEntity aika : hakuaikas) {
            if (endDate == null) {
                endDate = aika.getPaattymisPvm();
            } else if (aika.getPaattymisPvm().after(endDate)) {
                endDate = aika.getPaattymisPvm();
            }
        }
        return endDate;
    }

    private String getEndDateStr(List<HakuAikaIndexEntity> hakuaikas) {
        Date endDate = getEndDate(hakuaikas);
        if (endDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(endDate);
        }
        return "";
    }
}
