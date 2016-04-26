var app = angular.module('RichTextArea', [
    'Koodisto',
    'localisation',
    'pasvaz.bindonce',
    'ui.tinymce',
    'ngSanitize'
]);
app.directive('richTextarea', function(LocalisationService, $log, $sce) {
    'use strict';
    function RichTextareaController($scope) {
        var validElements = '@[align|style|lang],p,h1,h2,h3,h4,h5,h6,a[href|target],strong,b,em,i,div,span,br,' +
            'table,tbody,thead,tr,td[colspan|rowspan|width|valign],ul,ol,li,img[src],sup,sub';
        $scope.commands = {};
        $scope.directive = {
            model: $scope.model ? $scope.model : null
        };
        $scope.dataListener = $scope.$watch('model', function(val) {
            $scope.directive.model = $scope.model;
        });
        function execCommand(cmd, arg) {
            console.log('execCommand ', [
                cmd,
                arg
            ]);
            $scope.editor.execCommand(cmd, false, arg);
        }
        function replaceElements(node, from, to) {
            $(from, node).each(function(i, em) {
                var e = $(em);
                // tyhjä -> poista
                if (e.text().trim() === '') {
                    e.remove();
                    return;
                }
                e.wrap($('<' + to + '></' + to + '>'));
                e.contents().unwrap();
            });
        }
        function postFilterElement(em, d) {
            var align = em.css('text-align');
            var deco = em.css('text-decoration');
            // jos style:
            // - jos elementti on span, salli yli- ja alleviivaus, muutoin -> none
            if (em.context.tagName != 'SPAN') {
                deco = 'none';
            }
            // - jos elementti on td tai th, salli text-align, muutoin -> start
            if (em.context.tagName != 'TD' && em.context.tagName != 'TH') {
                align = 'start';
            }
            em.css({
                'text-align': align,
                'text-decoration': deco
            });
        }
        function postFilter(src) {
            if (!angular.isDefined(src)) {
                return src;
            }
            var data = $('<div>' + src + '</div>');
            data.find('[style]').each(function(i, em) {
                postFilterElement($(em));
            });
            $scope.model = angular.copy(data[0].innerHTML);
            return src;
        }
        $scope.tinymceOptions = {
            height: '100%',
            statusbar: false,
            menubar: 'format table insert',
            menu: {
                format: {
                    title: 'Format',
                    items: 'bold italic underline strikethrough | _aligns _headings | subscript superscript' +
                            ' | removeformat'
                },
                table: {
                    title: 'Table',
                    items: 'inserttable tableprops deletetable | cell row column'
                },
                insert: {
                    title: 'Insert',
                    items: 'bullist numlist | link'
                }
            },
            resize: false,
            schema: 'html5',
            language: LocalisationService.getLocale(),
            plugins: 'link table paste lists advlist',
            //extended_valid_elements: "span[style|class|lang],div[style|class|lang]",
            //valid_elements: validElements,
            paste_word_valid_elements: validElements,
            postFilter: postFilter,
            setup: function(editor) {
                //console.log("SETUP ",editor);
                $scope.editor = editor;
                editor.addMenuItem('bullist', {
                    text: 'Bullet list',
                    icon: 'bullist',
                    onclick: function() {
                        editor.execCommand('InsertUnorderedList');
                    }
                });
                editor.addMenuItem('numlist', {
                    text: 'Numbered list',
                    icon: 'numlist',
                    onclick: function() {
                        editor.execCommand('InsertOrderedList');
                    }
                });
                editor.addMenuItem('_aligns', {
                    text: 'Alignment',
                    //separator: "before",
                    menu: [
                        {
                            text: 'Justify',
                            icon: 'alignjustify',
                            onclick: function() {
                                execCommand('justifyFull');
                            }
                        },
                        {
                            text: 'Align left',
                            icon: 'alignleft',
                            onclick: function() {
                                execCommand('justifyLeft');
                            }
                        },
                        {
                            text: 'Align right',
                            icon: 'alignright',
                            onclick: function() {
                                execCommand('justifyRight');
                            }
                        },
                        {
                            text: 'Align center',
                            icon: 'aligncenter',
                            onclick: function() {
                                execCommand('justifyCenter');
                            }
                        }
                    ]
                });
                editor.addMenuItem('_headings', {
                    text: 'Headers',
                    menu: [
                        {
                            text: 'Header 1',
                            onclick: function() {
                                execCommand('formatBlock', 'h1');
                            }
                        },
                        {
                            text: 'Header 2',
                            onclick: function() {
                                execCommand('formatBlock', 'h2');
                            }
                        },
                        {
                            text: 'Header 3',
                            onclick: function() {
                                execCommand('formatBlock', 'h3');
                            }
                        },
                        {
                            text: 'Header 4',
                            onclick: function() {
                                execCommand('formatBlock', 'h4');
                            }
                        },
                        {
                            text: 'Header 5',
                            onclick: function() {
                                execCommand('formatBlock', 'h5');
                            }
                        },
                        {
                            text: 'Header 6',
                            onclick: function() {
                                execCommand('formatBlock', 'h6');
                            }
                        }
                    ]
                });
                /* Esim. linkkityökalun kautta tehty linkkimuutos ei päivity modeliin,
                 * joten tällä varmistetaan, että viimeistään tallennuksen yhteydessä
                 * wysiwyg-kentän sisältö asetetaan modeliin.
                 */
                editor.on('blur', function() {
                    $scope.model = $scope.editor.getContent();
                    $scope.$apply();
                });
            },
            paste_preprocess: function(plugin, args) {
                console.log('Pasting:', args.content);
            },
            paste_postprocess: function(plugin, args) {
                var node = $(args.node);
                // tyhjät kappaleet rivinvaihdoiksi <p></p> -> <br/>
                $('p', node).each(function(i, em) {
                    var e = $(em);
                    if (e.text().trim() === '') {
                        e.replaceWith('<br/>');
                    }
                });
                // TODO filtteröinti valid-elementsin mukaan tehdään vasta tallennettaessa joten liitettävä html
                // olisi periaatteessa mahdollista filtteröidä jo tässä vaiheessa
                // strong -> b
                replaceElements(node, 'strong', 'b');
                // em -> i
                replaceElements(node, 'em', 'i');
                // tyylit
                $('[style]', node).each(function(i, em) {
                    var e = $(em);
                    // säilytä h/v align jos td tai th, poista muut
                    if (em.localName != 'th' && em.localName != 'td') {
                        e.attr('style', null);
                        return;
                    }
                    var halign = e.css('text-align');
                    var valign = e.css('vertical-align');
                    e.attr('style', null);
                    e.css('text-align', halign);
                    e.css('vertical-align', valign);
                });
                // align -> css
                $('[align]', node).each(function(i, em) {
                    var e = $(em);
                    var align = e.attr('align');
                    e.attr('align', null);
                    // jos elementti on td tai th
                    if (em.localName == 'td' || em.localName == 'th') {
                        e.css('text-align', align);
                    }
                });
                console.log('Pasted:', node);
            },
            toolbar: false //tools:"inserttable"
            //toolbar_items_size:"small"
            //content_css:"/css/bootstrap.css,/css/virkailija.css,/css/app.css"
        };
        $scope.model = $scope.model ? $scope.model : '';
        $scope.showMax = $scope.max !== undefined && $scope.max !== null && $scope.max > 0;
        $scope.html = function() {
            return $sce.trustAsHtml($scope.model);
        };
        $scope.isEmpty = function() {
            return !$scope.model || $scope.model.trim().length === 0;
        };
        $scope.charCount = function() {
            return $($scope.element).text().length;
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/richTextarea.html',
        controller: RichTextareaController,
        scope: {
            model: '=',
            // teksti
            disableEdit: '&',
            max: '@',
            // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
            onChange: '&' // funktio, jota kutsutaan modelin muuttuessa
        },
        link: function(scope, element, attrs) {
            scope.rootElement = element;
            scope.element = $('.previewBody', element);
        }
    };
});