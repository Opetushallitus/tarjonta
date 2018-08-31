package fi.vm.sade.tarjonta.service.impl.conversion.rest;


import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;

public class OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter implements Converter<OrganisaatioRDTOV3, OrganisaatioPerustieto> {
    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter.class);

    @Override
    public OrganisaatioPerustieto convert(OrganisaatioRDTOV3 t) {
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //List<Yhteystieto> yhteystietos = new ArrayList<Yhteystieto>();
        OrganisaatioPerustieto s = new OrganisaatioPerustieto();

        s.setOid(t.getOid());
        s.setAlkuPvm(t.getAlkuPvm());
        s.setLakkautusPvm(t.getLakkautusPvm());
        s.setParentOid(t.getParentOid());
        s.setParentOidPath(t.getParentOid());
        s.setYtunnus(t.getYTunnus());
        s.setVirastoTunnus(t.getVirastoTunnus());
        //s.setAliOrganisaatioMaara(t.getAliOrganisaatioMaara);
        s.setOppilaitosKoodi(t.getOppilaitosKoodi());
        s.setOppilaitostyyppi(t.getOppilaitosTyyppiUri());
        s.setToimipistekoodi(t.getToimipistekoodi());
        //s.setMatch();
        s.setNimi(t.getNimi());
        for (String organisaatioTyyppi : t.getTyypit()) {
            s.getOrganisaatiotyypit().add(OrganisaatioTyyppi.fromValue(organisaatioTyyppi));
        }

        t.getKieletUris();
        s.setKotipaikkaUri(t.getKotipaikkaUri());
        //s.setChildren();

        return s;
    }
}
