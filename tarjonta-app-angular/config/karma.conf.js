module.exports = function(config){
    config.set({
    basePath : '../',

    files : [
      JASMINE,
      JASMINE_ADAPTER,
      'app/lib/underscore/underscore.js',
      'app/lib/angular/angular.js',
      'app/lib/angular/angular-resource.js',
      'test/lib/angular/angular-mocks.js',
      'app/js/**/*.js',
      'app/partials/**/*.js',
      'test/unit/**/*.js'
    ],

    autoWatch : true,


    frameworks: ['jasmine'],

    browsers : ['Chrome'],

    plugins : [
            'karma-junit-reporter',
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-firefox-launcher',
            'karma-jasmine'
            ],

    reporters: ['progress', 'junit'],

    junitReporter : {
      outputFile: 'target/surefire-reports/karma-junit-report.xml',
      suite: ''
    }
    });
    };

