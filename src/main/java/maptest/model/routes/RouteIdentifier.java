package maptest.model.routes;

public class RouteIdentifier {
    
    public int routeId;
    
    public int routeDirection;

    
    public RouteIdentifier(int routeId, int routeDirection) {
        this.routeId = routeId;
        this.routeDirection = routeDirection;
    }

    @Override
    public String toString() {
        return "RouteIdentifier [routeId=" + routeId + ", routeDirection="
                + routeDirection + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + routeDirection;
        result = prime * result + routeId;
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
        RouteIdentifier other = (RouteIdentifier) obj;
        if (routeDirection != other.routeDirection)
            return false;
        if (routeId != other.routeId)
            return false;
        return true;
    }
}
