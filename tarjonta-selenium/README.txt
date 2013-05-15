Configuration
   Copy properties files to oph-configuration directory
     cp ./src/test/resources/tarjonta-selenium.properties.*config /home/user/oph-configuration

Set target and run
   export TESTTARGET=luokka_config
   mvn clean test -DskipTests=false

Target options
   luokka_config
