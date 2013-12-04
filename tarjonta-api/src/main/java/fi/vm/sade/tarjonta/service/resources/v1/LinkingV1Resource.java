package fi.vm.sade.tarjonta.service.resources.v1;

import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

/**
 * Rajapinta koulutusten linkkaamiseen
 */
@Path("/v1/link")
public interface LinkingV1Resource {

    public static final String PARENT = "parent";
    public static final String CHILD = "child";

    /**
     * Palauta koulutuksen yläpuoliset koulutukset
     * 
     * @param parent
     * @return
     */
    @GET
    @Path("/parents/{" + CHILD + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Set<String>> parents(@PathParam(CHILD) String parent);

    /**
     * Linkkaa kaksi koulutusta yhteen
     * 
     * @param oids
     * @return
     */
    @POST
    @Path("/{" + PARENT + "}/{" + CHILD + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO link(@PathParam(PARENT) String parent,
            @PathParam(CHILD) List<String> child);

    /**
     * Poista linkki kahden koulutuksen väliltä
     * 
     * @param parent
     * @param child
     * @return
     */
    @DELETE
    @Path("/{" + PARENT + "}/{" + CHILD + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO unlink(@PathParam(PARENT) String parent,
            @PathParam(CHILD) String child);

    /**
     * Palauta koulutuksen lapset
     * 
     * @param parent
     * @return
     */
    @GET
    @Path("/{" + PARENT + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Set<String>> children(@PathParam(PARENT) String parent);

}
