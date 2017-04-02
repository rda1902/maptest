package maptest.service;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maptest.api.TransportAPI;
import maptest.model.LonLatRectangle;
import maptest.model.locations.TransportLocation;
import maptest.model.routes.RouteIdentifier;
import maptest.model.routes.TransportRoute;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.data.LocationPoint;
import maptest.service.data.Transport;


public class TransportLocationService {

    TransportAPI api = new TransportAPI();
    
    TIntObjectHashMap<Transport> transports = new TIntObjectHashMap<>();
    
    THashMap<RouteIdentifier, TransportRoute> routes = new THashMap<>();
    
    
    NewTransportsAddedCallback newTransportsAddedCallback;
    
    
    public TransportLocationService(
        NewTransportsAddedCallback newTransportsAddedCallback)
    {
        this.newTransportsAddedCallback = newTransportsAddedCallback;
    }
    
    
    public void updateLocations(LonLatRectangle rectangle) {
        
        System.out.println("\nTransportLocationService.updateLocations()");
        
        
        /* 1) Get locations in rectangle from API */
        
        List<TransportLocation> locations =
            api.getLocationsInRectangle(rectangle);
        
        
        /* 2) Process each location and find transports and routes for them */
        
        List<Transport> newTransports = new ArrayList<>();
        
        Set<RouteIdentifier> unknownRouteIdentifiers = new HashSet<>();
        
        THashMap<RouteIdentifier, List<Transport>> unknownRouteIdentifier2transports = new THashMap<>();
        
        for (TransportLocation location : locations) {
            
            Transport transport =
                transports.get(location.vehicleId);
            
            if (transport == null) {
                
                // Add new transport
                
                transport = new Transport(
                    location.vehicleId,
                    location.vehicleLabel,                
                    location.orderNumber,
                    location.licensePlate);
                
                transports.put(location.vehicleId, transport);
                
                newTransports.add(transport);
                
                // Find route for new transport
                
                RouteIdentifier routeIdentifier =
                    new RouteIdentifier(
                        location.routeId,
                        location.directionId);
                
                TransportRoute route = routes.get(routeIdentifier);
                
                if (route != null) {
                    
                    // Ok, route already exists
                    
                    transport.addRoute(route);
                }
                else {
                    
                    // This route is unknown, adding it to request list
                    
                    unknownRouteIdentifiers.add(routeIdentifier);
                    
                    List<Transport> transportsWithUnknownRoute =
                        unknownRouteIdentifier2transports.get(routeIdentifier);
                    
                    if (transportsWithUnknownRoute == null) {
                        transportsWithUnknownRoute = new ArrayList<>();
                        unknownRouteIdentifier2transports.put(routeIdentifier, transportsWithUnknownRoute);
                    }
                    
                    transportsWithUnknownRoute.add(transport);                    
                }        
            }
                        
            LocationPoint locationPoint =
                new LocationPoint(
                    location.timestamp,
                    location.position,
                    location.direction,
                    location.velocity,
                    location.directionId);
            
            transport.addLocationPoint(locationPoint);
        }
        
        System.out.println("New transports received: " + newTransports.size());
        
        /* Notify consumer about new transports */
        
        if (!newTransports.isEmpty()) {
            newTransportsAddedCallback.onNewTransportsAdded(newTransports);
        }
        
        
        /* 3) Get unknown routes from API */
        
        System.out.println("Unknown routes: " + unknownRouteIdentifiers.size());
        
        int batchSize = 100; // How much routes API can return at once
        int couter = 0;        
        
        List<RouteIdentifier> unknownRouteIdentifiersBatch = new ArrayList<>(batchSize);
        
        List<TransportRoute> newRoutes = new ArrayList<>();
                
        for (RouteIdentifier routeIdentifier : unknownRouteIdentifiers) {
            
            unknownRouteIdentifiersBatch.add(routeIdentifier);
            
            couter++;
            
            if (unknownRouteIdentifiersBatch.size() == batchSize
                || couter == unknownRouteIdentifiers.size())
            {
                newRoutes.addAll(
                    api.getRoutes(unknownRouteIdentifiersBatch));
                
                unknownRouteIdentifiersBatch.clear();
            }
        }    
        
        
        /* 4) Attach new routes to transports */
        
        for (TransportRoute route : newRoutes) {
            
            RouteIdentifier routeIdentifier =
                new RouteIdentifier(
                    route.routeId,
                    route.direction);
            
            List<Transport> transports =
                unknownRouteIdentifier2transports.get(routeIdentifier);
            
            if (transports == null) {
                System.out.println("Error in API: unexpected routeId");
                continue;
            }
            
            for (Transport transport : transports) {
                transport.addRoute(route);
            }
            
            routes.put(routeIdentifier, route);
        }
        
        System.out.println("Total transports: " + transports.size());
        System.out.println("Total routes: " + routes.size());
    }
}
