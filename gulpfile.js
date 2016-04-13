var builder = require('jenkins-js-builder');

//
// Bundle the modules.
// See https://github.com/jenkinsci/js-builder
//
builder.bundle('src/main/js/buildactionresultsdisplay.js')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3')
    .less('src/main/less/bootstrapprefix.less')
    .inDir('src/main/webapp/jsbundles').minify();

builder.bundle('src/main/js/floatingBox.js')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3')
    .inDir('src/main/webapp/jsbundles').minify();

/*builder.bundle('src/main/js/projectaction.js')
    .withExternalModuleMapping('bootstrap-detached', 'bootstrap:bootstrap3', {addDefaultCSS: true})
    .withExternalModuleMapping('jquery', 'bootstrap:bootstrap3')
 .inDir('src/main/webapp/jsbundles');*/

builder.defineTasks(['test', 'bundle', 'rebundle']);
builder.defineTask('lint', function () {
});
