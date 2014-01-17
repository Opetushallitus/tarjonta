var app = angular.module('CommonUtilServiceModule', ['ngResource','config']);


app.service('CommonUtilService',function($resource, $log,$q, Config,OrganisaatioService){

    this.haeOppilaitostyypit = function(organisaatio) {

        var deferred = $q.defer();
        var oppilaitostyypit=[];

        /*
         * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se != undefined ja ei jo ole siinä
         */
        var addTyyppi=function(organisaatio){
            if(organisaatio.oppilaitostyyppi!==undefined && oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi)==-1){
                oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
            }
        };

        if(organisaatio.organisaatiotyypit.indexOf("KOULUTUSTOIMIJA")!=-1 && organisaatio.children!==undefined) {
            //	koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
            for(var i=0;i<organisaatio.children.length;i++) {
                addTyyppi(organisaatio.children[i]);
            }
            deferred.resolve(oppilaitostyypit);
        }

        else if(organisaatio.organisaatiotyypit.indexOf("OPPILAITOS")!=-1 && organisaatio.oppilaitostyyppi!==undefined) {
            //oppilaitos, kerää tyyppi
            addTyyppi(organisaatio);
            deferred.resolve(oppilaitostyypit);
        }

        else if(organisaatio.organisaatiotyypit.indexOf("OPETUSPISTE")!=-1) {
            //opetuspiste, kerää parentin tyyppi
            var parent = $scope.organisaatiomap[organisaatio.parentOid];

            if(undefined!== parent) {
                addTyyppi(parent);
                deferred.resolve(oppilaitostyypit);
            } else {
                //parentti ei ole saatavilla, kysytään organisaatioservicestä
                console.log("organisaatio:", organisaatio);
                OrganisaatioService.etsi({oidRestrictionList:organisaatio.parentOid}).then(function(vastaus) {
                    $scope.organisaatiomap[organisaatio.parentoid] = vastaus.organisaatiot[0].oppilaitostyyppi;
                    deferred.resolve([vastaus.organisaatiot[0].oppilaitostyyppi]);
                }, function(){
                    deferred.resolve([]);
                });
            }
        } else {
            console.log( "Tuntematon organisaatiotyyppi:", organisaatio.organisaatiotyypit );
        }
        return deferred.promise;
    };




});