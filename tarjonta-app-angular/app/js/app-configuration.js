/*
 * Help:
 * Add service factory(/js/shared/config.js) to your module.
 * Module name : 'config'.
 * Factory name : 'Config'.
 *
 * FAQ:
 * How to get an environment variable by a key: <factory-object>.env[<string-key>].
 * How to get AngularJS application variable by a key: <factory-object>.app[<string-key>].
 *
 * Example:
 * cfg.env["koodi-uri.koulutuslaji.nuortenKoulutus"];
 * result value : "koulutuslaji_n"
 */
/*
 * Defined for unit tests.
 */
window.CONFIG = window.CONFIG || {};

window.CONFIG.app = {
    'tarjonta.tilat': {}, // HUOM! sisältö haetaan index.html:ssä
    'userLanguages': ['kieli_fi', 'kieli_sv', 'kieli_en'], //default languages
    'tarjonta.koulutusaste.korkeakoulut': [60, 61, 62, 63, 70, 71, 72, 73, 80, 81, 82, 90],
    'tarjonta.koulutusaste.korkeakoulu-uris':  ['koulutusasteoph2002_60',
                                                'koulutusasteoph2002_61',
                                                'koulutusasteoph2002_62',
                                                'koulutusasteoph2002_63',
                                                'koulutusasteoph2002_70',
                                                'koulutusasteoph2002_71',
                                                'koulutusasteoph2002_72',
                                                'koulutusasteoph2002_73',
                                                'koulutusasteoph2002_80',
                                                'koulutusasteoph2002_81',
                                                'koulutusasteoph2002_82',
                                                'koulutusasteoph2002_90'],
    'koodisto-uris.tutkintotyyppi': 'tutkintotyyppi',
    'koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto': 'tutkintotyyppi_13',
    'koodisto-uri.tutkintotyyppi.ylempiKorkeakoulututkinto': 'tutkintotyyppi_14',
    'haku.kohdejoukko.kk.uri' : 'haunkohdejoukko_12',
    'haku.kohdejoukko.aiku.uri' : 'haunkohdejoukko_10',
    'haku.kohdejoukko.aikuistenPerusopetus.uri': 'haunkohdejoukko_19',
    'haku.hakutapa.erillishaku.uri' : 'hakutapa_02',
    'haku.hakutapa.jatkuvahaku.uri' : 'hakutapa_03',
    'root.organisaatio.oid':'1.2.246.562.10.00000000001',
    'ui.timeout.short': 10000,
    'ui.timeout.long': 60000,
    'errorlog.disabled': false,
    'errorlog.timeout': 60000
};