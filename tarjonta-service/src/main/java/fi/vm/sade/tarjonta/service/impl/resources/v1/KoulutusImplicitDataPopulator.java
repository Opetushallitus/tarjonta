package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.copy.NullAwareBeanUtilsBean;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    private BeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

    @Autowired
    private EntityConverterToRDTO converterToRDTO;

    @Autowired
    private ContextDataService contextDataService;

    public KoulutusV1RDTO populateFields(KoulutusV1RDTO dto) throws IllegalAccessException, InvocationTargetException {
        dto = populateFieldsByKoulutuskoodi(dto);

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByKoulutusId(
                new KoulutusIdentification(dto.getOid(), dto.getUniqueExternalId())
        );

        if (komoto != null) {
            KoulutusV1RDTO originalDto = converterToRDTO.convert(dto.getClass(), komoto, RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang()));

            // Tekstis must be handled separately, because they are in a map-structure
            // and we must support delta edit of individual map entries
            dto.setKuvausKomoto(mergeKuvaus(KomotoTeksti.class, dto.getKuvausKomoto(), originalDto.getKuvausKomoto()));
            dto.setKuvausKomo(mergeKuvaus(KomoTeksti.class, dto.getKuvausKomo(), originalDto.getKuvausKomo()));

            beanUtils.copyProperties(originalDto, dto);
            dto = originalDto;
        }

        dto = defaultValuesForDto(dto);

        return dto;
    }

    private static <T extends Enum> KuvausV1RDTO<T> mergeKuvaus(Class<T> clazz, KuvausV1RDTO dto, KuvausV1RDTO originalDto) {
        if (dto == null) {
            return originalDto;
        }

        KuvausV1RDTO<T> kuvaus = new KuvausV1RDTO<T>();
        kuvaus.putAll(originalDto);
        kuvaus.putAll(dto);
        return kuvaus;
    }

    public KoulutusV1RDTO defaultValuesForDto(KoulutusV1RDTO dto) {
        if (dto instanceof KoulutusKorkeakouluV1RDTO) {
            dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        }
        try {
            KoulutusV1RDTO defaultDto = dto.getClass().newInstance();
            defaultDto.setOpetusTarjoajat(new HashSet<String>());
            defaultDto.setOpetusJarjestajat(new HashSet<String>());
            defaultDto.setYhteyshenkilos(new HashSet<YhteyshenkiloTyyppi>());
            defaultDto.setOrganisaatio(new OrganisaatioV1RDTO());
            defaultDto.setKuvausKomo(new KuvausV1RDTO<KomoTeksti>());
            defaultDto.setKuvausKomoto(new KuvausV1RDTO<KomotoTeksti>());
            defaultDto.setKoulutusohjelma(new NimiV1RDTO());
            defaultDto.setKoulutuksenAlkamisPvms(new HashSet<Date>());
            defaultDto.setOpetuskielis(new KoodiUrisV1RDTO());
            defaultDto.setOpetusmuodos(new KoodiUrisV1RDTO());
            defaultDto.setOpetusAikas(new KoodiUrisV1RDTO());
            defaultDto.setOpetusPaikkas(new KoodiUrisV1RDTO());
            defaultDto.setAmmattinimikkeet(new KoodiUrisV1RDTO());
            defaultDto.setAihees(new KoodiUrisV1RDTO());

            beanUtils.copyProperties(defaultDto, dto);
            return defaultDto;
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    private KoulutusV1RDTO populateFieldsByKoulutuskoodi(final KoulutusV1RDTO dto) {
        if (dto.getKoulutuskoodi() != null && !StringUtils.isBlank(dto.getKoulutuskoodi().getUri())) {
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

            if (dto instanceof KoulutusKorkeakouluV1RDTO) {
                ((KoulutusKorkeakouluV1RDTO) dto).setTutkintonimikes(findCodes(sisaltaaKoodit, TUTKINTONIMIKEKK));
            }
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
