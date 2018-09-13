var app = angular.module('CommonUtilServiceModule', [
    'ngResource',
    'config',
    'Logging'
]);
app.service('CommonUtilService', function($resource, $log, $q, Config, OrganisaatioService) {
    var julkaistuVal = 'JULKAISTU';
    var luonnosVal = 'LUONNOS';
    var valmisVal = 'VALMIS';
    var peruttuVal = 'PERUTTU';
    this.canSaveAsLuonnos = function(tila) {
        if (tila === luonnosVal) {
            return true;
        }
        else if (tila === valmisVal || tila === julkaistuVal || tila === peruttuVal) {
            return false;
        }
        else if (tila === undefined) {
            return true;
        }
        else {
            return true;
        }
    };
    this.haeOrganisaationTiedot = function(organisaatioOid) {
        var deferred = $q.defer();
        OrganisaatioService.byOid(organisaatioOid).then(function(vastaus) {
            deferred.resolve(vastaus);
        });
        return deferred.promise;
    };
    this.haeOppilaitostyypit = function(organisaatio) {
        var deferred = $q.defer();
        var oppilaitostyypit = [];
        /*
             * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se != undefined ja ei jo ole siinä
             */
        var addTyyppi = function(organisaatio) {
            if (organisaatio.oppilaitostyyppi !== undefined &&
                oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi) == -1) {
                oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
            }
        };
        if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') != -1 &&
            organisaatio.children !== undefined) {
            //	koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
            for (var i = 0; i < organisaatio.children.length; i++) {
                addTyyppi(organisaatio.children[i]);
            }
            deferred.resolve(oppilaitostyypit);
        }
        else if (organisaatio.organisaatiotyypit.indexOf('OPPILAITOS') != -1 &&
                    organisaatio.oppilaitostyyppi !== undefined) {
            //oppilaitos, kerää tyyppi
            addTyyppi(organisaatio);
            deferred.resolve(oppilaitostyypit);
        }
        else if (organisaatio.organisaatiotyypit.indexOf('TOIMIPISTE') != -1) {
            var findOppilaitosFromParents = function(oid) {
                OrganisaatioService.etsi({
                    aktiiviset: true,
                    suunnitellut: true,
                    oidRestrictionList: oid
                }).then(function(vastaus) {
                    if (vastaus.organisaatiot[0].organisaatiotyypit.indexOf('TOIMIPISTE') !== -1) {
                        findOppilaitosFromParents(vastaus.organisaatiot[0].parentOid);
                    }
                    else {
                        deferred.resolve([vastaus.organisaatiot[0].oppilaitostyyppi]);
                    }
                }, function() {
                        deferred.resolve([]);
                    });
            };
            findOppilaitosFromParents(organisaatio.parentOid);
        }
        else {
            console.log('Tuntematon organisaatiotyyppi:', organisaatio.organisaatiotyypit);
        }
        return deferred.promise;
    };

    this.valintakoeAjankohtaToCurrentTime = function(ajankohta) {
        //Split to get date parts in fin locale
        //Example of split: ["2016", "09", "14", "13", "27", "47", "03", "00"]
        var t = moment.tz(ajankohta, "Europe/Helsinki").format().split(/[^0-9]/),
            year = t[0],
            month = t[1] - 1,
            day = t[2],
            hours = t[3],
            minutes = t[4];
        return new Date(year, month, day, hours, minutes).getTime();
    };

    this.valintakoeAjankohtaToFinnishTime = function(ajankohta) {
        var t = new Date(ajankohta),
            year = t.getFullYear(),
            month = t.getMonth() + 1,
            day = t.getDate(),
            hours = t.getHours(),
            minutes = t.getMinutes(),
            date = day + '-' + month + '-' + year + ' ' + hours + ':' + minutes;
        return moment.tz(date, 'DD-MM-YYYY HH:mm', 'Europe/Helsinki').valueOf();
    }
});