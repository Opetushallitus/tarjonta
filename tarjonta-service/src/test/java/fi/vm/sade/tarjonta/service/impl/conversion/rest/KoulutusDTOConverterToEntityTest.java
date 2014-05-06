/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.powermock.reflect.Whitebox;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;

/**
 * SIMPLE DATA MAPPING TEST CLASS: 
 * Test REST DTO koodi uri data mapping to entity objects.
 *
 * @author jani
 */
public class KoulutusDTOConverterToEntityTest extends KoulutusRestBase {

    private static final String KOMO_OID = "komo_oid";

    private KoulutusCommonConverter commonConverterMock;

    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConvertersMock;

    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConvertersMock;

    private KoulutusmoduuliDAO koulutusmoduuliDAOMock;

    private KoulutusDTOConverterToEntity instance;

    private OidService oidServiceMock;

    @Before
    public void setUp() {
        instance = new KoulutusDTOConverterToEntity();

        commonConverterMock = createMock(KoulutusCommonConverter.class);
        komotoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        komoKuvausConvertersMock = createMock(KoulutusKuvausV1RDTO.class);
        koulutusmoduuliDAOMock = createMock(KoulutusmoduuliDAO.class);
        oidServiceMock = createMock(OidService.class);

        Whitebox.setInternalState(instance, "commonConverter", commonConverterMock);
        Whitebox.setInternalState(instance, "komoKuvausConverters", komoKuvausConvertersMock);
        Whitebox.setInternalState(instance, "komotoKuvausConverters", komotoKuvausConvertersMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAOMock);
        Whitebox.setInternalState(instance, "oidService", oidServiceMock);
    }

