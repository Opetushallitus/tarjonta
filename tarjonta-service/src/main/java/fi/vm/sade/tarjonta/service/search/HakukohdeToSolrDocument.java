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
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.search.resolver.OppilaitostyyppiResolver;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

@Component
public class HakukohdeToSolrDocument implements Function<Long, List<SolrInputDocument>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Autowired
    private TarjontaKoodistoHelper koodistoHelper;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private OppilaitostyyppiResolver oppilaitostyyppiResolver;

    @Override
    public List<SolrInputDocument> apply(final Long hakukohdeId) {
        Hakukohde hakukohde = hakukohdeDAO.findBy("id", hakukohdeId).get(0);

        Set<KoulutusmoduuliToteutus> koulutukses = hakukohde.getKoulutusmoduuliToteutuses();
        if (koulutukses.isEmpty()) {
            logger.warn("There is a hakukohde without komotos." + hakukohde);
            return Collections.EMPTY_LIST;
        }

        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        final SolrInputDocument hakukohdeDoc = new SolrInputDocument();

        boolean orgFound = addOrganisaatiotiedot(hakukohdeDoc, hakukohde);

        if (!orgFound) {
            logger.warn("Skipping hakukohde %s: no organisation found.", hakukohde.getOid());
            return Lists.newArrayList();
        }

        addOid(hakukohdeDoc, hakukohde);
        addKausikoodiTiedot(hakukohdeDoc, hakukohde);
        addVuosi(hakukohdeDoc, hakukohde);
        addHakutapaTiedot(hakukohdeDoc, hakukohde);
        addAloituspaikkatiedot(hakukohdeDoc, hakukohde);
        addTila(hakukohdeDoc, hakukohde);
        addNimitiedot(hakukohdeDoc, hakukohde);
        addHakuTiedot(hakukohdeDoc, hakukohde);
        addHakuOid(hakukohdeDoc, hakukohde);
        addHakutyyppi(hakukohdeDoc, hakukohde);
        addRyhmat(hakukohdeDoc, hakukohde);
        addKomotoOids(hakukohdeDoc, hakukohde);
        addKoulutuslajit(hakukohdeDoc, hakukohde);
        addKoulutusAsteTyyppi(hakukohdeDoc, hakukohde);
        addToteutustyyppi(hakukohdeDoc, hakukohde);
        addPohjakoulutusvaatimus(hakukohdeDoc, hakukohde);
        addKohdejoukko(hakukohdeDoc, hakukohde);
        addRyhmaliitokset(hakukohdeDoc, hakukohde);
        addDataFromKoulutus(hakukohdeDoc, hakukohde);
        addTekstihaku(hakukohdeDoc);
        addHakuaika(hakukohdeDoc, hakukohde);

        docs.add(hakukohdeDoc);

        return docs;
    }

    private void addHakuaika(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        if (hakukohde.getHakuaikaAlkuPvm() != null && hakukohde.getHakuaikaLoppuPvm() != null) {
            String hakukaika = DATE_FORMAT.format(hakukohde.getHakuaikaAlkuPvm());
            hakukaika += " - " + DATE_FORMAT.format(hakukohde.getHakuaikaLoppuPvm());
            add(hakukohdeDoc, HAKUAIKA_STRING, hakukaika);
        }

        Hakuaika hakuaika = hakukohde.getHakuaika();

        // Jos ei löytynyt hakuaikaa -> ota haun ensimmäinen hakuaika
        if (hakuaika == null) {
            Set<Hakuaika> hakuaikas = hakukohde.getHaku().getHakuaikas();
            if (hakuaikas != null && !hakuaikas.isEmpty()) {
                hakuaika = hakuaikas.iterator().next();
            }
        }

        if (hakuaika != null) {
            add(hakukohdeDoc, HAKUAIKA_RYHMA, getHakuaikaRyhmaString(hakuaika));
        }
    }

    private String getHakuaikaRyhmaString(Hakuaika hakuaika) {
        String hakuaikaRyhma = "";

        if (hakuaika.getNimi() != null && hakuaika.getNimi().getFirstNonEmptyKaannos() != null) {
            hakuaikaRyhma = hakuaika.getNimi().getFirstNonEmptyKaannos() + " ";
        }

        if (hakuaika.getAlkamisPvm() != null) {
            hakuaikaRyhma += "(" + DATE_FORMAT.format(hakuaika.getAlkamisPvm()) + " - ";
        }
        else {
            hakuaikaRyhma += "( - ";
        }

        if (hakuaika.getPaattymisPvm() != null) {
            hakuaikaRyhma += DATE_FORMAT.format(hakuaika.getPaattymisPvm()) + ")";
        }
        else {
            hakuaikaRyhma += ")";
        }

        return hakuaikaRyhma;
    }

    private void addRyhmaliitokset(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        for (Ryhmaliitos ryhmaliitos : hakukohde.getRyhmaliitokset()) {
            add(hakukohdeDoc, RYHMA_OIDS, ryhmaliitos.getRyhmaOid());

            if (ryhmaliitos.getPrioriteetti() == null) {
                add(hakukohdeDoc, RYHMA_PRIORITEETIT, SolrFields.RYHMA_PRIORITEETTI_EI_MAARITELTY);
            } else {
                add(hakukohdeDoc, RYHMA_PRIORITEETIT, ryhmaliitos.getPrioriteetti());
            }
        }
    }

    private boolean addOrganisaatiotiedot(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        boolean orgFound = false;

        Map<String, KoulutusmoduuliToteutusTarjoajatiedot> koulutusmoduuliToteutusTarjoajatiedot = hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot();

        // Jos ei ole monta tarjoajaa -> indeksoi kuten ennen KJOH-778
        if (koulutusmoduuliToteutusTarjoajatiedot.isEmpty()) {
            orgFound = addOrganisaatioTiedotForTarjoaja(hakukohdeDoc, hakukohde.getFirstKoulutus().getTarjoaja());
        } else { // Monta tarjoajaa
            for (KoulutusmoduuliToteutusTarjoajatiedot tarjoajaTiedot : koulutusmoduuliToteutusTarjoajatiedot.values()) {
                for (String tarjoajaOid : tarjoajaTiedot.getTarjoajaOids()) {
                    orgFound = addOrganisaatioTiedotForTarjoaja(hakukohdeDoc, tarjoajaOid);
                }
            }
        }
        return orgFound;
    }

    private void addKohdejoukko(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, KOHDEJOUKKO_URI, getKoodiURIFromVersionedUri(hakukohde.getHaku().getKohdejoukkoUri()));
    }

    private void addKausikoodiTiedot(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        IndexDataUtils.addKausikoodiTiedot(hakukohdeDoc, hakukohde.getFirstKoulutus().getAlkamiskausiUri(), koodistoHelper);
    }

    private void addOid(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, OID, hakukohde.getOid());
    }

    private void addVuosi(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, VUOSI_KOODI, hakukohde.getFirstKoulutus().getAlkamisVuosi());
    }

    private void addTila(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, TILA, hakukohde.getTila());
    }

    private void addHakuOid(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, HAUN_OID, hakukohde.getHaku().getOid());
    }

    private void addHakutyyppi(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, HAKUTYYPPI_URI, hakukohde.getHaku().getHakutyyppiUri());
    }

    private void addDataFromKoulutus(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        addOppilaitostyypit(hakukohdeDoc, hakukohde);
        addKunnat(hakukohdeDoc, hakukohde);
        addOpetuskielet(hakukohdeDoc, hakukohde);
        addKoulutusmoduuliTyyppi(hakukohdeDoc, hakukohde);
    }

    private void addKoulutusmoduuliTyyppi(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
            KoulutusmoduuliTyyppi moduuliTyyppi = hakukohde.getFirstKoulutus().getKoulutusmoduuli().getModuuliTyyppi();
            add(hakukohdeDoc, KOULUTUSMODUULITYYPPI_ENUM, moduuliTyyppi);
        }
    }

    private void addOpetuskielet(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        Set<String> kieles = new HashSet<String>();

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
                kieles.add(getKoodiURIFromVersionedUri(koodistoUri.getKoodiUri()));
            }
        }

        for (String kieli : kieles) {
            add(hakukohdeDoc, OPETUSKIELI_URIS, kieli);
        }

    }

    private void addKunnat(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        Set<String> kuntas = new HashSet<String>();

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            List<OrganisaatioPerustieto> organisaatiotiedot = organisaatioSearchService.findByOidSet(koulutusmoduuliToteutus.getOwnerOids());

            for (OrganisaatioPerustieto organisaatioPerustieto : organisaatiotiedot) {
                kuntas.add(getKoodiURIFromVersionedUri(organisaatioPerustieto.getKotipaikkaUri()));
            }
        }

        for (String kunta : kuntas) {
            add(hakukohdeDoc, KUNTA_URIS, kunta);
        }
    }

    private void addOppilaitostyypit(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        Set<String> oppilaitostyypit = new HashSet<String>();

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            List<OrganisaatioPerustieto> organisaatiotiedot = organisaatioSearchService.findByOidSet(koulutusmoduuliToteutus.getOwnerOids());

            for (OrganisaatioPerustieto organisaatioPerustieto : organisaatiotiedot) {
                String oppilaitostyyppi = oppilaitostyyppiResolver.resolve(organisaatioPerustieto);
                if (oppilaitostyyppi != null) {
                    oppilaitostyypit.add(oppilaitostyyppi);
                }
            }
        }

        for (String oppilaitostyyppi : oppilaitostyypit) {
            add(hakukohdeDoc, OPPILAITOSTYYPPI_URIS, oppilaitostyyppi);
        }
    }

    private void addAloituspaikkatiedot(SolrInputDocument doc, Hakukohde hakukohde) {
        add(doc, ALOITUSPAIKAT, hakukohde.getAloituspaikatLkm());
        add(doc, ENSIKERTALAISTEN_ALOITUSPAIKAT, hakukohde.getEnsikertalaistenAloituspaikat());

        MonikielinenTeksti aloituspaikatKuvaus = hakukohde.getAloituspaikatKuvaus();

        if (aloituspaikatKuvaus != null) {

            for (TekstiKaannos tekstikaannos : aloituspaikatKuvaus.getTekstiKaannos()) {

                KoodiType type = koodistoHelper.getKoodiByUri(tekstikaannos.getKieliKoodi());

                if (type != null) {
                    add(doc, ALOITUSPAIKAT_KUVAUKSET, tekstikaannos.getArvo());
                    add(doc, ALOITUSPAIKAT_KIELET, type.getKoodiArvo().toLowerCase());
                    add(doc, TEKSTIHAKU, tekstikaannos.getArvo());
                }
            }
        }
    }

    private void addRyhmat(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        for (Ryhmaliitos ryhmaliitos : hakukohde.getRyhmaliitokset()) {
            hakukohdeDoc.addField(ORGANISAATIORYHMAOID, ryhmaliitos.getRyhmaOid());
        }
    }

    private void addKoulutusAsteTyyppi(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
            String koulutusastetyyppi = hakukohde.getFirstKoulutus().getKoulutusmoduuli().getKoulutustyyppiEnum().getKoulutusasteTyyppi().value();
            hakukohdeDoc.addField(KOULUTUSASTETYYPPI, koulutusastetyyppi);
        }
    }

    private void addToteutustyyppi(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
            KoulutusmoduuliToteutus koulutusmoduuliToteutus = hakukohde.getFirstKoulutus();
            add(hakukohdeDoc, KOULUTUSTYYPPI_URI, koulutusmoduuliToteutus.getToteutustyyppi().uri());
            add(hakukohdeDoc, TOTEUTUSTYYPPI_ENUM, koulutusmoduuliToteutus.getToteutustyyppi());
        }
    }

    private void addPohjakoulutusvaatimus(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
            IndexDataUtils.addKoodiLyhytnimiTiedot(hakukohdeDoc, komoto.getPohjakoulutusvaatimusUri(),
                    koodistoHelper,
                    POHJAKOULUTUSVAATIMUS_URI,
                    POHJAKOULUTUSVAATIMUS_FI,
                    POHJAKOULUTUSVAATIMUS_SV,
                    POHJAKOULUTUSVAATIMUS_EN);
            return;
        }
    }

    private void addKomotoOids(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        for (KoulutusmoduuliToteutus komoto : hakukohde.getKoulutusmoduuliToteutuses()) {
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

    private void addHakuTiedot(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        add(hakukohdeDoc, HAUN_ALKAMISPVM, getStartDateStr(hakukohde.getHaku().getHakuaikas()));
        add(hakukohdeDoc, HAUN_PAATTYMISPVM, getEndDateStr(hakukohde.getHaku().getHakuaikas()));
    }

    private void addNimitiedot(SolrInputDocument hakukohdeDoc, Hakukohde hakukohde) {
        if (hakukohde.getHakukohdeNimi() == null) {
            MonikielinenTeksti nimi = hakukohde.getHakukohdeMonikielinenNimi();
            for (TekstiKaannos tekstikaannos : nimi.getTekstiKaannos()) {

                Preconditions.checkNotNull(koodistoHelper);
                KoodiType type = koodistoHelper.getKoodiByUri(tekstikaannos.getKieliKoodi());

                if (type != null) {
                    add(hakukohdeDoc, NIMET, tekstikaannos.getArvo());
                    add(hakukohdeDoc, NIMIEN_KIELET, type.getKoodiArvo().toLowerCase());
                    add(hakukohdeDoc, TEKSTIHAKU, tekstikaannos.getArvo());
                }
            }
            return;
        }

        if (!hakukohde.getKoulutusmoduuliToteutuses().isEmpty() &&
                !ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS.equals(hakukohde.getFirstKoulutus().getToteutustyyppi())) {

            KoodiType koodi = koodistoHelper.getKoodiByUri(hakukohde.getHakukohdeNimi());

            if (koodi != null) {
                KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
                add(hakukohdeDoc, HAKUKOHTEEN_NIMI_FI, metadata.getNimi());

                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
                add(hakukohdeDoc, HAKUKOHTEEN_NIMI_SV, metadata.getNimi());

                metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
                add(hakukohdeDoc, HAKUKOHTEEN_NIMI_EN, metadata.getNimi());

                add(hakukohdeDoc, HAKUKOHTEEN_NIMI_URI, hakukohde.getHakukohdeNimi());
            }
        } else {
            add(hakukohdeDoc, HAKUKOHTEEN_NIMI_FI, hakukohde.getHakukohdeNimi());
            add(hakukohdeDoc, HAKUKOHTEEN_NIMI_SV, hakukohde.getHakukohdeNimi());
            add(hakukohdeDoc, HAKUKOHTEEN_NIMI_EN, hakukohde.getHakukohdeNimi());
            add(hakukohdeDoc, HAKUKOHTEEN_NIMI_URI, hakukohde.getHakukohdeNimi());
        }
    }

    private void addHakutapaTiedot(SolrInputDocument doc, Hakukohde hakukohde) {
        KoodiType koodi = koodistoHelper.getKoodiByUri(hakukohde.getHaku().getHakutapaUri());

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, HAKUTAPA_FI, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, HAKUTAPA_SV, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, HAKUTAPA_EN, metadata.getNimi());

            add(doc, HAKUTAPA_URI, hakukohde.getHaku().getHakutapaUri());
        }
    }


    private void addKoulutuslajit(SolrInputDocument doc, Hakukohde hakukohde) {
        if (hakukohde.getKoulutusmoduuliToteutuses().isEmpty()) {
            return;
        }

        KoulutusmoduuliToteutus koulutusmoduuliToteutus = hakukohde.getFirstKoulutus();

        if (koulutusmoduuliToteutus.getKoulutuslajis().isEmpty()) {
            return;
        }

        KoodiType koodi = koodistoHelper.getKoodiByUri(koulutusmoduuliToteutus.getKoulutuslajis().iterator().next().getKoodiUri());

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSLAJI_FI, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSLAJI_SV, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSLAJI_EN, metadata.getNimi());

            add(doc, KOULUTUSLAJI_URIS, koulutusmoduuliToteutus.getKoulutuslajis().iterator().next().getKoodiUri());
        }
    }

    private void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }

    private boolean addOrganisaatioTiedotForTarjoaja(SolrInputDocument hakukohdeDoc, String tarjoaja) {
        final List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(Sets.newHashSet(tarjoaja));
        if (orgs.size() == 0) {
            return false;
        }

        final OrganisaatioPerustieto perus = orgs.get(0);

        // Non-multivalued field -> ensure it's added only once
        if (hakukohdeDoc.getFieldValue(ORG_NIMI) == null) {
            String orgNimi = perus.getNimi().get("fi");
            // Fallback jos ei löydy suomeksi
            if (StringUtils.isBlank(orgNimi)) {
                orgNimi = perus.getNimi().values().iterator().next();
            }
            add(hakukohdeDoc, ORG_NIMI, orgNimi);
            add(hakukohdeDoc, ORG_NIMI_LOWERCASE, orgNimi.toLowerCase());
        }
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

    private Date getStartDate(Set<Hakuaika> hakuaikas) {
        Date startDate = null;
        for (Hakuaika aika : hakuaikas) {
            if (startDate == null) {
                startDate = aika.getAlkamisPvm();
            } else if (aika.getAlkamisPvm() != null && aika.getAlkamisPvm().before(startDate)) {
                startDate = aika.getAlkamisPvm();
            }
        }
        return startDate;
    }

    private String getStartDateStr(Set<Hakuaika> hakuaikas) {
        Date startDate = getStartDate(hakuaikas);
        if (startDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(startDate);
        }
        return "";
    }

    private Date getEndDate(Set<Hakuaika> hakuaikas) {
        Date endDate = null;
        for (Hakuaika aika : hakuaikas) {
            if (endDate == null) {
                endDate = aika.getPaattymisPvm();
            } else if (aika.getPaattymisPvm() != null && aika.getPaattymisPvm().after(endDate)) {
                endDate = aika.getPaattymisPvm();
            }
        }
        return endDate;
    }

    private String getEndDateStr(Set<Hakuaika> hakuaikas) {
        Date endDate = getEndDate(hakuaikas);
        if (endDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(endDate);
        }
        return "";
    }
}
