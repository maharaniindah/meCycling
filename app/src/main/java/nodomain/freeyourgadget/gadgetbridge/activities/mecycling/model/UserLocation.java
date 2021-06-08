package nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation implements Comparable<UserLocation> {
    public LatLng latLng;
    public int order;

    public UserLocation(String input) {
        System.out.println("got input: "+input);
        String[] splitted = input.split(";");
        if(splitted.length == 3) {
            double lat = Double.parseDouble(splitted[0]);
            double lng = Double.parseDouble(splitted[1]);
            System.out.println("got latt: " +lat);
            System.out.println("got long: " +lng);
            order = Integer.parseInt(splitted[2]);
            latLng = new LatLng(lat, lng);
        }
    }

    @Override
    public int compareTo(UserLocation o) {
        return Integer.compare(order,o.order);
    }
}

