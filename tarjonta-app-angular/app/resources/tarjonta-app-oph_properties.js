window.urls.addProperties( {
    "koulutusinformaatio-app-web.preview":"/app/preview.html#!/$1/$2?lang=$3",

    "tarjonta-service.togglePublished":"/tarjonta-service/rest/v1/$1/$2/tila?state=$3",
    "tarjonta-service.tila":"/tarjonta-service/rest/v1/tila",
    "tarjonta-service.process":"/tarjonta-service/rest/v1/process/$1",

    "tarjonta-service.permission.authorize":"/tarjonta-service/rest/v1/permission/authorize",
    "tarjonta-service.permission.get":"/tarjonta-service/rest/v1/permission/permissions/$1/$2",
    "tarjonta-service.permission.recordUiStacktrace":"/tarjonta-service/rest/v1/permission/recordUiStacktrace",

    "tarjonta-service.haku.find":"/tarjonta-service/rest/v1/haku/find",
    "tarjonta-service.haku.multi":"/tarjonta-service/rest/v1/haku/multi",
    "tarjonta-service.haku.byOid":"/tarjonta-service/rest/v1/haku/$1",
    "tarjonta-service.haku.checkStateChange":"/tarjonta-service/rest/v1/haku/$1/stateChangeCheck",
    "tarjonta-service.haku.changeState":"/tarjonta-service/rest/v1/haku/$1/state?state=$2",
    "tarjonta-service.haku.copy":"/tarjonta-service/rest/v1/haku/$1/copy",
    "tarjonta-service.haku.paste":"/tarjonta-service/rest/v1/haku/paste/$1/$2",

    "tarjonta-service.hakukohde.removeKoulutuksesFromHakukohde":"/tarjonta-service/rest/v1/hakukohde/$1/koulutukset",
    "tarjonta-service.hakukohde.addKoulutuksesToHakukohde":"/tarjonta-service/rest/v1/hakukohde/$1/koulutukset/lisaa",
    "tarjonta-service.hakukohde.validateHakukohdeKomotos":"/tarjonta-service/rest/v1/hakukohde/komotoSelectedCheck",
    "tarjonta-service.hakukohde.byKuvausId":"/tarjonta-service/rest/v1/hakukohde/findHakukohdesByKuvausId/$1",
    "tarjonta-service.hakukohde.valintakoe":"/tarjonta-service/rest/v1/hakukohde/$1/valintakoe/$2",
    "tarjonta-service.hakukohde.liite":"/tarjonta-service/rest/v1/hakukohde/$1/liite/$2",
    "tarjonta-service.hakukohde.byOid":"/tarjonta-service/rest/v1/hakukohde/$1",
    "tarjonta-service.hakukohde.haku":"/tarjonta-service/rest/v1/hakukohde/search",
    "tarjonta-service.hakukohde.checkStateChange":"/tarjonta-service/rest/v1/hakukohde/$1/stateChangeCheck",
    "tarjonta-service.hakukohde.ryhmaoperaatiot":"/tarjonta-service/rest/v1/hakukohde/ryhmat/operate",
    "tarjonta-service.hakukohde.getHakukohde":"/tarjonta-service/rest/v1/hakukohde/ui/$1",

    "tarjonta-service.kuvaus.byTunniste":"/tarjonta-service/rest/v1/kuvaus/$1",
    "tarjonta-service.kuvaus.search":"/tarjonta-service/rest/v1/kuvaus/$1/search",
    "tarjonta-service.kuvaus.nimet":"/tarjonta-service/rest/v1/kuvaus/$1/nimet",
    "tarjonta-service.kuvaus.findWithVuosiOppilaitostyyppiTyyppiVuosi":"/tarjonta-service/rest/v1/kuvaus/$1/$2/$3/kuvaustenTiedot",
    "tarjonta-service.kuvaus.findKuvausWithTyyppiNimiOppilaitos":"/tarjonta-service/rest/v1/kuvaus/$1$2$3",
    "tarjonta-service.kuvaus.findKuvausBasicInformation":"/tarjonta-service/rest/v1/kuvaus/$1/$2/kuvaustenTiedot",

    "tarjonta-service.koulutus.byOid":"/tarjonta-service/rest/v1/koulutus/$1",
    "tarjonta-service.koulutus.siirraByOid":"/tarjonta-service/rest/v1/koulutus/$1/siirra",
    "tarjonta-service.koulutus.siirra":"/tarjonta-service/rest/v1/koulutus/$1/siirra",
    "tarjonta-service.koulutus.kuva":"/tarjonta-service/rest/v1/koulutus/$1/kuva",
    "tarjonta-service.koulutus.kuvaLang":"/tarjonta-service/rest/v1/koulutus/$1/kuva/$2",
    "tarjonta-service.koulutus.oppiaineet":"/tarjonta-service/rest/v1/koulutus/oppiaineet",
    "tarjonta-service.koulutus.haku":"/tarjonta-service/rest/v1/koulutus/search",
    "tarjonta-service.koulutus.tekstis":"/tarjonta-service/rest/v1/koulutus/$1/tekstis",
    "tarjonta-service.koulutus.komotekstis":"/tarjonta-service/rest/v1/koulutus/$1/tekstis/KOMO",
    "tarjonta-service.koulutus.jarjestettavatKoulutukset":"/tarjonta-service/rest/v1/koulutus/$1/jarjestettavatKoulutukset",
    "tarjonta-service.koulutus.koulutuskoodiRelations":"/tarjonta-service/rest/v1/koulutus/koodisto/$1/$2?meta=false&lang=$3",

    "tarjonta-service.komo.byOid":"/tarjonta-service/rest/v1/komo/$1",
    "tarjonta-service.komo.import":"/tarjonta-service/rest/v1/komo/import/$1",
    "tarjonta-service.komo.search":"/tarjonta-service/rest/v1/komo/search?koulutuskoodi=$1",
    "tarjonta-service.komo.searchModules":"/tarjonta-service/rest/v1/komo/search/$1/$2",
    "tarjonta-service.komo.tekstis":"/tarjonta-service/rest/v1/komo/$1/tekstis",

    "tarjonta-service.link.link":"/tarjonta-service/rest/v1/link/$1",
    "tarjonta-service.link.parents":"/tarjonta-service/rest/v1/link/$1/parents",
    "tarjonta-service.link.remove":"/tarjonta-service/rest/v1/link/$1/$2",

    "ohjausparametrit-service.all":"/ohjausparametrit-service/api/v1/rest/parametri/ALL",
    "ohjausparametrit-service.parametri":"/ohjausparametrit-service/api/v1/rest/parametri/$1/$2",

    "haku-app.pingLomake":"/haku-app/generatelomake/ping/$1",
    "haku-app.generateLomake":"/haku-app/generatelomake/one/$1",

    "lokalisointi.byId":"/lokalisointi/cxf/rest/v1/localisation/$1",
    "lokalisointi.loadAll":"/lokalisointi/cxf/rest/v1/localisation",

    "koodisto-service.ylakoodi":"/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/$1",
    "koodisto-service.alakoodi":"/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/$1",
    "koodisto-service.koodi":"/koodisto-service/rest/json/$1/koodi",
    "koodisto-service.arvo":"/koodisto-service/rest/json/$1/koodi/arvo/$2",

    "koodisto-service.koodiInKoodisto":"/koodisto-service/rest/json/$1/koodi/$2",
    "koodisto-service.search":"/koodisto-service/rest/json/searchKoodis",

    "organisaatio-service.search":"/organisaatio-service/rest/organisaatio/v2/hae",
    "organisaatio-service.ryhmat":"/organisaatio-service/rest/organisaatio/v2/ryhmat",
    "organisaatio-service.byOid":"/organisaatio-service/rest/organisaatio/$1",
    "organisaatio-service.parentOids":"/organisaatio-service/rest/organisaatio/$1/parentoids",

    "organisaatio-ui.linkToOrganisaatio":"/organisaatio-ui/html/organisaatiot/$1",

    "ataru-service.editor":"/lomake-editori/",
    "ataru-service.rest.forms":"/lomake-editori/api/forms",
    "ataru-app-web.form":"/hakemus/$1",


    "oppijanumerorekisteri-service.henkilo":"/oppijanumerorekisteri-service/henkilo",
    "oppijanumerorekisteri-service.henkilotypes":"/oppijanumerorekisteri-service/henkilo/henkilotypes",
    "oppijanumerorekisteri-service.urlHaeTiedot":"/oppijanumerorekisteri-service/henkilo/$1",
    "kayttooikeus-service.organisaatiohenkilo":"/kayttooikeus-service/henkilo/$1/organisaatiohenkilo",
    "kayttooikeus-service.prequel":"/kayttooikeus-service/cas/prequel"
})
