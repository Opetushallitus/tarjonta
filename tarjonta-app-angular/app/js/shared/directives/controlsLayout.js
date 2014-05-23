'use strict';

/* Layout-komponentti/direktiivistö formien toimintonappeja, ilmoitustekstejä ja muita yleisiä asioita varten.
 *
 * 1. Direktiivit kommunikoivat scopessa määritellyn olion välityksellä joka controllerin on alustettava tyhjäksi
 *    olioksi:
 *
 * 	 $scope.formControls = {};
 *
 *   - Tämän olion sisältö ei ole osa direktiivistön julkista apia eli siihen ei pidä missään tapauksessa viitata
 *     suoraan.
 *
 * 2. Toiminnot ja ilmoitukset määritellään controls-model-tagilla (tämä ei vielä lisää mitään sisältöä sivulle vaan
 *    kokoaa tiedot modeliin):
 *
 *   <controls-model model="formControls" tt-create="..." tt-edit="..." title="..." dto="...">
 *
 *   	<controls-button primary="true|false" tt="..." action="f()", disabled="f()"/>
 *   	...
 *   	<controls-message type="message|success|error|error-detail" type="" tt="..." tp="..." show="f()"/>
 *   	...
 *
 *   </controls-model>
 *
 *   - Parametrien tarkempi dokumentaatio on alempana, direktiivimääritysten yhteydessä
 *   - Otsikkona näytetään tt-create- (kun luodaan uutta) tai tt-edit (kun muokataan olemassaolevaa) -atribuuteillä
 *     määritelty kielistysavain jolle annetaan parametriksi title -atribuutilla annettu yksi- tai monikielinenteksti
 *     (jolloin näytetään valitun kielen mukainen sisältö), tai pelkkä title:n mukainen teksti jos em. avaimia ei ole
 *     määritelty
 *   - Se, luodaanko uutta vai muokataanko, päätellään dto-parametrin mukaan (olemassaolevasta oletetaan löytyvän created-
 *     ja createdBy -arvot); em. oliosta tarkastetaan myös arvo 'tila', joka näytetään muiden metatietojen kanssa
 *
 *   Ilmoitustyypit:
 *
 *   message		Normaali ilmoitusviestityyli, näytetään vain headerissa. Esim. "Olet muokkaamassa..."
 *   success		Ilmoitus onnistumisesta; tätä ei näytetä, jos yksikin error- tai error-detail -viesti on näkyvissä.
 *   error			Päätason virheviesti, esim. "Tallennus epäonnistui"
 *   error-detail   Virheviestin tarkennus, validointia ja muuta varten, esim. "Kenttä X on pakollinen"
 *
 * 3. Määritelly napit ja ilmoitukset näytetään sivulla display-controls -tagilla:
 *
 *   <display-controls model="formControls" display="header|footer"/>
 *
 *   - Display-parametri määrittää kumpi variaatio näytetään; headerin ja footerin ero on käytännössä se, että footerissa
 *     ei näytetä message-tyypin ilmoitustekstiä vaan vaakaviiva (riippumatta siitä onko kyseisentyyppistä viestiä
 *     määritelty.
 *
 * TODO
 *
 *   - Sivun otsikon ja muokkaustietojen (kuka ja milloin) näyttäminen headerissa.
 *   - X-nappi jolla virheviestin saa piilotettua
 *   - Virheviestien tarkennusten piilotus niiden määrän ollessa suuri (esim. näytä lisää -linkki)
 *
 *
 */

var app = angular.module('ControlsLayout', ['localisation', 'Yhteyshenkilo', 'Logging']);

