package maptest.service.data;

import java.util.Deque;
import java.util.LinkedList;

import maptest.model.routes.TransportRoute;


public class Transport {
        
    public int vehicleId;
    
    public String vehicleLabel;
    
    public Integer orderNumber;
    
    public String licensePlate;
    
    
    protected TransportRoute[] routes = new TransportRoute[2]; // 2 directions
    
    protected Deque<LocationPoint> locations = new LinkedList<>();

    protected LocationPoint approximatedLocationPoint;
    
    
    public Transport(
        int vehicleId,
        String vehicleLabel,
        Integer orderNumber,
        String licensePlate)
    {
        this.vehicleId = vehicleId;
        this.vehicleLabel = vehicleLabel;
        this.orderNumber = orderNumber;
        this.licensePlate = licensePlate;
    }
    
    
    public synchronized void addRoute(TransportRoute route) {
        
        this.routes[route.direction] = route;
    }
    
    
    public synchronized TransportRoute getRoute(int direction) {
        
        return routes[direction];
    }
    
    
    public synchronized void addLocationPoint(LocationPoint locationPoint) {
        
        // Check if timestamp already exists
        if (!locations.isEmpty()) {
            if (locations.getLast().timestamp.equals(locationPoint.timestamp)) {
                return;
            }
        }
        
        locations.add(locationPoint);
    }
    
    
    public synchronized LocationPoint getRecentLocationPoint() {
        
        return locations.getLast();
    }
    
    
    public synchronized void setApproxymatedLocationPoint(LocationPoint approximatedLocationPoint) {
        
        this.approximatedLocationPoint = approximatedLocationPoint;
    }
    
    
    public synchronized LocationPoint getApproximatedLocationPoint() {
        
        return approximatedLocationPoint;
    }
}