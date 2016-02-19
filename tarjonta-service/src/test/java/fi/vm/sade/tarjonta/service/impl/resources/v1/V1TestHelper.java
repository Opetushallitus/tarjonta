package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.apache.commons.collections.map.HashedMap;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Service
public class V1TestHelper {

    @Autowired
    OrganisaatioService organisaatioService;

    @Autowired
    PermissionChecker permissionChecker;

    @Autowired
    OidService oidService;

    @Autowired
    OidServiceMock oidServiceMock;

    @Autowired
    KoulutusV1Resource koulutusResourceV1;

    @Autowired
    V1TestHelper helper;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public final static String TARJOAJA1 = "1.2.3";
    public final static String TARJOAJA2 = "1.2.3.214.3425.23";
    public final static String TUNNISTE = "kk virkailijan tunniste, ei uniikki";

    public enum KoulutusField {
        TILA,
        ORGANISAATIO,
        KOULUTUSMODUULITYYPPI,
        JARJESTAJAT,
        KOULUTUSOHJELMA,
        TUNNISTE,
        HAKIJAN_TUNNISTE,
        OPINNON_TYYPPI,
        LAAJUUSPISTETTA,
        ALKAMISPVMS,
        LOPPUMISPVM,
        OPETUSKIELIS,
        OPINTOJEN_MAKSULLISUUS,
        HINTA_STRING,
        OPETUSAIKAS,
        OPETUSMUODOS,
        OPETUSPAIKKAS,
        AIHEES,
        OPPIAINEET,
        OPETTAJA,
        AVOIMEN_YLIOPISTON_KOULUTUS,
        YHTEYSHENKILOS;
    }

