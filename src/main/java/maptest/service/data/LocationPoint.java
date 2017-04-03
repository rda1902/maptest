package maptest.service.data;

import org.joda.time.DateTime;

import maptest.model.LonLat;


public class LocationPoint {
    
    public DateTime timestamp;
  
    public LonLat position;
    
    public int direction;

    public int velocity;
            
    public int directionId;

    
    public LocationPoint(
        DateTime timestamp,
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