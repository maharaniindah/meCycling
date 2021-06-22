package nodomain.freeyourgadget.gadgetbridge.activities.mecycling.model;

public class Speed implements Comparable<Speed> {

    public double speed;
    public int order;

    public Speed(String input){
        String[] split = input.split(";");
        if (split.length == 2){
            speed = Double.parseDouble(split[0]);
            order = Integer.parseInt(split[1]);
        }

    }
    @Override
    public int compareTo(Speed o) {
        return Integer.compare(order, o.order);
    }
}