app.directive('displayControls', function($log, LocalisationService, $filter, YhteyshenkiloService) {
    
    $log = $log.getInstance("<displayControls>");

    return {
        restrict: 'E',
        templateUrl: "js/shared/directives/controlsLayout.html",
        replace: true,
        scope: {
            model: "=", // model johon controls-model viittaa
            display: "@" // header|footer
        },
        controller: function($scope) {

            switch ($scope.display) {
                case "header":
                case "footer":
                    break;
                default:
                    throw new Error("Invalid display type: " + $scope.display);
            }

            $scope.t = function(k, a) {
                return LocalisationService.t(k, a);
            }

            function showMessage(msgs, msg) {
                if (msg == undefined) {
                    for (var i in msgs) {
                        var ms = msgs[i].show();
                        //$log.debug(msgs[i].tt+" -> MS=",ms);
                        if (ms == undefined || ms == null || ms == true) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    var ms = msg.show();
                    return ms == undefined || ms == null || ms == true;
                }
            }

            $scope.showErrorDetail = function(msg) {
                return showMessage($scope.model.notifs.errorDetail, msg);
            };

            $scope.showError = function(msg) {
                if (msg == undefined) {
                    return showMessage($scope.model.notifs.error) || showMessage($scope.model.notifs.errorDetail);
                }
                return showMessage($scope.model.notifs.error, msg);
            };

            $scope.showSuccess = function(msg) {
                return showMessage($scope.model.notifs.success, msg) && (msg != null || !$scope.showError());
            };

            $scope.showMessage = function(msg) {
                return showMessage($scope.model.notifs.message, msg);
            };

            $scope.dto = $scope.model.dto();
            $scope.model.metadata = [];

            function appendMetadata(md, key, user, timestamp) {
                if (!user && !timestamp) {
                    return;
                }
                if (!user || user.length == 0) {
                    user = LocalisationService.t("tarjonta.metadata.unknown");
                }
                
                var msg = LocalisationService.t(key,
                        [$filter("date")(timestamp, "d.M.yyyy"), $filter("date")(timestamp, "H:mm"), user]);
                if (md.indexOf(msg)==-1) { // jostain tulee duplikaatteja??
                	md.push(msg);
                }
            }
            //Tuomas Katva, OVT-6946  5.3.2014 watch for 'modified' property and update the layout when it is changed
            $scope.$watch('dto.modified', function(newValue, oldValue) {
                /*if (angular.isUndefined(newValue) && angular.isUndefined(oldValue)) {
                    //missing date.
                    return;
                } else if (newValue !== oldValue || $scope.model.metadata.length === 0) {*/
                    $scope.model._reloadDisplayControls();
                //}
            });
            
            
            $scope.model.reloadDisplayControls = function() {
            	// TODO poista tämä delegaattifunktio kun virheelliset viittaukset on poistettu
            	alert("reloadDisplayControls() EI OLE OSA DIREKTIIVIN JULKISTA APIA ELI ÄLÄ KÄYTÄ!!!");
            	$scope.model._reloadDisplayControls();
            }

            /*
             * Reload modified&status data.
             */
            $scope.model._reloadDisplayControls = function() {
                $scope.dto = $scope.model.dto();
            	//console.log("controlsLayout._reloadDisplayControls() ", [ $scope.dto.modified, $scope.dto.modifiedBy, $scope.dto.created, $scope.dto.createdBy ]);

                $scope.model.metadata = [];

                // oletuksenä näytetään muokkaustiedot
                var userOid = $scope.dto.modifiedBy;
                var date = $scope.dto.modified;
                var lokalisointiKey = "tarjonta.metadata.modified";
                
                if (!date) { // ei muokkaustietoja -> näytetään luontitiedot
                    userOid = $scope.dto.createdBy;
                    date = $scope.dto.created;
                    lokalisointiKey = "tarjonta.metadata.created";
                	
                }

                // tila
                if ($scope.dto.tila) {
                    $scope.model.metadata.push(LocalisationService.t("tarjonta.tila." + $scope.dto.tila));
                }

                //load user info by oid
                if (userOid) {
                    var promise = YhteyshenkiloService.haeHenkilo(userOid);
                    promise.then(function(response) {
                        var name = "";
                        if (response.etunimet) {
                            name = response.etunimet + " ";
                        }
                        if (response.sukunimi) {
                            name += response.sukunimi;
                        }
                        appendMetadata($scope.model.metadata, lokalisointiKey, name, date);
                    }, function(){
                        appendMetadata($scope.model.metadata, lokalisointiKey, userOid, date);
                    });
                } else {
                    appendMetadata($scope.model.metadata, lokalisointiKey, userOid, date);
                }
            };

            $scope.isNew = function() {
                return !$scope.dto.oid && $scope.model.metadata.length==0;
            }

            function titleText() {
                var title = $scope.model.title();
                if ((typeof title) == "object") {
                    if (title[LocalisationService.getLocale()]) {
                        // käyttäjän localen mukaan
                        return title[LocalisationService.getLocale()];
                    } else {
                        // vakiolocalejen mukaan
                        for (var i in window.CONFIG.app.userLanguages) {
                            var k = window.CONFIG.app.userLanguages[i];
                            if (title[k]) {
                                return title[k];
                            }
                        }
                        // 1. vaihtoehto
                        for (var i in title) {
                            var k = title[i];
                            return title[window.CONFIG.app.userLanguages[i]];
                        }
                    }
                }
                return title;
            }

            $scope.getTitle = function() {
                var ttext = titleText();
                if (!angular.isString(ttext)) {
                    //ttext is an objects, but we need text title
                    return "";
                }

                var tkey = $scope.isNew() ? $scope.model.ttCreate : $scope.model.ttEdit;

                return tkey == null ? ttext : LocalisationService.t(tkey, [ttext]);
            }

            return $scope;
        }
    }

});

app.directive('controlsModel', function($log) {

    $log = $log.getInstance("<controlsModel>");

    return {
        restrict: 'E',
        template: "<div style=\"display:none;\" ng-transclude></div>",
        replace: true,
        transclude: true,
        scope: {
            model: "=", // viittaus modeliin
            ttCreate: "@", // otsikkoavain, jota käytetään luotaessa uutta
            ttEdit: "@", // otsikkoavain, jota käytetään muokattaessa olemassaolevaa
            title: "&", // otsikkoteksti (string tai monikielinen teksti), joka annetaan ttEdit:lle / ttCreate:lle parametriksi
            dto: "&" // dto, josta haetaan muokkaustiedot (created, createdBy, ...); ttCreate/ttEdit valitaan näiden tietojen mukaan
        },
        controller: function($scope) {
            $scope.model.notifs = {
                message: [],
                success: [],
                error: [],
                errorDetail: []
            }
            $scope.model.buttons = [];

            $scope.model.ttCreate = $scope.ttCreate;
            $scope.model.ttEdit = $scope.ttEdit;
            $scope.model.title = $scope.title;
            $scope.model.dto = $scope.dto;

            return $scope;
        }
    }

});

app.directive('controlsButton', function($log) {

    $log = $log.getInstance("<controlsButton>");

    return {
        restrict: 'E',
        //replace: true,
        require: "^controlsModel",
        link: function(scope, element, attrs, controlsLayout) {
            controlsLayout.model.buttons.push({
                ttKey: scope.ttKey,
                primary: scope.primary,
                action: scope.action,
                disabled: scope.disabled,
                icon: scope.icon});
        },
        scope: {
            ttKey: "@", // otsikko (lokalisaatioavain)
            primary: "@", // boolean; jos tosi, nappi on ensisijainen (vaikuttaa vain ulkoasuun)
            action: "&", // funktio jota klikatessa kutsutaan
            disabled: "&", // funktio jonka perusteella nappi disabloidaan palauttaessa true
            icon: "@"	   // napin ikoni (viittaus bootstrapin icon-x -luokkaan)

        }
    }
});

app.directive('controlsNotify', function($log) {

    $log = $log.getInstance("<controlsNotify>");

    return {
        restrict: 'E',
        ///replace: true,
        require: "^controlsModel",
        link: function(scope, element, attrs, controlsLayout) {

            var notifs;
            switch (scope.type) {
                case "message":
                case "success":
                case "error":
                    notifs = controlsLayout.model.notifs[scope.type];
                    break;
                case "error-detail":
                    notifs = controlsLayout.model.notifs.errorDetail;
                    break;
                default:
                    throw new Error("Invalid notification type: " + scope.type);
            }

            notifs.push({
                ttExpr: function(){
                	var ret = scope.ttExpr();
                	if (!ret) {
                		throw new Error("ttExpr returned illegal translation key: '"+ret+"'");
                	}
                	return ret;
                },
                ttParams: scope.ttParams,
                type: scope.type,
                show: scope.show});

            $log.info("controlsNotify - notifs = ", notifs);

        },
        scope: {
            ttExpr: "&", // viesti (lokalisaatioavain); huom.: ei string vaan expr
            ttParams: "&", // lokalisaatioavaimen parametrit
            type: "@", // ilmoituksen tyyli: message|success|error|error-detail
            show: "&"      // funktio jonka perusteella viesti näytetään
        }
    }
});
