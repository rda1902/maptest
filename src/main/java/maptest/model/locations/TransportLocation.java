
package maptest.model.locations;

import maptest.model.LonLat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "timestamp",
    "position",
    "direction",
    "routeId",
    "vehicleLabel",
    "velocity",
    "vehicleId",
    "orderNumber",
    "licensePlate",
    "directionId"
})
public class TransportLocation {

    @JsonProperty("timestamp")
    public String timestamp;
  
    @JsonProperty("position")
    public LonLat position;
    
    @JsonProperty("direction")
    public int direction;
    
    @JsonProperty("routeId")
    public Integer routeId;
    
    @JsonProperty("vehicleLabel")
    public String vehicleLabel;
    
    @JsonProperty("velocity")
    public int velocity;
    
    @JsonProperty("vehicleId")
    public int vehicleId;
    
    @JsonProperty("orderNumber")
    public Integer orderNumber;
    
    @JsonProperty("licensePlate")
    public String licensePlate;
    
    @JsonProperty("directionId")
    public int directionId;

    
    public TransportLocation() {
    }

    public TransportLocation(
    	String timestamp,
    	LonLat position,
    	int direction,
    	Integer routeId,
    	String vehicleLabel,
    	int velocity,
    	int vehicleId,
    	Integer orderNumber,
    	String licensePlate,
    	int directionId)
    {
        this.timestamp = timestamp;
        this.position = position;
        this.direction = direction;
        this.routeId = routeId;
        this.vehicleLabel = vehicleLabel;
        this.velocity = velocity;
        this.vehicleId = vehicleId;
        this.orderNumber = orderNumber;
        this.licensePlate = licensePlate;
        this.directionId = directionId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        	.append(timestamp).append(position)
        	.append(direction).append(routeId)
        	.append(vehicleLabel).append(velocity)
        	.append(vehicleId).append(orderNumber)
        	.append(licensePlate).append(directionId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TransportLocation) == false) {
            return false;
        }
        TransportLocation rhs = ((TransportLocation) other);
        return new EqualsBuilder()
        	.append(timestamp, rhs.timestamp)
        	.append(position, rhs.position)
        	.append(direction, rhs.direction)
        	.append(routeId, rhs.routeId)
        	.append(vehicleLabel, rhs.vehicleLabel)
        	.append(velocity, rhs.velocity)
        	.append(vehicleId, rhs.vehicleId)
        	.append(orderNumber, rhs.orderNumber)
        	.append(licensePlate, rhs.licensePlate)
        	.append(directionId, rhs.directionId).isEquals();
    }
}
