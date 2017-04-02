package maptest.service.data;

import maptest.model.LonLat;


public class LocationPoint {
	
    public String timestamp;
  
    public LonLat position;
    
    public int direction;

    public int velocity;
    	    
    public int directionId;

    
	public LocationPoint(
		String timestamp,
		LonLat position,
		int direction,
		int velocity,
		int directionId)
	{
		this.timestamp = timestamp;
		this.position = position;
		this.direction = direction;
		this.velocity = velocity;
		this.directionId = directionId;
	}	
}