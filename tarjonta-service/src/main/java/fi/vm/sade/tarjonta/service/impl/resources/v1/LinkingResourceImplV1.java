package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.dao.impl.KoulutusSisaltyvyysDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys.ValintaTyyppi;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.impl.resources.v1.linking.validation.LinkingValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import java.util.Arrays;

/**
 * TODO, authorization!!
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class LinkingResourceImplV1 implements LinkingV1Resource {

    private Logger logger = LoggerFactory.getLogger(LinkingResourceImplV1.class);

    @Autowired
    KoulutusSisaltyvyysDAOImpl koulutusSisaltyvyysDAO;

    @Autowired
    KoulutusmoduuliDAOImpl koulutusmoduuliDAO;

    /**
     * TODO: tekee aina uuden linkin!
     */
    @Override
    public ResultV1RDTO link(KomoLink link) {
        return doLinking(link, false);
    }

    /**
     * TODO: tekee aina uuden linkin!
     */
    @Override
    public ResultV1RDTO test(KomoLink link) {
        return doLinking(link, true);
    }

    private void removeAlamoduulitByKomoOid(String removableKomoOid, Set<KoulutusSisaltyvyys> sisaltyvyydet) {
        for (KoulutusSisaltyvyys sisaltyvyys : sisaltyvyydet) {
            List<Koulutusmoduuli> remove = new ArrayList<Koulutusmoduuli>();
            for (Koulutusmoduuli sisaltyva : sisaltyvyys.getAlamoduuliList()) {
                if (sisaltyva.getOid().equals(removableKomoOid)) {
                    remove.add(sisaltyva);
                }
            }
            if (sisaltyvyys.getAlamoduuliList().size() == remove.size()) {
                //logger.debug("poistetaan kokonaan!");
                koulutusSisaltyvyysDAO.remove(sisaltyvyys);
                logger.debug("linkki poistettu!");
            } else {
                if (!remove.isEmpty()) {
                    for (Koulutusmoduuli moduuli : remove) {
                        sisaltyvyys.removeAlamoduuli(moduuli);
                    }
                    koulutusSisaltyvyysDAO.update(sisaltyvyys);
                    logger.debug("linkin osa poistettu!");
                }
            }
        }

    }

    @Override
    public ResultV1RDTO multiUnlink(String parentKomoOid, String childOids) {
        ResultV1RDTO result = new ResultV1RDTO();
        List<String> komoChildOids = Arrays.asList(childOids.split(","));

        logger.debug("unlinking " + parentKomoOid + " from " + childOids);

        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findByOid(parentKomoOid);
        if (parentKomo != null) {
            logger.debug("parent komo found");

            if (parentKomo.getSisaltyvyysList().isEmpty()) {
                logger.warn("no childs found");
                for (final String missingKomoOids : komoChildOids) {
                    result.addError(ErrorV1RDTO.createValidationError("childs", LinkingValidationMessages.LINKING_OID_NOT_FOUND.name(), missingKomoOids));
                }
            } else {
                for (final String childKomoOid : komoChildOids) {
                    //validate data 
                    final List<String> childKomosChidren = koulutusSisaltyvyysDAO.getChildren(childKomoOid);
                    if (childKomosChidren.isEmpty()) {
                        for (KoulutusSisaltyvyys sisaltyva : parentKomo.getSisaltyvyysList()) {
                            boolean notFound = true;

                            for (Koulutusmoduuli komo : sisaltyva.getAlamoduuliList()) {
                                if (childKomoOid.equals(komo.getOid())) {
                                    notFound = false;
                                }
                            }

                            if (notFound) {
                                logger.warn("link komo oid {} not found", childKomoOid);
                                result.addError(ErrorV1RDTO.createValidationError("childs", LinkingValidationMessages.LINKING_OID_NOT_FOUND.name(), childKomoOid));
                            }
                        }
                    } else {
                        logger.warn("link komo oid {} has children {}", childKomoOid, childKomosChidren);
                        result.addError(ErrorV1RDTO.createValidationError("childs", LinkingValidationMessages.LINKING_OID_HAS_CHILDREN.name(), childKomoOid));
                    }
                }

                if (result.getErrors().isEmpty()) {
                    for (String childKomoOid : komoChildOids) {
                        logger.info("remove link by komo oid {}", childKomoOid);
                        removeAlamoduulitByKomoOid(childKomoOid, parentKomo.getSisaltyvyysList());
                    }
                }
            }
        } else {
            logger.info("parent not found");
            result.addError(ErrorV1RDTO.createValidationError("parent", LinkingValidationMessages.LINKING_MISSING_PARENT_OID.name()));
        }

        if (!result.getErrors().isEmpty()) {
            result.setStatus(ResultStatus.VALIDATION);
        }

        return result;
    }

    private ResultV1RDTO doLinking(KomoLink link, boolean dryRun) {

        String parent = link.getParent();
        List<String> children = link.getChildren();

        final ResultV1RDTO result = new ResultV1RDTO();

        logger.debug("linking (parent-child)" + parent + " -> " + children);

        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findByOid(parent);
        List<Koulutusmoduuli> komoList = new ArrayList<Koulutusmoduuli>();

        if (children.size() == 0) {
            result.addError(ErrorV1RDTO.createValidationError("child", "error.no.childs", parent));
            result.setStatus(ResultStatus.ERROR);
            return result;
        }

        for (String child : children) {
            Koulutusmoduuli childKomo = koulutusmoduuliDAO.findByOid(child);
            if (childKomo == null) {
                result.addError(ErrorV1RDTO.createValidationError("child", "error.child.not.found", child));
            } else {
                komoList.add(childKomo);
            }
        }

        if (result.getErrors() != null) {
            result.setStatus(ResultStatus.ERROR);
            return result;
        }

        if (parentKomo == null || komoList.size() == 0) {
            logger.info("child or parent is null");

            if (parentKomo == null) {
                result.addError(ErrorV1RDTO.createValidationError("parent", "error.parent.not.found", parent));
            }
            if (komoList.size() == 0) {
                result.addError(ErrorV1RDTO.createValidationError("child", "error.child.not.found"));
            }

            result.setStatus(ResultStatus.ERROR);
            return result;
        }

        Set<String> loopEdges = getLoopEdges(parentKomo, Sets.newHashSet(children));
        if (loopEdges.size() > 0) {
            for (String oid : loopEdges) {
                //TODO add oidit!!
                result.addError(ErrorV1RDTO.createValidationError("loop", "error.cannot.create.loop", oid));
                result.setStatus(ResultStatus.ERROR);
            }
            return result;
        }

        //TODO ValintaTyyppi??
        if (!dryRun) {
            for (Koulutusmoduuli komo : komoList) {
                KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                        parentKomo, komo, ValintaTyyppi.ALL_OFF);
                koulutusSisaltyvyysDAO.insert(sisaltyvyys);
            }
        }
        return result;
    }

    private Set<String> getLoopEdges(Koulutusmoduuli parent, Set<String> children) {
        DefaultDirectedGraph<String, DefaultEdge> komoGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        komoGraph.addVertex(parent.getOid());
        addChilds(komoGraph, parent.getOid());
        addParents(komoGraph, parent.getOid());

        for (String child : children) {
            komoGraph.addVertex(child);
            addChilds(komoGraph, child);
            addParents(komoGraph, child);

            logger.debug("adding edge " + parent.getOid() + " -> " + child);
            komoGraph.addEdge(parent.getOid(), child);
        }

        CycleDetector<String, DefaultEdge> detector = new CycleDetector<String, DefaultEdge>(komoGraph);

        return Sets.intersection(children, detector.findCycles());

    }

    private void addChilds(DefaultDirectedGraph<String, DefaultEdge> komoGraph,
            String parent) {
        List<String> children = koulutusSisaltyvyysDAO.getChildren(parent);
        for (String child : children) {
            if (!komoGraph.vertexSet().contains(child)) {
                komoGraph.addVertex(child);
            }
            logger.debug("adding edge " + parent + " -> " + child);
            komoGraph.addEdge(parent, child);
            addChilds(komoGraph, child);
        }
    }

    private void addParents(DefaultDirectedGraph<String, DefaultEdge> komoGraph,
            String child) {
        List<String> parents = koulutusSisaltyvyysDAO.getParents(child);
        for (String parent : parents) {
            if (!komoGraph.vertexSet().contains(parent)) {
                komoGraph.addVertex(parent);
            }
            logger.debug("adding edge " + parent + " -> " + child);
            komoGraph.addEdge(parent, child);
            addParents(komoGraph, parent);
        }
    }

    @Override
    public ResultV1RDTO unlink(String parent, String child) {
        ResultV1RDTO result = new ResultV1RDTO();

        logger.debug("unlinking " + parent + " from " + child);
        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findByOid(parent);

        if (parentKomo != null) {
            logger.debug("parent komo found");
            Set<KoulutusSisaltyvyys> sisaltyvyydet = parentKomo
                    .getSisaltyvyysList();

            if (sisaltyvyydet.size() == 0) {
                logger.debug("no childs found");

                result.addError(ErrorV1RDTO.createInfo("ei sisaltyvyyksia!"));
            } else {

                for (KoulutusSisaltyvyys sisaltyvyys : sisaltyvyydet) {
                    List<Koulutusmoduuli> remove = new ArrayList<Koulutusmoduuli>();
                    for (Koulutusmoduuli sisaltyva : sisaltyvyys
                            .getAlamoduuliList()) {
                        if (sisaltyva.getOid().equals(child)) {

                            remove.add(sisaltyva);
                        }
                    }
                    if (sisaltyvyys.getAlamoduuliList().size() == remove.size()) {
                        //logger.debug("poistetaan kokonaan!");
                        koulutusSisaltyvyysDAO.remove(sisaltyvyys);
                        result.addError(ErrorV1RDTO.createInfo("linkki poistettu!"));
                    } else {
                        if (remove.size() != 0) {
                            for (Koulutusmoduuli moduuli : remove) {
                                //logger.debug("poistetaan linkki " + parentKomo.getOid() + "->" + moduuli.getOid());
                                sisaltyvyys.removeAlamoduuli(moduuli);
                            }
                            koulutusSisaltyvyysDAO.update(sisaltyvyys);
                            result.addError(ErrorV1RDTO
                                    .createInfo("linkin osa poistettu!"));
                        }
                    }
                }
            }
        } else {
            logger.info("parent not found");
            result.addError(ErrorV1RDTO.createValidationError("parent", "not found"));
        }
        return result;
    }

    @Override
    public ResultV1RDTO<Set<String>> children(String parent) {
        Set<String> oids = Sets.newHashSet();
        oids.addAll(koulutusSisaltyvyysDAO.getChildren(parent));
        logger.debug("getting children for " + parent + " found:" + oids.size());
        return new ResultV1RDTO<Set<String>>(oids);
    }

    @Override
    public ResultV1RDTO<Set<String>> parents(String child) {
        Set<String> oids = Sets.newHashSet();
        oids.addAll(koulutusSisaltyvyysDAO.getParents(child));
        logger.debug("getting parents for " + child + " found:" + oids.size());
        return new ResultV1RDTO<Set<String>>(oids);
    }

}
