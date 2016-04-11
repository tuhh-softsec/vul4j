var builder = require('jenkins-js-builder');

var gulp = require('gulp');
gulp.task('lint', function () {
});

//
// Bundle the modules.
// See https://github.com/jenkinsci/js-builder
//
builder.bundle('src/main/js/buildactionresultsdisplay.js')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3', {addDefaultCSS: true})
    .inDir('src/main/webapp/jsbundles').minify();

builder.bundle('src/main/js/floatingBox.js')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3', {addDefaultCSS: true})
    .inDir('src/main/webapp/jsbundles').minify();
