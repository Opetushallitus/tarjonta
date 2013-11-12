package db.migration;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
/*
* @author: Tuomas Katva 12/11/13
*/
public class V046__addAlkamiskausiAndVuosi implements SpringJdbcMigration{


    private static final Logger LOG = LoggerFactory.getLogger(V046__addAlkamiskausiAndVuosi.class);

    String koulutusModuuliTotQuery = "SELECT * FROM koulutusmoduuli_toteutus kt WHERE alkamiskausi IS NULL";

    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        LOG.debug("Running V046 migration...");
        List<QueryResult> results = jdbcTemplate.query(koulutusModuuliTotQuery,new RowMapper<QueryResult>() {
            @Override
            public QueryResult mapRow(ResultSet resultSet, int i) throws SQLException {
                QueryResult result = new QueryResult();

                result.setAlkamisPvm(resultSet.getDate("koulutuksen_alkamis_pvm"));
                result.setOid(resultSet.getString("oid"));
                result.setId(result.getId());

                return result;
            }
        });
        LOG.debug("UPDATING {} rows",results.size());
        for (QueryResult result : results) {
            updateKomoto(result,jdbcTemplate);
        }
    }

    private void updateKomoto(QueryResult result, JdbcTemplate jdbcTemplate) {
      try {
       String kausi = IndexDataUtils.parseKausi(result.getAlkamisPvm());
       String vuosi = IndexDataUtils.parseYear(result.getAlkamisPvm());

       jdbcTemplate.update("UPDATE koulutusmoduuli_toteutus SET alkamiskausi = ?, alkamisvuosi = ? WHERE id = ?",new Object[]{kausi,vuosi,result.getId()});
       LOG.debug("Update row {} with values {}", result.getId(),kausi + " " +vuosi);
      } catch (Exception exp ){
          LOG.warn("SOMETHING WENT WRONG IN V046-migration, with row {} EXCEPTION : {}", result.getId(),exp.toString());
      }
    }

    class QueryResult {

        private String oid;
        private int id;
        private Date alkamisPvm;


        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Date getAlkamisPvm() {
            return alkamisPvm;
        }

        public void setAlkamisPvm(Date alkamisPvm) {
            this.alkamisPvm = alkamisPvm;
        }
    }
}
