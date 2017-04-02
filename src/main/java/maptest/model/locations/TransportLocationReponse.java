
package maptest.model.locations;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "result",
    "success"
})
public class TransportLocationReponse {

    @JsonProperty("result")
    public List<TransportLocation> result = null;

    @JsonProperty("success")
    public boolean success;


    public TransportLocationReponse() {
    }

    public TransportLocationReponse(List<TransportLocation> result, boolean success) {
        this.result = result;
        this.success = success;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(result).append(success).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TransportLocationReponse) == false) {
            return false;
        }
        TransportLocationReponse rhs = ((TransportLocationReponse) other);
        return new EqualsBuilder().append(result, rhs.result).append(success, rhs.success).isEquals();
    }
}
