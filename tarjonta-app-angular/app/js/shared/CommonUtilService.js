var app = angular.module('CommonUtilServiceModule', ['ngResource','config']);


app.service('CommonUtilService',function($resource, $log,$q, Config,OrganisaatioService){

    var julkaistuVal = "JULKAISTU";

    var luonnosVal = "LUONNOS";

    var valmisVal = "VALMIS";

    var peruttuVal = "PERUTTU";

    this.canSaveAsLuonnos = function(tila) {

        if (tila === luonnosVal) {

            return true;

        } else if (tila === valmisVal || tila === julkaistuVal || tila === peruttuVal) {

            return false;

        } else if (tila === undefined) {

            return true;

        } else {
            return true;
        }


    },

    this.haeOrganisaationTyypit = function(organisaatioOid) {

        var deferred = $q.defer();

        OrganisaatioService.byOid(organisaatioOid).then(function(vastaus) {
            deferred.resolve([vastaus.organisaatiot[0].organisaatiotyypit]);
        });

        return deferred.promise;

    }

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
                //parentti ei ole saatavilla, kysytään organisaatioservicestä
                OrganisaatioService.etsi({oidRestrictionList:organisaatio.parentOid}).then(function(vastaus) {
                    deferred.resolve([vastaus.organisaatiot[0].oppilaitostyyppi]);
                }, function(){
                    deferred.resolve([]);
                });

        } else {
            console.log( "Tuntematon organisaatiotyyppi:", organisaatio.organisaatiotyypit );
        }
        return deferred.promise;
    };




});