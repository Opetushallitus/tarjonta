package fi.vm.sade.tarjonta.dao;

import java.util.List;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.model.TarjontaBaseEntity;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import java.io.IOException;
import java.util.Date;

/**
 * @author Jani
 */
public interface MassakopiointiDAO extends JpaDAO<Massakopiointi, Long> {

    public List<Massakopiointi> findByHakuOid(String hakuOid);

    public Object find(String hakuOid, String oid, Class clazz);

    public Massakopiointi find(String hakuOid, String oid);

    public List<Massakopiointi> findByHakuOidAndOids(final String hakuOid, final List<String> oids);

    /**
     * Save and convert object to json
     *
     * @param hakuOid
     * @param oid
     * @param type
     * @param clazz
     * @param entityToJson
     * @param meta
     */
    public void saveEntityAsJson(String hakuOid, String oid, Massakopiointi.Tyyppi type, Class clazz, TarjontaBaseEntity entityToJson, MetaObject meta);

    public long updateTila(String hakuOid, String oid, Massakopiointi.KopioinninTila toTila, Date updated);

    /**
     * Delete all by haku oid;
     *
     * @param hakuOid
     */
    public long deleteAllByHakuOid(final String hakuOid);

    /**
     * Delete only objects by haku oid and tila;
     *
     * @param hakuOid
     * @param tila
     */
    public long deleteByHakuOidAndKopioinninTila(final String hakuOid, Massakopiointi.KopioinninTila tila);

    public String convertToJson(final TarjontaBaseEntity entityToJson) throws IOException;

    public Object convertToEntity(final String json, final Class clazz) throws IOException;

}
