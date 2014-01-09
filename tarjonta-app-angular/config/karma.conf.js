
module.exports = function(config){
    config.set({
    basePath : '../app',

    files : [
      'lib/underscore/underscore.js',
      'lib/angular/angular.js',
      'lib/angular/angular-resource.js',
      'lib/angular/angular-route.js',
      'lib/angular/angular-mocks.js',
      'js/**/*.js',
      'js/shared/directives/*.html',
      'js/shared/directives/org-puu.js',
      'partials/**/*.js',
      '../test/unit/**/*.js',
      '../test/lib/jasmine.async.node.js',
      'lib/jquery-1.10.2.min.js',
      'jquery-1.10.2.min.map',
      'lib/ngGrid/ng-grid-2.0.7.min.js',
      'lib/ui-bootstrap-0.9.0.js',
      'lib/ui-bootstrap-tpls-0.9.0.js',
      'lib/imageupload.js'
    ],

    autoWatch : true,

        preprocessors : {
        'js/shared/directives/*.html': 'ng-html2js'
    },


    frameworks: ['jasmine'],

    browsers : ['Chrome'],

    plugins : [
	       'karma-junit-reporter', // npm install karma-junit-reporter
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-firefox-launcher',
            'karma-ng-html2js-preprocessor',
            //Install this to node using -> npm -g install karma-ng-html2js-preprocessor
            //'karma-html2js-preprocessor',
            'karma-jasmine'
            ],

    reporters: ['progress', 'junit'],

    junitReporter : {
      outputFile: 'target/surefire-reports/karma-junit-report.xml',
      suite: ''
    }
    });
    };

