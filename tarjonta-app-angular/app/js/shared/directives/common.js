/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
var app = angular.module('CommonDirectives', [
    'ngResource',
    'Koodisto'
]);
app.directive('kuvaus', function() {
    return {
        restrict: 'E',
        replace: true,
        require: 'ngModel',
        scope: {
            ngModel: '=',
            lang: '=',
            tt: '='
        },
        template: '<div>jhhjhkkk<br/><div tt="tt"></div>' + '<div class="help" tt="{{tt}}"></div>' + '<textarea ui-tinymce></textarea>' + '</div>',
        link: function(scope, element, attrs, ngModel) {
            console.log('foo'); // scope.getKuvausApiModelLanguageUri($parse(atts.ngModel), lang);
        }
    };
}) /**
 *
 * tulostaa koodin nimen
 *
 * <koodi uri="jokukoodi_22" lang="fi">
 *
 */ .directive('koodi', function(Koodisto) {
    return {
        restrict: 'EA',
        link: function(scope, element, attrs) {
            var uri = scope.$eval(attrs.uri);
            var lang = scope.$eval(attrs.lang);
            Koodisto.searchKoodi(uri, lang).then(function(data) {
                //console.log(element);
                element.replaceWith(data);
            });
        }
    };
}) /**
 * tulostaa päivämäärän: <t-show-date value="haku.alkoitusPvm" timestamp/>
 * <t-show-date value="haku.alkoitusPvm" timestamp/> <div t-show-date
 * value="haku.alkoitusPvm" timestamp>replaced</div> <div t-show-date
 * value="haku.alkoitusPvm" timestamp/>
 *
 * mikäli arvo ei ole määritelty asettaa watchin ja hoitaa tulostuksen heti kun
 * mahdollista, eli ts dataa ei tarvitse ladata routessa valmiiksi
 */ .directive('tShowDate', function($filter) {
    return {
        restrict: 'EA',
        link: function(scope, element, attrs) {
            var value = scope.$eval(attrs.value);
            if (!value) {
                var unregister = scope.$watch(attrs.value, function(nv, ov) {
                    if (nv) {
                        processValue(nv);
                    }
                });
            }
            var processValue = function(value) {
                // console.log("tShowDate, value: ", attrs.value, value);
                var isLong = typeof value == 'number';
                var date = isLong ? new Date(value) : value;
                var format = 'd.M.yyyy';
                if (angular.isDefined(attrs.timestamp)) {
                    format += ' HH:mm';
                }
                var result = $filter('date')(date, format);
                // console.log("DATE == " + result, format);
                element.replaceWith(result);
                if (unregister) {
                    unregister();
                }
            };
            if (value) {
                processValue(value);
            }
        }
    };
});