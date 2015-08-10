package fi.vm.sade.tarjonta.dao;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.expr.BooleanExpression;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.QKoulutusPermission;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class KoulutusPermissionDAOImpl extends AbstractJpaDAOImpl<KoulutusPermission, Long> implements KoulutusPermissionDAO {

    @Override
    public List<KoulutusPermission> getAll() {
        QKoulutusPermission qKoulutusPermission = QKoulutusPermission.koulutusPermission;
        return from(qKoulutusPermission).list(qKoulutusPermission);
    }

    @Override
    public List<KoulutusPermission> find(List<String> orgOids, String koodisto, String koodiUri) {
        return find(orgOids, koodisto, koodiUri, null, null);
    }

    @Override
    public List<KoulutusPermission> find(List<String> orgOids, String koodisto, String koodiUri, Date alkuPvm, Date loppuPvm) {

        QKoulutusPermission qKoulutusPermission = QKoulutusPermission.koulutusPermission;

        BooleanExpression where = qKoulutusPermission.orgOid.in(orgOids)
                .and(qKoulutusPermission.koodisto.eq(koodisto))
                .and(qKoulutusPermission.koodiUri.eq(koodiUri));

        if (alkuPvm != null) {
            where.and(
                    qKoulutusPermission.alkuPvm.before(alkuPvm).or(qKoulutusPermission.alkuPvm.isNull())
            );
        }

        if (loppuPvm != null) {
            where.and(
                    qKoulutusPermission.loppuPvm.after(loppuPvm).or(qKoulutusPermission.loppuPvm.isNull())
            );
        }

        return from(qKoulutusPermission).where(where).list(qKoulutusPermission);
    }

    @Override
    public Long removeAll() {
        QKoulutusPermission qKoulutusPermission= QKoulutusPermission.koulutusPermission;
        return new JPADeleteClause(getEntityManager(), qKoulutusPermission).execute();
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

}
