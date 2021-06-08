package nodomain.freeyourgadget.gadgetbridge.activities.mecycling.util;

public class Helpers {
    public static double calculateHaversine(double lat1, double lon1, double lat2, double lon2){
        double lat1r = Math.toRadians(lat1);
        double lon1r = Math.toRadians(lon1);
        double lat2r = Math.toRadians(lat2);
        double lon2r = Math.toRadians(lon2);

        double deltaLat = lat2r - lat1r;
        double deltaLon = lon2r - lon1r;

        int r = 6371;
        double sin2x = Math.pow(Math.sin(deltaLat/2),2);
        double cosLat = Math.cos(lat1r);
        double cosLon = Math.cos(lat2r);
        double sin2y = Math.pow(Math.sin(deltaLon/2),2);

        double akar = Math.sqrt(sin2x + (cosLat*cosLon*sin2x));
        double asin = Math.asin(akar);

        return r * 2 * asin;
    }
}
