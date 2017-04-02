package maptest.service;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maptest.api.TransportAPI;
import maptest.model.LonLatRectangle;
import maptest.model.locations.TransportLocation;
import maptest.model.routes.RouteRequest;
import maptest.model.routes.TransportRoute;
import maptest.service.callback.NewTransportsAddedCallback;
import maptest.service.data.LocationPoint;
import maptest.service.data.Transport;


public class TransportLocationService {

	TransportAPI api = new TransportAPI();
	
	TIntObjectHashMap<Transport> transports = new TIntObjectHashMap<>();
	
	TIntObjectHashMap<TransportRoute> routes = new TIntObjectHashMap<>();
	
	
	NewTransportsAddedCallback newTransportsAddedCallback;
	
	
	public TransportLocationService(
		NewTransportsAddedCallback newTransportsAddedCallback)
	{
		this.newTransportsAddedCallback = newTransportsAddedCallback;
	}
	
	
	public void updateLocations(LonLatRectangle rectangle) {
		
		System.out.println("TransportLocationService.updateLocations()");
		
		
		/* 1) Get locations in rectangle from API */
		
		List<TransportLocation> locations =
			api.getLocationsInRectangle(rectangle);
		
		
		/* 2) Process each location and find transports and routes for them */
		
		List<Transport> newTransports = new ArrayList<>();
		
		Set<RouteRequest> unknownRouteRequests = new HashSet<>();
		
		TIntObjectHashMap<List<Transport>> unknownRouteId2transports = new TIntObjectHashMap<>();
		
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
				
				TransportRoute route = routes.get(location.routeId);
				
				if (route != null) {
					
					// Ok, route already exists
					
					transport.route = route;
				}
				else {
					
					// This route is unknown, adding it to request list
					
					unknownRouteRequests.add(
						new RouteRequest(
							location.routeId,
							location.directionId));
					
					List<Transport> transportsWithUnknownRouteId =
						unknownRouteId2transports.get(location.routeId);
					
					if (transportsWithUnknownRouteId == null) {
						transportsWithUnknownRouteId = new ArrayList<>();
						unknownRouteId2transports.put(location.routeId, transportsWithUnknownRouteId);
					}
					
					transportsWithUnknownRouteId.add(transport);					
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
		
		System.out.println("Unknown routes: " + unknownRouteRequests.size());
		
		int batchSize = 100; // How much routes API can return at once
		int couter = 0;		
		
		List<RouteRequest> unknownRouteRequestsBatch = new ArrayList<>(batchSize);
		
		List<TransportRoute> newRoutes = new ArrayList<>();
				
		for (RouteRequest routeRequest : unknownRouteRequests) {
			
			unknownRouteRequestsBatch.add(routeRequest);
			
			if (unknownRouteRequestsBatch.size() == batchSize
				|| couter == unknownRouteRequests.size())
			{
				newRoutes.addAll(
					api.getRoutes(unknownRouteRequestsBatch));
				
				unknownRouteRequestsBatch.clear();
			}
		}	
		
		
		/* 4) Attach new routes to transports */
		
		for (TransportRoute route : newRoutes) {
			
			List<Transport> transports = unknownRouteId2transports.get(route.routeId);
			
			if (transports == null) {
				System.out.println("Error in API: unexpected routeId");
				continue;
			}
			
			for (Transport transport : transports) {
				transport.route = route;
			}
			
			routes.put(route.routeId, route);
		}
	}
	
}
