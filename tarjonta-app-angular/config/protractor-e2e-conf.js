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
            args: ['disable-web-security', 'user-agent=protractorTest']
        }
    },
    baseUrl: 'http://localhost:8080'
}
