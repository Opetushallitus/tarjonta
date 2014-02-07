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

var app = angular.module('CommonDirectives', ['ngResource', 'Koodisto']);

app.directive('kuvaus', function() {
    return {
        restrict: 'E',
        replace: true,
        require: 'ngModel',
        scope: {
            ngModel: '=',
            lang: "=",
            tt: "="
        },
        template:'<div>jhhjhkkk<br/><div tt="tt"></div>' +
                '<div class="help" tt="{{tt}}"></div>' +
                '<textarea ui-tinymce></textarea>' +
                '</div>',
        link: function(scope, element, attrs, ngModel) {
           console.log("foo");
            // scope.getKuvausApiModelLanguageUri($parse(atts.ngModel), lang);
        }
    };
})


/** 
 * tulostaa koodin nimen
 * 
 */
.directive('koodi',function(Koodisto) {
  
    return {
        restrict: 'E',
        scope: {
            uri: '=uri',
            lang: '=lang',
        },
        link: function(scope, element, attrs) {
          Koodisto.searchKoodi(scope.uri, scope.lang).then(
              function(data){
                console.log(element);
                element.replaceWith(data);
                }
              );
          }
        
    };
});

