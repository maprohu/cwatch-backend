requirejs([
    'mfw-web',
    'jquery',
    'angular',
    'openlayers',
    'sockjs-client',
    'stomp-websocket',
    'bootstrap',
    'modules/angular-openlayers-directive',
    'ui-bootstrap-tpls',
], function(
        mfwweb,
        jquery,
        angular,
        ol
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
        
    }).controller('MapCtrl', function($scope, $timeout, $http, olData, olHelpers, olMapDefaults) {
        
        angular.extend($scope, {
            center: {
                lat: 38.705879,
                lon: -9.142667,
                zoom: 6
            },
            mapDefaults: {
                interactions: {
                    mouseWheelZoom: true
                },
                events: {
                    map: [ 'moveend' ]
                }
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
        
        var socket = new SockJS("/web");
        var stompClient = Stomp.over(socket);

        //stompClient.debug = null;
        stompClient.connect({}, function(frame) {
            
//            stompClient.subscribe('/topic/positions', function(position){
//            	console.log(position);
//            });            
            
            $http.get('/service/position/start').success(function(viewId) {
                
                olData.getMap().then(function(map) {
                    var vesselsLayer = olHelpers.createVectorLayer();
                    
//                    var stroke = new ol.style.Stroke({color: 'black', width: 2});
//                    var fill = new ol.style.Fill({color: 'red'});
//                    vesselsLayer.setStyle(
//                        new ol.style.Style({
//                            image: new ol.style.RegularShape({
//                                fill: fill,
//                                stroke: stroke,
//                                points: 3,
//                                radius: 10
//                            }) 
//                        })
//                    );
                    
                    map.addLayer(vesselsLayer);
                    
                    var vessels = {};
                    
                    stompClient.subscribe('/queue/positions/' + viewId, function(position){
                        
                        var mapDefaults = olMapDefaults.getDefaults();
                        var viewProjection = mapDefaults.view.projection;
                        
                        var pos = JSON.parse(position.body);

                        var geometry = new ol.geom.Point([pos.lon, pos.lat]);
                        geometry = geometry.transform('EPSG:4326', viewProjection);
                        
                        var style = new ol.style.Style({
                            image: new ol.style.Icon({
                                src: 'images/vessel.png',
                                scale: 0.4,
                                rotation: pos.hdg * Math.PI / 180
                            }) 
                        });
                        
                        var id = pos.id;
                        var vessel = vessels[id];
                        
                        if (!angular.isDefined(vessel)) {
                            vessel = new ol.Feature({
                                geometry: geometry
                            });
                            vesselsLayer.getSource().addFeature(vessel);
                            vessels[id] = vessel;
                        } else {
                            vessel.setGeometry(geometry);
                        }
                        vessel.setStyle(style);
                        
                    });            
                    
                    $scope.$on('openlayers.map.moveend', function(event) {
                        console.log("boo");
                        var view = map.getView();
                        var size = map.getSize();
                        var center = map.getView().getCenter();
                        stompClient.send("/app/positions/view", {}, JSON.stringify({
                            viewId: viewId,
                            centerLatitude: center[1],
                            centerLongitude: center[0],
                            zoom: view.getZoom(),
                            rotation: view.getRotation(),
                            projection: view.getProjection().getCode(),
                            width: size[0],
                            height: size[1]
                        }));
                        
                    })
                                        

                });
                
                
            });
        });        
        
        
    });
    
    mfwweb.launch();
});
