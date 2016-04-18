var builder = require('@jenkins-cd/js-builder');

//
// Bundle the modules.
// See https://github.com/jenkinsci/js-builder
//
builder.bundle('src/main/js/buildactionresultsdisplay.js').minify();
builder.bundle('src/main/js/floatingBox.js').minify();
builder.bundle('src/main/less/bootstrapprefix.less');

//builder.bundle('src/main/js/projectaction.js')

builder.defineTasks(['test', 'bundle']);
builder.defineTask('lint', function () {
});
