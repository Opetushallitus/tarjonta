package fi.vm.sade.tarjonta.service.impl.resources.v1;


import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KoulutusUtilService {

    @Autowired
    private EntityConverterToRDTO converterToRDTO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    private KoulutusDTOConverterToEntity convertToEntity;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    PermissionChecker permissionChecker;

    public KoulutusV1RDTO koulutusDtoForCopy(Class clazz, KoulutusmoduuliToteutus komoto, String orgOid) {
        //convert entity to dto
        KoulutusV1RDTO copy = converterToRDTO.convert(clazz, komoto, RestParam.showImageAndNoMeta());
        //keep the komo oid, we do need it to search correct komo for the komoto
        copy.setOid(null);
        copy.setKomotoOid(null);
        copy.setTila(TarjontaTila.LUONNOS);
        copy.setOrganisaatio(new OrganisaatioV1RDTO(orgOid, null, null));
        copy.setOpetusTarjoajat(Sets.newHashSet(orgOid));
        return copy;
    }

    public KoulutusmoduuliToteutus copyKorkeakoulutus(KoulutusmoduuliToteutus originalKomoto, String orgOid,
                                                      String newKomotoOid, Boolean checkPermission) {
        if (checkPermission) {
            permissionChecker.checkCreateKoulutus(orgOid);
        }

        Koulutusmoduuli originalKomo = originalKomoto.getKoulutusmoduuli();

        KoulutusKorkeakouluV1RDTO dto = (KoulutusKorkeakouluV1RDTO) koulutusDtoForCopy(KoulutusKorkeakouluV1RDTO.class, originalKomoto, orgOid);

        KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid(), newKomotoOid);
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        Koulutusmoduuli newKomo = newKomoto.getKoulutusmoduuli();
        newKomo.setKoulutuksenTunnisteOid(originalKomo.getKoulutuksenTunnisteOid());
        koulutusmoduuliDAO.insert(newKomo);

        // Kopioi koulutuksen sisältyvyydet
        for (KoulutusSisaltyvyys sisaltyvyys : originalKomo.getSisaltyvyysList()) {
            KoulutusSisaltyvyys copy = new KoulutusSisaltyvyys();
            copy.setYlamoduuli(newKomo);
            for (Koulutusmoduuli alamoduuli : sisaltyvyys.getAlamoduuliList()) {
                copy.addAlamoduuli(alamoduuli);
            }
            copy.setValintaTyyppi(KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF);
            koulutusSisaltyvyysDAO.insert(copy);
        }

        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

}
