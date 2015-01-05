package fi.vm.sade.tarjonta.dao.impl;


import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.dao.impl.util.QuerydslUtils;
import fi.vm.sade.tarjonta.model.QMonikielinenTeksti;
import fi.vm.sade.tarjonta.model.QTekstiKaannos;
import fi.vm.sade.tarjonta.model.QValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KuvausDaoImpl extends AbstractJpaDAOImpl<ValintaperusteSoraKuvaus, Long> implements KuvausDAO {

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppi(ValintaperusteSoraKuvaus.Tyyppi tyyppi) {
        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;


        return from(qValintaperusteSoraKuvaus)
                .where(
                        qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi)
                                .and(qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU))
                )
                .list(qValintaperusteSoraKuvaus);

    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppiAndOrganizationType(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;


        return from(qValintaperusteSoraKuvaus)
                .where(
                        qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi)
                                .and(qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(orgType))
                                .and(qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU))
                )
                .list(qValintaperusteSoraKuvaus);


    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppiOrgTypeYearKausi(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType, String kausi, int year) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;

        return from(qValintaperusteSoraKuvaus)
                .where(
                        qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi).and(qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(orgType))
                                .and(qValintaperusteSoraKuvaus.kausi.eq(kausi)).and(qValintaperusteSoraKuvaus.vuosi.eq(year))
                                .and(qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU))
                )
                .list(qValintaperusteSoraKuvaus);

    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByAvainTyyppiYearKausi(String avain, ValintaperusteSoraKuvaus.Tyyppi tyyppi, String kausi, int year) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;

        return from(qValintaperusteSoraKuvaus).where(qValintaperusteSoraKuvaus.avain.eq(avain)
                .and(qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi))
                .and(qValintaperusteSoraKuvaus.kausi.eq(kausi)).and(qValintaperusteSoraKuvaus.vuosi.eq(year)))
                .list(qValintaperusteSoraKuvaus);
    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppiOrgTypeAndYear(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType, int year) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;


        return from(qValintaperusteSoraKuvaus)
                .where(
                        qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi).and(qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(orgType))
                                .and(qValintaperusteSoraKuvaus.vuosi.eq(year))
                                .and(qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU))
                )
                .list(qValintaperusteSoraKuvaus);

    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByOppilaitosTyyppiTyyppiAndNimi(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String nimi, String oppilaitosTyyppi) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;
        QMonikielinenTeksti qMonikielinenTeksti = QMonikielinenTeksti.monikielinenTeksti;


        return from(qValintaperusteSoraKuvaus)
                .where(
                        qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi)
                                .and(qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(oppilaitosTyyppi.trim()))
                                .and(qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU))
                ).list(qValintaperusteSoraKuvaus);
        //TODO: how to query "IN" monikielinentekstis ?


    }

    @Override
    public List<ValintaperusteSoraKuvaus> findBySearchSpec(KuvausSearchV1RDTO searchSpec, ValintaperusteSoraKuvaus.Tyyppi tyyppi) {
        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;
        QMonikielinenTeksti qMonikielinenTeksti = QMonikielinenTeksti.monikielinenTeksti;
        QTekstiKaannos qTekstiKaannos = QTekstiKaannos.tekstiKaannos;

        BooleanExpression whereExpr = qValintaperusteSoraKuvaus.tila.ne(ValintaperusteSoraKuvaus.Tila.POISTETTU);

        if (tyyppi != null) {
            whereExpr = QuerydslUtils.and(whereExpr, qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi));
        }

        if (searchSpec.getOppilaitosTyyppi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(searchSpec.getOppilaitosTyyppi().trim()));
        }

        if (searchSpec.getVuosi() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, qValintaperusteSoraKuvaus.vuosi.eq(searchSpec.getVuosi()));
        }

        if (searchSpec.getKausiUri() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, qValintaperusteSoraKuvaus.kausi.eq(searchSpec.getKausiUri()));
        }

        if (searchSpec.getAvain() != null) {
            whereExpr = QuerydslUtils.and(whereExpr,qValintaperusteSoraKuvaus.avain.eq(searchSpec.getAvain()));
        }

        if (searchSpec.getHakusana() != null) {
            whereExpr = QuerydslUtils.and(whereExpr, qTekstiKaannos.arvo.toLowerCase().like("%" + searchSpec.getHakusana().toLowerCase() + "%"));
            whereExpr = QuerydslUtils.and(whereExpr, qTekstiKaannos.teksti.id.eq(qMonikielinenTeksti.id));
            whereExpr = QuerydslUtils.and(whereExpr, qValintaperusteSoraKuvaus.monikielinenNimi.eq(qMonikielinenTeksti));
            return from(qValintaperusteSoraKuvaus, qMonikielinenTeksti, qTekstiKaannos).where(whereExpr).listDistinct(qValintaperusteSoraKuvaus);
        } else {
            return from(qValintaperusteSoraKuvaus).where(whereExpr).listDistinct(qValintaperusteSoraKuvaus);
        }
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
}
