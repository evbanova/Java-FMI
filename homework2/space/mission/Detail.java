package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {
    public static Detail parse(String field) {
        String[] fields = field.split("\\|", 2);
        int index = 0;

        String rocketName = fields[index++].trim().replace("\"", "");
        String payload = fields[index].trim().replace("\"", "");

        return new Detail(rocketName, payload);
    }
}
