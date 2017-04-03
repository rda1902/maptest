package maptest;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import maptest.model.LonLat;
import maptest.model.LonLatRectangle;
import maptest.model.routes.TransportRoute;
import maptest.service.TransportLocationService;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.data.LocationPoint;
import maptest.service.data.Transport;
import maptest.service.eval.LocationPointApproxymator;
import processing.core.PApplet;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;


public class MapTestApplet extends PApplet {

    TransportLocationService transportLocationService;

    UnfoldingMap map;

    
    public class TransportMarker extends SimplePointMarker {

        Transport transport;

        public TransportMarker(Transport transport) {
            super(toLocation(transport.getRecentLocationPoint().position));

            this.transport = transport;
        }

        @Override
        public void draw(PGraphics pg, float x, float y) {

            LocationPoint recentLocationPoint = transport.getRecentLocationPoint();
            LocationPoint approximatedLocationPoint = transport.getApproximatedLocationPoint();

            // 1) Move marker to next location
            
            setLocation(toLocation(approximatedLocationPoint.position));

            
            if (isSelected()) {
                
                // 2) Draw recent location
                
                ScreenPosition recent = map.getScreenPosition(toLocation(recentLocationPoint.position));
                ellipse(recent.x, recent.y, 10, 10);
                
                // 3) Draw route
                
                TransportRoute route = transport.getRoute(recentLocationPoint.directionId);
            
                if (route != null) {
                    
                    List<LonLat> points = route.path;
                    
                    LonLat prevPoint = null; //points.get(points.size() - 1);
                                        
                    for (LonLat point : points) {
                        
                        if (prevPoint != null) {
                            ScreenPosition from = map.getScreenPosition(toLocation(prevPoint));
                            ScreenPosition to = map.getScreenPosition(toLocation(point));
                            
                            line(from.x, from.y, to.x, to.y);
                        }
                        
                        prevPoint = point;
                    }
                }
            }
            
            super.draw(pg, x, y);
        }
    } 
    
    
    public void setup() {
        
        /* 1) Setun map */
        
        size(1680, 1050);
        map = new UnfoldingMap(this, new OpenStreetMapProvider());
        map.zoomToLevel(10);
        map.panTo(new Location(60.0, 30.0));
 
        MapUtils.createDefaultEventDispatcher(this, map);

        
        /* 2) Init TransportLocationService */
        
        transportLocationService = new TransportLocationService(
            
            /* Setting callback to obtain newly received transports */    
                
            new NewTransportsAddedCallback() {
                
                @Override
                public void onNewTransportsAdded(List<Transport> transports) {
                                        
                    synchronized (map) {    
                    
                        System.out.println("Add markers");
                        
                        for (Transport transport : transports) {
                            
                            map.addMarker(
                                new TransportMarker(transport)); 
                        }
                    }
                }
            });
        
        
        /* 3) Locations update task */
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
                
                @Override
                public void run() {
                    
                    Location topLeft = map.getTopLeftBorder();
                    Location bottomRight = map.getBottomRightBorder(); 
                    
                    LonLatRectangle displayedMapRectangle =
                        new LonLatRectangle(
                            new LonLat(
                                topLeft.getLon(),
                                topLeft.getLat()),
                            new LonLat(
                                bottomRight.getLon(),
                                bottomRight.getLat()));
                    
                    transportLocationService.updateLocations(displayedMapRectangle);
                }
            }, 
            0,
            10000 // every 10 seconds 
        );
        
        
        /* 4) Locations approximation update task */
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
                
                @Override
                public void run() {
                    
                    List<Marker> markers;
                    
                    synchronized (map) {
                        
                        markers = new ArrayList<Marker>(
                            map.getDefaultMarkerManager().getMarkers());
                    }
                    
                    for (Marker marker : markers) {
                                                    
                        LocationPointApproxymator.applyApproximation(
                            ((TransportMarker) marker).transport);
                    }
                }
            }, 
            0,
            100 // every 0.1 seconds 
        );
        
    }
    
    
    @Override
    public void mouseClicked()
    {        
        Marker hitMarker =
            map.getFirstHitMarker(mouseX, mouseY);
        
        if (hitMarker != null) {
        
            hitMarker.setSelected(!hitMarker.isSelected());
        }
    }
    
    
    public void draw() {
        
        synchronized (map) {
            
            map.draw();
        }
    }
     
    
    protected Location toLocation(LonLat location) {
        
        return new Location(
            location.lat,
            location.lon);
    }
    
    
    private static final long serialVersionUID = 1L;
}
