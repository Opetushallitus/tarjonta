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
package fi.vm.sade.tarjonta.publication.enricher;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.vm.sade.tarjonta.publication.enricher.KoodistoLookupService.KoodiValue;
import fi.vm.sade.tarjonta.publication.utils.StringUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles elements that are of type: {http://publication.tarjonta.sade.vm.fi/types}/CodeValueType
 * by injecting any missing labels by koodi value - if any. Any existing labels are left intact.
 *
 * @author Jukka Raanamo
 */
public class KoodistoCodeValueEnricher extends ElementEnricher {

    private static final Logger log = LoggerFactory.getLogger(KoodistoCodeValueEnricher.class);

    private static final String TAG_CODE = "Code";

    private static final String TAG_SCHEME = "scheme";

    private static final String TAG_VERSION = "version";

    private static final String TAG_LABEL = "Label";

    private static final String TAG_LANG = "lang";

    private static final String SCHEME_KOODISTO = "Koodisto";

    private KoodistoLookupService koodistoService;

    private String koodiUri;

    private Integer koodiVersion;

    private String currentTag;

    private Set<String> existingLabels = new HashSet<String>();

    public void setKoodistoService(KoodistoLookupService koodistoService) {
        this.koodistoService = koodistoService;
    }

    @Override
    public void reset() {
        currentTag = null;
        koodiUri = null;
        koodiVersion = null;
        existingLabels.clear();
    }

    @Override
    public int startElement(String localName, Attributes attributes)
        throws SAXException {

        currentTag = localName;

        if (TAG_CODE.equals(localName)) {

            final String scheme = attributes.getValue(EMPTY_STRING, TAG_SCHEME);
            if (!SCHEME_KOODISTO.equals(scheme)) {
                // this is not Koodisto based, nothing we can do about it
                if (log.isDebugEnabled()) {
                    log.debug("not Koodisto based, skipping scheme: " + scheme);
                }
                return WRITE_AND_EXIT;
            }

            final String version = attributes.getValue(EMPTY_STRING, TAG_VERSION);
            if (version != null) {
                try {
                    koodiVersion = new Integer(version);
                } catch (NumberFormatException e) {
                    // uri could contain version?
                }
            }

        } else if (TAG_LABEL.equals(localName)) {

            // label already exist, remember
            final String lang = attributes.getValue(TAG_LANG);
            if (StringUtils.notEmpty(lang)) {
                existingLabels.add(lang);
            }

        }

        return WRITE_AND_CONTINUE;

    }

    @Override
    public int endElement(String localName) throws SAXException {

        currentTag = null;

        if (mappedElementName.equals(localName)) {
            maybeWriteLabels();
            return WRITE_AND_EXIT;
        } else {
            return WRITE_AND_CONTINUE;
        }

    }

    @Override
    public int characters(char[] characters, int start, int length) throws SAXException {

        if (TAG_CODE.equals(currentTag)) {
            koodiUri = text(characters, start, length);
        }

        return WRITE_AND_CONTINUE;

    }

    private void maybeWriteLabels() throws SAXException {

        if (koodiUri == null) {
            if (log.isDebugEnabled()) {
                log.debug("element missing uri, skipping");
            }
            return;
        }

        // todo: version may be null - latest looked up or make mandatory?

        KoodiValue value = koodistoService.lookupKoodi(koodiUri, (koodiVersion != null ? koodiVersion.intValue() : 0));

        if (value != null) {
            writeLabel(value);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("no koodi value found, skipping. uri: " + koodiUri + ", version: " + koodiVersion);
            }
        }

    }

    private void writeLabel(KoodiValue value) throws SAXException {

        writeLabel(KoodiValue.LANG_FI, value.getMetaName(KoodiValue.LANG_FI));
        writeLabel(KoodiValue.LANG_SV, value.getMetaName(KoodiValue.LANG_SV));
        writeLabel(KoodiValue.LANG_EN, value.getMetaName(KoodiValue.LANG_EN));

    }

    private void writeLabel(String lang, String value) throws SAXException {

        if (value != null && !existingLabels.contains(lang)) {
            parent.writeStartElement(TAG_LABEL, TAG_LANG, lang);
            parent.writeCharacters(value);
            parent.writeEndElement(TAG_LABEL);
        } 

    }

}

