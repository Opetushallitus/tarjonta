//All application directory names:
var KK_TUTKINTO = 'kkTutkinto';
var MAIN = 'tarjonta';

//Angular modules:
var NG_ROUTE = 'ngRoute';
var NG_RESOURCE = 'ngResource';

//All sub module file names:
var SERVICES = '.services';
var FILTERS = '.filters';
var DIRECTIVES = '.directives';
var CTRL = '.controllers';

var APP_PREFIX = 'App';

//All available services:
var TARJONTA_SERVICE = MAIN + APP_PREFIX + SERVICES;

function toApp(appNameWithoutPrefix) {
    return appNameWithoutPrefix + APP_PREFIX;
}

function toSub(appNameWithoutPrefix, subModule) {
    return appNameWithoutPrefix + APP_PREFIX + subModule;
}

function toDependency(appName, arr) {
    //add module base files
    arr.push(
            appName + APP_PREFIX + CTRL,
            appName + APP_PREFIX + DIRECTIVES,
            appName + APP_PREFIX + FILTERS,
            appName + APP_PREFIX + SERVICES);
    return arr;
}

function toTemplateUrl(moduleName, fileName) {
    //add module base files
    return 'partials/' + moduleName + '/' + fileName + '.html';
}

