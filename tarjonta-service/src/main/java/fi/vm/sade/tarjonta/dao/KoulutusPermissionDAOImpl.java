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
    public List<KoulutusPermission> findByOrganization(List<String> orgOids) {
        QKoulutusPermission qKoulutusPermission = QKoulutusPermission.koulutusPermission;

        return from(qKoulutusPermission)
                .where(qKoulutusPermission.orgOid.in(orgOids))
                .list(qKoulutusPermission);
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
