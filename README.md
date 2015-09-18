# Tarjonta

## Paikallinen ajoympäristö

Tarjonta koostuu frontend-paketista `tarjonta-app.war` sekä
`tarjonta-service.war` backend-paketista.

Tarjonnan tarvitsemat ulkoiset palveluriippuvuudet tulevat
"Luokka"-ympäristöstä. IDE-ohjeet IntelliJ IDEA:lle.

Ajoympäristöä varten tarvitaan:

- Java 7 ja [Java Cryptography Extension (JCE)](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)
- Tomcat (projektissa ei ole embedded palvelinta)
- PostgreSQL-palvelin
- Solr-palvelin


## PostgreSQL-palvelin

- Käynnistä palvelin (esim. `docker run -p 5432:5432 postgres`)
- Luo tyhjä kanta
  - `psql -Upostgres -h192.168.59.103`
  - `CREATE DATABASE tarjonta;`
- Kopioi pohjatadata omalle palvelimelle [Confluencen ohjeiden mukaan](https://confluence.oph.ware.fi/confluence/display/TD/Tarjontapalvelu#Tarjontapalvelu-Tietokanta)


## Solr-palvelin

- Lataa ja pura Solr haluamaasi paikkaan

  ```
  wget https://archive.apache.org/dist/lucene/solr/4.10.4/solr-4.10.4-src.tgz
  tar xzf solr-4.10.4-src.tgz
  ```

- Käynnistä Solr, `solr.data.dir` hakemistoon muodostuu palvelimen tietokannat,
  `solr.solr.home` pitää osoittaa projektista löytyvään hakemistoon.

  ```
  cd solr-4.10.2/example
  java -Dsolr.solr.home=../../tarjonta-service/src/main/resources/solr \
    -Dsolr.data.dir=../../core -jar ./start.jar
  ```

- Tyhjään Solr:iin pitää populoida dataa jotta käyttöliittymässä voi tehdä
  hakuja. Tarjonnan voi pakottaa indeksoimaan kaiken datan PostgreSQL
  kannasta tai vaihtoehtoisesti haluttuja tietoja voi pakottaa indeksoitavaksi
  asettamalla kyseisten rivien indeksointiajankohdan `NULL`:ksi. Tarjonta
  indeksoi nämä rivit sekuntien viiveellä.

  Esimerkiksi `UPDATE SET tarjonta.hakukohde.viimindeksointipvm = NULL WHERE oid = '1.2.3...';`


### Tomcat

- Asenna Tomcat-palvelin (OSX `brew install tomcat`)
- Luo IDEA:an uusi ajoympäristö
  - Run -> Edit Configurations...
  - + -> Tomcat Server -> Local
  - Server tab
    - Application server: Configure...
    - + -> Tomcat Home: palvelimen `libexec` hakemisto (esim.
      `/usr/local/Cellar/tomcat/8.0.26/libexec`)
    - On 'Update' action: Update classes and resources (restart vaaditaan
      uusien luokkien luonnin yhteydessä, joten se on varmatoimisin mutta
      hitaampi)
    - VM options:

      ```
      -Duser.home="<REPO ROOT>/tarjonta/src/main/resources"
      -Dpostgresql.maxActive=10
      -Dpostgresql.host=192.168.59.103
      -Dpostgresql.port=5432
      -Dpostgresql.database=tarjonta
      -Dpostgresql.user="postgres"
      -Dpostgresql.password=""
      -Xmx2048m
      -XX:PermSize=512m
      ```

  - Deployment tab
    - + -> Artifact -> `tarjonta-app:war exploded` ja `tarjonta-service:war
      exploded`
    - Deploy at the server startup (**note to set BOTH contexts**)
      - `tarjonta-app:war exploded` Application context: `/tarjonta-app`
      - `tarjonta-service:war exploded` Application context:
        `/tarjonta-service`
- Muokkaa `<REPO ROOT>/tarjonta/src/main/resources/oph-configuration`
  hakemistoa
  - `cd <REPO ROOT>/src/main/resources`
  -
    ```
    ln -s ../develop/override.properties oph-configuration/override.properties
    ln -s ../develop/security-context-backend.xml oph-configuration/security-context-backend.xml
    ```

- Aja `mvn install` jotta frontend koodit paketoituvat (`tarjonta-app-angular/dist`)
- Run -> Run ... -> <luotu Tomcat ympäristö>
  - Selaimen pitäisi avautua automaattisesti tarjonnan etusivulle
