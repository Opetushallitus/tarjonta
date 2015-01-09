package fi.vm.sade.tarjonta.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.searchParams.ListHakuSearchParam;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * @author Antti
 */
public interface HakuDAO extends JpaDAO<Haku, Long> {

    Haku findByOid(String oidString);

    List<Haku> findByOids(List<String> oids);

    /**
     * @param searchString
     * @param kieliKoodi
     * @return
     * @deprecated in 9.0 Use {@link #findBySearchCriteria()} instead
     */
    @Deprecated
    List<Haku> findBySearchString(String searchString, String kieliKoodi);

    /**
     * @param searchString
     * @param kieliKoodi
     * @return
     * @deprecated in 9.0 Use {@link HakukohdeDAO} instead
     */
    @Deprecated
    List<Haku> findHakukohdeHakus(Haku haku);

    /**
     * Listing of Hakus, for REST apis.
     *
     * @param tila
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return
     */
    List<String> findOIDsBy(TarjontaTila tila, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince);

    /**
     * @param searchString
     * @param kieliKoodi
     * @return
     * @deprecated in 9.0 Use {@link #findBySearchCriteria()} instead
     */
    @Deprecated
    List<Haku> findByKoulutuksenKausi(String kausi, Integer alkamisVuosi);

    /**
     * @param searchString
     * @param kieliKoodi
     * @return
     * @deprecated in 9.0 Use {@link #findBySearchCriteria()} instead
     */
    @Deprecated
    List<Haku> findBySearchCriteria(ListHakuSearchParam param);

    List<String> findOIDByCriteria(int count, int startIndex,
                                   List<HakuSearchCriteria> criteriaList);

    List<Haku> findHakuByCriteria(int count, int startIndex,
                                  List<HakuSearchCriteria> criteriaList);

    /**
     * Vaihtaa haun tilan suoraa poistetuksi/passivoiduksi.
     *
     * @param hakuOid
     * @param userOid
     */
    void safeDelete(final String hakuOid, final String userOid);

    Set<String> findOrganisaatioOidsFromHakukohteetByHakuOid(String hakuOid);
}
