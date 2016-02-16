package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KoulutusImplicitDataPopulator {

    @Autowired
    KoodiService koodiService;

    @Autowired
    KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    public static final String KOULUTUSALAOPH2002 = "koulutusalaoph2002";
    public static final String KOULUTUSASTEOPH2002 = "koulutusasteoph2002";
    public static final String OPINTOALAOPH2002 = "opintoalaoph2002";
    public static final String EQF = "eqf";
    public static final String TUTKINTO = "tutkinto";
    public static final String TUTKINTONIMIKEKK = "tutkintonimikekk";

    public KoulutusV1RDTO populateFields(final KoulutusV1RDTO dto) {
        if (StringUtils.isBlank(dto.getOid()) && !StringUtils.isBlank(dto.getUniqueExternalId())) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByUniqueExternalId(dto.getUniqueExternalId());
            if (komoto != null) {
                dto.setOid(komoto.getOid());
            }
        }

        if (dto.getKoulutuskoodi() == null || StringUtils.isBlank(dto.getKoulutuskoodi().getUri())) {
            return dto;
        }

        List<KoodiType> sisaltaaKoodit = koodiService.listKoodiByRelation(
                new KoodiUriAndVersioType() {{
                    this.setKoodiUri(dto.getKoulutuskoodi().getUri());
                    this.setVersio(dto.getKoulutuskoodi().getVersio());
                }},
                false,
                SuhteenTyyppiType.SISALTYY
        );

        dto.setKoulutusala(findCode(sisaltaaKoodit, KOULUTUSALAOPH2002));
        dto.setKoulutusaste(findCode(sisaltaaKoodit, KOULUTUSASTEOPH2002));
        dto.setOpintoala(findCode(sisaltaaKoodit, OPINTOALAOPH2002));
        dto.setEqf(findCode(sisaltaaKoodit, EQF));
        dto.setTutkinto(findCode(sisaltaaKoodit, TUTKINTO));

        if (dto instanceof KoulutusKorkeakouluV1RDTO){
            ((KoulutusKorkeakouluV1RDTO) dto).setTutkintonimikes(findCodes(sisaltaaKoodit, TUTKINTONIMIKEKK));
        }

        return dto;
    }

    private KoodiV1RDTO findCode(final List<KoodiType> codes, final String koodisto) {
        KoodiType code = (KoodiType) Iterables.find(codes, matchKoodisto(koodisto), null);
        if (code == null) {
            return null;
        }
        return new KoodiV1RDTO(code.getKoodiUri(), code.getVersio(), code.getKoodiArvo());
    }

    private KoodiUrisV1RDTO findCodes(final List<KoodiType> codes, final String koodisto) {
        Map<String, Integer> uris = new HashMap<String, Integer>();
        Iterable<KoodiType> filtered = Iterables.filter(codes, matchKoodisto(koodisto));
        for (KoodiType koodi : filtered) {
            uris.put(koodi.getKoodiUri(), koodi.getVersio());
        }
        KoodiUrisV1RDTO koodiUris = new KoodiUrisV1RDTO();
        koodiUris.setUris(uris);
        return koodiUris;
    }

    private static Predicate matchKoodisto(final String koodisto) {
        return new Predicate<KoodiType>() {
            @Override
            public boolean apply(KoodiType candidate) {
                return koodisto.equals(candidate.getKoodisto().getKoodistoUri());
            }
        };
    }

}
