/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestConverters;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.v1.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmattikorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;

import java.util.*;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API V1 converters to/from model/domain.
 *
 * @author mlyly
 */
public class V1Converter {

    private static final Logger LOG = LoggerFactory.getLogger(V1Converter.class);
    // ----------------------------------------------------------------------
    // HAKU
    // ----------------------------------------------------------------------
    HakuDAO _hakuDao;

    public void setHakuDao(HakuDAO _hakuDao) {
        this._hakuDao = _hakuDao;
    }

    public HakuDAO getHakuDao() {
        if (_hakuDao == null) {
            throw new IllegalStateException("HAKUDAO == NULL");
        }
        return _hakuDao;
    }




    // ----------------------------------------------------------------------
    // HAKUKOHDE
    // ----------------------------------------------------------------------
    HakukohdeDAO _hakukohdeDao;

    public HakukohdeDAO getHakukohdeDao() {
        if (_hakukohdeDao == null) {
            throw new IllegalStateException("HAKUKOHDEDAO == NULL");
        }
        return _hakukohdeDao;
    }

    public void setHakukohdeDao(HakukohdeDAO _hakukohdeDao) {
        this._hakukohdeDao = _hakukohdeDao;
    }


    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public TarjontaKoodistoHelper getTarjontaKoodistoHelper() {
        return tarjontaKoodistoHelper;
    }

    public void setTarjontaKoodistoHelper(TarjontaKoodistoHelper tarjontaKoodistoHelper) {
        this.tarjontaKoodistoHelper = tarjontaKoodistoHelper;
    }


    /**
     * Convert domain Hakukohde to REST HakukohdeRDTO.
     *
     * @param hakukohde
     * @return
     */
    public HakukohdeRDTO toHakukohdeRDTO(Hakukohde hakukohde) {
        LOG.info("toHakukohdeRDTO({})", hakukohde);

        HakukohdeRDTO t = new HakukohdeRDTO();


        LOG.info("  -> result = {}", t);
        return t;
    }

    /**
     * Convert from REST HakukohdeRDTO to domain Hakukohde.
     *
     * @param hakukohde
     * @return
     */
    public Hakukohde toHakukohde(HakukohdeRDTO hakukohde) {
        LOG.info("toHakukohde({})", hakukohde);
        Hakukohde t = null;

        LOG.info("  -> result = {}", t);
        return t;
    }

