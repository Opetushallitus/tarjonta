package fi.vm.sade.tarjonta.dao;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.QKoulutusPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KoulutusPermissionDAOImpl extends AbstractJpaDAOImpl<KoulutusPermission, Long> implements KoulutusPermissionDAO {

    @Override
    public List<KoulutusPermission> findByOrganization(List<String> orgOids) {
        QKoulutusPermission qKoulutusPermission = QKoulutusPermission.koulutusPermission;

        return queryFactory().selectFrom(qKoulutusPermission)
                .where(qKoulutusPermission.orgOid.in(orgOids))
                .fetch();
    }

    @Override
    public Long removeAll() {
        QKoulutusPermission qKoulutusPermission= QKoulutusPermission.koulutusPermission;
        return new JPADeleteClause(getEntityManager(), qKoulutusPermission).execute();
    }

    protected JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(getEntityManager());
    }

}
