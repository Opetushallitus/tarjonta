var app = angular.module('TinyMceRichText', ['ngResource']);


app.directive('tinyMceRichText',function($compile){
    return {
        restrict: 'E',
        replace : true,
        scope: {
            text : "="
        },

        link : function(scope, element, attrs, ngModel) {


            //Helper function to create unique class id for tinymce
            var idMaker = function ()
            {
                var text = "";
                var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

                for( var i=0; i < 5; i++ )
                    text += possible.charAt(Math.floor(Math.random() * possible.length));

                return text;
            };

            //Create unique id
            var randomId = idMaker();
            //Create tinymce selector string
            var selectorVal =  "textarea."+randomId;



            //Create and compile text are for tinymce
            var markup = "<textarea class="+randomId+">{{text}}</textarea>";

            angular.element(element).html($compile(markup)(scope));

            //Initialize tinymce and hook callbacks on it to update the bound variable
            tinymce.init({
                menu : {},
                toolbar : 'formatselect,bold,italic,underline,strikethrough,numlist,bullist,|,undo,redo,|,link,table,|,pastetext,pasteword,selectall,|,removeformat',
                entity_encoding : 'raw',
                statusbar : false,
                apply_source_formatting : false,
                remove_linebreaks : true,
                selector: selectorVal,
                setup : function(ed) {
                    ed.on('change', function(e) {
                      scope.text = ed.getContent();
                    });
                    ed.on('blur',function(e){
                        scope.text = ed.getContent();
                    });
                    ed.on('keyup',function(e){
                        scope.text = ed.getContent();
                    });
                }
            });





        }
    }


});

