var protractor = require("gulp-protractor").protractor;
var jshint = require('gulp-jshint');
var gulp   = require('gulp');

gulp.task('lint', function() {
    return gulp.src([
            './app/*.js',
            './app/js/shared/**/*.js',
            './app/partials/**/*.js',
        ])
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

gulp.task('test', function() {
    return gulp.src('./test/e2e/*.js')
        .pipe(protractor({
            configFile: "./config/protractor-e2e-conf.js"
        }))
        .on('error', function(e) { throw e });
});
