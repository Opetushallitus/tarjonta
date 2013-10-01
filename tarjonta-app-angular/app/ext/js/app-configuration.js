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
window.CONFIG.app = {
    "developer.config.location": ['dev/dev-configuration.json'],
    "tarjonta.tilat": ["LUONNOS", "VALMIS", "JULKAISTU", "PERUTTU", "KOPIOITU"],
    "tarjonta.koulutusaste.korkeakoulut": [60, 61, 62, 63, 70, 71, 72, 73, 80, 81, 82, 90],
    "tarjonta.koulutusaste.korkeakoulu-uris": ["koulutusasteoph2002_60", "koulutusasteoph2002_61", "koulutusasteoph2002_62", "koulutusasteoph2002_63", "koulutusasteoph2002_70", "koulutusasteoph2002_71", "koulutusasteoph2002_72", "koulutusasteoph2002_73", "koulutusasteoph2002_80", "koulutusasteoph2002_81", "koulutusasteoph2002_82", "koulutusasteoph2002_90"],
};
