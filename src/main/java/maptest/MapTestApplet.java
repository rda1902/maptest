package maptest;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import maptest.model.LonLat;
import maptest.model.LonLatRectangle;
import maptest.service.TransportLocationService;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.data.LocationPoint;
import maptest.service.data.Transport;
import processing.core.PApplet;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

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

            setLocation(toLocation(locationPoint.position));

            super.draw(pg, x, y);
        }
    } 
    
    public void setup() {
        
        size(800, 600);
        map = new UnfoldingMap(this, new OpenStreetMapProvider());
        
        // Show map around the location in the given zoom level.
        map.zoomAndPanTo(new Location(60.0, 30.0), 10);
 
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
            1000
        );
        
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
