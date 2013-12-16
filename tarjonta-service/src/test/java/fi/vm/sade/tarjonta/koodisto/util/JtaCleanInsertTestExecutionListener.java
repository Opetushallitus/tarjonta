package fi.vm.sade.tarjonta.koodisto.util;

import java.sql.Connection;

import javax.persistence.EntityManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;

/**
 * Taken as-is from Koodisto Service test.
 *
 *
 * Spring framework transactional test extension for JUnit4. Cleans the database
 * for DBUnit tests and inserts data set defined in {@link DataSetLocation}
 * annotation. Supports only JTA datasources.
 *
 * @author kkammone
 *
 */
public class JtaCleanInsertTestExecutionListener extends TransactionalTestExecutionListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void beforeTestMethod(TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);

        // location of the data set
        String dataSetResourcePath = null;

        // first, the annotation on the test class
        DataSetLocation dsLocation = testContext.getTestInstance().getClass().getAnnotation(DataSetLocation.class);

        if (dsLocation != null) {
            // found the annotation
            dataSetResourcePath = dsLocation.value();
            log.info("Annotated test, using data set: " + dataSetResourcePath);
        }

        if (dataSetResourcePath != null) {

            Resource dataSetResource = testContext.getApplicationContext().getResource(dataSetResourcePath);
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            IDataSet dataSet = builder.build(dataSetResource.getInputStream());

            LocalContainerEntityManagerFactoryBean emf = testContext.getApplicationContext().getBean(
                    org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.class);

            EntityManager entityManager = (EntityManager) emf.getObject().createEntityManager();

            // entityManager.getTransaction().begin();
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection jdbcConn = session.connection();
            IDatabaseConnection con = new DatabaseConnection(jdbcConn);
            // DatabaseOperation.DELETE_ALL.execute(con, dataSet);
            DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);
            // entityManager.getTransaction().commit();
            con.close();

        } else {
            log.info(testContext.getClass().getName() + " does not have any data set, no data injection.");
        }
    }
}
