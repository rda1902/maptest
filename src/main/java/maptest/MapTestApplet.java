package maptest;


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

            LocationPoint locationPoint = transport.getRecentLocationPoint();

            // 1) Move marker to next location
            
            setLocation(toLocation(locationPoint.position));

            // 2) Draw route if marker is selected
            
            if (isSelected()) {

                TransportRoute route = transport.getRoute(locationPoint.directionId);
            
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
        
        size(1680, 1050);
        map = new UnfoldingMap(this, new OpenStreetMapProvider());
        map.zoomToLevel(10);
        map.panTo(new Location(60.0, 30.0));
 
        // Add mouse and keyboard interactions
        MapUtils.createDefaultEventDispatcher(this, map);

        
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
            5000
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
