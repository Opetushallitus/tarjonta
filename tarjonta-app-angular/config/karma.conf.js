module.exports = function(config){
    config.set({
    basePath : '../app',

    files : [
      JASMINE,
      JASMINE_ADAPTER,
      'lib/underscore/underscore.js',
      'lib/angular/angular.js',
      'lib/angular/angular-resource.js',
      '../test/lib/angular/angular-mocks.js',
      'js/**/*.js',
      'js/shared/directives/*.html',
      'partials/**/*.js',
      '../test/unit/**/*.js'
    ],

    autoWatch : true,

        preprocessors : {
        'js/shared/directives/*.html': 'ng-html2js'
    },


    frameworks: ['jasmine'],

    browsers : ['Chrome'],

    plugins : [
            'karma-junit-reporter',
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

