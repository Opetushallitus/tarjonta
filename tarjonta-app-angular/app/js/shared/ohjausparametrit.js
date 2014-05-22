/*
 * Parametri palvelu
 */
angular
    .module('Parameter', [ 'ngResource', 'config', 'Logging' ])

    .factory(
        'ParameterService',
        function($q, $resource, $log, Config, $injector) {
          
          var loadingService = $injector.get('loadingService')

          $log = $log.getInstance("ParameterService");

          var NAME="tarjontaOhjausparametritRestUrlPrefix";
          if(Config.env[NAME]==undefined) {
            throw "'" + NAME + "' ei ole määritelty!";
          }
          var baseUrl = Config.env[NAME] + '/api/v1/rest/parametri';

          var haeUrl = baseUrl + "/:target/:name";

          // Luo resurssi hakemiseen ja päivittämiseen
          var parametrit = $resource(haeUrl, {}, {
            cache : false,
            get : {
              params: {target:"@target", name:"@name"},
              method : "GET",
              isArray : false
            },
            authorize : {
                params: { target : "authorize" },
                method : "GET",
                headers: {"Accept":"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
                withCredentials : true,
                isArray : false
            },
            save : {
              params: {target:"@target", name:"@name"},
              method : "POST",
              withCredentials : true,
              headers: {'Content-Type': 'application/json; charset=UTF-8'}
            }
          });

          /**
           * Parametrien hakeminen.
           * Parauttaa parametrin / parametrit "targetille" oliona.
           * 
           * Esim: /OID -> { PH_HKMT: { date : 98238473 }, PH_XXX: { dateStart: 76764726, dateEnd: 3746364 } }
           * Esim: /OID/PH_HKMT -> { date : 98238473 }
           * 
           * @param {type} hakuehdot, esim: {target : HAKUOID}, {target : HAKUOID, name: "PH_KMT"}
           * @returns {$q@call;defer.promise}
           */
          var haeParametrit = function(hakuehdot) {
            $log.debug('haetaan parametrit, q:', hakuehdot);
            var ret = $q.defer();
            parametrit.get(hakuehdot, function(result) {
              ret.resolve(result);
            }, function(err) {
              loadingService.onErrorHandled();
              ret.resolve({});
              $log.debug("Error loading parameter data", hakuehdot, err);
            });
            return ret.promise;
          };

          /**
           * Tallennetaan parametri annetulle kohteelle (esim. Haku OID), parametrin nimelle (voi olla tyhjä)
           * jos tallennetaan kerralla kaikki.
           * 
           * @param {type} kohde esim. Haku OID
           * @param {type} parametrinNimi voi olla tyhjä, silloin arvo korvaa KAIKKI parametrit
           * @param {type} parametrinArvo yksi tai useampia parametreja - json
           * @returns {promise}
           */
          var tallennaParametrit = function(kohde, parametrinNimi, parametrinArvo) {
              //var hakuehdot = {};
              if (kohde) {
                  parametrinArvo.target = kohde;
              }
              if (parametrinNimi) {
                parametrinArvo.name = parametrinNimi;
              }
              
              $log.debug('tallennetaan parametrit, q:', parametrinArvo);
              

              // TODO miten tähän saatiin se result handleri?
              return parametrit.save(parametrinArvo).$promise;
          };
            
          return {
              
              /**
               * Hakee kohteen parametrit:
               * 
               * Tuloksena esim: {}, { PH_HKMT : { date : 28498897 } }
               * 
               * @param {type} target esim. Haku OID
               * @returns {$q@call;defer.promise}
               */
              haeParametritUUSI : function(target) {
                  return haeParametrit({ target : target });
              },

              /**
               * Hakee nimetyn parametrin.
               * 
               * Tuloksena esim: {}, { date : 28498897 }
               * 
               * @param {type} target
               * @param {type} parametri
               * @returns {$q@call;defer.promise}
               */
              haeParametriUUSI : function(target, parametri) {
                  return haeParametrit({ target : target, name : parametri });
              },

            /**
             * @param hakuOid
             *                haun oidi (== target)
             * @param parametrit
             *                tallennettavat parametrit oliona, esim: { PH_HKMT : {date: 239848747} }
             */
            tallennaUUSI : function(hakuOid, parametritArvo) {
              $log.debug("tallennetaan parametreja:");
              parametrit.authorize();
              $log.debug("tallennetaan parametreja:", hakuOid, parametritArvo);
              return tallennaParametrit(hakuOid, undefined, parametritArvo);
            }

          };
        });
