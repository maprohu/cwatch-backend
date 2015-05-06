requirejs.config({
    paths: {
        "openlayers3":["/webjars/openlayers/3.4.0/ol","ol"]
    },
    shim:  {
        "openlayers3": {
            exports: "ol"
        },
        'modules/angular-openlayers-directive/angular-openlayers-directive': {
            deps: [
                'angular', 
                'angular-sanitize', 
                'openlayers3'
            ]
        }
    }
    
});

requirejs([
    'mfw-web', 
    'angular',
//    'angular-sanitize',
//    'openlayers',
    'modules/angular-openlayers-directive/angular-openlayers-directive',
//    'angular-openlayers-directive',    
], function(
        mfwweb, 
        angular
) {
    
    angular.module('mfw-app', [
        'mfw-web',
        'openlayers-directive'
    ]);
    
    mfwweb.launch();
});
