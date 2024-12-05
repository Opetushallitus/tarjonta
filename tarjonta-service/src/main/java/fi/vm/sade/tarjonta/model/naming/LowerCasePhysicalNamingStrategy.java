package fi.vm.sade.tarjonta.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class LowerCasePhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

  public static final LowerCasePhysicalNamingStrategy INSTANCE =
      new LowerCasePhysicalNamingStrategy();

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
    return super.toPhysicalCatalogName(toLowerCase(name), context);
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
    return super.toPhysicalColumnName(toLowerCase(name), context);
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
    return super.toPhysicalSchemaName(toLowerCase(name), context);
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
    return super.toPhysicalSequenceName(toLowerCase(name), context);
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
    return super.toPhysicalTableName(toLowerCase(name), context);
  }

  private Identifier toLowerCase(Identifier id) {
    if (id == null) return id;

    String name = id.getText();
    String lowerCaseName = name.toLowerCase();
    if (!lowerCaseName.equals(name)) return new Identifier(lowerCaseName, id.isQuoted());
    else return id;
  }
}
