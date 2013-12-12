package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import javax.ws.rs.QueryParam;

/**
 * Rajapinta koulutusten linkkaamiseen
 */
@Path("/v1/link")
@Api(value = "/v1/link", description = "Koulutushierarkian hallinnan rajapinnat")
public interface LinkingV1Resource {

    public static final String PARENT = "parent";
    public static final String CHILD = "child";
    public static final String CHILDS = "childs";

    /**
     * Palauta koulutuksen yläpuoliset koulutukset
     *
     * @param parent
     * @return
     */
    @GET
    @Path("/{" + CHILD + "}/parents")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa annetun koulutuksen parent-koulutukset",
            notes = "Palauttaa annetun koulutuksen parent-koulutukset")
    public ResultV1RDTO<Set<String>> parents(@PathParam(CHILD) String parent);

    /**
     * Linkkaa kaksi (tai useampi) koulutusta yhteen
     */
    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Linkkaa kaksi (tai useampi) koulutus yhteen", notes = "Linkkaa kaksi (tai useampi) koulutus yhteen")
    public ResultV1RDTO link(KomoLink link);

    /**
     * Testaa onko linkki mahdollinen (tekee sama tatrkistukset kuin link mutta ei linkkaa)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Testaa, onko kahden koulutuksen likkaaminen mahdollista", notes = "Testaa, onko kahden koulutuksen likkaaminen mahdollista")
    public ResultV1RDTO test(KomoLink link);

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
    @ApiOperation(value = "Poista linkki annettujen koulutusten valilta", notes = "Poista linkki annettujen koulutusten valilta")
    public ResultV1RDTO unlink(@PathParam(PARENT) String parent,
            @PathParam(CHILD) String child);

    /**
     * Poista linkkit kahden koulutuksen väliltä
     */
    @DELETE
    @Path("/{" + PARENT + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poista linkki parentin ja annettujen koulutusten valilta", notes = "Poista linkki parentin ja annettujen koulutusten valilta")
    public ResultV1RDTO multiUnlink(
            @PathParam(PARENT) String parent,
            @QueryParam(CHILDS) String childs);

    /**
     * Palauta koulutuksen lapset
     *
     * @param parent
     * @return
     */
    @GET
    @Path("/{" + PARENT + "}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauta annetun koulutuksen lapset", notes = "Palauta annetun koulutuksen lapset")
    public ResultV1RDTO<Set<String>> children(@PathParam(PARENT) String parent);

}
