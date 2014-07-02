/**
 * Binds a TinyMCE widget to <textarea> elements.
 */
angular.module('ui.tinymce', [])
  .value('uiTinymceConfig', {})
  .directive('uiTinymce', ['uiTinymceConfig', function (uiTinymceConfig) {
    uiTinymceConfig = uiTinymceConfig || {};
    var generatedIds = 0;
    
    return {
      require: 'ngModel',
      link: function (scope, elm, attrs, ngModel) {
        var expression, options, tinyInstance, interval;

    	function updateView() {
        	clearInterval(interval);
        	interval = setTimeout(function(){
        		var ret = postFilter(elm.val());
        		ngModel.$setViewValue(ret);
        		if (elm.val() != ret) {
        			updateContent(ret);
        		}
                if (!scope.$root.$$phase) {
                	scope.$apply();
                }
                interval = null;
        	}, 100);
        }
          
          
        // generate an ID if not present
        if (!attrs.id) {
          attrs.$set('id', 'uiTinymce' + generatedIds++);
        }

        if (attrs.uiTinymce) {
          expression = scope.$eval(attrs.uiTinymce);
        } else {
          expression = {};
        }
        
        var exprSetup = expression.setup;
        delete expression.setup;
        
        var postFilter = expression.postFilter || function(data) { return data; };
        
        options = {
          // Update model when calling setContent (such as from the source editor popup)
          setup: function (ed) {
            var args;
            ed.on('init', function(args) {
              ngModel.$render();
            });
            // Update model on button click
            ed.on('ExecCommand', function (e) {
              ed.save();
              updateView();
            });
            // Update model on keypress
            ed.on('KeyUp', function (e) {
              ed.save();
              updateView();
            });
            // Update model on change, i.e. copy/pasted text, plugins altering content
            ed.on('SetContent', function (e) {
              if(!e.initial){
                ed.save();
                updateView();
              }
            });
            if (exprSetup) {
            	exprSetup(ed);
              }
          },
          mode: 'exact',
          elements: attrs.id
        };

        // extend options with initial uiTinymceConfig and options from directive attribute value
        angular.extend(options, uiTinymceConfig, expression);

        setTimeout(function () {
          tinymce.init(options);
        });


        function updateContent(content) {
        	if (content == undefined) {
				content = "";
			}
			if (!tinyInstance) {
				tinyInstance = tinymce.get(attrs.id);
			}
			if (tinyInstance && tinyInstance.getContent() != content) {
				tinyInstance.setContent(postFilter(content));
			}
        }
        
        ngModel.$render = function() {
        	updateContent(ngModel.$viewValue || '');
        };

        scope.$watch(attrs.ngModel, function(nv, ov) {
        	//console.log("UPDATE ", [interval, nv, ov]);
        	if (interval==null) {
                updateContent(nv);
        	}
        });
        
      }
    };
  }]);
