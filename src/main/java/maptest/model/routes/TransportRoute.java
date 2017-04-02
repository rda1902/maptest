package maptest.model.routes;

import java.util.List;

import maptest.model.LonLat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransportRoute {

    @JsonProperty("routeId")
    public Integer routeId;
    
    @JsonProperty("direction")
    public int direction;
    
    @JsonProperty("path")
    public List<LonLat> path = null;
    
    @JsonProperty("stopIDs")
    public List<Integer> stopIDs = null;

    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + direction;
        result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransportRoute other = (TransportRoute) obj;
        if (direction != other.direction)
            return false;
        if (routeId == null) {
            if (other.routeId != null)
                return false;
        } else if (!routeId.equals(other.routeId))
            return false;
        return true;
    }
}