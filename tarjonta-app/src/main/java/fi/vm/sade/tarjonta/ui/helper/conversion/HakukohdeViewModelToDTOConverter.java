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

import static fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils.convertTekstiToVM;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.PainotettavaOppiaineTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.enums.BasicLanguage;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.LinkitettyTekstiModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;

/**
 *
 * @author Tuomas Katva
 * @author Timo Santasalo / Teknokala Ky
 */
@Component
public class HakukohdeViewModelToDTOConverter {

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private OIDService oidService;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HakukohdeViewModelToDTOConverter.class);
    private static final String NUMBER_FORMAT = "#.##";
    
    public HakukohdeTyyppi convertHakukohdeViewModelToDTO(HakukohdeViewModel hakukohdevm) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setVersion(hakukohdevm.getVersion());
        hakukohde.setViimeisinPaivittajaOid(SecurityContextHolder.getContext().getAuthentication().getName());

        if (hakukohdevm.isCustomHakuaikaEnabled()) {
            hakukohde.setSisaisetHakuajat(null);
            hakukohde.setHakuaikaAlkuPvm(hakukohdevm.getHakuaikaAlkuPvm());
            hakukohde.setHakuaikaLoppuPvm(hakukohdevm.getHakuaikaLoppuPvm());
        } else if (hakukohdevm.getHakuaika() != null) {
            hakukohde.setSisaisetHakuajat(hakukohdevm.getHakuaika().getHakuaikaDto());
            hakukohde.setHakuaikaAlkuPvm(null);
            hakukohde.setHakuaikaLoppuPvm(null);
        } else {
        	// hakuaikaa ei määritelty -> pitäisikö tästä huolestua??
            hakukohde.setSisaisetHakuajat(null);
            hakukohde.setHakuaikaAlkuPvm(null);
            hakukohde.setHakuaikaLoppuPvm(null);
        }
        
        if (hakukohdevm.getValintaPerusteidenKuvaus().getUri()!=null) {
        	hakukohde.setValintaperustekuvausKoodiUri(hakukohdevm.getValintaPerusteidenKuvaus().getUri());
        } else {
        	hakukohde.setValintaperustekuvausTeksti(convertTekstis(hakukohdevm.getValintaPerusteidenKuvaus().getKaannokset()));
        }
        
        if (hakukohdevm.getSoraKuvaus().getUri()!=null) {
        	hakukohde.setSoraKuvausKoodiUri(hakukohdevm.getSoraKuvaus().getUri());
        } else {
        	hakukohde.setSoraKuvausTeksti(convertTekstis(hakukohdevm.getSoraKuvaus().getKaannokset()));
        }


        hakukohde.setAloituspaikat(hakukohdevm.getAloitusPaikat());
        hakukohde.setHakukelpoisuusVaatimukset(hakukohdevm.getHakukelpoisuusVaatimus());
        KoulutusasteTyyppi hkKoulutusaste = hakukohdevm.getKoulutusasteTyyppi();
        hakukohde.setHakukohteenKoulutusaste(hkKoulutusaste);
        if (hkKoulutusaste.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {
            hakukohde.setHakukohdeNimi(hakukohdevm.getEditedHakukohdeNimi());
            hakukohde.setHakukohdeKoodistoNimi(hakukohdevm.getEditedHakukohdeNimi());
        } else if (hakukohdevm.getHakukohdeNimi() != null) {
            hakukohde.setHakukohdeNimi(hakukohdevm.getHakukohdeNimi());
            hakukohde.setHakukohdeKoodistoNimi(hakukohdevm.getHakukohdeKoodistoNimi());
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
        
        
        hakukohde.getHakukohteenKoulutusOidit().addAll(hakukohdevm.getKomotoOids());
        hakukohde.setLisatiedot(convertTekstis(hakukohdevm.getLisatiedot()));
//        hakukohde.sey(convertTekstis(hakukohdevm.getValintaPerusteidenKuvaus()));
        hakukohde.setLiitteidenToimitusPvm(hakukohdevm.getLiitteidenToimitusPvm());
        hakukohde.setValinnanAloituspaikat(hakukohdevm.getValinnoissaKaytettavatPaikat());
        

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
                if (teksti.getKieliKoodi().trim().contains(BasicLanguage.FI.getLowercaseLanguageCode())) {
                    haku.setMlNimiFi(teksti.getValue());
                } else if (teksti.getKieliKoodi().trim().contains(BasicLanguage.EN.getLowercaseLanguageCode())) {
                    haku.setMlNimiEn(teksti.getValue());
                } else if (teksti.getKieliKoodi().trim().contains(BasicLanguage.SV.getLowercaseLanguageCode())) {
                    haku.setMlNimiSv(teksti.getValue());
                }
            }
        }

        return haku;
    }

    public HakukohdeViewModel convertDTOToHakukohdeViewMode(HakukohdeViewModel hakukohdeVM, HakukohdeTyyppi hakukohdeTyyppi) {
        Preconditions.checkNotNull(hakukohdeVM, "HakukohdeViewModel object cannot be null.");
        Preconditions.checkNotNull(hakukohdeTyyppi, "HakukohdeTyyppi object cannot be null.");
        Preconditions.checkNotNull(hakukohdeTyyppi.getHakukohdeNimi(), "Hakukohde koodi URI cannot be null.");
        Preconditions.checkNotNull(hakukohdeTyyppi.getHakukohteenKoulutusaste(), "KoulutusasteTyyppi enum cannot be null.");
        hakukohdeVM.setKoulutusasteTyyppi(hakukohdeTyyppi.getHakukohteenKoulutusaste());
        
        

        hakukohdeVM.setVersion(hakukohdeTyyppi.getVersion());
        hakukohdeVM.setAloitusPaikat(hakukohdeTyyppi.getAloituspaikat());
        hakukohdeVM.setHakukelpoisuusVaatimus(hakukohdeTyyppi.getHakukelpoisuusVaatimukset());;
        hakukohdeVM.setTila(hakukohdeTyyppi.getHakukohteenTila());
        hakukohdeVM.setOpetusKielet(new TreeSet<String>(hakukohdeTyyppi.getOpetuskieliUris()));
        
        hakukohdeVM.setValintaPerusteidenKuvaus(new LinkitettyTekstiModel(hakukohdeTyyppi.getValintaperustekuvausKoodiUri(), convertTekstiToVM(hakukohdeTyyppi.getValintaperustekuvausTeksti())));
        hakukohdeVM.setSoraKuvaus(new LinkitettyTekstiModel(hakukohdeTyyppi.getSoraKuvausKoodiUri(), convertTekstiToVM(hakukohdeTyyppi.getSoraKuvausTeksti())));
        
        if (hakukohdeVM.getValintaPerusteidenKuvaus().getUri()!=null) {
        	hakukohdeTyyppi.setValintaperustekuvausKoodiUri(hakukohdeVM.getValintaPerusteidenKuvaus().getUri());
        	hakukohdeTyyppi.setValintaperustekuvausTeksti(null);
        } else {
        	hakukohdeTyyppi.setValintaperustekuvausKoodiUri(null);
        	hakukohdeTyyppi.setValintaperustekuvausTeksti(convertTekstis(hakukohdeVM.getValintaPerusteidenKuvaus().getKaannokset()));
        }
        
        if (hakukohdeVM.getSoraKuvaus().getUri()!=null) {
        	hakukohdeTyyppi.setSoraKuvausKoodiUri(hakukohdeVM.getSoraKuvaus().getUri());
        	hakukohdeTyyppi.setSoraKuvausTeksti(null);
        } else {
        	hakukohdeTyyppi.setSoraKuvausKoodiUri(null);
        	hakukohdeTyyppi.setSoraKuvausTeksti(convertTekstis(hakukohdeVM.getSoraKuvaus().getKaannokset()));
        }

        HakuViewModel haku = mapHakuNimi(hakukohdeTyyppi.getHakukohteenHaunNimi());
        haku.setHakutyyppi(hakukohdeTyyppi.getHakukohteenHakutyyppiUri());
        haku.setHakuOid(hakukohdeTyyppi.getHakukohteenHakuOid());
        //hakukohdeTyyppi.getHa

        if (hakukohdeTyyppi.getSisaisetHakuajat() != null) {
            hakukohdeVM.setHakuaika(new HakuaikaViewModel(hakukohdeTyyppi.getSisaisetHakuajat()));
            hakukohdeVM.setCustomHakuaikaEnabled(false);
        } //else {
        	hakukohdeVM.setHakuaikaAlkuPvm(hakukohdeTyyppi.getHakuaikaAlkuPvm());
        	hakukohdeVM.setHakuaikaLoppuPvm(hakukohdeTyyppi.getHakuaikaLoppuPvm());
        if (hakukohdeVM.getHakuaikaAlkuPvm() != null && hakukohdeVM.getHakuaikaLoppuPvm() != null) {
            hakukohdeVM.setCustomHakuaikaEnabled(true);
        }
        //}

        hakukohdeVM.setKaytaHaunPaattymisenAikaa(hakukohdeTyyppi.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeVM.setHakuViewModel(haku);

        if (hakukohdeTyyppi.getHakukohteenKoulutusaste().equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {
            hakukohdeVM.setEditedHakukohdeNimi(hakukohdeTyyppi.getHakukohdeNimi());
            hakukohdeVM.setHakukohdeKoodistoNimi(hakukohdeTyyppi.getHakukohdeKoodistoNimi());
        } else {        
            hakukohdeVM.setHakukohdeKoodistoNimi(hakukohdeTyyppi.getHakukohdeKoodistoNimi());
            hakukohdeVM.setSelectedHakukohdeNimi(hakukohdeNameUriModelFromKoodi(tarjontaKoodistoHelper.getKoodiByUri(hakukohdeTyyppi.getHakukohdeNimi())));
        }

        hakukohdeVM.setOid(hakukohdeTyyppi.getOid());
        hakukohdeVM.setKomotoOids(hakukohdeTyyppi.getHakukohteenKoulutusOidit());

        hakukohdeVM.getLisatiedot().clear();
        hakukohdeVM.getLisatiedot().addAll(convertTekstiToVM(hakukohdeTyyppi.getLisatiedot()));

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

        /*
         * only for lukio
         */
        if (hakukohdeTyyppi.getHakukohteenKoulutusaste().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            //painotettavat oppiaineet
            hakukohdeVM.getPainotettavat().clear(); //after the clear, we need to recreate form data binding 
            final int visible = hakukohdeTyyppi.getPainotettavatOppiaineet() != null ? hakukohdeTyyppi.getPainotettavatOppiaineet().size() : 0;

            if (hakukohdeTyyppi.getPainotettavatOppiaineet() != null && !hakukohdeTyyppi.getPainotettavatOppiaineet().isEmpty()) {
                for (PainotettavaOppiaineTyyppi pot : hakukohdeTyyppi.getPainotettavatOppiaineet()) {
                    PainotettavaOppiaineViewModel painotettava = new PainotettavaOppiaineViewModel(pot.getOppiaine(),
                            pot.getPainokerroin(), pot.getPainotettavaOppiaineTunniste(), pot.getVersion());
                    hakukohdeVM.addPainotettavaOppiaine(painotettava);
                }
            }
            hakukohdeVM.addPainotettavaOppiainees(HakukohdeViewModel.OPPIAINEET_MAX - Math.min(HakukohdeViewModel.OPPIAINEET_MAX, visible));
        }
        //alin hyväksyttava keskiarvo

        if (hakukohdeTyyppi.getAlinHyvaksyttavaKeskiarvo()
                != null) {
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

    public static HakukohdeNameUriModel hakukohdeNameUriModelFromKoodi(final KoodiType koodiType) {
        Preconditions.checkNotNull(koodiType, "Hakukohde KoodiType object not found?");

        HakukohdeNameUriModel hakukohdeNameUriModel = new HakukohdeNameUriModel();
        hakukohdeNameUriModel.setUriVersio(koodiType.getVersio());
        hakukohdeNameUriModel.setHakukohdeUri(koodiType.getKoodiUri());
        hakukohdeNameUriModel.setHakukohdeArvo(koodiType.getKoodiArvo());

        KoodiMetadataType meta = TarjontaUIHelper.getKoodiMetadataForLanguage(koodiType, I18N.getLocale());
        if (meta != null) {
            hakukohdeNameUriModel.setHakukohdeNimi(meta.getNimi());
        } else {
            //no text found for any language, so only way to show something is to show a koodiuri.
            hakukohdeNameUriModel.setHakukohdeNimi(TarjontaUIHelper.createVersionUri(koodiType.getKoodiUri(), koodiType.getVersio()));
        }

        return hakukohdeNameUriModel;
    }
}
