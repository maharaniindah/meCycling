package nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model;

public class Distance implements Comparable<Distance>{

    public double distance;
    public int order;

    public Distance(String input){
        String[] split = input.split(";");
        if (split.length == 2){
            distance = Double.parseDouble(split[0]);
            order = Integer.parseInt(split[1]);
        }

    }
    @Override
    public int compareTo(Distance o) {
        return Integer.compare(order, o.order);

    }
}
