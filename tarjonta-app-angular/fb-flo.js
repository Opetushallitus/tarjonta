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
            '**/*.css'
        ]
    },
    function resolver(filepath, callback) {
        callback({
            resourceURL: filepath,
            contents: fs.readFileSync(dirToWatch + '/' + filepath),
            update: function (_window, _resourceURL) {
                console.log("Resource " + _resourceURL + " has just been updated with new content");
            }
        });
    }
);