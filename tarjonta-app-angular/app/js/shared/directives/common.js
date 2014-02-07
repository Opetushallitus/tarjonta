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
 * 
 * tulostaa koodin nimen
 * 
 * <koodi uri="jokukoodi_22" lang="fi">
 * 
 */
.directive('koodi',function(Koodisto) {
  
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
          var uri = scope.$eval(attrs.uri);
          var lang = scope.$eval(attrs.lang);
          Koodisto.searchKoodi(uri, lang).then(
              function(data){
                //console.log(element);
                element.replaceWith(data);
                }
              );
          }
        
    };
})


/** 
 * tulostaa päivämäärän:
 * <t-date value="haku.alkoitusPvm" timestamp="true"/>
 */
.directive('tShowdate',function() {
  
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
          console.log(attrs);
          console.log(scope);
          console.log(element);
          var isLong = !"long" !== attrs.type;
          var isTimestamp = attrs.timestamp!==undefined||true;
          var value = scope.$eval(attrs.value);
          var date = isLong?new Date(value):value;
          
          var d = date.getDate();
          var m = date.getMonth() + 1;
          var y = date.getFullYear();
          var datestring = (d <= 9 ? '0' + d : d) + '. ' + (m<=9 ? '0' + m : m) + '. ' + y;
          
          
          
          console.log("value:", value);
          if(value) {
            element.replaceWith(datestring);
          } else {
            "-"
          }
        }
        
    };
});


