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
package fi.vm.sade.tarjonta.data.test.modules;

import fi.vm.sade.tarjonta.data.util.KoodistoURIHelper;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public abstract class AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGenerator.class);
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    private static final String OID_FORMAT = "%09d_";
    protected static final String UPDATED_BY_USER = "DATA UPLOAD";
    protected static final Date UPDATED_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    protected static final String LANGUAGE_FI = "fi";
    protected static final String[] LANGUAGES = new String[]{LANGUAGE_FI, "sv", "en"};
    protected static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer quis magna libero. "
            + "Sed rutrum, nisi ac bibendum venenatis, sem libero convallis ligula, et vehicula metus mi non "
            + "tellus. Vestibulum sit amet augue ut est aliquet feugiat placerat ac dui. Lorem ipsum dolor sit "
            + "amet, consectetur adipiscing elit. Donec quam elit, commodo in ultrices eu, tempor non urna. "
            + "Mauris ornare varius diam, quis lobortis quam hendrerit eget. Curabitur consectetur eleifend suscipit. "
            + "Ut sit amet nibh nunc. Suspendisse sem diam, varius vitae fringilla in, cursus a quam. In lacinia dui non mi"
            + " vulputate adipiscing. Proin dolor augue, fermentum quis tempus nec, bibendum sit amet mi. Proin dolor nunc,"
            + " eleifend sit amet dictum a, euismod sit amet dui. Cras suscipit dui nec turpis posuere in accumsan mi tristique. "
            + "Nulla at mi id libero vestibulum condimentum. Duis lacinia elementum nisi, in congue ligula tincidunt et."
            + "Aliquam ut augue tellus. Nulla laoreet enim vel nisl tempor dictum. Nullam elit turpis, tincidunt non malesuada "
            + "id, gravida vel erat. Mauris neque turpis, ultrices a laoreet id, congue a dui. Quisque in viverra felis. Duis in "
            + "enim ac felis malesuada hendrerit. Nullam vehicula vestibulum bibendum. Cras hendrerit tincidunt diam at iaculis. "
            + "Mauris interdum massa ultrices lorem pretium sit amet sodales justo malesuada. Praesent lorem est, tincidunt"
            + "a commodo at, volutpat et ipsum. Etiam diam tellus";
    private String oidType;
    private long oid = 1l;

    public AbstractGenerator(String oidType) {
        this.oidType = oidType;
    }

    protected String generateOid() {
        final String strOid = new StringBuilder(oidType).append(String.format(OID_FORMAT, oid)).append(FORMATTER.format(new Date())).toString();
        oid++;
        //LOG.info("generate OID {}", strOid);

        return strOid;
    }

    /**
     * Lorem Ipsum
     *
     * @return ~1500 characters
     */
    protected MonikielinenTekstiTyyppi createKoodiUriLorem() {
        return createMonikielinenTekstiTyyppi(LOREM);
    }

    protected MonikielinenTekstiTyyppi createMonikielinenTekstiTyyppi(String text) {
        MonikielinenTekstiTyyppi mktt = new MonikielinenTekstiTyyppi();

        for (String lang : LANGUAGES) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_KIELI_URI, lang));
            teksti.setValue(text);
            mktt.getTeksti().add(teksti);
        }

        return mktt;
    }

    protected OsoiteTyyppi createPostiosoite() {
        OsoiteTyyppi osoite = new OsoiteTyyppi();
        osoite.setPostinumero(KoodistoUtil.toKoodiUri(KoodistoURIHelper.KOODISTO_POSTINUMERO_URI, "02070"));
        osoite.setPostitoimipaikka("ESPOON KAUPUNKI");
        osoite.setOsoiteRivi("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer sit amet odio eget metus porttitor rhoncus vitae at nisi.");
        return osoite;
    }
}
