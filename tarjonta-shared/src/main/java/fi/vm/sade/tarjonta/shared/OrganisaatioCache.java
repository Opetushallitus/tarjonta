package fi.vm.sade.tarjonta.shared;

import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class OrganisaatioCache {
    private OrganisaatioPerustieto root;
    private Map<String,OrganisaatioPerustieto> byOid;
    private LocalDateTime lastUpdated;

    public OrganisaatioCache() {
        this.clear();
    }

    public void populateOrganisaatioCache(OrganisaatioPerustieto root, List<OrganisaatioPerustieto> rootChildren) {
        this.root = root;
        this.byOid = new HashMap<>();
        root.setChildren(Sets.newHashSet(rootChildren));
        this.setParents(root, rootChildren);
        this.byOid.put(root.getOid(), root);
        this.byOid.putAll(rootChildren.stream().flatMap(this::andChildren)
            .collect(toMap(OrganisaatioPerustieto::getOid, identity())));
        this.lastUpdated = LocalDateTime.now();
    }

    public void add(OrganisaatioPerustieto organisaatioPerustieto) {
        this.byOid.put(organisaatioPerustieto.getOid(), organisaatioPerustieto);
    }
    
    private void setParents(OrganisaatioPerustieto root, Collection<OrganisaatioPerustieto> children) {
        children.forEach(c -> {
            c.setParentOid(root.getOid());
            this.setParents(c, c.getChildren());
        });
    }

    public Stream<OrganisaatioPerustieto> andChildren(OrganisaatioPerustieto parent) {
        return Stream.concat(Stream.of(parent),
                parent.getChildren().stream().flatMap(this::andChildren));
    }

    public OrganisaatioPerustieto getRoot() {
        return root;
    }
    
    public Optional<OrganisaatioPerustieto> getByOid(String oid) {
        return ofNullable(byOid.get(oid));
    }
    
    public Stream<OrganisaatioPerustieto> getAllOrganisaatios() {
        return this.byOid.values().stream();
    }

    public Long getCacheCount() {
        return (long) this.byOid.size();
    }

    public void clear() {
        this.root = null;
        this.byOid = new HashMap<>();
        this.lastUpdated = LocalDateTime.MIN;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
