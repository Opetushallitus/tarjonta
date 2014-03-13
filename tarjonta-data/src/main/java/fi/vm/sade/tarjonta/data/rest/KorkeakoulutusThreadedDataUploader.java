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
package fi.vm.sade.tarjonta.data.rest;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.data.ws.ThreadedDataUploader;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.data.ws.AbstractGenerator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class KorkeakoulutusThreadedDataUploader extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadedDataUploader.class);
    private OrganisaatioDTO organisaatio;
    private String hakuOid;
    private KoulutusGenerator koulutus;
    private int maxKoulutusPerOrganisation = 1;
    private List<KoulutusKorkeakouluV1RDTO> kkObjects;
    private HakukohdeGenerator hakukohde;
    private WebResource linkResource;
    private boolean amk = false;
    private String tarjontaServiceCasTicket;

    public KorkeakoulutusThreadedDataUploader(
            String threadName,
            String hakuOid,
            int maxKoulutus,
            WebResource tarjontaAdminService,
            WebResource permissionResource,
            WebResource hakuResource,
            WebResource hakukohdeResource,
            WebResource linkResource,
            OrganisaatioDTO organisaatio,
            String tarjontaServiceCasTicket
    ) throws IOException {
        super(threadName);

        this.organisaatio = organisaatio;
        this.maxKoulutusPerOrganisation = maxKoulutus;
        this.hakuOid = hakuOid;
        this.linkResource = linkResource;
        this.amk = organisaatio.getOppilaitosTyyppi().contains("42");
        this.tarjontaServiceCasTicket = tarjontaServiceCasTicket;
        this.koulutus = new KoulutusGenerator(threadName, tarjontaServiceCasTicket, tarjontaAdminService, permissionResource, this.amk);
        this.hakukohde = new HakukohdeGenerator(hakukohdeResource, tarjontaServiceCasTicket, organisaatio.getOid());
        this.kkObjects = Lists.<KoulutusKorkeakouluV1RDTO>newArrayList();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        LOG.info("Thread start {}, oppilaitostyyppi : {}, amk : " + amk, getName(), organisaatio.getOppilaitosTyyppi());
        final int koulutusPerOrg = AbstractGenerator.randomIntByRange(1, maxKoulutusPerOrganisation);
        Preconditions.checkArgument(koulutusPerOrg != 0, "Invalid count of random = " + koulutusPerOrg + " user max : " + maxKoulutusPerOrganisation);

        for (int i = 0; i < koulutusPerOrg; i++) {
            try {
                if (amk) {
                    LOG.info("amk {}, i : {}", koulutusPerOrg, i);
                    kkObjects.add(koulutus.create(organisaatio.getOid(), KoulutusGenerator.KTYPE.amk));
                } else {
                    LOG.info("yli {}, i : {}", koulutusPerOrg, i);
                    if (i == 0) {
                        kkObjects.add(koulutus.create(organisaatio.getOid(), KoulutusGenerator.KTYPE.wrapper));
                    } else if (i == 1) {
                        kkObjects.add(koulutus.create(organisaatio.getOid(), KoulutusGenerator.KTYPE.kandi));
                    } else if (i > 1) {
                        kkObjects.add(koulutus.create(organisaatio.getOid(), KoulutusGenerator.KTYPE.maisteri));
                    }
                }
            } catch (IOException ex) {
                LOG.error("GENERATOR ERROR", ex);

                throw new RuntimeException("Error - ", ex);
            }
        }

        String baseKomotoOid = null;
        String baseKomoOid = null;
        List<String> komotoOids = Lists.<String>newArrayList();
        List<String> komoOids = Lists.<String>newArrayList();

        KoulutusKorkeakouluV1RDTO first = kkObjects.get(0);
        Preconditions.checkNotNull(first, "Koulutus object cannot be null.");

        baseKomotoOid = first.getKomotoOid();
        Preconditions.checkNotNull(baseKomotoOid, "Base komoto OID cannot be null.");

        baseKomoOid = first.getKomoOid();
        if (kkObjects.size() > 1 && !amk) {
            //link childs
            for (KoulutusKorkeakouluV1RDTO kk : kkObjects) {
                Preconditions.checkNotNull(kk.getKomotoOid(), "Komoto OID cannot be null.");

                if (!kk.getKomotoOid().equals(baseKomotoOid)) {
                    komotoOids.add(kk.getKomotoOid());
                    komoOids.add(kk.getKomoOid());
                }
            }

            KomoLink link = new KomoLink();
            link.setChildren(komoOids);
            link.setParent(baseKomoOid);
            linkResource.
                    queryParam("ticket", tarjontaServiceCasTicket).
                    accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
                    header("Content-Type", "application/json; charset=UTF-8").
                    header("Connection", "keep-alive").post(link);
        }

        final int maxHakukohdes = AbstractGenerator.randomIntByRange(2, HakukohdeGenerator.HAKUKOHTEET_KOODISTO_ARVO.length - 1);
        for (int i = 0; i < maxHakukohdes; i++) {
            hakukohde.create(hakuOid, "korkeakoulu " + HakukohdeGenerator.HAKUKOHTEET_KOODISTO_ARVO[i], baseKomotoOid, komotoOids);
        }

        long timePassed = System.currentTimeMillis() - startTime;
        LOG.info("Thread end {}, time passed {} seconds", getName(), TimeUnit.MILLISECONDS.toSeconds(timePassed));
    }

    /**
     * @return the organisaatio
     */
    public OrganisaatioDTO getOrganisaatio() {
        return organisaatio;
    }

}