    public void init() {
        when(organisaatioService.findByOid(TARJOAJA1)).thenReturn(
                new OrganisaatioDTO(){{
                    setOid(TARJOAJA1);
                    setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti("test", "fi"))));
                }}
        );
        when(organisaatioService.findByOid(TARJOAJA2)).thenReturn(
                new OrganisaatioDTO(){{
                    setOid(TARJOAJA2);
                    setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti("test", "fi"))));
                }}
        );
        doNothing().when(permissionChecker).checkCreateKoulutus(TARJOAJA1);
        doNothing().when(permissionChecker).checkUpdateKoulutusByTarjoajaOid(TARJOAJA1);
        when(tarjontaKoodistoHelper.convertKielikoodiToKoodiType("kieli_fi")).thenReturn(
                new KoodiType(){{
                    setKoodiUri("kieli_fi");
                    setKoodiArvo("suomi");
                    setVersio(1);
                }}
        );
        when(tarjontaKoodistoHelper.convertKielikoodiToKoodiType("kieli_sv")).thenReturn(
                new KoodiType(){{
                    setKoodiUri("kieli_sv");
                    setKoodiArvo("ruotsi");
                    setVersio(1);
                }}
        );
        when(tarjontaKoodistoHelper.getKoodiByUri(Matchers.startsWith("koulutustyyppi_"))).thenReturn(
                new KoodiType(){{
                    setKoodiArvo("Koulutustyyppii");
                    setKoodiUri("koulutustyppi_lorem");
                    setVersio(1);
                }}
        );
    }

    public static <T extends Enum> void assertDelta(KorkeakouluOpintoV1RDTO original, KorkeakouluOpintoV1RDTO modified, T fieldThatShouldDiffer) {
        List<T> fieldsThatDiffer = new ArrayList<T>();

        if (!Objects.equals(original.getTila(), modified.getTila())) {
            fieldsThatDiffer.add((T) KoulutusField.TILA);
        }
        if (!Objects.equals(original.getOrganisaatio().getOid(), modified.getOrganisaatio().getOid())) {
            fieldsThatDiffer.add((T) KoulutusField.ORGANISAATIO);
        }
        if (!Objects.equals(original.getKoulutusmoduuliTyyppi(), modified.getKoulutusmoduuliTyyppi())) {
            fieldsThatDiffer.add((T) KoulutusField.KOULUTUSMODUULITYYPPI);
        }
        if (!Objects.equals(original.getOpetusJarjestajat(), modified.getOpetusJarjestajat())) {
            fieldsThatDiffer.add((T) KoulutusField.JARJESTAJAT);
        }
        if (!Objects.equals(original.getKoulutusohjelma().getTekstis(),
                modified.getKoulutusohjelma().getTekstis())) {
            fieldsThatDiffer.add((T) KoulutusField.KOULUTUSOHJELMA);
        }
        if (!Objects.equals(original.getTunniste(), modified.getTunniste())) {
            fieldsThatDiffer.add((T) KoulutusField.TUNNISTE);
        }
        if (!Objects.equals(original.getHakijalleNaytettavaTunniste(), modified.getHakijalleNaytettavaTunniste())) {
            fieldsThatDiffer.add((T) KoulutusField.HAKIJAN_TUNNISTE);
        }
        if (!Objects.equals(original.getOpinnonTyyppiUri(), modified.getOpinnonTyyppiUri())) {
            fieldsThatDiffer.add((T) KoulutusField.OPINNON_TYYPPI);
        }
        if (!Objects.equals(original.getOpintojenLaajuusPistetta(), modified.getOpintojenLaajuusPistetta())) {
            fieldsThatDiffer.add((T) KoulutusField.LAAJUUSPISTETTA);
        }
        if (datesDiffer(original.getKoulutuksenAlkamisPvms(), modified.getKoulutuksenAlkamisPvms())
                || datesDiffer(modified.getKoulutuksenAlkamisPvms(), original.getKoulutuksenAlkamisPvms())) {
            fieldsThatDiffer.add((T) KoulutusField.ALKAMISPVMS);
        }
        if (!isSameDate(original.getKoulutuksenLoppumisPvm(), modified.getKoulutuksenLoppumisPvm())) {
            fieldsThatDiffer.add((T) KoulutusField.LOPPUMISPVM);
        }
        if (!Objects.equals(original.getOpetuskielis().getUris(), modified.getOpetuskielis().getUris())) {
            fieldsThatDiffer.add((T) KoulutusField.OPETUSKIELIS);
        }
        if (!Objects.equals(original.getOpintojenMaksullisuus(), modified.getOpintojenMaksullisuus())) {
            fieldsThatDiffer.add((T) KoulutusField.OPINTOJEN_MAKSULLISUUS);
        }
        if (!Objects.equals(original.getHintaString(), modified.getHintaString())) {
            fieldsThatDiffer.add((T) KoulutusField.HINTA_STRING);
        }
        if (!Objects.equals(original.getOpetusAikas().getUris(), modified.getOpetusAikas().getUris())) {
            fieldsThatDiffer.add((T) KoulutusField.OPETUSAIKAS);
        }
        if (!Objects.equals(original.getOpetusmuodos().getUris(), modified.getOpetusmuodos().getUris())) {
            fieldsThatDiffer.add((T) KoulutusField.OPETUSMUODOS);
        }
        if (!Objects.equals(original.getOpetusPaikkas().getUris(), modified.getOpetusPaikkas().getUris())) {
            fieldsThatDiffer.add((T) KoulutusField.OPETUSPAIKKAS);
        }
        if (!Objects.equals(original.getAihees().getUris(), modified.getAihees().getUris())) {
            fieldsThatDiffer.add((T) KoulutusField.AIHEES);
        }
        if (!Objects.equals(original.getOppiaineet(), modified.getOppiaineet())) {
            fieldsThatDiffer.add((T) KoulutusField.OPPIAINEET);
        }
        if (!Objects.equals(original.getOpettaja(), modified.getOpettaja())) {
            fieldsThatDiffer.add((T) KoulutusField.OPETTAJA);
        }
        if (!Objects.equals(original.getIsAvoimenYliopistonKoulutus(), modified.getIsAvoimenYliopistonKoulutus())) {
            fieldsThatDiffer.add((T) KoulutusField.AVOIMEN_YLIOPISTON_KOULUTUS);
        }
        if (!isSameYhteyshenkilo(original.getYhteyshenkilos(), modified.getYhteyshenkilos())) {
            fieldsThatDiffer.add((T) KoulutusField.YHTEYSHENKILOS);
        }

        fieldsThatDiffer.addAll((List<T>) tekstiDiff(KomoTeksti.class, original.getKuvausKomo(), modified.getKuvausKomo()));
        fieldsThatDiffer.addAll((List<T>) tekstiDiff(KomotoTeksti.class, original.getKuvausKomoto(), modified.getKuvausKomoto()));

        assertEquals(1, fieldsThatDiffer.size());
        assertEquals(fieldThatShouldDiffer, fieldsThatDiffer.get(0));
    }

    private static <T extends Enum> List<T> tekstiDiff(Class<T> clazz, KuvausV1RDTO original, KuvausV1RDTO modified) {
        Set<T> fieldsThatDiffer = new HashSet<T>();

        MapDifference<T, NimiV1RDTO> tekstiDiff = Maps.difference(original, modified);
        fieldsThatDiffer.addAll(tekstiDiff.entriesDiffering().keySet());
        fieldsThatDiffer.addAll(tekstiDiff.entriesOnlyOnLeft().keySet());
        fieldsThatDiffer.addAll(tekstiDiff.entriesOnlyOnRight().keySet());

        return Lists.newArrayList(fieldsThatDiffer);
    }

    public static boolean isSameDate(Date a, Date b) {
        return a.getYear() == b.getYear()
                && a.getMonth() == b.getMonth()
                && a.getDay() == b.getDay();
    }

    public static boolean isSameYhteyshenkilo(Set<YhteyshenkiloTyyppi> a, Set<YhteyshenkiloTyyppi> b) {
        return Objects.equals(a.iterator().next().getNimi(), b.iterator().next().getNimi());
    }

    public static boolean datesDiffer(Set<Date> a, Set<Date> b) {
        for (final Date d : b) {
            try {
                Iterables.find(a, new Predicate<Date>() {
                    @Override
                    public boolean apply(Date candidate) {
                        return candidate.getTime() == d.getTime();
                    }
                });
            } catch (NoSuchElementException e) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsError(List<ErrorV1RDTO> errors, final String fieldname) {
        return Iterables.find(errors, new Predicate<ErrorV1RDTO>() {
            @Override
            public boolean apply(ErrorV1RDTO error) {
                return fieldname.equals(error.getErrorField());
            }
        }, null) != null;
    }

    public static KoodiUrisV1RDTO koodiUris(Set<String> codeUris) {
        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();
        Map<String, Integer> uris = new HashedMap();
        for (String uri : codeUris) {
            uris.put(uri, 1);
        }
        dto.setUris(uris);
        return dto;
    }

}
