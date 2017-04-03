package maptest.service.eval;

import java.util.List;

import org.joda.time.DateTime;

import maptest.model.LonLat;
import maptest.model.routes.TransportRoute;
import maptest.service.data.LocationPoint;
import maptest.service.data.Transport;
import maptest.utils.GeometryUtils;
import maptest.utils.LengthUnit;
import maptest.utils.LonLatUtils;

public class LocationPointApproxymator {

    /* How much recent location points to use in approximation */
    public static int RECENT_LOCATION_POINTS = 12;
    
    public static void applyApproximation(Transport transport) {

        List<LocationPoint> locationPoints = transport.getRecentLocationPoints();
        
        LocationPoint recentLocationPoint = locationPoints.get(locationPoints.size() - 1);
        
        /* 1) Получаем значения средних скоростей на каждом из N последних известных отрезков */
        
        /*
        
        if (locationPoints.size() < 2) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            return;
        }
        
        double a = 0;
        double b = 0;
        
        for (int i = 0; i < Math.min(RECENT_LOCATION_POINTS, locationPoints.size()) - 1; i++) {

            int j = locationPoints.size() - i - 1;
            
            LocationPoint p1 = locationPoints.get(j);
            
            LocationPoint p2 = locationPoints.get(j - 1);

            double d_t = (p2.timestamp.getMillis() - p1.timestamp.getMillis()) / 1000.0;
            
            if (d_t < 0.1) {
                continue;
            }
            
            double d_s = LonLatUtils.distance(p1.position, p2.position, LengthUnit.METER);
            
            double v = d_s / d_t;
            
            double alpha = j + 1;
            
            a += (v * alpha);
            b += alpha;
        }
        
        // 2) Вычисляем среднее значение из этих скоростей (с учетом весовых коэффициэнтов)
        
        if (b > 0) {
             v_mean = a / b;
        }
        */
        
        double v_mean = recentLocationPoint.velocity / 3.6;
                
        System.out.println(recentLocationPoint.velocity);
        
        /* 3) Получаем аппроксимированное расстояние */
        
        /*
         * !!! Время на удаленном сервере может отличаться!
         * TODO: нужно запоминать последнее время из данных
         */
        
        DateTime currentTime = DateTime.now();
        
        double d_t = (
            //currentTime.getMillis() -
            System.currentTimeMillis() -
            recentLocationPoint.timestamp.getMillis()) / 1000.0;
        
        if (d_t < 1) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            System.out.println("LocationPointApproxymator: invalid time");
            return;
        }
        
        double length = v_mean * d_t;
        
        if (length < 1) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            return;
        }
        
        //System.out.println("d_t:" + d_t);
        //System.out.println("LEngth:" + length);
        
        /* 4) Отмеряем расстояние на пути начиная с recentLocationPoint */
        
        /*
        TransportRoute route = transport.getRoute(recentLocationPoint.directionId);
        
        if (route == null) {
            transport.setApproxymatedLocationPoint(recentLocationPoint);
            return;
        }
        
        List<LonLat> pathPoints = route.path;
        */

        // XXXXXXXXXXXXXXXXXX
        // XXX HARD CODE:
        // XXXXXXXXXXXXXXXXxXX
        
        LonLat approximatedPosition =
            GeometryUtils.getPointOnDirection(
                recentLocationPoint.position,
                recentLocationPoint.direction,
                length);
        
        transport.setApproxymatedLocationPoint(
            new LocationPoint(
                currentTime,
                approximatedPosition,
                recentLocationPoint.direction,
                recentLocationPoint.velocity,
                recentLocationPoint.directionId));
    }
}
