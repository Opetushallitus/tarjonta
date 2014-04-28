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
package fi.vm.sade.tarjonta.service.impl.conversion;

import static fi.vm.sade.tarjonta.service.business.impl.EntityUtils.KoulutusTyyppiStrToKoulutusAsteTyyppi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.PainotettavaOppiaine;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.enums.ModuleRowType;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.PainotettavaOppiaineTyyppi;

/**
 *
 * @author Tuomas Katva
 */
public class HakukohdeToDTOConverter extends AbstractFromDomainConverter<Hakukohde, HakukohdeTyyppi> {

    @Override
    public HakukohdeTyyppi convert(Hakukohde s) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setVersion(s.getVersion());
        hakukohde.setAloituspaikat(s.getAloituspaikatLkm());
       // hakukohde.setHakukelpoisuusVaatimukset();
        hakukohde.setHakukohdeNimi(s.getHakukohdeNimi());
        hakukohde.setHakukohteenHakuOid(s.getHaku().getOid());
        hakukohde.setHakukohteenTila(EntityUtils.convertTila(s.getTila()));
        hakukohde.setOid(s.getOid());
        hakukohde.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        hakukohde.setLisatiedot(EntityUtils.copyFields(s.getLisatiedot()));
        hakukohde.setHakuaikaAlkuPvm(s.getHakuaikaAlkuPvm());
        hakukohde.setHakuaikaLoppuPvm(s.getHakuaikaLoppuPvm());
        hakukohde.setKaksoisTutkinto(s.isKaksoisTutkinto());

        Set<String> okielet = new TreeSet<String>();
        for (KoulutusmoduuliToteutus komoto : s.getKoulutusmoduuliToteutuses()) {
        	for (KoodistoUri ku : komoto.getOpetuskielis()) {
        		okielet.add(ku.getKoodiUri()); // versio?
        	}
        }

        hakukohde.getOpetuskieliUris().addAll(okielet);
        hakukohde.setHakukohteenHakutyyppiUri(s.getHaku().getHakutyyppiUri());
        

        if (s.getHakuaika() == null && s.getHaku().getHakuaikas().size() == 1) {
            // jos hakuaikaa ei valittu ja vain yksi on tarjolla, näytetään se
            hakukohde.setSisaisetHakuajat(CommonToDTOConverter.convertHakuaikaToSisaisetHakuAjat(s.getHaku().getHakuaikas().iterator().next()));
        } else {
            hakukohde.setSisaisetHakuajat(CommonToDTOConverter.convertHakuaikaToSisaisetHakuAjat(s.getHakuaika()));
        }
        
        //TODO: hakukohde.setValintaPerusteidenKuvaukset(null);
        hakukohde.getHakukohteenKoulutusOidit().addAll(convertKoulutukses(s.getKoulutusmoduuliToteutuses()));

        if (s.getKoulutusmoduuliToteutuses() != null && !s.getKoulutusmoduuliToteutuses().isEmpty()) {
            ModuleRowType rowType = s.getKoulutusmoduuliToteutuses().iterator().next().getKoulutusmoduuli().getRowType();
            hakukohde.setHakukohteenKoulutusaste(rowType.getKoulutusasteTyyppi());
        }

        hakukohde.setKaytetaanHaunPaattymisenAikaa(s.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setValinnanAloituspaikat(s.getValintojenAloituspaikatLkm());
        hakukohde.setSahkoinenToimitusOsoite(s.getSahkoinenToimitusOsoite());
        hakukohde.setLiitteidenToimitusPvm(s.getLiitteidenToimitusPvm());

        // sora- ja vape-kuvaus: varmistetaan, että joko url, teksti tai molemmat ovat null
        hakukohde.setSoraKuvausKoodiUri(s.getSoraKuvausKoodiUri());
        hakukohde.setSoraKuvausTeksti(s.getSoraKuvausKoodiUri()!=null ? null :
        	CommonToDTOConverter.convertMonikielinenTekstiToTekstiTyyppi(s.getSoraKuvaus()) );
        hakukohde.setValintaperustekuvausKoodiUri(s.getValintaperustekuvausKoodiUri());
        hakukohde.setValintaperustekuvausTeksti(s.getValintaperustekuvausKoodiUri()!=null ? null :
        	CommonToDTOConverter.convertMonikielinenTekstiToTekstiTyyppi(s.getValintaperusteKuvaus()) );

        if (s.getLiitteidenToimitusOsoite() != null) {
            hakukohde.setLiitteidenToimitusOsoite(osoiteTyyppiFromOsoite(s.getLiitteidenToimitusOsoite()));
        }
        if (s.getAlinHyvaksyttavaKeskiarvo() != null) {
            hakukohde.setAlinHyvaksyttavaKeskiarvo(new BigDecimal(s.getAlinHyvaksyttavaKeskiarvo()));
        }
        if (s.getPainotettavatOppiaineet() != null) {
            hakukohde.getPainotettavatOppiaineet().addAll(convertPainotettavatOppiaineet(s.getPainotettavatOppiaineet()));
        }

        if (s.getLastUpdatedByOid() != null) {
            hakukohde.setViimeisinPaivittajaOid(s.getLastUpdatedByOid());
        }
        if (s.getLastUpdateDate() != null) {
            hakukohde.setViimeisinPaivitysPvm(s.getLastUpdateDate());
        }

        return hakukohde;
    }

    private List<PainotettavaOppiaineTyyppi> convertPainotettavatOppiaineet(Set<PainotettavaOppiaine> oppiaineet) {
        List<PainotettavaOppiaineTyyppi> painotettavatOppiaineet = new ArrayList<PainotettavaOppiaineTyyppi>();

        for (PainotettavaOppiaine oppiaine : oppiaineet) {
            PainotettavaOppiaineTyyppi painotettavaOppiaine = new PainotettavaOppiaineTyyppi();
            painotettavaOppiaine.setOppiaine(oppiaine.getOppiaine());
            painotettavaOppiaine.setPainokerroin(oppiaine.getPainokerroin().doubleValue());
            painotettavaOppiaine.setPainotettavaOppiaineTunniste(oppiaine.getId().toString());
            painotettavaOppiaine.setVersion(oppiaine.getVersion());
            painotettavatOppiaineet.add(painotettavaOppiaine);
        }

        return painotettavatOppiaineet;
    }

    private List<String> convertKoulutukses(Set<KoulutusmoduuliToteutus> komotos) {
        List<String> komotoOids = new ArrayList<String>();

        for (KoulutusmoduuliToteutus komoto : komotos) {
            komotoOids.add(komoto.getOid());
        }

        return komotoOids;
    }

    private OsoiteTyyppi osoiteTyyppiFromOsoite(Osoite osoite) {
        OsoiteTyyppi osoiteTyyppi = new OsoiteTyyppi();

        osoiteTyyppi.setOsoiteRivi(osoite.getOsoiterivi1());
        osoiteTyyppi.setLisaOsoiteRivi(osoite.getOsoiterivi2());
        osoiteTyyppi.setPostinumero(osoite.getPostinumero());
        osoiteTyyppi.setPostitoimipaikka(osoite.getPostitoimipaikka());

        return osoiteTyyppi;
    }
}
