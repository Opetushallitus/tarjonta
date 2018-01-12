package fi.vm.sade.tarjonta.service.impl.aspects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

@Service
public class KoulutusPermissionService {

    private static final String OPH_OID = "1.2.246.562.10.00000000001";
    public static final String VALMA = "koulutus_999901";
    private static final String VALMA_ERITYISOPETUKSENA = "koulutus_999902";


    @Autowired
    private KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    private OrganisaatioService organisaatioService;


    public static List<ToteutustyyppiEnum> toteustustyyppisToCheckPermissionFor() {
        return Lists.newArrayList(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO,
                ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA,
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA,
//                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER, Otetaan käyttöön, kun Oivan JSON API palauttaa luvat myös näille
                ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS
        );
    }

    public void checkThatOrganizationIsAllowedToOrganizeEducation(KoulutusmoduuliToteutus komoto) {
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        KoulutusV1RDTO dto;

        switch(komoto.getToteutustyyppi()) {
            case AMMATILLINEN_PERUSTUTKINTO:
                dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
                break;
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                dto = new KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO();
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
                dto = new KoulutusValmentavaJaKuntouttavaV1RDTO();
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
                dto = new KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO();
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
                dto = new KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO();
                break;
            default:
                return;
        }

        dto.setOrganisaatio(new OrganisaatioV1RDTO(komoto.getTarjoaja()));
        dto.setToteutustyyppi(komoto.getToteutustyyppi());
        dto.setKoulutuskoodi(getKoodi(getFromKomotoOrKomo(komoto.getKoulutusUri(), komo.getKoulutusUri())));
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(komoto.getKoulutuksenAlkamisPvms()));
        dto.setKoulutuksenAlkamiskausi(getKoodi(komoto.getAlkamiskausiUri()));
        dto.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());

        NimiV1RDTO osaamisala = new NimiV1RDTO();
        osaamisala.setUri(getFromKomotoOrKomo(komoto.getOsaamisalaUri(), komo.getOsaamisalaUri()));
        dto.setKoulutusohjelma(osaamisala);

        Map<String, Integer> kielet = new HashMap<>();
        for (KoodistoUri uri : komoto.getOpetuskielis()) {
            kielet.put(uri.getKoodiUri().split("#")[0], 1);
        }
        dto.setOpetuskielis(new KoodiUrisV1RDTO(kielet));

        checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    private String getFromKomotoOrKomo(String komotoUri, String komoUri) {
        return komotoUri != null ? komotoUri : komoUri;
    }

    private KoodiV1RDTO getKoodi(String uri) {
        KoodiV1RDTO koodi = new KoodiV1RDTO();
        koodi.setUri(uri);
        return koodi;
    }


    public void checkThatOrganizationIsAllowedToOrganizeEducation(KoulutusV1RDTO dto) {

        // If saving an existing education -> ignore check
        if (dto.getOid() != null && dto.getOid().length() > 0) {
            return;
        }

        if (!toteustustyyppisToCheckPermissionFor().contains(dto.getToteutustyyppi())) {
            return;
        }

        String orgOid = dto.getOrganisaatio().getOid();
        String koulutusKoodi = null;
        if (dto.getKoulutuskoodi() != null) {
            koulutusKoodi = dto.getKoulutuskoodi().getUri();
        }
        String osaamisalaKoodi = null;
        if (dto.getKoulutusohjelma() != null) {
            osaamisalaKoodi = dto.getKoulutusohjelma().getUri();
        }
        Set<String> opetuskielet = Sets.newHashSet();
        if (dto.getOpetuskielis() != null && dto.getOpetuskielis().getUris() != null) {
            opetuskielet.addAll(dto.getOpetuskielis().getUrisAsStringList(false));
        }

        Set<Date> alkamispvmt = Sets.newHashSet(dto.getKoulutuksenAlkamisPvms());
        if (alkamispvmt.isEmpty()) {
            alkamispvmt.add(IndexDataUtils.getDateFromYearAndKausi(
                    dto.getKoulutuksenAlkamisvuosi(), dto.getKoulutuksenAlkamiskausi().getUri()
            ));
        }

        Preconditions.checkArgument(!alkamispvmt.isEmpty(), "alkamispvm cannot be empty!");

        OrganisaatioRDTO org = organisaatioService.findByOid(orgOid);

        List<String> orgOids = Lists.newArrayList(org.getOid());
        if (org.getParentOidPath() != null) {
            String[] parentPath = org.getParentOidPath().split("\\|");
            for (String parent : parentPath) {
                if (!parent.isEmpty() && !OPH_OID.equals(parent)) {
                    orgOids.add(parent);
                }
            }
        }

        List<KoulutusPermission> permissions = koulutusPermissionDAO.findByOrganization(orgOids);

        for (Date pvm : alkamispvmt) {
            if (koulutusKoodi != null) {
                checkKoulutusPermissionExists(permissions, org, koulutusKoodi, pvm);
            }

            if (osaamisalaKoodi != null) { // Luvassa voi olla poikkeus jollekin osaamisalalle
                checkOsaamisalaRestrictionDoesNotExist(permissions, org, osaamisalaKoodi, pvm);
            }

            checkLanguageRestrictionDoesNotExist(permissions, org, opetuskielet, koulutusKoodi, osaamisalaKoodi, pvm);
        }

    }

    private static void checkKoulutusPermissionExists(List<KoulutusPermission> permissions, OrganisaatioRDTO orgDto, final String koulutusKoodiWithVersion, final Date pvm) {
        String koulutusKoodi = getKoodiURIFromVersionedUri(koulutusKoodiWithVersion);
        if (permissions.stream()
                .filter(p -> p.getKoodisto().equals("koulutus"))
                .filter(p -> KoulutusPermissionType.OIKEUS.equals(p.getType()))
                .filter(p -> koulutusKoodi.equals(p.getKoodiUri()))
                .noneMatch(checkPermissionIsOngoingWhenKoulutusStarts(pvm))) {
            throwPermissionException(orgDto, koulutusKoodiWithVersion, koulutusKoodiWithVersion, "koulutus");
        }
    }

    private static void checkOsaamisalaRestrictionDoesNotExist(List<KoulutusPermission> permissions, OrganisaatioRDTO orgDto, final String code, final Date pvm) {
        permissions.stream()
                .filter(p -> p.getKoodisto().equals("osaamisala"))
                .filter(p -> KoulutusPermissionType.RAJOITE.equals(p.getType()))
                .filter(p -> getKoodiURIFromVersionedUri(code).equals(p.getKoodiUri()))
                .filter(checkPermissionIsOngoingWhenKoulutusStarts(pvm))
                .findAny()
                .ifPresent(violatingOsaamisalaRestriction ->
                        throwPermissionException(orgDto, violatingOsaamisalaRestriction.getKoodiUri(), violatingOsaamisalaRestriction.getKohdeKoodi(), "osaamisala"));
    }

    private void checkLanguageRestrictionDoesNotExist(List<KoulutusPermission> permissions, OrganisaatioRDTO orgDto, Set<String> kielet, String koulutusKoodi, String osaamisalaKoodi, final Date pvm) {
        permissions.stream()
                .filter(p -> p.getKoodisto().equals("kieli"))
                .filter(p -> !kielet.contains(p.getKoodiUri()))
                .filter(p -> p.getKohdeKoodi().equals(koulutusKoodi) || p.getKohdeKoodi().equals(osaamisalaKoodi))
                .filter(p -> KoulutusPermissionType.VELVOITE.equals(p.getType()))
                .filter(checkPermissionIsOngoingWhenKoulutusStarts(pvm))
                .findAny()
                .ifPresent(
                        violatingLanguageRestriction ->
                                throwPermissionException(orgDto, violatingLanguageRestriction.getKoodiUri(), violatingLanguageRestriction.getKohdeKoodi(),"kieli"));

    }

    private static void throwPermissionException(OrganisaatioRDTO orgDto, String puuttuvakoodi, String kohdekoodi, String koodisto) {
        String organisaationNimi = "-";
        try {
            organisaationNimi = orgDto.getNimi().values().iterator().next();
        } catch(Exception ignored) {}

        throw new KoulutusPermissionException(
                organisaationNimi,
                orgDto.getOid(),
                koodisto,
                puuttuvakoodi,
                kohdekoodi
        );
    }

    private static Predicate<KoulutusPermission> checkPermissionIsOngoingWhenKoulutusStarts(final Date pvm) {
        return p -> {
            Date alkuPvm = p.getAlkuPvm();
            Date loppuPvm = p.getLoppuPvm();
            return (alkuPvm == null || isAfterOrEquals(pvm, alkuPvm))
                    && (loppuPvm == null || isBeforeOrEquals(pvm, loppuPvm));
        };
    }

    private static boolean isAfterOrEquals(Date pvm, Date alkuPvm) {
        return pvm.after(alkuPvm) || pvm.equals(alkuPvm);
    }

    private static boolean isBeforeOrEquals(Date pvm, Date loppuPvm) {
        return pvm.before(loppuPvm) || pvm.equals(loppuPvm);
    }

}
