package fi.vm.sade.tarjonta.dao.impl;



import fi.vm.sade.tarjonta.model.QValintaperusteSoraKuvaus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* @author: Tuomas Katva 17/12/13
*/
@Repository
public class KuvausDaoImpl extends AbstractJpaDAOImpl<ValintaperusteSoraKuvaus, Long> implements KuvausDAO {

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppi(ValintaperusteSoraKuvaus.Tyyppi tyyppi) {
        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;



        return from(qValintaperusteSoraKuvaus)
                .where(qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi))
                .list(qValintaperusteSoraKuvaus);

    }

    @Override
    public List<ValintaperusteSoraKuvaus> findByTyyppiAndOrganizationType(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType) {

        QValintaperusteSoraKuvaus qValintaperusteSoraKuvaus = QValintaperusteSoraKuvaus.valintaperusteSoraKuvaus;



        return from(qValintaperusteSoraKuvaus)
                .where(qValintaperusteSoraKuvaus.tyyppi.eq(tyyppi).and(qValintaperusteSoraKuvaus.organisaatioTyyppi.eq(orgType)))
                .list(qValintaperusteSoraKuvaus);


    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }
}
