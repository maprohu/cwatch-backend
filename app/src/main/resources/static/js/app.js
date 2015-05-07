requirejs([
    'mfw-web',
    'jquery',
    'angular',
    'sockjs-client',
    'bootstrap',
    'modules/angular-openlayers-directive',
    'ui-bootstrap-tpls'
], function(
        mfwweb,
        jquery,
        angular
) {
    
    angular.module('mfw-app', [
        'mfw-web',
        'openlayers-directive',
        'ui.bootstrap'
    ]).controller('TabsCtrl', function($scope, $templateCache) {
        
        $scope.tabdata = {};
        
        $templateCache.put("template/tabs/tabset.html",
            "\n" +
            "<div>\n" +
            "  <ul class=\"nav nav-{{type || 'tabs'}}\" ng-class=\"{'nav-stacked': vertical, 'nav-justified': justified}\" ng-transclude></ul>\n" +
            "  <div class=\"vbox boxItem tab-content\">\n" +
            "    <div class=\"vbox boxItem tab-pane\" \n" +
            "         ng-repeat=\"tab in tabs\" \n" +
            "         ng-class=\"{active: tab.active}\"\n" +
            "         tab-content-transclude=\"tab\">\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</div>\n" +
            "");
        
    }).controller('MapCtrl', function($scope, $timeout,  olData) {
        
        angular.extend($scope, {
            center: {
                lat: 38.705879,
                lon: -9.142667,
                zoom: 6
            },
            map: {
                interactions: {
                    mouseWheelZoom: true
                },
            },
        });
        
        $scope.resizeMap = function() {
            olData.getMap().then(function(map) {
                $timeout(function() {
                    map.updateSize(); 
                });
            });
        };
        
        olData.getMap().then(function(map) {
            $timeout(function() {
                var vp = angular.element(map.getViewport());
                var view = map.getView();
                vp.css('width', '');
                vp.css('height', '');
                var canvas = vp.children().first();
                canvas.css('width', '');
                canvas.css('height', '');
            });
        });
        
        var socket = new SockJS("/app/portfolio");
        var stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            
            stompClient.subscribe('/topic/positions', function(position){
                console.log(position);
            });            
            
        });        
        
    });
    
    mfwweb.launch();
});
