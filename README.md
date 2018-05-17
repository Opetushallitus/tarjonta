# Tarjonta

## Paikallinen ajoympäristö

Tarjonta koostuu frontend-paketista `tarjonta-app.war` sekä
`tarjonta-service.war` backend-paketista.

Tarjonnan tarvitsemat ulkoiset palveluriippuvuudet tulevat
"Luokka"-ympäristöstä. IDE-ohjeet IntelliJ IDEA:lle.

Ajoympäristöä varten tarvitaan:

- Java 8 ja [Java Cryptography Extension (JCE)](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- Tomcat (projektissa ei ole embedded palvelinta)
- PostgreSQL-palvelin
- Solr-palvelin


## PostgreSQL-palvelin

- Käynnistä palvelin (esim. `docker run -p 5432:5432 postgres`)
- Luo tyhjä kanta
  - `psql -Upostgres -h192.168.59.103`
  - `CREATE DATABASE tarjonta;`
- Kopioi pohjatadata omalle palvelimelle [Confluencen ohjeiden mukaan](https://confluence.oph.ware.fi/confluence/display/TEK/Tarjontapalvelu)


## Solr-palvelin

- Lataa ja pura Solr haluamaasi paikkaan

  ```
  wget https://archive.apache.org/dist/lucene/solr/4.10.4/solr-4.10.4.tgz
  tar -xf solr-4.10.4.tgz
  cd solr-4.10.4/example
  ```

- Käynnistä Solr, `solr.data.dir` hakemistoon muodostuu palvelimen tietokannat,
  `solr.solr.home` pitää osoittaa projektista löytyvään hakemistoon.

  ```
  java -Dsolr.solr.home=../../../tarjonta/tarjonta-service/src/main/resources/solr \
    -Dsolr.data.dir=../../../core -jar ./start.jar
  ```

- Tyhjään Solr:iin pitää populoida dataa jotta käyttöliittymässä voi tehdä
  hakuja. Tarjonnan voi pakottaa indeksoimaan kaiken datan PostgreSQL
  kannasta tai vaihtoehtoisesti haluttuja tietoja voi pakottaa indeksoitavaksi
  asettamalla kyseisten rivien indeksointiajankohdan `NULL`:ksi. Tarjonta
  tarkistaa uusia indeksoitavia tietoja 10 sekunnin välein.

  - Kaiken indeksointi (clear-parametrin kanssa kannattaa olla varoivainen,
    koska se tyhjentää koko indeksin ja uudelleenindeksoinnissa voi mennä jopa pari päivää!)

    ```
    curl http://localhost:8302/tarjonta-service/rest/indexer/koulutukset?clear=true
    curl http://localhost:8302/tarjonta-service/rest/indexer/hakukohteet?clear=true
    ```

  - Yksittäisiä koulutuksia ja hakukohteita voi asettaa indeksoitavaksi tietokantakyselyllä.
  Alla olevassa esimerkissä indeksoidaan Aalto Yliopiston perustieteiden korkeakoulun kaikki
  koulutukset ja hakukohteet (organisaation OID on 1.2.246.562.10.39920288212).

    ```sql
    /* koulutusten indeksointi */
    UPDATE koulutusmoduuli_toteutus SET viimindeksointipvm = NULL WHERE tarjoaja = '1.2.246.562.10.39920288212';
    
    /* hakukohteiden indeksointi */
    UPDATE hakukohde SET viimindeksointipvm = NULL WHERE id IN (
      SELECT hakukohde_id
      FROM koulutus_hakukohde
      JOIN hakukohde ON hakukohde.id = koulutus_hakukohde.hakukohde_id
      JOIN koulutusmoduuli_toteutus ON koulutusmoduuli_toteutus.id = koulutus_hakukohde.koulutus_id
      WHERE koulutusmoduuli_toteutus.tarjoaja = '1.2.246.562.10.39920288212'
    )
    ```


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
      -Dfront.lokalisointi.baseUrl="https://virkailija.hahtuvaopintopolku.fi"
      -Dfront.authentication-service.baseUrl="https://virkailija.hahtuvaopintopolku.fi"
      -Dfront.ohjausparametrit-service.baseUrl="https://virkailija.hahtuvaopintopolku.fi"
      -Dfront.koodisto-service.baseUrl="https://virkailija.hahtuvaopintopolku.fi"
      -Dfront.organisaatio-service.baseUrl="https://virkailija.hahtuvaopintopolku.fi"
      ```
    - Jos haluat nähdä logeja, voit myös lisätä VM-optionilla haluamasilaisen log4j-konfiguraation, esim
    `-Dlog4j.configuration=file:///work/oph/opintopolku/tarjonta/tarjonta-service/src/main/profile/standalone/log4j.properties`

  - Deployment tab
    - + -> Artifact -> `tarjonta-app:war exploded` ja `tarjonta-service:war exploded`
    - Deploy at the server startup (**note to set BOTH contexts**)
      - `tarjonta-app:war exploded` Application context: `/tarjonta-app`
      - `tarjonta-service:war exploded` Application context:
        `/tarjonta-service`
      - lisää External source `<REPO ROOT>/tarjonta/tarjonta-app-angular/dist` Application context: `/dist`
- Muokkaa `<REPO ROOT>/tarjonta/src/main/resources/oph-configuration` hakemistoa
  - Linkitä tarvittavat konfiguraatiotiedostopohjat

    ```
    cd <REPO ROOT>/src/main/resources
    ln -s ../develop/override.properties oph-configuration/override.properties
    ln -s ../develop/security-context-backend.xml oph-configuration/security-context-backend.xml
    ```
  - Hanki sopiva `common.properties`, esim
  `scp luokka:/data00/oph/tarjonta/oph-configuration/common.properties src/main/resources/oph-configuration/`

- Aja `mvn install` jotta frontend koodit paketoituvat (`tarjonta-app-angular/dist`)
- Run -> Run ... -> *luotu Tomcat ympäristö*
  - Selaimen pitäisi avautua automaattisesti tarjonnan etusivulle
  - katso tunnukset `security-context-backend.xml`:sta
- Front-kehityksessä voit käyttää selaimessa /dist polkua. Staattinen sovellus buildataan komennolla `(cd tarjonta-app-angular && gulp build:dev)`


### Ylläpidon toimintoja

#### Uusien koulutusmoduulien luonti (KOMO-import)

Koulutusmoduulit (komo) sisältävät pohjatiedot esim. toisen asteen koulutuksille.
Kun tulee uusia koulutuksia (esim. uusi lukiolinja tai osaamisala)
pitää näitä varten luoda uusia koulutusmoduuleja tarjontaan ennen kuin oppilaitokset
voivat tallentaa kyseisiä koulutuksia.

Komojen luonti tapahtuu osoitteessa */tarjonta-app/#/komo*:

1. Valitse koulutustyypiksi uuden komon koulutustyyppi (esim. lukiolinjan kohdalla "Lukiokoulutus")
2. Klikkaa "Hae sisäänvietävät moduulit" (tämä operaatio hakee koodistosta valitun koulutustyypin
"mahdollisia" koulutusmoduuleja ja listaa sitten ne koulutusmoduulit, joita ei vielä löydy tarjonnan tietokannasta)
3. Siirry välilehdelle "Moduulien sisäänvienti"
4. Katso läpi lista moduuleista, joita ollaan viemässä tarjontaan
5. Jos lista näyttää oikealta, klikkaa "Lisää puuttuvat koulutusmoduuli"
6. Valmista!


#### Koulutuskoodin vaihto

Joskus tulee tarve vaihtaa jo tallennettujen koulutusten koulutuskoodeja (tai muita koodeja). Tämä tehdään kanta-ajolla koulutusmoduuli_toteutus-tauluun. Esimerkkitapaus keväältä 2016:

```
update koulutusmoduuli_toteutus

set koulutus_uri = 'koulutus_999903#2', // asetetaan uusi koulutuskoodi
viimindeksointipvm = NULL, // pakotetaan Solrin indeksointi tälle koulutukselle
koulutusmoduuli_id = 6385791 // vaihdetaan koulutusmoduuli uutta koulutuskoodia vastaavaksi (pitää siis selvittää uuden koulutusmoduulin id ensin)

where koulutus_uri LIKE 'koulutus_039999#%' // alkuperäinen koulutuskoodi
and alkamisvuosi >= 2016 // ei kosketa menneisyydessä oleviin koulutuksiin
and tila != 'POISTETTU'; // ei kosketa poistettuihin
```
