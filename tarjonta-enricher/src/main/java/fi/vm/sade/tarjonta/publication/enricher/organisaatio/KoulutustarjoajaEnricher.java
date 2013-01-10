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
package fi.vm.sade.tarjonta.publication.enricher.organisaatio;

import fi.vm.sade.tarjoaja.service.types.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import fi.vm.sade.tarjonta.publication.enricher.ElementEnricher;
import fi.vm.sade.tarjonta.publication.types.DescriptionType;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutustarjoajaEnricher extends ElementEnricher {

    private KoulutustarjoajaLookupService tarjoajaService;
    private static final Logger log = LoggerFactory.getLogger(KoulutustarjoajaEnricher.class);
    private String currentTag;
    private String currentOid;

    @Override
    public void reset() {
        currentTag = null;
        currentOid = null;
    }

    public void setTarjoajaService(KoulutustarjoajaLookupService tarjoajaService) {
        this.tarjoajaService = tarjoajaService;
    }

    @Override
    public int endElement(String localName) throws SAXException {

        if (mappedElementName.equals(localName)) {
            maybeWriteElements();
        }

        currentTag = null;

        return super.endElement(localName);

    }

    @Override
    public int characters(char[] characters, int start, int length) throws SAXException {

        if (Tags.OID_REF.equals(currentTag)) {
            currentOid = charsToString(characters, start, length);
        }

        return super.characters(characters, start, length);

    }

    @Override
    public int startElement(String localName, Attributes attributes) throws SAXException {

        currentTag = localName;
        return super.startElement(localName, attributes);

    }

    private void maybeWriteElements() throws SAXException {

        if (currentOid == null) {
            log.warn("no oid defined, skipping element");
            return;
        }

        try {
            log.debug("search by provider oid: '{}'", currentOid);
            KoulutustarjoajaTyyppi tarjoaja = tarjoajaService.lookupKoulutustarjoajaByOrganisaatioOid(currentOid);

            if (tarjoaja == null) {
                log.error("no provider found, skipping element, oid: " + currentOid);
            } else {
                writeName(tarjoaja);
                writeDescriptions(tarjoaja);
            }
        } catch (Exception e) {
            log.error("error while looking up provider, skipping element, oid: " + currentOid, e.getMessage());
        }
    }

    private void writeName(KoulutustarjoajaTyyppi tarjoaja) throws SAXException {

        for (KielistettyTekstiTyyppi nimi : tarjoaja.getNimi()) {
            writeName(nimi.getLang(), nimi.getValue());
        }

    }

    private void writeName(String lang, String value) throws SAXException {
        log.debug("provider name : " + value);
        parent.writeStartElement(Tags.NAME, Tags.LANG, lang);
        parent.writeCharacters(value);
        parent.writeEndElement(Tags.NAME);

    }

    private void writeDescriptions(KoulutustarjoajaTyyppi tarjoaja) throws SAXException {

        for (MetatietoTyyppi m : tarjoaja.getMetatieto()) {
            writeDescription(m);
        }

    }

    private void writeDescription(MetatietoTyyppi meta) throws SAXException {

        final String key = translateMetaname(meta.getAvain());

        parent.writeStartElement(Tags.DESCRIPTION, Tags.TYPE, key);

        for (MetatietoArvoTyyppi localizedData : meta.getArvos()) {
            parent.writeStartElement(Tags.TEXT, Tags.LANG, localizedData.getKieliKoodi());
            parent.writeCharacters(localizedData.getArvo());
            parent.writeEndElement(Tags.TEXT);
        }

        parent.writeEndElement(Tags.DESCRIPTION);

    }

    /**
     * Translates enumeration used in Tarjoaja -service to constants know by the
     * Publication API. If there is no proper mapping, input parameters value as
     * string is returned instead.
     */
    private String translateMetaname(MetatietoAvainTyyppi metaKey) {

        if (MetatietoAvainTyyppi.ESTEETTOMYYS_PALVELUT.equals(metaKey)) {
            return DescriptionType.FACILITIES_FOR_STUDENTS_WITH_SPECIAL_NEEDS.value();
        } else if (MetatietoAvainTyyppi.KUSTANNUKSET.equals(metaKey)) {
            return DescriptionType.COST_OF_LIVING.value();
        } else if (MetatietoAvainTyyppi.OPPIMISYMPARISTOT.equals(metaKey)) {
            return DescriptionType.STUDY_FACILITIES.value();
        } else if (MetatietoAvainTyyppi.RUOKAILU.equals(metaKey)) {
            return DescriptionType.MEALS.value();
        } else if (MetatietoAvainTyyppi.TERVEYDENHUOLTO.equals(metaKey)) {
            return DescriptionType.MEDICAL_FACILITIES.value();
        }

        return metaKey.value();

    }

    private interface Tags {

        String OID_REF = "OidRef";
        String DESCRIPTION = "Description";
        String TYPE = "type";
        String TEXT = "Text";
        String LANG = "lang";
        String NAME = "Name";
    }
}
