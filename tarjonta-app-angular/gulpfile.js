var protractor = require("gulp-protractor").protractor;
var jshint = require('gulp-jshint');
var gulp   = require('gulp');
var inject = require('gulp-inject');
var _ = require('lodash');
var concat = require('gulp-concat');
var del = require('del');
var runSequence = require('run-sequence');
var bless = require('gulp-bless');

var BUILD_DIR = './dist';

var jsFiles = require('./config/jsFiles').map(fixPath);
var cssFiles = require('./config/cssFiles').map(fixPath);

function fixPath(path) {
    return './app/' + path;
}

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
        .on('error', function(e) { throw e; });
});

gulp.task('clean', function(cb) {
    return del([BUILD_DIR], cb);
});

gulp.task('copy', function() {
    return gulp.src('./app/**/*').pipe(gulp.dest(BUILD_DIR));
});

gulp.task('inject', function () {
    return gulp.src('./app/index.html')
            .pipe(inject(gulp.src([BUILD_DIR + '/js/all.js', BUILD_DIR + '/css/all.css'], {read:false}), {
                addRootSlash: false,
                ignorePath: '/dist/'
            }))
            .pipe(gulp.dest(BUILD_DIR));
});

gulp.task('injectDev', function () {
    return gulp.src('./app/index.html')
        .pipe(inject(gulp.src(_.flatten([jsFiles, cssFiles]), {read:false}), {
            addRootSlash: false,
            ignorePath: '/app/'
        }))
        .pipe(gulp.dest(BUILD_DIR));
});

gulp.task('concatJS', function () {
    return gulp.src(jsFiles)
            .pipe(concat('all.js'))
            .pipe(gulp.dest(BUILD_DIR + '/js'));
});

gulp.task('concatCSS', function () {
    return gulp.src(cssFiles)
            .pipe(concat('all.css'))
            .pipe(bless())
            .pipe(gulp.dest(BUILD_DIR + '/css'));
});

gulp.task('build:prod', function(cb) {
    runSequence(
        'clean',
        'copy',
        ['concatJS', 'concatCSS'],
        'inject',
        cb
    );
});

gulp.task('build:dev', function(cb) {
    runSequence(
        'clean',
        'copy',
        'injectDev',
        cb
    );
});
