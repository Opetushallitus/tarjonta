var app = angular.module('Koodisto', ['ngResource']);

app.factory('Koodisto',function($resource, $log,$q){
    //TODO: This should be replaced with something better, just for testing
    var host = "https://itest-virkailija.oph.ware.fi";

    var nimiWithLocale =   function(locale,metadata) {
        var metas = _.select(metadata,function(koodiMetaData) {

            if (koodiMetaData.kieli === locale) {

                return koodiMetaData.nimi;
            }
        });

        if (metas.length === 1 && metas.length > 0) {
            return metas[0].nimi;
        } else {
            return "";
        }
    } ;


    /*
        This JS-object is view representation of koodisto koodi.
        Example koodisto Koodi:

       {
            koodiArvo :"",
            koodiUri  : "",
            koodiTila : "",
            koodiVersio : "",
            koodiKoodisto : "",
            koodiOrganisaatioOid :"",
            -> Koodinimi is localized with given locale
            koodiNimi : ""
       }

     */

    var getKoodiViewModelFromKoodi = function(koodi,locale) {
        var tarjontaKoodi  = {
            koodiArvo : koodi.koodiArvo,
            koodiUri : koodi.koodiUri,
            koodiTila : koodi.tila,
            koodiVersio : koodi.versio,
            koodiKoodisto : koodi.koodisto.koodistoUri,
            koodiOrganisaatioOid : koodi.koodisto.organisaatioOid,
            koodiNimi : nimiWithLocale(locale,koodi.metadata)
        };
        return tarjontaKoodi
    };

    return {

        /*
            @param {array} array of koodis received from Koodisto.
            @param {string} locale in which koodi name should be shown
            @returns {array} array of koodi view model objects
         */

        convertKoodistoKoodiToViewModelKoodi : function(koodisParam,locale) {

            var koodis = [];

            angular.forEach(koodisParam,function(koodi){
                koodis.push(getKoodiViewModelFromKoodi(koodi,locale));
            });
            return koodis;
        },

        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getYlapuolisetKoodit : function(koodiUriParam,locale) {

            $log.info('getYlapuolisetKoodit called with : ' + koodiUriParam + ' locale : ' + locale );

            var returnYlapuoliKoodis= $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + '/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri,{koodiUri:'@koodiUri'}).query({koodiUri:koodiUriParam},function(koodis){
                angular.forEach(koodis,function(koodi){

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi,locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },

        getAlapuolisetKoodit : function(koodiUriParam,locale) {

            $log.info('getAlapuolisetKoodi called with : ' + koodiUriParam + ' locale : ' + locale );

            var returnYlapuoliKoodis= $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + '/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri,{koodiUri:'@koodiUri'}).query({koodiUri:koodiUriParam},function(koodis){
                angular.forEach(koodis,function(koodi){

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi,locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },

        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getAllKoodisWithKoodiUri : function(koodistoUriParam, locale) {


            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);

            var returnKoodisPromise = $q.defer();

            var returnKoodis = [];

            var koodiUri = host + '/koodisto-service/rest/json/:koodistoUri/koodi';


            $resource(koodiUri,{koodistoUri : '@koodistoUri'}).query({koodistoUri:koodistoUriParam},function(koodis){



                angular.forEach(koodis,function(koodi){



                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi,locale));
                });
                returnKoodisPromise.resolve(returnKoodis);
            });

            return returnKoodisPromise.promise  ;
        } ,

        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodisto view model objects
         */

        getKoodistoWithKoodiUri : function(koodiUriParam,locale) {

            var returnKoodi = $q.defer();


            var koodiUri = host + "/koodisto-service/rest/json/:koodistoUri";

            console.log('Calling getKoodistoWithKoodiUri with : ' + koodiUriParam + ' ' +locale);

            var resource = $resource(koodiUri,{koodistoUri : '@koodistoUri'}).get({koodistoUri:koodiUriParam},function(data){
                var returnTarjontaKoodi = {
                    koodistoUri : data.koodistoUri,
                    tila : data.tila
                };
                returnKoodi.resolve(returnTarjontaKoodi);

            });
            console.log('Returning promise from getKoodistoWithKoodiUri');
            return returnKoodi.promise;
        }

    };

});
