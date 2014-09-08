var credentials = require('./protractor-credentials.js');

exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            /**
            Älä välitä CORS virheistä. Osa palveluista
            ajetaan lokaalisti ja osa luokalta, ja CORS
            headerit eivät aina ole asetettu...
            */
            args: ['disable-web-security']
        }
    },
    baseUrl: 'http://localhost:8080',
    allScriptsTimeout: 20000,
    onPrepare: function() {
        // Kirjaudu järjestelmään ja aseta session keksi
        browser.driver.get('http://' + encodeURIComponent(credentials.username) + ':' +
            encodeURIComponent(credentials.password) +
            '@localhost:8080/tarjonta-service/rest/v1/permission/authorize');
    },
    jasmineNodeOpts: {
        onComplete: null,
        isVerbose: true,
        showColors: true,
        includeStackTrace: true,
        defaultTimeoutInterval: 60000 // 60s
    }
}