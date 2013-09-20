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

        getYlapuolisetKoodit : function(koodiUriParam,locale) {

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
