package fi.vm.sade.tarjonta.ui.helper;/*
 *
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



import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

/**
 * @author: Tuomas Katva
 * Date: 3.9.2013
 */
@Component
public class RaportointiRestClientHelper {

    @Value("${raportointi.host}")
    private String aloitusPaikatReportHost;

    @Value("${raportointi.port}")
    private String aloitusPaikatReportPort;

    @Value("${raportointi.pathPdf}")
    private String aloitusPaikatReportPathPdf;

    @Value("${raportointi.pathExcel}")
    private String aloitusPaikatReportPathExcel;


    public static final String EXCEL_TYPE = "Excel";
    public static final String PDF_TYPE = "PDF";

    public static final String KOULUTUSTOIMIJA_PARAM = "koulutusjarjestaja";
    public static final String VUOSI_PARAM = "vuosi";
    public static final String KAUSI_PARAM = "kausi";
    public static final String KIELI_PARAM = "kieli";
    public static final String OPPILAITOS_PARAM = "oppilaitos";
    public static final String TOIMIPISTE_PARAM = "opetuspiste";
    public static final String POHJAKOULUTUS_PARAM = "pohjakoulutus";

    public String createAloitusPaikatRaporttiUrl(String koulutustoimija,String oppilaitos,String toimipiste,
                                                 String kausi,String vuosi, String pohjakoulutus,String reportOutputType, String kieli) {



        URIBuilder builder = new URIBuilder();
        if (reportOutputType.equalsIgnoreCase(PDF_TYPE))   {
            builder.setScheme("http").setHost(aloitusPaikatReportHost).setPort(new Integer(aloitusPaikatReportPort)).setPath(aloitusPaikatReportPathPdf);


        } else if (reportOutputType.equalsIgnoreCase(EXCEL_TYPE)) {
            builder.setScheme("http").setHost(aloitusPaikatReportHost).setPort(new Integer(aloitusPaikatReportPort)).setPath(aloitusPaikatReportPathExcel);
        }

                    builder.setParameter(KOULUTUSTOIMIJA_PARAM, koulutustoimija)
                    .setParameter(VUOSI_PARAM,vuosi)
                    .setParameter(KAUSI_PARAM,kausi)
                    .setParameter(KIELI_PARAM,kieli);

            if (oppilaitos != null && oppilaitos.length() > 1) {
                builder.setParameter(OPPILAITOS_PARAM,oppilaitos);
            }
            if (toimipiste != null && oppilaitos.length() > 1 ) {
                builder.setParameter(TOIMIPISTE_PARAM,toimipiste);
            }
            if (pohjakoulutus != null && pohjakoulutus.length() > 1) {
                builder.setParameter(POHJAKOULUTUS_PARAM,pohjakoulutus);
            }




        try {
            return builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
