/*
 * Parametri palvelu
 */
angular
    .module('Parameter', [ 'ngResource', 'config' ])

    .factory(
        'ParameterService',
        function($q, $resource, $log, Config) {
          var NAME="tarjontaOhjausparametritResutUrlPrefix";
          if(Config.env[NAME]==undefined) {
            throw "'" + NAME + "' ei ole määritelty!";
          }
          var baseUrl = Config.env[NAME] + '/api/rest/parametri';

          var prefixes = [ "PH_" ];
          haeUrl = baseUrl + "/:path/:name";
          haeTemplatetUrl = baseUrl + "/template/:path/:name";
          tallennaUrl = baseUrl;

          var parametrit = $resource(haeUrl, {}, {
            cache : false,
            get : {
              method : "GET",
              // withCredentials : true,
              isArray : true
            }
          });

          var templatet = $resource(haeTemplatetUrl, {}, {
            cache : true,
            get : {
              method : "GET",
              // withCredentials : true,
              isArray : true
            }
          });
          
          var haeTemplatet=function(hakuehdot) {
            $log.debug('haetaan templatet, q:', hakuehdot);
            var ret = $q.defer();
            templatet.get(hakuehdot, function(result) {
              ret.resolve(result);
            }, function(err) {
              console.error("Error loading template data", err);
            });
            return ret.promise;
          };

          var haeParametrit = function(hakuehdot) {
            $log.debug('haetaan parametrit, q:', hakuehdot);
            var ret = $q.defer();
            parametrit.get(hakuehdot, function(result) {
              ret.resolve(result);
            }, function(err) {
              console.error("Error loading data", err);
            });
            return ret.promise;
          };

          var tallennaParametri = function(parametri) {
            var tallenna = $resource(baseUrl + "/" + parametri.path + "/"
                + parametri.name, {}, {
              save : {
                method : "PUT"
              }
            });
            var param = {
              value : parametri.value
            };
            tallenna.save(param);
          };

          return {

            /**
             * Hakee parametrit hakuehdoilla, esim
             * 
             * <pre>
             * {
             *   &quot;path&quot; : &quot;a&quot;,
             *   &quot;name&quot; : &quot;b&quot;
             * }
             * </pre>
             * 
             * hakee parametriarvot joiden pathi alkaa a:lla ja nimi on b
             * 
             * @returns promise
             */
            haeParametrit : haeParametrit,

            /**
             * Hakee templatet hakuehdoilla, esim
             * 
             * <pre>
             * {
             *   &quot;path&quot; : &quot;a&quot;
             * }
             * </pre>
             * 
             * hakee templatet joiden pathi alkaa a:lla
             * 
             * @returns promise
             */
            haeTemplatet : haeTemplatet,

            /**
             * 
             * @param hakuOid
             *                haun oidi
             * @param parametri
             *                tallennettavat parametrit listassa
             */
            tallenna : function(hakuOid, parametrit) {

              console.log("tallemnnetaan parametreja:", parametrit);
              // console.log("target:", hakuOid);

              // query all parameters
              var currentParams = {};
              var promises = [];
              // haetaan nykyiset parametrit

              for ( var i = 0; i < prefixes.length; i++) {
                (function(prefix) {
                  var p = haeParametrit({
                    path : prefix,
                    name : hakuOid
                  }).then(function(params) {
                    console.log("current params from service", params);
                    for ( var i = 0; i < params.length; i++) {
                      console.log(params[i]);
                      currentParams[params[i].path] = params[i];
                    }
                  });
                  promises.push(p);
                })(prefixes[i]);
              }
              console.log("current parameters retrieved");
              $q.all(promises).then(function() {
                console.log("saving parameters from form:", parametrit);
                for ( var key in parametrit) {
                  tallennaParametri({
                    name : hakuOid,
                    path : key,
                    value : parametrit[key]
                  });
                  console.log("p:", key, parametrit[key]);
                }
              });

              console.log("exit from service");
              //TODO update all changed
            },
            
            /**
             * Hakee haun parametrit
             * @param hakuOid haun oidi
             * @param target kohde olio johon parametrit asetetaan key-value pareina.
             * @returns nothing
             */
            haeHaunParametrit:function(hakuOid, target){
              var paramTemplates={};
              for ( var i = 0; i < prefixes.length; i++) {

                (function(prefix) {
                  var p = haeTemplatet({
                    path : prefix
                  }).then(function(params) {
                    for ( var i = 0; i < params.length; i++) {
                      // var path = params[i].path;
                      // model.parameter[path]=undefined;
                      paramTemplates[params[i].path] = params[i];
                    }
                    ;
                  });

                  p.then(function() {
                    haeParametrit({
                      path : prefix,
                      name : hakuOid
                    }).then(function(params) {
                      for ( var i = 0; i < params.length; i++) {
                        var path = params[i].path;
                        var type = paramTemplates[path].type;
                        var value = params[i].value;
                        console.log("path, type", path, type);
                        // if("DATE"===type) {
                        // value=new Date(value);
                        // }

                        target[path] = value;
                      }
                    });
                  });
                })(prefixes[i]);
              }
            }
          };
        });