    public Valintakoe toValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        LOG.debug("toValintakoe({})",valintakoeV1RDTO);
        Valintakoe valintakoe = convertValintakoeRDTOToValintakoe(valintakoeV1RDTO);
        LOG.debug("toValintakoe result ->  {}", valintakoe);
        return valintakoe;
    }

    public ValintakoeV1RDTO fromValintakoe(Valintakoe valintakoe) {
        LOG.debug("fromValintakoe({})",valintakoe);
        ValintakoeV1RDTO valintakoeV1RDTO = convertValintakoeToValintakoeV1RDTO(valintakoe);
        LOG.debug("fromValintakoe result -> {}",valintakoeV1RDTO);
        return valintakoeV1RDTO;

    }

    //------------------------------------
    //Hakukohde helper converters
    //------------------------------------
    private Valintakoe convertValintakoeRDTOToValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        Valintakoe valintakoe = new Valintakoe();

        if (valintakoeV1RDTO.getOid() != null) {
            try {
                valintakoe.setId(new Long(valintakoeV1RDTO.getOid()));
            } catch (Exception exp) {

            }
            valintakoe.setValintakoeNimi(valintakoeV1RDTO.getValintakoeNimi());
            valintakoe.setKieli(valintakoeV1RDTO.getKieliUri());
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();
            tekstiRDTOs.add(valintakoeV1RDTO.getValintakokeenKuvaus());
            valintakoe.setKuvaus(convertTekstiRDTOToMonikielinenTeksti(tekstiRDTOs));
            if (valintakoeV1RDTO.getValintakoeAjankohtas() != null) {
                valintakoe.getAjankohtas().addAll(convertAjankohtaRDTOToValintakoeAjankohta(valintakoeV1RDTO.getValintakoeAjankohtas()));
            }

        }

        return valintakoe;
    }


    private Set<ValintakoeAjankohta> convertAjankohtaRDTOToValintakoeAjankohta(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtaRDTOs) {
        Set<ValintakoeAjankohta> valintakoeAjankohtas = new HashSet<ValintakoeAjankohta>();

        for (ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO:valintakoeAjankohtaRDTOs) {
            ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();

            valintakoeAjankohta.setLisatietoja(valintakoeAjankohtaRDTO.getLisatiedot());
            valintakoeAjankohta.setAjankohdanOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(valintakoeAjankohtaRDTO.getOsoite()));
            valintakoeAjankohta.setAlkamisaika(valintakoeAjankohtaRDTO.getAlkaa());
            valintakoeAjankohta.setPaattymisaika(valintakoeAjankohtaRDTO.getLoppuu());
            valintakoeAjankohtas.add(valintakoeAjankohta);

        }

        return valintakoeAjankohtas;
    }

    private MonikielinenTeksti convertTekstiRDTOToMonikielinenTeksti(List<TekstiRDTO> tekstis) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (TekstiRDTO tekstiRDTO:tekstis){
            monikielinenTeksti.addTekstiKaannos(tekstiRDTO.getUri(),tekstiRDTO.getTeksti());
            LOG.debug("MONIKIELINEN TEKSTI : {}", tekstiRDTO.getTeksti());
        }

        return monikielinenTeksti;
    }


    private ValintakoeV1RDTO convertValintakoeToValintakoeV1RDTO(Valintakoe valintakoe) {
        ValintakoeV1RDTO valintakoeV1RDTO = new ValintakoeV1RDTO();

        valintakoeV1RDTO.setKieliUri(valintakoe.getKieli());
        valintakoeV1RDTO.setValintakoeNimi(valintakoe.getValintakoeNimi());
        List<TekstiRDTO> lisatiedot = convertMonikielinenTekstiToTekstiDTOs(valintakoe.getKuvaus());
        if (lisatiedot != null && lisatiedot.size() > 0) {
            valintakoeV1RDTO.setValintakokeenKuvaus(lisatiedot.get(0));
        }

        if (valintakoe.getAjankohtas() != null) {
            for (ValintakoeAjankohta valintakoeAjankohta : valintakoe.getAjankohtas()) {
                valintakoeV1RDTO.getValintakoeAjankohtas().add(convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(valintakoeAjankohta));
            }
        }

        return valintakoeV1RDTO;
    }


    private ValintakoeAjankohtaRDTO convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(ValintakoeAjankohta valintakoeAjankohta) {

        ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO = new ValintakoeAjankohtaRDTO();

        valintakoeAjankohtaRDTO.setOid(valintakoeAjankohta.getId().toString());
        valintakoeAjankohtaRDTO.setAlkaa(valintakoeAjankohta.getAlkamisaika());
        valintakoeAjankohtaRDTO.setLoppuu(valintakoeAjankohta.getPaattymisaika());
        valintakoeAjankohtaRDTO.setLisatiedot(valintakoeAjankohta.getLisatietoja());
        valintakoeAjankohtaRDTO.setOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(valintakoeAjankohta.getAjankohdanOsoite()));

        return valintakoeAjankohtaRDTO;

    }

    private List<TekstiRDTO> convertMonikielinenTekstiToTekstiDTOs(MonikielinenTeksti monikielinenTeksti) {

        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();

            for(TekstiKaannos tekstiKaannos:monikielinenTeksti.getTekstis()) {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.setUri(checkAndRemoveForEmbeddedVersionInUri(tekstiKaannos.getKieliKoodi()));
                tekstiRDTO.setTeksti(tekstiKaannos.getArvo());
                try {
                    KoodiType koodiType = getTarjontaKoodistoHelper().getKoodiByUri(tekstiKaannos.getKieliKoodi());
                    //TODO: should it return nimi instead ? But with what language ?
                    tekstiRDTO.setArvo(koodiType.getKoodiArvo());
                    tekstiRDTO.setVersio(koodiType.getVersio());
                    if (koodiType.getMetadata() != null) {
                        for (KoodiMetadataType meta:koodiType.getMetadata()) {
                            //By default set default name finnish
                            if (meta.getKieli().equals(KieliType.FI)) {
                                tekstiRDTO.setNimi(meta.getNimi());
                            }
                            tekstiRDTO.addKieliAndNimi(meta.getKieli().value(),meta.getNimi());
                        }
                    }


                } catch (Exception exp) {

                }
                tekstiRDTOs.add(tekstiRDTO);

            }

            return tekstiRDTOs;
        }  else {
            return null;
        }

    }

    private String checkAndRemoveForEmbeddedVersionInUri(String uri) {
        if (uri != null) {
            if (uri.contains("#")) {
                StringTokenizer st = new StringTokenizer(uri,"#");
                return st.nextToken();
            } else {
                return uri;
            }
        } else {
            return null;
        }
    }


    // ----------------------------------------------------------------------
    // KOULUTUS
    // ----------------------------------------------------------------------

    KoulutusmoduuliDAO _komoDao;
    KoulutusmoduuliToteutusDAO _komotoDao;

    public KoulutusmoduuliDAO getKomoDao() {
        if (_komoDao == null) {
            throw new IllegalStateException("KOMODAO == NULL");
        }
        return _komoDao;
    }

    public KoulutusmoduuliToteutusDAO getKomotoDao() {
        if (_komotoDao == null) {
            throw new IllegalStateException("KOMOTODAO == NULL");
        }
        return _komotoDao;
    }

    public void setKomoDao(KoulutusmoduuliDAO _komoDao) {
        this._komoDao = _komoDao;
    }

    public void setKomotoDao(KoulutusmoduuliToteutusDAO _komotoDao) {
        this._komotoDao = _komotoDao;
    }

    public KoulutusRDTO fromKomotoToKoulutusRDTO(KoulutusmoduuliToteutus komoto) {
        LOG.warn("fromKomotoToKoulutusRDTO({}) -- ONLY PARTIALLY IMPLEMENTED!", komoto);

        // TODO implement me!

        KoulutusRDTO t = null;

        if (komoto != null) {
            // TODO TYYPPI!?
            KoulutusAmmattikorkeakouluRDTO k = new KoulutusAmmattikorkeakouluRDTO();

            k.setCreated(komoto.getUpdated());
            k.setCreatedBy(komoto.getLastUpdatedByOid());
            k.setModified(komoto.getUpdated());
            k.setModifiedBy(komoto.getLastUpdatedByOid());

            Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

            k.setKomotoOid(komoto.getOid());
            k.setKomoOid(komo.getOid());


            t = k;
        }

        return t;
    }

    public KoulutusRDTO fromKomotoToKoulutusRDTO(String oid) {
        return fromKomotoToKoulutusRDTO(getKomotoDao().findByOid(oid));
    }


}