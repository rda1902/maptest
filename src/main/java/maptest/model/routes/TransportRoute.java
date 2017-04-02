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
@JsonPropertyOrder({
	"path",
	"direction",
	"routeId",
	"stopIDs"
})
public class TransportRoute {

	@JsonProperty("path")
	public List<LonLat> path = null;
	
	@JsonProperty("direction")
	public int direction;
	
	@JsonProperty("routeId")
	public Integer routeId;
	
	@JsonProperty("stopIDs")
	public List<Integer> stopIDs = null;

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(path).append(direction)
			.append(routeId).append(stopIDs).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof TransportRoute) == false) {
			return false;
		}
		TransportRoute rhs = ((TransportRoute) other);
		return new EqualsBuilder().append(path, rhs.path)
			.append(direction, rhs.direction).append(routeId, rhs.routeId)
			.append(stopIDs, rhs.stopIDs).isEquals();
	}
}