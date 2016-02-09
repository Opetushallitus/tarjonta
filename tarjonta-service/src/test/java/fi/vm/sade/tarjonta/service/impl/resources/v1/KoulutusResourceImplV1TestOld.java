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

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Oppiaine;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplV1TestOld extends KoulutusBase {

    @Before
    public void setUp() throws OIDCreationException {
        reload();
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        /*
         * KOMO data fields:
         */
        teksti(dto.getKoulutusohjelma(), KOULUTUSOHJELMA, URI_KIELI_FI);
        dto.getKoulutusohjelma().getTekstis().put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setKoulutustyyppi(toKoodiUri(KOULUTUSTYYPPI));

        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setHintaString("1.11");
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());

        koodiUrisMap(dto.getTutkintonimikes(), URI_KIELI_FI, MAP_TUTKINTONIMIKE);
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, MAP_OPETUSAIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        koodiUrisMap(dto.getAmmattinimikkeet(), URI_KIELI_FI, (MAP_AMMATTINIMIKE));
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, MAP_POHJAKOULUTUS);

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        dto.setOppiaineet(getOppiaineet());

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(3);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /* 
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity 
         */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO_TYYPPI);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        /* 2nd round, convert to dto */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO_TYYPPI);
        expectMetaMapUris(MAP_AMMATTINIMIKE);
        expectHierarchy();

        //  expectKoulutusohjelmaUris(KOULUTUSOHELMA);

        /* REPLAY */
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        replay(koulutusSisaltyvyysDAO);
        /*
         * INSERT KORKEAKOULU TO DB
         */
        ResultV1RDTO<KoulutusV1RDTO> v = instance.postKoulutus(dto);
        assertEquals("Validation errors", true, v.getErrors() == null || v.getErrors().isEmpty());

        /*
         * LOAD KORKEAKOULU DTO FROM DB
         */
        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, false, "FI");
        KoulutusKorkeakouluV1RDTO result1 = (KoulutusKorkeakouluV1RDTO) result.getResult();
        assertLoadData(result1);

        verify(organisaatioServiceMock);
    }

    private OppiaineV1RDTO getOppiaine(String oppiaine, String kieliKoodi) {
        OppiaineV1RDTO oppiaineDto = new OppiaineV1RDTO();
        oppiaineDto.setOppiaine(oppiaine);
        oppiaineDto.setKieliKoodi(kieliKoodi);
        return oppiaineDto;
    }

    private Set<OppiaineV1RDTO> getOppiaineet() {
        Set<OppiaineV1RDTO> oppiaineet = new HashSet<OppiaineV1RDTO>();

        oppiaineet.add(getOppiaine("biologia", "kieli_fi"));
        oppiaineet.add(getOppiaine("gymnastik", "kieli_sv"));

        return oppiaineet;
    }

    private void expectHierarchy() {
        expect(koulutusSisaltyvyysDAO.getParents("komo_oid")).andReturn(new ArrayList<String>()).atLeastOnce();
        expect(koulutusSisaltyvyysDAO.getChildren("komo_oid")).andReturn(new ArrayList<String>()).atLeastOnce();
    }

    private void assertLoadData(final KoulutusKorkeakouluV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());

        final String key = URI_KIELI_FI + "_uri";

        assertNotNull(KOULUTUSOHJELMA, result.getKoulutusohjelma().getTekstis().get(key));
        assertEqualDtoKoodi(KOULUTUSASTE, result.getKoulutusaste());
        assertEqualDtoKoodi(KOULUTUSALA, result.getKoulutusala());
        assertEqualDtoKoodi(OPINTOALA, result.getOpintoala());
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(KOULUTUSKOODI, result.getKoulutuskoodi());
        assertEqualDtoKoodi(LAAJUUSARVO, result.getOpintojenLaajuusarvo());
        assertEqualDtoKoodi(LAAJUUSYKSIKKO, result.getOpintojenLaajuusyksikko());
        assertEqualDtoKoodi(TUTKINTO, result.getTutkinto());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO, result.getKoulutusmoduuliTyyppi());
        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals("1.11", result.getHintaString());
        assertEquals(Boolean.TRUE, result.getOpintojenMaksullisuus());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEqualDtoKoodi(KAUSI_KOODI_URI, result.getKoulutuksenAlkamiskausi(), true);
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());

        assertEqualMetaDto(MAP_OPETUSAIHEES, result.getAihees());
        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(MAP_POHJAKOULUTUS, result.getPohjakoulutusvaatimukset());
        assertEqualMetaDto(MAP_AMMATTINIMIKE, result.getAmmattinimikkeet());
        assertEqualMetaDto(MAP_TUTKINTONIMIKE, result.getTutkintonimikes());

        assertEquals(SUUNNITELTU_KESTO_VALUE, result.getSuunniteltuKestoArvo());
        assertEquals(SUUNNITELTU_KESTO_TYYPPI + "_uri", result.getSuunniteltuKestoTyyppi().getUri());
        assertEquals(new Integer(1), result.getSuunniteltuKestoTyyppi().getVersio());
        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());
        assertEquals(USER_OID, result.getModifiedBy());

        Set<OppiaineV1RDTO> oppiaineet = result.getOppiaineet();
        assertEquals(getOppiaineet().size(), oppiaineet.size());

        int oppiaineMatchCount = 0;
        for (OppiaineV1RDTO oppiaine : getOppiaineet()) {
            for (OppiaineV1RDTO savedOppiaine : oppiaineet) {
                if (oppiaine.getKieliKoodi().equals(savedOppiaine.getKieliKoodi())
                        && oppiaine.getOppiaine().equals(savedOppiaine.getOppiaine())) {
                    oppiaineMatchCount ++;
                    break;
                }
            }
        }
        assertEquals(getOppiaineet().size(), oppiaineMatchCount);
    }


    /**
     * Tests creating and loading tjkk korkeakouluopintokokonaisuus.
     * @throws ExceptionMessage
     */
    @Test
    public void testCreateAndLoadKorkeaKouluOpintokokonaisuus() throws ExceptionMessage {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        /*
         * KOMO data fields:
         */
        teksti(dto.getKoulutusohjelma(), KOULUTUSOHJELMA, URI_KIELI_FI);
        dto.getKoulutusohjelma().getTekstis().put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISATION_OID);
        dto.setKoulutustyyppi(toKoodiUri(KOULUTUSTYYPPI));

        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
        dto.setTunniste(TUNNISTE);
        dto.setHintaString("1.11");
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        HashSet<Date> alkamisPvms = new HashSet<Date>();
        alkamisPvms.add(DATE.toDate());
        dto.setKoulutuksenAlkamisPvms(alkamisPvms);
        dto.setKoulutuksenLoppumisPvm(DATE.toDate());

        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, MAP_OPETUSAIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, MAP_POHJAKOULUTUS);

        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(3);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /*
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity
         */
        expectKausi();
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);

        /* 2nd round, convert to dto */
        expectKausi();
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(EQF);
        expectMetaUri(KOULUTUSTYYPPI);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);

        expectHierarchy();

        //  expectKoulutusohjelmaUris(KOULUTUSOHELMA);

        /* REPLAY */
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        replay(koulutusSisaltyvyysDAO);
        /*
         * INSERT KORKEAKOULU TO DB
         */
        ResultV1RDTO<KoulutusV1RDTO> v = instance.postKoulutus(dto);
        assertEquals("Validation errors", true, v.getErrors() == null || v.getErrors().isEmpty());

        /*
         * LOAD KORKEAKOULU DTO FROM DB
         */
        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, false, "FI");
        KorkeakouluOpintoV1RDTO result1 = (KorkeakouluOpintoV1RDTO) result.getResult();
        assertLoadKorkeakouluOpintokokonaisuus(result1);

        verify(organisaatioServiceMock);
    }

    private void assertLoadKorkeakouluOpintokokonaisuus(final KorkeakouluOpintoV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(ORGANISATION_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());

        final String key = URI_KIELI_FI + "_uri";

        assertNotNull(KOULUTUSOHJELMA, result.getKoulutusohjelma().getTekstis().get(key));
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(LAAJUUSARVO, result.getOpintojenLaajuusarvo());
        assertEqualDtoKoodi(LAAJUUSYKSIKKO, result.getOpintojenLaajuusyksikko());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS, result.getKoulutusmoduuliTyyppi());
        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals("1.11", result.getHintaString());
        assertEquals(Boolean.TRUE, result.getOpintojenMaksullisuus());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());
        assertEquals(DATE.toDate(), result.getKoulutuksenLoppumisPvm());

        assertEqualMetaDto(MAP_OPETUSAIHEES, result.getAihees());
        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(MAP_POHJAKOULUTUS, result.getPohjakoulutusvaatimukset());

        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());
        assertEquals(USER_OID, result.getModifiedBy());
    }

    public Oppiaine getOppiaineEntity(String oppiaine, String kieliKoodi) {
        Oppiaine oppiaineEntity = new Oppiaine();
        oppiaineEntity.setOppiaine(oppiaine);
        oppiaineEntity.setKieliKoodi(kieliKoodi);
        return oppiaineEntity;
    }

    @Test
    public void testGetOppiaineet() {
        OppiaineV1RDTO dto;
        ResultV1RDTO<Set<OppiaineV1RDTO>> v;
        Set<OppiaineV1RDTO> res;

        oppiaineDAO.insert(getOppiaineEntity("matematiikka", "kieli_fi"));
        oppiaineDAO.insert(getOppiaineEntity("historia", "kieli_fi"));
        oppiaineDAO.insert(getOppiaineEntity("geologi", "kieli_sv"));

        v = instance.getOppiaineet("mat", "kieli_fi");
        res = v.getResult();
        assertEquals(1, res.size());
        dto = res.iterator().next();
        assertEquals("matematiikka", dto.getOppiaine());
        assertEquals("kieli_fi", dto.getKieliKoodi());

        v = instance.getOppiaineet("oria", "kieli_fi");
        res = v.getResult();
        assertEquals(1, res.size());
        dto = res.iterator().next();
        assertEquals("historia", dto.getOppiaine());
        assertEquals("kieli_fi", dto.getKieliKoodi());

        v = instance.getOppiaineet("or", "kieli_fi");
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, v.getStatus());
    }

    public KoulutusmoduuliToteutus getKomoto(String oid) {
        Koulutusmoduuli komo = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(komo);

        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus();
        komoto.setKoulutusmoduuli(komo);
        komoto.setOid(oid);
        komoto.setTila(TarjontaTila.JULKAISTU);

        return komoto;
    }

    @Test
    public void thatUnusedOppiaineetAreDeleted() {
        Oppiaine matematiikka = oppiaineDAO.insert(getOppiaineEntity("matematiikka", "kieli_fi"));
        Oppiaine historia = oppiaineDAO.insert(getOppiaineEntity("historia", "kieli_fi"));
        Oppiaine geologi = oppiaineDAO.insert(getOppiaineEntity("geologi", "kieli_sv"));

        Set<Oppiaine> oppiaineet = new HashSet<Oppiaine>();
        oppiaineet.add(matematiikka);
        oppiaineet.add(historia);

        KoulutusmoduuliToteutus komoto = getKomoto("koulutus-with-oppiaine");
        komoto.setOppiaineet(oppiaineet);
        koulutusmoduuliToteutusDAO.insert(komoto);

        KoulutusmoduuliToteutus deletedKomoto = getKomoto("deleted-koulutus-with-oppiaine");
        deletedKomoto.setTila(TarjontaTila.POISTETTU);
        Set<Oppiaine> oppiaineet2 = new HashSet<Oppiaine>();
        oppiaineet2.add(oppiaineDAO.insert(getOppiaineEntity("history", "kieli_en")));
        deletedKomoto.setOppiaineet(oppiaineet2);
        koulutusmoduuliToteutusDAO.insert(deletedKomoto);

        Oppiaine geologiFromDb = oppiaineDAO.findOneByOppiaineKieliKoodi("geologi", "kieli_sv");
        assertNotNull(geologiFromDb);
        assertEquals("geologi", geologiFromDb.getOppiaine());
        assertEquals("kieli_sv", geologiFromDb.getKieliKoodi());

        oppiaineDAO.deleteUnusedOppiaineet();

        geologiFromDb = oppiaineDAO.findOneByOppiaineKieliKoodi("geologi", "kieli_sv");
        // Should be deleted now
        assertNull(geologiFromDb);

        Oppiaine historyFromDb = oppiaineDAO.findOneByOppiaineKieliKoodi("history", "kieli_en");
        // Should be deleted now
        assertNull(historyFromDb);

        Oppiaine matematiikkaFromDb = oppiaineDAO.findOneByOppiaineKieliKoodi("matematiikka", "kieli_fi");
        // Should not be deleted, because it's referenced from a koulutus
        assertNotNull(matematiikkaFromDb);
        assertEquals("matematiikka", matematiikkaFromDb.getOppiaine());
        assertEquals("kieli_fi", matematiikkaFromDb.getKieliKoodi());
    }

}
