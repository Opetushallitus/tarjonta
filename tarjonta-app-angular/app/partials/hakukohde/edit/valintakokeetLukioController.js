var app = angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetLukioController',
    function ($scope, $filter, LocalisationService, dialogService) {

        var getPisterajat = function(valintakoe, targetPisterajaTyyppi) {
            for(var i in valintakoe.pisterajat) {
                var pisterajatyyppi = valintakoe.pisterajat[i].pisterajatyyppi;
                if(pisterajatyyppi === targetPisterajaTyyppi) {
                    return valintakoe.pisterajat[i];
                }
            }
            return undefined;
        }

        var removePaasykoe = function() {
            $scope.valintakoe.paasykoePisterajat = [];
            $scope.valintakoe.kuvaukset = {};
            $scope.valintakoe.valintakoeAjankohtas = [];
            for(var i = $scope.valintakoe.pisterajat.length - 1; i >= 0; i--) {
                if($scope.valintakoe.pisterajat[i].pisterajatyyppi === 'Paasykoe') {
                    $scope.valintakoe.pisterajat.splice(i, 1);
                }
            }
        };

        var removeLisanaytot = function() {
            $scope.valintakoe.lisanaytotPisterajat = [];
            $scope.valintakoe.lisanaytot = {};
            for(var i = $scope.valintakoe.pisterajat.length - 1; i >= 0; i--) {
                if($scope.valintakoe.pisterajat[i].pisterajatyyppi === 'Lisapisteet') {
                    $scope.valintakoe.pisterajat.splice(i, 1);
                }
            }
        }

        var removeKokonaispisteet = function() {
            $scope.valintakoe.kokonaispisteetPisterajat = [];
            for(var i = $scope.valintakoe.pisterajat.length - 1; i >= 0; i--) {
                if($scope.valintakoe.pisterajat[i].pisterajatyyppi === 'Kokonaispisteet') {
                    $scope.valintakoe.pisterajat.splice(i, 1);
                }
            }
        };

        var newAjankohta = function() {
            return {
                lisatiedot: "",
                alkaa: null,
                loppuu: null,
                osoite: {
                    osoiterivi1: "",
                    postinumero: "",
                    postitoimipaikka: "",
                    postinumeroArvo: ""
                }
            }
        }

        var init = function() {
            $scope.valintakoe = $scope.model.hakukohde.valintakokeet[0];

            if($scope.valintakoe === undefined) {
                $scope.valintakoe = {};
                $scope.model.hakukohde.valintakokeet.push($scope.valintakoe);
            }

            if($scope.valintakoe.valintakoeAjankohtas === undefined) {
                $scope.valintakoe.valintakoeAjankohtas = [];
            }

            if($scope.valintakoe.pisterajat === undefined) {
                $scope.valintakoe.pisterajat= [];
            }

            if($scope.valintakoe.kuvaukset === undefined) {
                $scope.valintakoe.kuvaukset = {};
            }

            if($scope.valintakoe.lisanaytot === undefined) {
                $scope.valintakoe.lisanaytot = {};
            }

            $scope.valintakoe.paasykoePisterajat = getPisterajat($scope.valintakoe, 'Paasykoe');
            $scope.valintakoe.lisanaytotPisterajat = getPisterajat($scope.valintakoe, 'Lisapisteet');
            $scope.valintakoe.kokonaispisteetPisterajat = getPisterajat($scope.valintakoe, 'Kokonaispisteet');

            $scope.valintakoe.hasPaasykoe = $scope.valintakoe.paasykoePisterajat !== undefined;
            $scope.valintakoe.hasLisanaytot = $scope.valintakoe.lisanaytotPisterajat !== undefined;

            if($scope.valintakoe.paasykoePisterajat === undefined) {
                $scope.valintakoe.paasykoePisterajat = {};
            }

            if($scope.valintakoe.lisanaytotPisterajat === undefined) {
                $scope.valintakoe.lisanaytotPisterajat = {};
            }

            if($scope.valintakoe.kokonaispisteetPisterajat === undefined) {
                $scope.valintakoe.kokonaispisteetPisterajat = {};
            }
        }

        init();

        $scope.$on('reloadValintakokeet', function(){
            init();
        });

        $scope.kokonaispisteetChanged = function() {
            var value = $scope.valintakoe.kokonaispisteetPisterajat.alinHyvaksyttyPistemaara;
            if(value.length == 0) {
                removeKokonaispisteet();
            } else {
                if(getPisterajat($scope.valintakoe, 'Kokonaispisteet') == undefined) {
                    $scope.valintakoe.pisterajat.push({pisterajatyyppi: "Kokonaispisteet", alinHyvaksyttyPistemaara: value});
                    $scope.valintakoe.kokonaispisteetPisterajat = $scope.valintakoe.pisterajat[$scope.valintakoe.pisterajat.length - 1];
                }
            }
        }

        $scope.paasykoeStateChanged = function() {
            if(!$scope.valintakoe.hasPaasykoe) {
                dialogService.showDialog({
                        title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.pisterajat.paasykoe.title"),
                        description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.pisterajat.paasykoe")
                    }).result.then(function (ret) {
                        if (ret) {
                            removePaasykoe();
                            if(!$scope.valintakoe.hasLisanaytot) {
                                removeKokonaispisteet();
                            }
                        } else {
                            $scope.valintakoe.hasPaasykoe = true;
                        }
                    });
            } else {
                $scope.valintakoe.hasPaasykoe = true;
                $scope.valintakoe.pisterajat.push({pisterajatyyppi: "Paasykoe"})
                $scope.valintakoe.paasykoePisterajat = $scope.valintakoe.pisterajat[$scope.valintakoe.pisterajat.length - 1];
                $scope.addAjankohta($scope.valintakoe);
            }
        };

        $scope.lisanaytotStateChanged = function() {
            if(!$scope.valintakoe.hasLisanaytot) {
                dialogService.showDialog({
                    title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.pisterajat.lisanaytot.title"),
                    description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.pisterajat.lisanaytot")
                }).result.then(function (ret) {
                        if (ret) {
                            removeLisanaytot();
                            if(!$scope.valintakoe.hasPaasykoe) {
                                removeKokonaispisteet();
                            }
                        } else {
                            $scope.valintakoe.hasLisanaytot = true;
                        }
                    });
            } else {
                $scope.valintakoe.hasLisanaytot = true;
                $scope.valintakoe.pisterajat.push({pisterajatyyppi: "Lisapisteet"})
                $scope.valintakoe.lisanaytotPisterajat = $scope.valintakoe.pisterajat[$scope.valintakoe.pisterajat.length - 1];
            }
        };

        $scope.addAjankohta = function (valintakoe) {
            valintakoe.valintakoeAjankohtas.push(newAjankohta());
            $scope.status.dirtify();
        };

        $scope.deleteAjankohta = function (valintakoe, ajankohta, confirm) {
            if (!ajankohta.alkaa && !ajankohta.loppuu
                && !ajankohta.osoite.osoiterivi1
                && !ajankohta.osoite.postinumero) {
                confirm = true;
            }
            if (confirm) {
                if (ajankohta == valintakoe.selectedAjankohta) {
                    valintakoe.selectedAjankohta = newAjankohta();
                }
                var p = valintakoe.valintakoeAjankohtas.indexOf(ajankohta);
                if (p != -1) {
                    valintakoe.valintakoeAjankohtas.splice(p, 1);
                }
                $scope.status.dirtify();
            } else {
                dialogService
                    .showDialog({
                        title: LocalisationService
                            .t("tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta.title"),
                        description: LocalisationService
                            .t(
                            "tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta",
                            [
                                valintakoe.valintakoeNimi,
                                    $filter("date")(ajankohta.alkaa,
                                        "d.M.yyyy H:mm")
                                    || "?",
                                    $filter("date")(ajankohta.loppuu,
                                        "d.M.yyyy H:mm")
                                    || "?"])
                    }).result.then(function (ret) {
                        if (ret) {
                            $scope.deleteAjankohta(valintakoe, ajankohta, true);
                        }
                    });
            }
        };
    });
