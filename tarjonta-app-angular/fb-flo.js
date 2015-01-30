var flo = require('fb-flo');
var fs = require('fs');
var dirToWatch = './app';

var server = flo(
    dirToWatch,
    {
        port: 8888,
        host: 'localhost',
        verbose: false,
        glob: [
            '**/*.js',
            '**/*.css',
            '**/*.html'
        ]
    },
    function resolver(filepath, callback) {

        callback({
            resourceURL: filepath,
            contents: fs.readFileSync(dirToWatch + '/' + filepath),
            update: function (_window, _resourceURL) {

                console.log('update');

                _resourceURL = _resourceURL.replace('.notFound', '');

                var el = angular.element($("[data-ng-include=\"'" + _resourceURL + "'\"]")[0]);
                if (el.length === 0) {
                    el = angular.element($("[ng-include=\"'" + _resourceURL + "'\"]")[0]);
                }
                if (el.length === 0) {
                    angular.element('body').scope().$root.$digest();
                    console.log('No matching element found');
                    return;
                }

                var $injector = angular.element('body').injector();
                var $compile = $injector.get('$compile');
                var $http = $injector.get('$http');

                console.log('Element found', el.length);

                $http.get(_resourceURL).success(function(contents) {
                    var newHtml = $compile(contents)(el.scope());
                    el.html(newHtml);
                    angular.element('body').scope().$root.$digest();
                });

                console.log("Resource " + _resourceURL + " has just been updated with new content");
            }
        });
    }
);
