/*
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
package fi.vm.sade.tarjonta.ui.helper.conversion;

import com.google.common.base.Preconditions;
import fi.vm.sade.generic.ui.feature.UserFeature;
import fi.vm.sade.generic.ui.portlet.security.User;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.PainotettavaOppiaineTyyppi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import org.springframework.stereotype.Component;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.ui.enums.BasicLanguage;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils.convertTekstiToVM;
import static fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel.OPPIAINEET_MAX;

/**
 *
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 */
@Component
public class HakukohdeViewModelToDTOConverter {

    @Autowired(required = true)
    private OIDService oidService;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HakukohdeViewModelToDTOConverter.class);
    private static final String NUMBER_FORMAT = "#.##";

    public HakukohdeTyyppi convertHakukohdeViewModelToDTO(HakukohdeViewModel hakukohdevm) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setVersion(hakukohdevm.getVersion());
        User usr = UserFeature.get();
        hakukohde.setViimeisinPaivittajaOid(usr.getOid());

        if (hakukohdevm.getHakuaika() != null) {
            hakukohde.setSisaisetHakuajat(hakukohdevm.getHakuaika().getHakuaikaDto());
        }

        hakukohde.setAloituspaikat(hakukohdevm.getAloitusPaikat());
        hakukohde.setHakukelpoisuusVaatimukset(hakukohdevm.getHakukelpoisuusVaatimus());
        if (hakukohdevm.getHakukohdeNimi() != null) {
            hakukohde.setHakukohdeNimi(hakukohdevm.getHakukohdeNimi());
        } else {
            throw new RuntimeException("Hakukohde koodisto koodi cannot be null!");
        }

        hakukohde.setHakukohteenHakuOid(hakukohdevm.getHakuViewModel().getHakuOid());
        hakukohde.setHakukohteenTila(hakukohdevm.getTila());
        if (hakukohdevm.getOid() == null) {
            try {
                hakukohde.setOid(oidService.newOid(NodeClassCode.PALVELUT));
            } catch (ExceptionMessage ex) {
                LOG.warn("UNABLE TO GET OID : " + ex.toString());
            }
        } else {
            hakukohde.setOid(hakukohdevm.getOid());
        }
        hakukohde.setHakukohdeKoodistoNimi(hakukohdevm.getHakukohdeKoodistoNimi());
        hakukohde.getHakukohteenKoulutusOidit().addAll(hakukohdevm.getKomotoOids());
        hakukohde.setLisatiedot(convertTekstis(hakukohdevm.getLisatiedot()));
