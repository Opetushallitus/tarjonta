package db.migration;

import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V046__addAlkamiskausiAndVuosi extends BaseJavaMigration {

  private static final Logger LOG = LoggerFactory.getLogger(V046__addAlkamiskausiAndVuosi.class);

  String koulutusModuuliTotQuery =
      "SELECT * FROM koulutusmoduuli_toteutus kt WHERE alkamiskausi IS NULL";

  public void migrate(Context context) throws Exception {
    LOG.info("RUNNING V046 MIGRATION...");
    System.out.print("RUNNING V046 MIGRATION...");
    JdbcTemplate jdbcTemplate =
        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));
    List<QueryResult> results =
        jdbcTemplate.query(
            koulutusModuuliTotQuery,
            new RowMapper<QueryResult>() {
              @Override
              public QueryResult mapRow(ResultSet resultSet, int i) throws SQLException {
                QueryResult result = new QueryResult();

                result.setAlkamisPvm(resultSet.getDate("koulutuksen_alkamis_pvm"));
                result.setOid(resultSet.getString("oid"));
                result.setId(resultSet.getInt("id"));

                return result;
              }
            });
    LOG.debug("UPDATING {} rows", results.size());
    System.out.println("UPDATING " + results.size() + " ROWS");
    for (QueryResult result : results) {
      updateKomoto(result, jdbcTemplate);
    }
  }

  private void updateKomoto(QueryResult result, JdbcTemplate jdbcTemplate) {
    try {
      if (result.getAlkamisPvm() != null) {
        String kausi = IndexDataUtils.parseKausiKoodi(result.getAlkamisPvm());
        String vuosi = IndexDataUtils.parseYear(result.getAlkamisPvm());
        String updateSql =
            "UPDATE koulutusmoduuli_toteutus SET alkamiskausi = '"
                + kausi
                + "' , alkamisvuosi = "
                + vuosi
                + "  WHERE id = "
                + result.getId();
        System.out.println("UPDATING WITH SQL : " + updateSql);
        jdbcTemplate.update(updateSql);
        LOG.info("Updated row {} with values {}", result.getId(), kausi + " " + vuosi);
        System.out.println(
            "UPDATE ROW " + result.getId() + " WITH VALUES : " + kausi + " " + vuosi);
      } else {
        System.out.println("ROW " + result.getId() + " HAD ALKAMISPVM NULL. NOT UPDATED");
      }
    } catch (Exception exp) {
      LOG.warn(
          "SOMETHING WENT WRONG IN V046-migration, with row {} EXCEPTION : {}",
          result.getId(),
          exp.toString());
      System.out.println(
          "SOMETHING WENT WRONG IN V046-migration, with row : "
              + result.getId()
              + " EXCEPTION : "
              + exp.toString());
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
