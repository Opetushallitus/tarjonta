package fi.vm.sade.tarjonta.service.impl.aspects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KoulutusPermissionService {

    private static final String OPH_OID = "1.2.246.562.10.00000000001";

    @Autowired
    private KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    private OrganisaatioService organisaatioService;

    public void checkThatOrganizationIsAllowedToOrganizeEducation(KoulutusmoduuliToteutus komoto) {
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        KoulutusV1RDTO dto;

        switch (komoto.getToteutustyyppi()) {
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

        Map<String, Integer> kielet = new HashMap<String, Integer>();
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

    public static List<ToteutustyyppiEnum> getToteustustyyppisToCheckPermissionFor() {
        return Lists.newArrayList(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO,
                ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA,
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA,
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER,
                ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS
        );
    }

    public void checkThatOrganizationIsAllowedToOrganizeEducation(KoulutusV1RDTO dto) {

        // If saving an existing education -> ignore check
        if (dto.getOid() != null && dto.getOid().length() > 0) {
            return;
        }

        if (!getToteustustyyppisToCheckPermissionFor().contains(dto.getToteutustyyppi())) {
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
        List<String> opetuskielet = new ArrayList<String>();
        if (dto.getOpetuskielis() != null && dto.getOpetuskielis().getUris() != null) {
            for (String kieli : dto.getOpetuskielis().getUrisAsStringList(false)) {
                opetuskielet.add(kieli);
            }
        }

        Set<Date> alkamispvmt = Sets.newHashSet(dto.getKoulutuksenAlkamisPvms());
        if (alkamispvmt.isEmpty()) {
            alkamispvmt.add(IndexDataUtils.getDateFromYearAndKausi(
                    dto.getKoulutuksenAlkamisvuosi(), dto.getKoulutuksenAlkamiskausi().getUri()
            ));
        }

        Preconditions.checkArgument(!alkamispvmt.isEmpty(), "alkamispvm cannot be empty!");

        OrganisaatioRDTO org = organisaatioService.findByOid(orgOid);

        String kuntaKoodi = org.getKotipaikkaUri();

        List<String> orgOids = Lists.newArrayList(org.getOid());
        if (org.getParentOidPath() != null) {
            String[] parentPath = org.getParentOidPath().split("\\|");
            for (String parent : parentPath) {
                if (!parent.isEmpty() && !OPH_OID.equals(parent)) {
                    orgOids.add(parent);
                }
            }
        }

        for (Date pvm : alkamispvmt) {
            if (koulutusKoodi != null) {
                checkPermissions(orgOids, org, "koulutus", koulutusKoodi, pvm);
            }

            if (osaamisalaKoodi != null) {
                checkPermissions(orgOids, org, "osaamisala", osaamisalaKoodi, pvm);
            }

            for (String kieli : opetuskielet) {
                checkPermissions(orgOids, org, "kieli", kieli, pvm);
            }

            checkPermissions(orgOids, org, "kunta", kuntaKoodi, pvm);
        }

    }

    private void checkPermissions(List<String> orgOids, OrganisaatioRDTO orgDto, String koodisto, String code, Date pvm) {
        List<KoulutusPermission> permissions = koulutusPermissionDAO.find(orgOids, koodisto, code, pvm);
        if (permissions.isEmpty()) {
            String organisaationNimi = "-";
            try {
                organisaationNimi = orgDto.getNimi().values().iterator().next();
            } catch (Exception e) {}

            throw new KoulutusPermissionException(
                    organisaationNimi,
                    orgDto.getOid(),
                    koodisto,
                    code
            );
        }
    }

}
