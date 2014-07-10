package fi.vm.sade.tarjonta.dao;

import com.mysema.commons.lang.Pair;
import java.util.List;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.model.TarjontaBaseEntity;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import java.util.Date;

/**
 * @author Jani
 */
public interface MassakopiointiDAO extends JpaDAO<Massakopiointi, Long> {

    public List<Massakopiointi> findByHakuOid(String hakuOid);

    public Pair<Object, MetaObject> find(final String processId, final String oldOid, Class clazz);

    public Massakopiointi find(String processId, String oldOid);

    public List<Massakopiointi> findByHakuOidAndOids(final String hakuOid, final List<String> oids);

    /**
     * Convert given entity instance to json format and save it to database.
     *
     * @param hakuOid
     * @param oldOid
     * @param newOid
     * @param processId
     * @param type type of the entity
     * @param clazz class of the entity
     * @param entityToJson the entity instance
     * @param meta custom data object
     */
    public void saveEntityAsJson(
            final String hakuOid,
            final String oldOid,
            final String newOid,
            final String processId,
            final Massakopiointi.Tyyppi type,
            final Class clazz,
            final TarjontaBaseEntity entityToJson,
            final MetaObject meta);

    /**
     * Update object status by process ID and base OID.
     *
     * @param processId
     * @param oldOid
     * @param toTila
     * @param updated
     * @return count of updated items
     */
    public long updateTila(String processId, String oldOid, Massakopiointi.KopioinninTila toTila, Date updated);

    /**
     * Delete all by haku base OID;
     *
     * @param hakuOid
     * @return count of deleted items
     */
    public long deleteAllByHakuOid(final String hakuOid);

    /**
     * Delete only objects by haku oldOid and tila;
     *
     * @param hakuOid
     * @param tila
     * @return count of deleted items
     */
    public long deleteByHakuOidAndKopioinninTila(final String hakuOid, Massakopiointi.KopioinninTila tila);

    /**
     * Search all Massakopiointi objects by given paramters.
     *
     * @param search
     * @return
     */
    public List<Massakopiointi> search(final SearchCriteria search);

    /**
     * Search all original 'old' OIDs by given parameters.
     *
     * @param search
     * @return
     */
    public List<String> searchOids(final SearchCriteria search);

    public long rowCount(final String hakuOid);

    public class SearchCriteria {

        private String oldOid;
        private String newOid;
        private String hakuOid;
        private Massakopiointi.Tyyppi tyyppi;
        private String processId;
        private Massakopiointi.KopioinninTila tila;

        public SearchCriteria() {
        }

        public SearchCriteria(
                String hakuOid,
                String oldOid,
                String newOid,
                Massakopiointi.Tyyppi tyyppi,
                String processId,
                Massakopiointi.KopioinninTila tila) {
            this.oldOid = oldOid;
            this.newOid = newOid;
            this.hakuOid = hakuOid;
            this.tyyppi = tyyppi;
            this.processId = processId;
            this.tila = tila;
        }

        /**
         * @return the oldOid
         */
        public String getOldOid() {
            return oldOid;
        }

        /**
         * @param oldOid the oldOid to set
         */
        public void setOldOid(String oldOid) {
            this.oldOid = oldOid;
        }

        /**
         * @return the hakuOid
         */
        public String getHakuOid() {
            return hakuOid;
        }

        /**
         * @param hakuOid the hakuOid to set
         */
        public void setHakuOid(String hakuOid) {
            this.hakuOid = hakuOid;
        }

        /**
         * @return the tyyppi
         */
        public Massakopiointi.Tyyppi getTyyppi() {
            return tyyppi;
        }

        /**
         * @param tyyppi the tyyppi to set
         */
        public void setTyyppi(Massakopiointi.Tyyppi tyyppi) {
            this.tyyppi = tyyppi;
        }

        /**
         * @return the newOid
         */
        public String getNewOid() {
            return newOid;
        }

        /**
         * @param newOid the newOid to set
         */
        public void setNewOid(String newOid) {
            this.newOid = newOid;
        }

        /**
         * @return the processId
         */
        public String getProcessId() {
            return processId;
        }

        /**
         * @param processId the processId to set
         */
        public void setProcessId(String processId) {
            this.processId = processId;
        }

        /**
         * @return the tila
         */
        public Massakopiointi.KopioinninTila getTila() {
            return tila;
        }

        /**
         * @param tila the tila to set
         */
        public void setTila(Massakopiointi.KopioinninTila tila) {
            this.tila = tila;
        }
    }

}
