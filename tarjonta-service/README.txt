======================================================================
			   Tarjonta Service
======================================================================

Running:
--------------------------------------------------

  mvn -Dlog4j.configuration=file:`pwd`/src/test/resources/log4j.properties jetty:run

(if you want to use local db see src tarjonta-service/src/main/webapp/META-INF/jetty-env.xml)

Get luokka db to localhost:
--------------------------------------------------
  dropdb -U oph tarjonta
  createdb -U oph tarjonta
  pg_dump -b -h taulu.hard.ware.fi -p 5432 tarjonta -U oph > tarjonta.sql
  psql tarjonta -U oph < tarjonta.sql




Tietokannan alustaminen lokaaliin "tarjonta" kantaan:
----------------------------------------------------------------------
1. Luo kanta

  createdb tarjonta

2. Luo skeemat
  (Älä aja flyway:init - se näyttäisi varaavan migraation "000", joka on myös meillä käytössä)

  mvn -Dflyway.user=XXX -Dflyway.password=XXX -P flyway flyway:migrate                                                                                                                                                                


Palvelun deployment
----------------------------------------------------------------------

1. tyhjä tomcat

2. Solr
Lataa solr paketti urlista http://www.nic.funet.fi/pub/mirrors/apache.org/lucene/solr

kopioi solr.war paketin hakemistosta solr-4.2.1/example/webapps/ tomcatin webapps -hakemistoon

Muokkaa catalina.sh scriptistä solr asetukset:

JAVA_OPTS="-Dsolr.solr.home=<organisaatio-git-checkout>/organisaatio-solrconfig/src/main/resources -Dsolr.data.dir=<joku-paikallinen-hakemisto>/solr-data"

Windowsissa: set "JAVA.... "


2. alusta palvelu

./conf/Catalina/localhost/tarjonta-service.xml:

<Context displayName="organisaatio-service" 
         docBase="/Users/mlyly/work/OPH/svn/tarjonta/trunk/tarjonta-service/target/tarjonta-service"
         reloadable="true"/>

3. muuta default portit ja luo datasourcet

./conf/context.xml:

    <Resource auth="Container" 
              name="jdbc/tarjonta" 
              type="javax.sql.DataSource" 
              username="oph" 
              password="oph" 
              driverClassName="org.postgresql.Driver" 
              url="jdbc:postgresql://localhost:5432/tarjonta?ApplicationName=tarjonta-service"
              maxActive="150"
              maxIdle="4"/>

./conf/server.xml:

Ports:

<Server port="8105" shutdown="SHUTDOWN">

    <Connector port="8181" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />

    <Connector port="8109" protocol="AJP/1.3" redirectPort="8443" />






Propertyt joita ladataan:

  classpath:tarjonta-service.properties
  ~/oph-configuration/common.properties
  ~/oph-configuration/tarjonta-service.properties




Esimerkkidataa:

----------------------------------------------------------------------
# dao-context.xml:
jpa.schemaUpdate
jpa.showSql

# ws-context.xml:
activemq.brokerurl=xxx
activeMq.targetDestinationAdmin.tarjonta=xxx
activeMq.targetDestinationPublic.tarjonta=xxx

# koodisto-sync-context.xml: ??? tätä ei kai enää käytetä ???
# koodi.webservice.url
# koodisto.webservice.url
#solr
tarjonta.solr.baseurl=http://127.0.0.1:8181/solr

----------------------------------------------------------------------
