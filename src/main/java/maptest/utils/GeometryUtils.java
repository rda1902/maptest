package maptest.utils;

import java.awt.geom.Point2D;

import maptest.model.LonLat;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.GeometryFactory;


public class GeometryUtils {

	/**
	 * Отложить под углом angle от точки a точку на расстоянии meters.
	 */
	public static LonLat getPointOnDirection(LonLat a, double angle, double meters) {

		GeodeticCalculator geodeticCalculator = new GeodeticCalculator();

		// get point on direction
		geodeticCalculator.setStartingGeographicPoint(a.lon, a.lat);
		geodeticCalculator.setDirection(angle, meters);
		Point2D destination = geodeticCalculator.getDestinationGeographicPoint();

		return new LonLat(
			destination.getX(),
			destination.getY());
	}


	protected static GeometryFactory geometryFactory = new GeometryFactory();

}
