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
package fi.vm.sade.tarjonta.publication.enricher.ext;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.KoodiValue;
import fi.vm.sade.tarjonta.publication.utils.StringUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles elements that are of type:
 * {http://publication.tarjonta.sade.vm.fi/types}/CodeValueType by injecting any
 * missing labels by koodi value - if any. Any existing labels are left intact.
 *
 * @author Jukka Raanamo
 */
public class KoodistoCodeValueEnricher extends AbstractKoodistoEnricher {

    private static final Logger log = LoggerFactory.getLogger(KoodistoCodeValueEnricher.class);
    protected static final String TAG_CODE = "Code";
    protected static final String ATTRIBUTE_SCHEME = "scheme";
    protected static final String ATTRIBUTE_VERSION = "version";
    protected static final String TAG_LABEL = "Label";
    protected static final String ATTRIBUTE_LANG = "lang";
    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String SCHEME_TYPE_KOODISTO = "Koodisto";
    protected String koodiUri;
    protected Integer koodiVersion;
    private Set<String> existingLabels = new HashSet<String>();
    private Attributes attributes;
    private String value;
    protected String scheme;

    @Override
    public void reset() {
        attributes = null;
        koodiUri = null;
        koodiVersion = null;
        scheme = null;
        value = null;
        existingLabels.clear();
    }

    @Override
    public int startElement(String uri, String localName, Attributes attributes)
            throws SAXException {

        scheme = attributes.getValue(EMPTY_STRING, ATTRIBUTE_SCHEME);
        return startElementHandler(TAG_CODE, localName, attributes);
    }

    protected int startElementHandler(final String tag, final String localName, final Attributes attributes) throws SAXException {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes object cannot be null.");
        }

        this.attributes = attributes;

        if (tag.equals(localName)) {
            if (!SCHEME_TYPE_KOODISTO.equals(scheme)) {
                // this is not Koodisto based, nothing we can do about it
                if (log.isDebugEnabled()) {
                    log.warn("Tag data '{}' not Koodisto based, skipping scheme: '{}'", localName, scheme);
                }

                return WRITE_AND_EXIT;
            }

            findKoodiAttributes();

        } else if (TAG_LABEL.equals(localName)) {
            // label already exist, remember
            final String lang = attributes.getValue(ATTRIBUTE_LANG);
            if (StringUtils.notEmpty(lang)) {
                existingLabels.add(lang);
            }
        }

        return WRITE_AND_CONTINUE;
    }

    protected void findKoodiAttributes() {
        koodiVersion = koodiVersion(attributes);
        koodiUri = koodiUri(attributes);
    }

    protected Integer koodiVersion(final Attributes attributes) {
        final String version = attributes.getValue(EMPTY_STRING, ATTRIBUTE_VERSION);
        if (version != null && !version.isEmpty()) {
            try {
                return Integer.parseInt(version);
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    protected String koodiUri(final Attributes attributes) {
        if (attributes != null) {
            return attributes.getValue(EMPTY_STRING, ATTRIBUTE_URI);
        }
        return null;
    }

    @Override
    public int endElement(String uri, String localName) throws SAXException {
        KoodiValue koodistoKoodi = getKoodistoKoodi(localName);
        if (TAG_CODE.equals(localName) && koodistoKoodi != null) {
            parent.writeCharacters(koodistoKoodi.getValue());
        }

        if (mappedElementName.equals(localName)) {
            maybeWriteLabels(uri, localName, koodistoKoodi);
            return WRITE_AND_EXIT;
        } else {
            return WRITE_AND_CONTINUE;
        }
    }

    @Override
    public int characters(char[] characters, int start, int length) throws SAXException {
        value = charsToString(characters, start, length);

        return WRITE_AND_CONTINUE;
    }

    /**
     * Search koodisto koodis by code uri and koodi version.
     *
     * @param localName
     * @return
     */
    protected KoodiValue getKoodistoKoodi(String localName) {
        if (koodiUri == null || koodiVersion == null) {
            log.warn("no koodi value found for element named '{}', skipping. uri: '{}", localName, koodiUri + "#" + koodiVersion);
            return null;
        }
        return lookupKoodi(koodiUri, koodiVersion);
    }

    protected void maybeWriteLabels(final String uri, final String localName, KoodiValue koodistoKoodi) throws SAXException {
        if (koodistoKoodi != null) {
            writeLabel(uri, koodistoKoodi);
        }
    }

    private void writeLabel(final String uri, KoodiValue value) throws SAXException {
        writeLabel(uri, KoodiValue.LANG_FI, value.getMetaName(KoodiValue.LANG_FI));
        writeLabel(uri, KoodiValue.LANG_SV, value.getMetaName(KoodiValue.LANG_SV));
        writeLabel(uri, KoodiValue.LANG_EN, value.getMetaName(KoodiValue.LANG_EN));
    }

    private void writeLabel(final String uri, String lang, String value) throws SAXException {

        if (value != null && !existingLabels.contains(lang)) {
            log.debug(uri);
            parent.writeStartElement(uri, TAG_LABEL, ATTRIBUTE_LANG, lang);
            parent.writeCharacters(value);
            parent.writeEndElement(uri, TAG_LABEL);
        }

    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    protected Set<String> getExistingLabels() {
        return this.existingLabels;
    }
}