//        hakukohde.sey(convertTekstis(hakukohdevm.getValintaPerusteidenKuvaus()));
        hakukohde.setLiitteidenToimitusPvm(hakukohdevm.getLiitteidenToimitusPvm());
        try {
            hakukohde.setValinnanAloituspaikat(hakukohdevm.getValinnoissaKaytettavatPaikat());
        } catch (Exception exp) {
        }
        hakukohde.setSahkoinenToimitusOsoite(hakukohdevm.getLiitteidenSahkoinenToimitusOsoite());

        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdevm.isKaytaHaunPaattymisenAikaa());
        if (hakukohdevm.getOsoiteRivi1() != null) {
            OsoiteTyyppi osoite = new OsoiteTyyppi();
            osoite.setOsoiteRivi(hakukohdevm.getOsoiteRivi1());
            osoite.setLisaOsoiteRivi(hakukohdevm.getOsoiteRivi2());
            osoite.setPostinumero(hakukohdevm.getPostinumero());
            osoite.setPostitoimipaikka(hakukohdevm.getPostitoimipaikka());
            hakukohde.setLiitteidenToimitusOsoite(osoite);
        }

        //alin hyväksyttävä keskiarvo
        if (hakukohdevm.getAlinHyvaksyttavaKeskiarvo() != null
                && hakukohdevm.getAlinHyvaksyttavaKeskiarvo().trim().length() > 0) {
            hakukohde.setAlinHyvaksyttavaKeskiarvo(new BigDecimal(hakukohdevm.getAlinHyvaksyttavaKeskiarvo()));
        }

        //painotettavat oppiaineet
        for (PainotettavaOppiaineViewModel painotettava : hakukohdevm.getPainotettavat()) {
            if (painotettava.getOppiaine() != null && painotettava.getPainokerroin() != null) {
                final PainotettavaOppiaineTyyppi dto = new PainotettavaOppiaineTyyppi();
                dto.setOppiaine(painotettava.getOppiaine());
                dto.setPainokerroin(painotettava.getPainokerroin());
                dto.setPainotettavaOppiaineTunniste(painotettava.getPainotettavaOppiaineTunniste());
                if (painotettava.getVersion() != null) {
                    dto.setVersion(painotettava.getVersion());
                }
                hakukohde.getPainotettavatOppiaineet().add(dto);
            }
        }

        return hakukohde;
    }

    private HakuViewModel mapHakuNimi(MonikielinenTekstiTyyppi monikielinenTekstiTyyppi) {
        HakuViewModel haku = new HakuViewModel();

        if (monikielinenTekstiTyyppi != null) {

            for (MonikielinenTekstiTyyppi.Teksti teksti : monikielinenTekstiTyyppi.getTeksti()) {
                if (teksti.getKieliKoodi().trim().equalsIgnoreCase(BasicLanguage.FI.getLowercaseLanguageCode())) {
                    haku.setNimiFi(teksti.getValue());
                } else if (teksti.getKieliKoodi().trim().equalsIgnoreCase(BasicLanguage.EN.getLowercaseLanguageCode())) {
                    haku.setNimiEn(teksti.getValue());
                } else if (teksti.getKieliKoodi().trim().equalsIgnoreCase(BasicLanguage.SV.getLowercaseLanguageCode())) {
                    haku.setNimiSe(teksti.getValue());
                }
            }
        }

        return haku;
    }

    public HakukohdeViewModel convertDTOToHakukohdeViewMode(HakukohdeViewModel hakukohdeVM, HakukohdeTyyppi hakukohdeTyyppi) {
        Preconditions.checkNotNull(hakukohdeVM, "HakukohdeViewModel object cannot be null.");
        Preconditions.checkNotNull(hakukohdeTyyppi, "HakukohdeTyyppi object cannot be null.");
        hakukohdeVM.setVersion(hakukohdeTyyppi.getVersion());
        hakukohdeVM.setAloitusPaikat(hakukohdeTyyppi.getAloituspaikat());
        hakukohdeVM.setHakukelpoisuusVaatimus(hakukohdeTyyppi.getHakukelpoisuusVaatimukset());
        hakukohdeVM.setHakukohdeNimi(hakukohdeTyyppi.getHakukohdeNimi());
        hakukohdeVM.setTila(hakukohdeTyyppi.getHakukohteenTila());

        HakuViewModel haku = mapHakuNimi(hakukohdeTyyppi.getHakukohteenHaunNimi());
        haku.setHakuOid(hakukohdeTyyppi.getHakukohteenHakuOid());

        if (hakukohdeTyyppi.getSisaisetHakuajat() != null) {
            hakukohdeVM.setHakuaika(new HakuaikaViewModel(hakukohdeTyyppi.getSisaisetHakuajat()));
        }

        hakukohdeVM.setKaytaHaunPaattymisenAikaa(hakukohdeTyyppi.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeVM.setHakuViewModel(haku);
        hakukohdeVM.setHakukohdeKoodistoNimi(hakukohdeTyyppi.getHakukohdeKoodistoNimi());
        hakukohdeVM.setOid(hakukohdeTyyppi.getOid());
        hakukohdeVM.setKomotoOids(hakukohdeTyyppi.getHakukohteenKoulutusOidit());
        hakukohdeVM.getLisatiedot().addAll(convertTekstiToVM(hakukohdeTyyppi.getLisatiedot()));
//        hakukohdeVM.getValintaPerusteidenKuvaus().addAll(convertTekstiToVM(hakukohdeTyyppi.getValintaPerusteidenKuvaukset()));
        hakukohdeVM.setValinnoissaKaytettavatPaikat(hakukohdeTyyppi.getValinnanAloituspaikat());
        hakukohdeVM.setLiitteidenSahkoinenToimitusOsoite(hakukohdeTyyppi.getSahkoinenToimitusOsoite());
        hakukohdeVM.setLiitteidenToimitusPvm(hakukohdeTyyppi.getLiitteidenToimitusPvm());
        if (hakukohdeTyyppi.getLiitteidenToimitusOsoite() != null) {
            hakukohdeVM.setOsoiteRivi1(hakukohdeTyyppi.getLiitteidenToimitusOsoite().getOsoiteRivi());
            hakukohdeVM.setOsoiteRivi2(hakukohdeTyyppi.getLiitteidenToimitusOsoite().getLisaOsoiteRivi());
            hakukohdeVM.setPostinumero(hakukohdeTyyppi.getLiitteidenToimitusOsoite().getPostinumero());
            hakukohdeVM.setPostitoimipaikka(hakukohdeTyyppi.getLiitteidenToimitusOsoite().getPostitoimipaikka());
        }

        if (hakukohdeTyyppi.getViimeisinPaivittajaOid() != null) {
        }

        if (hakukohdeTyyppi.getViimeisinPaivittajaOid() != null) {
            hakukohdeVM.setViimeisinPaivittaja(hakukohdeTyyppi.getViimeisinPaivittajaOid());
        }
        if (hakukohdeTyyppi.getViimeisinPaivitysPvm() != null) {
            hakukohdeVM.setViimeisinPaivitysPvm(hakukohdeTyyppi.getViimeisinPaivitysPvm());
        }
        //painotettavat oppiaineet
        int visible = hakukohdeTyyppi.getPainotettavatOppiaineet() != null ? hakukohdeTyyppi.getPainotettavatOppiaineet().size() : 0;
        if (hakukohdeTyyppi.getPainotettavatOppiaineet() != null) {
            hakukohdeVM.getPainotettavat().clear();
            for (PainotettavaOppiaineTyyppi pot : hakukohdeTyyppi.getPainotettavatOppiaineet()) {
                PainotettavaOppiaineViewModel painotettava = new PainotettavaOppiaineViewModel(pot.getOppiaine(),
                        pot.getPainokerroin(), pot.getPainotettavaOppiaineTunniste(), pot.getVersion());
                hakukohdeVM.addPainotettavaOppiaine(painotettava);
            }
        }
        hakukohdeVM.addPainotettavaOppiainees( HakukohdeViewModel.OPPIAINEET_MAX - Math.min(HakukohdeViewModel.OPPIAINEET_MAX, visible));

        //alin hyväksyttava keskiarvo
        if (hakukohdeTyyppi.getAlinHyvaksyttavaKeskiarvo() != null) {
            hakukohdeVM.setAlinHyvaksyttavaKeskiarvo(new DecimalFormat(NUMBER_FORMAT).format(hakukohdeTyyppi.getAlinHyvaksyttavaKeskiarvo()));
        }

        return hakukohdeVM;
    }

    private MonikielinenTekstiTyyppi convertTekstis(List<KielikaannosViewModel> kaannokset) {
        MonikielinenTekstiTyyppi tekstis = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel kieli : kaannokset) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(kieli.getKielikoodi());
            teksti.setValue(kieli.getNimi());
            tekstis.getTeksti().add(teksti);
        }

        return tekstis;
    }

    /**
     * @return the oidService
     */
    public OIDService getOidService() {
        return oidService;
    }

    /**
     * @param oidService the oidService to set
     */
    public void setOidService(OIDService oidService) {
        this.oidService = oidService;
    }
}
