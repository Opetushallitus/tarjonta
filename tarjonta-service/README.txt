======================================================================
			   Tarjonta Service
======================================================================

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
----------------------------------------------------------------------
