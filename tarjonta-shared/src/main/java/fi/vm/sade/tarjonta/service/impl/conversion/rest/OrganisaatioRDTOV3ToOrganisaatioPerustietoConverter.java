package fi.vm.sade.tarjonta.service.impl.conversion.rest;


import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter implements Converter<OrganisaatioRDTOV3, OrganisaatioPerustieto> {
    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter.class);

    @Override
    public OrganisaatioPerustieto convert(OrganisaatioRDTOV3 t) {
        OrganisaatioPerustieto s = new OrganisaatioPerustieto();

        s.setOid(t.getOid());
        s.setAlkuPvm(t.getAlkuPvm());
        s.setLakkautusPvm(t.getLakkautusPvm());
        s.setParentOid(t.getParentOid());
        s.setParentOidPath(t.getParentOidPath());
        s.setYtunnus(t.getYTunnus());
        s.setVirastoTunnus(t.getVirastoTunnus());
        //s.setAliOrganisaatioMaara();
        s.setOppilaitosKoodi(t.getOppilaitosKoodi());
        s.setOppilaitostyyppi(t.getOppilaitosTyyppiUri());
        s.setToimipistekoodi(t.getToimipistekoodi());
        //s.setMatch();
        s.setNimi(t.getNimi());
        for (String organisaatioTyyppi : t.getTyypit()) {
            if ( organisaatioTyyppi != null ) { // organisaatioTyyppi might be null if unknown enum value (API change)
                s.getOrganisaatiotyypit().add(OrganisaatioTyyppi.fromValue(organisaatioTyyppi));
            }
        }

        for (String kieliUri : t.getKieletUris()) {
            s.getKieletUris().add(kieliUri);
        }

        s.setKotipaikkaUri(t.getKotipaikkaUri());
        //s.setChildren();

        return s;
    }
}
