======================================================================
			  Tarjonta APP (UI)
======================================================================

Käynnistäminen:

  mvn -Pdevelopment -Dlog4j.configuration=file:src/main/profile/development/log4j.properties jetty:run


Proertyfilet jota ladataan

  classpath:tarjonta-app.properties
  ~/oph-configuration/common.properties
  ~/oph-configuration/tarjonta-app.properties

----------------------------------------------------------------------
#
# Development time properties
#
# tarjonta-app.dev.redirect=KOULUTUS / HAKU
# tarjonta-app.dev.theme=oph-app-tarjonta / "" = reindeer

#
# To show last changed date, this will be shown by default - set "common.showIdentifier=false" if this is not wanted (like in production)
# TarjontaRootView.java:
#   common.showAppIdentifier:true
#   tarjonta-app.identifier=$Revision: 4254 $ $Date: 2012-11-12 15:42:52 +0200 (Mon, 12 Nov 2012) $


#
# Tarjonta APP properties
#

#
# Koodistot (KoodistoURIHelper.java: koodissa @Value annotaatiot)
#
koodisto-uris.hakutyyppi=HAKUTYYPPI
koodisto-uris.hakukausi=KAUSI
koodisto-uris.hakutapa=HAKUTAPA
koodisto-uris.haunKohdejoukko=Haun kohdejoukko
koodisto-uris.hakukohde=Hakukohde
koodisto-uris.hakukelpoisuusVaatimukset=hakukelpoisuusvaatimus
koodisto-uris.koulutuksenAlkamiskausi=KAUSI
koodisto-uris.kieli=KIELIKOEKIELI
koodisto-uris.opetusmuoto=OPETUSMUOTO
koodisto-uris.koulutuslaji=KOULUTUSLAJI
koodisto-uris.avainsanat=OPETUKSEN_TEEMAT
koodisto-uris.suunniteltuKesto=SUUNNITELTU_KESTO
koodisto-uris.oppilaitostyyppi=Oppilaitostyyppi
koodisto-uris.koulutus=Koulutusluokitus
koodisto-uris.koulutusohjelma=YO-koulutusohjelma
koodisto-uris.koulutusaste=opm02_koulutusaste

#TODO: the propertyes are missing real koodisto data:
koodisto-uris.kestoTyyppi=KIELI
koodisto-uris.kielivalikoima=KIELI
koodisto-uris.ammattinimikkeet=KIELI

# OrganisaatiohakuView.java:
#   root.organisaatio.oid=xxx

#
# Servicet (application-context-ws.xml)
#

# koodi.webservice.url=http://NO_KOODI_WEBSERVICE_URL_CONFIGURED}"/>
# koodisto.webservice.url=http://NO_KOODISTO_WEBSERVICE_URL_CONFIGURED}"/>
# oid.webservice.url=http://NO_OID_WEBSERVICE_URL_CONFIGURED}"/>
# tarjonta.admin.webservice.url=http://NO_TARJONTA_ADMIN_WEBSERVICE_URL_CONFIGURED}"/>
# tarjonta.public.webservice.url=http://NO_TARJONTA_PUBLIC_WEBSERVICE_URL_CONFIGURED}"/>
# organisaatio.webservice.url=http://NO_ORGANISAATIO_WEBSERVICE_URL_CONFIGURED}"/>
----------------------------------------------------------------------
