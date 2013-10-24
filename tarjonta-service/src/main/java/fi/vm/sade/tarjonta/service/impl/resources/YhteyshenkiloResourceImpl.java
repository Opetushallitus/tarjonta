package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.HenkiloPagingObjectType;
import fi.vm.sade.authentication.service.types.HenkiloSearchObjectType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.authentication.service.types.dto.OrganisaatioHenkiloType;
import fi.vm.sade.authentication.service.types.dto.SearchConnectiveType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.service.resources.YhteyshenkiloResource;
import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;

@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class YhteyshenkiloResourceImpl implements YhteyshenkiloResource {
    
    @Autowired
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private UserService userService;
    
    
    @Override
    public List<YhteyshenkiloRDTO> getByOID(String tarjoajaOid, String searchTerm) {
        List<String> organisaatioOids = new ArrayList<String>();//getTarjoaja().getOrganisaatioOidTree();
        organisaatioOids.add(tarjoajaOid); 

        //If given string is null or empty returning an empty list, i.e. not doing an empty search.
        Preconditions.checkNotNull(organisaatioOids, "A list of organisaatio OIDs cannot be null.");

        if (searchTerm == null || searchTerm.isEmpty()) {
            return new ArrayList<YhteyshenkiloRDTO>();//new ArrayList<HenkiloType>();
        }
        List<YhteyshenkiloRDTO> yhtHenkilot = new ArrayList<YhteyshenkiloRDTO>();
        //Doing the search to UserService
        HenkiloSearchObjectType searchType = new HenkiloSearchObjectType();
        searchType.setConnective(SearchConnectiveType.AND);
        String[] nimetSplit = searchTerm.split(" ");
        if (nimetSplit.length > 1) {
            searchType.setSukunimi(nimetSplit[nimetSplit.length - 1]);
            searchType.setEtunimet(searchTerm.substring(0, searchTerm.lastIndexOf(' ')));
        } else {
            searchType.setEtunimet(searchTerm);
        }
        searchType.getOrganisaatioOids().addAll(organisaatioOids);
        HenkiloPagingObjectType paging = new HenkiloPagingObjectType();
        List<HenkiloType> henkilos = new ArrayList<HenkiloType>();
        try {
            henkilos = this.userService.listHenkilos(searchType, paging);
            for (HenkiloType henkilo : henkilos) {
                YhteyshenkiloRDTO curYht = new YhteyshenkiloRDTO();
                curYht.setEtunimet(henkilo.getEtunimet());
                curYht.setHenkiloOid(henkilo.getOidHenkilo());
                List<OrganisaatioHenkiloType> orgHenks = henkilo.getOrganisaatioHenkilos();
                OrganisaatioHenkiloType orghenk = orgHenks.isEmpty() ? null : orgHenks.get(0);
                curYht.setPuhelin(orghenk.getPuhelinnumero());
                curYht.setEmail(orghenk.getSahkopostiosoite());
                curYht.setTitteli(orghenk.getTehtavanimike());
                curYht.setSukunimi(henkilo.getSukunimi());
                yhtHenkilot.add(curYht);
            }
        } catch (Exception ex) {
            //LOG.error("Problem fetching henkilos: {}", ex.getMessage());
            ex.printStackTrace();
        }

        //Returning the list of found henkilos.
        return yhtHenkilot;
        
        //return null;
    }

}