    @Test
    public void testKorkeakouluCopyCommonUrisToKomoAndKomoto() throws OIDCreationException {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setOrganisaatio(new OrganisaatioV1RDTO("org_oid", "org_name", null));
        dto.setKomoOid(KOMO_OID);
        dto.setOpintoala(toKoodiUri(Type.BOTH, FieldNames.OPINTOALA));
        dto.setKoulutusala(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSALA));
        dto.setKoulutusaste(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSASTE));
        dto.setTutkintonimikes(toKoodiUris(Type.BOTH, FieldNames.TUTKINTONIMIKE));
        dto.setKoulutuskoodi(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSKOODI));
        dto.setKoulutusohjelma(toKoodiUriNimi(Type.BOTH, FieldNames.KOULUTUSOHJELMA));
        dto.setOpintojenLaajuusarvo(toKoodiUri(Type.BOTH, FieldNames.OPINTOJEN_LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(Type.BOTH, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));
        dto.setKoulutustyyppi(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSTYYPPI));
        dto.setTutkinto(toKoodiUri(Type.BOTH, FieldNames.TUTKINTO));
        dto.setNqf(toKoodiUri(Type.BOTH, FieldNames.NQF));
        dto.setEqf(toKoodiUri(Type.BOTH, FieldNames.EQF));
        dto.setTunniste(testKey(Type.BOTH, FieldNames.TUNNISTE));
        dto.setKandidaatinKoulutuskoodi(toKoodiUri(Type.BOTH, FieldNames.KOULUTUSKOODI_KANDIDAATTI));
        dto.setOpintojenMaksullisuus(false);

        //expect(koulutusmoduuliDAOMock.findByOid(KOMO_OID)).andReturn(m);
        expect(oidServiceMock.get(TarjontaOidType.KOMO)).andReturn("komo_oid");
        expect(oidServiceMock.get(TarjontaOidType.KOMOTO)).andReturn("komoto_oid");

        commonConverterMock.handleDates(isA(KoulutusmoduuliToteutus.class), isA(KoulutusV1RDTO.class));
        komoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject());
        komotoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject());

        //EXPECT KOMO CALLS
        expectFieldNullable(FieldNames.TUTKINTO, Type.KOMO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, Type.KOMO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSARVO, Type.KOMO);
        expectFieldNullable(FieldNames.KOULUTUSASTE, Type.KOMO);
        expectField(FieldNames.OPINTOALA, Type.KOMO);
        expectField(FieldNames.KOULUTUSKOODI, Type.KOMO);
        expectField(FieldNames.KOULUTUSALA, Type.KOMO);
        expectFieldNullable(FieldNames.NQF, Type.KOMO);
        expectFieldNullable(FieldNames.EQF, Type.KOMO);
        expectFieldNullable(FieldNames.KOULUTUSTYYPPI, Type.KOMO);
        expectFieldNullable(FieldNames.KOULUTUSKOODI_KANDIDAATTI, Type.KOMO);
        expectFields(FieldNames.TUTKINTONIMIKE, Type.KOMO);

        //EXPECT KOMOTO CALLS
        expectFieldNullable(FieldNames.TUTKINTO, Type.KOMOTO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, Type.KOMOTO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSARVO, Type.KOMOTO);
        expectFieldNullable(FieldNames.KOULUTUSASTE, Type.KOMOTO);
        expectField(FieldNames.OPINTOALA, Type.KOMOTO);
        expectField(FieldNames.KOULUTUSKOODI, Type.KOMOTO);
        expectField(FieldNames.KOULUTUSALA, Type.KOMOTO);
        expectFieldNullable(FieldNames.NQF, Type.KOMOTO);
        expectFieldNullable(FieldNames.EQF, Type.KOMOTO);
        expectFieldNullable(FieldNames.KOULUTUSTYYPPI, Type.KOMOTO);
        expectFields(FieldNames.TUTKINTONIMIKE, Type.KOMOTO);
        expect(commonConverterMock.convertToTexts(EasyMock.isA(NimiV1RDTO.class), EasyMock.eq(FieldNames.KOULUTUSOHJELMA))).andReturn(new MonikielinenTeksti());

        expectFieldNotTested(FieldNames.SUUNNITELTUKESTO);
        expectFieldsNotTested(FieldNames.POHJALKOULUTUSVAATIMUS);
        expectFieldsNotTested(FieldNames.AIHEES);
        expectFieldsNotTested(FieldNames.OPETUSKIELIS);
        expectFieldsNotTested(FieldNames.OPETUSMUODOS);
        expectFieldsNotTested(FieldNames.OPETUSPAIKKAS);
        expectFieldsNotTested(FieldNames.OPETUSAIKAS);
        expectFieldsNotTested(FieldNames.AMMATTINIMIKKEET);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(oidServiceMock);

        final KoulutusmoduuliToteutus result = instance.convert(dto, "user_oid", false);
        EasyMock.verify(commonConverterMock);

        assertKorkeakouluKomoto(result, Type.KOMOTO);
        assertKorkeakouluKomo(result.getKoulutusmoduuli(), Type.KOMO);
    }

    /**
     * Test of convert method, of class KoulutusDTOConverterToEntity.
     */
    @Test
    public void testLukioCopyCommonUrisToKomoto() throws OIDCreationException {
        Koulutusmoduuli m = new Koulutusmoduuli();
        m.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setKoulutustyyppiEnum(KoulutustyyppiEnum.LUKIOKOULUTUS);

        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();
        dto.setOrganisaatio(new OrganisaatioV1RDTO("org_oid", "org_name", null));
        dto.setKomoOid(KOMO_OID);
        dto.setOpintoala(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOALA));
        dto.setKoulutusala(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSALA));
        dto.setKoulutusaste(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSASTE));
        dto.setTutkintonimike(toKoodiUri(Type.KOMOTO, FieldNames.TUTKINTONIMIKE));
        dto.setKoulutuskoodi(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSKOODI));
        dto.setKoulutusohjelma(toKoodiUriNimi(Type.KOMOTO, FieldNames.LUKIOLINJA));
        dto.setOpintojenLaajuusarvo(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(Type.KOMOTO, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO));
        dto.setKoulutustyyppi(toKoodiUri(Type.KOMOTO, FieldNames.KOULUTUSTYYPPI));
        dto.setTutkinto(toKoodiUri(Type.KOMOTO, FieldNames.TUTKINTO));
        dto.setNqf(toKoodiUri(Type.KOMOTO, FieldNames.NQF));
        dto.setEqf(toKoodiUri(Type.KOMOTO, FieldNames.EQF));
        dto.setTunniste(testKey(Type.KOMOTO, FieldNames.TUNNISTE));

        expect(koulutusmoduuliDAOMock.findByOid(KOMO_OID)).andReturn(m);
        expect(oidServiceMock.get(TarjontaOidType.KOMOTO)).andReturn("komoto_oid");
        commonConverterMock.handleDates(isA(KoulutusmoduuliToteutus.class), isA(KoulutusV1RDTO.class));
        komoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomoTeksti, MonikielinenTeksti>>anyObject());
        komotoKuvausConvertersMock.convertTekstiDTOToMonikielinenTeksti(EasyMock.isA(KuvausV1RDTO.class), EasyMock.<Map<KomotoTeksti, MonikielinenTeksti>>anyObject());

        expectFieldNullable(FieldNames.TUTKINTO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSYKSIKKO);
        expectField(FieldNames.OPINTOJEN_LAAJUUSARVO);
        expectField(FieldNames.OPINTOALA);
        expectField(FieldNames.KOULUTUSKOODI);
        expectFieldNimi(FieldNames.LUKIOLINJA);
        expectField(FieldNames.KOULUTUSALA);
        expectFieldNullable(FieldNames.KOULUTUSASTE);
        expectField(FieldNames.TUTKINTONIMIKE);
        expectFieldNullable(FieldNames.KOULUTUSTYYPPI);
        expectFieldNullable(FieldNames.NQF);
        expectFieldNullable(FieldNames.EQF);

        expectFieldNotTested(FieldNames.SUUNNITELTUKESTO);
        expectFieldNotTested(FieldNames.POHJALKOULUTUSVAATIMUS);
        expectFieldsNotTested(FieldNames.OPETUSKIELIS);
        expectFieldsNotTested(FieldNames.OPETUSMUODOS);
        expectFieldsNotTested(FieldNames.OPETUSPAIKKAS);
        expectFieldsNotTested(FieldNames.OPETUSAIKAS);
        expectFieldsNotTested(FieldNames.LUKIODIPLOMI);

        EasyMock.replay(komoKuvausConvertersMock);
        EasyMock.replay(komotoKuvausConvertersMock);
        EasyMock.replay(commonConverterMock);
        EasyMock.replay(koulutusmoduuliDAOMock);
        EasyMock.replay(oidServiceMock);

        final KoulutusmoduuliToteutus result = instance.convert(dto, "user_oid");
        EasyMock.verify(commonConverterMock);

        assertLukioKomoto(result, Type.KOMOTO);
        assertLukioKomoChild(result.getKoulutusmoduuli());
    }

    private void expectFieldNullable(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field), EasyMock.anyBoolean())).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectFieldNullable(FieldNames field, Type returntype) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field), EasyMock.eq(true))).andReturn(testKey(returntype, field));
    }

    private void expectField(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectField(FieldNames field, Type returntype) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(KoodiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(returntype, field));
    }

    private void expectFieldNimi(FieldNames field) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(NimiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(Type.KOMOTO, field));
    }

    private void expectFieldNimi(FieldNames field, Type returntype) {
        expect(commonConverterMock.convertToUri(EasyMock.isA(NimiV1RDTO.class), EasyMock.eq(field))).andReturn(testKey(returntype, field));
    }

    private void expectFieldNotTested(FieldNames field) {
        expect(commonConverterMock.convertToUri(null, field)).andReturn(testKey(Type.NOT_TESTED, field));
    }

    private void expectFieldNotTestedNullable(FieldNames field) {
        expect(commonConverterMock.convertToUri(null, field, true)).andReturn(testKey(Type.NOT_TESTED, field));
    }

    private void expectFieldsNotTested(FieldNames field) {
        expect(commonConverterMock.convertToUris(isA(KoodiUrisV1RDTO.class), EasyMock.<Set<KoodistoUri>>anyObject(), eq(field))).andReturn(new HashSet<KoodistoUri>());
    }

    private void expectFields(FieldNames field, Type returntype) {
        Set<KoodistoUri> set = new HashSet<KoodistoUri>();
        set.add(new KoodistoUri(testKey(returntype, field)));
        expect(commonConverterMock.convertToUris(isA(KoodiUrisV1RDTO.class), EasyMock.<Set<KoodistoUri>>anyObject(), eq(field))).andReturn(set);
    }

    private void assertLukioKomoto(final KoulutusmoduuliToteutus komoto, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), komoto.getOpintoalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), komoto.getKoulutusalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), komoto.getKoulutusasteUri());
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komoto.getTutkintonimikeUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSKOODI), komoto.getKoulutusUri());
        assertEquals(testKey(type, FieldNames.LUKIOLINJA), komoto.getLukiolinjaUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), komoto.getOpintojenLaajuusarvoUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), komoto.getOpintojenLaajuusyksikkoUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), komoto.getKoulutustyyppiUri());
        assertEquals(testKey(type, FieldNames.TUTKINTO), komoto.getTutkintoUri());
        assertEquals(testKey(type, FieldNames.TUNNISTE), komoto.getUlkoinenTunniste());
        assertEquals(testKey(type, FieldNames.NQF), komoto.getNqfUri());
        assertEquals(testKey(type, FieldNames.EQF), komoto.getEqfUri());
        assertEquals(null, komoto.getKoulutusohjelmaUri());
    }

    private void assertLukioKomoChild(final Koulutusmoduuli m) {
        //LUKIO : do not update komo object
        assertEquals(null, m.getOpintoalaUri());
        assertEquals(null, m.getKoulutusalaUri());
        assertEquals(null, m.getKoulutusasteUri());
        assertEquals(null, m.getTutkintonimikeUri());
        assertEquals(null, m.getKoulutusUri());
        assertEquals(null, m.getLukiolinjaUri());
        assertEquals(null, m.getOpintojenLaajuusarvoUri());
        assertEquals(null, m.getOpintojenLaajuusyksikkoUri());
        assertEquals(null, m.getKoulutustyyppiUri());
        assertEquals(null, m.getTutkintoUri());
        assertEquals(null, m.getUlkoinenTunniste());
        assertEquals(null, m.getNqfUri());
        assertEquals(null, m.getEqfUri());
        assertEquals(null, m.getKoulutusohjelmaUri());
    }

    private void assertKorkeakouluKomoto(final KoulutusmoduuliToteutus komoto, final Type type) {
        assertEquals(testKey(type, FieldNames.OPINTOALA), komoto.getOpintoalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), komoto.getKoulutusalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), komoto.getKoulutusasteUri());
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komoto.getTutkintonimikeUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSKOODI), komoto.getKoulutusUri());
        assertEquals(null, komoto.getKoulutusohjelmaUri());
        assertEquals(null, komoto.getLukiolinjaUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), komoto.getOpintojenLaajuusarvoUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), komoto.getOpintojenLaajuusyksikkoUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), komoto.getKoulutustyyppiUri());
        assertEquals(testKey(type, FieldNames.TUTKINTO), komoto.getTutkintoUri());
        assertEquals(testKey(Type.BOTH, FieldNames.TUNNISTE), komoto.getUlkoinenTunniste());
        assertEquals(testKey(type, FieldNames.NQF), komoto.getNqfUri());
        assertEquals(testKey(type, FieldNames.EQF), komoto.getEqfUri());
    }

    private void assertKorkeakouluKomo(final Koulutusmoduuli komo, final Type type) {
        //LUKIO : do not update komo object
        assertEquals(testKey(type, FieldNames.OPINTOALA), komo.getOpintoalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSALA), komo.getKoulutusalaUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSASTE), komo.getKoulutusasteUri());
        assertEquals(testKey(type, FieldNames.TUTKINTONIMIKE), komo.getTutkintonimikeUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSKOODI), komo.getKoulutusUri());
        assertEquals(null, komo.getKoulutusohjelmaUri());
        assertEquals(null, komo.getLukiolinjaUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSARVO), komo.getOpintojenLaajuusarvoUri());
        assertEquals(testKey(type, FieldNames.OPINTOJEN_LAAJUUSYKSIKKO), komo.getOpintojenLaajuusyksikkoUri());
        assertEquals(testKey(type, FieldNames.KOULUTUSTYYPPI), komo.getKoulutustyyppiUri());
        assertEquals(testKey(type, FieldNames.TUTKINTO), komo.getTutkintoUri());
        assertEquals(testKey(Type.BOTH, FieldNames.TUNNISTE), komo.getUlkoinenTunniste());
        assertEquals(testKey(type, FieldNames.NQF), komo.getNqfUri());
        assertEquals(testKey(type, FieldNames.EQF), komo.getEqfUri());
    }
}
