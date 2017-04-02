package maptest.api;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import maptest.model.LonLat;
import maptest.model.LonLatRectangle;
import maptest.model.locations.TransportLocation;
import maptest.model.locations.TransportLocationReponse;
import maptest.model.routes.RouteRequest;
import maptest.model.routes.TransportRoute;
import maptest.model.routes.TransportRoutesResponce;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;


public class TransportAPI {
		
	String serverString =
		"http://portal.gpt.adc.spb.ru" +
		"/Portal/transport/internalapi";
	
	
	public TransportAPI() {
	}
	
	
	public TransportAPI(String serverString) {
		this.serverString = serverString;
	}

	
	public List<TransportLocation> getLocationsInRectangle(LonLatRectangle rectangle) {

        String query = serverString +
        	"/vehicles/positions/?" +
        	"transports=bus,trolley,tram&bbox=" +
        	rectangle.topLeft.lon 	  + "," +
        	rectangle.topLeft.lat 	  + "," +
        	rectangle.bottomRight.lon + "," +
        	rectangle.bottomRight.lat;
        
        System.out.println("Requesting transport locations: " + query);
        		
        try {
			
        	TransportLocationReponse response =
        		getHttpResponse(query, TransportLocationReponse.class);
			
			if (response.success) {
	        	
		        System.out.println("Transport locations received: " + response.result.size());
				
				return response.result;
	        }
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        
        System.out.println("Failed to get transport location!");
        
        return Collections.emptyList();
    }
	

	public List<TransportRoute> getRoutes(List<RouteRequest> routeRequests) {

		String routes = "";
		String routeDirections = "";
		
		int i = 0;
		
		for (RouteRequest routeRequest : routeRequests) {
			
			routes += routeRequest.routeId;
			routeDirections += routeRequest.routeDirection;
			
			if (i < routeRequests.size() - 1) {
				routes += ",";
				routeDirections += ",";
			}
			
			i++;
		}
		
        String query = serverString +
	        "/routes/stops/?" + 
        	"routeIDs=" + routes +
        	"&directions=" + routeDirections;
        
        System.out.println("Requesting transport routes: " + query);
  
 		
        try {
			
        	TransportRoutesResponce response =
        		getHttpResponse(query, TransportRoutesResponce.class);
			
			if (response.success) {
	        	
				System.out.println("Transport routes received: " + response.result.size());
				
				return response.result;
	        }
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        
        System.out.println("Failed to get transport routes!");
        
        return Collections.emptyList();
    }
	
	
	protected HttpClient httpClient = HttpClientBuilder.create().build();
	
	protected ObjectMapper jsonMapper = new ObjectMapper();
	
	
	protected <T> T getHttpResponse(String query, Class<T> responseClass) throws Exception {
		
		HttpResponse response = httpClient.execute(new HttpGet(query));
        String result = new BasicResponseHandler().handleResponse(response);

        return jsonMapper.readValue(result, responseClass);
	}	


    public static void main(String[] args) throws UnknownHostException, Exception {

    	TransportAPI access = new TransportAPI();
    	
    	List<TransportLocation> transportLocations =
    		access.getLocationsInRectangle(
    			new LonLatRectangle(
					new LonLat(29.675824, 60.153766),
					new LonLat(30.626141, 59.813023)));
    	
    	for (TransportLocation location : transportLocations) {
    		
    		//System.out.println(location);
    	}
    	
    	List<TransportRoute> transportRoutes =
    		access.getRoutes(
    			Collections.singletonList(
    				new RouteRequest(1278, 0)));
    	
    	for (TransportRoute route : transportRoutes) {
    		
    		//System.out.println(route);
    	}
    }
}