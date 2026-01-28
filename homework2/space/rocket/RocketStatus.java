package bg.sofia.uni.fmi.mjt.space.rocket;

public enum RocketStatus {
    STATUS_RETIRED("StatusRetired"),
    STATUS_ACTIVE("StatusActive");

    private final String value;

    RocketStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    //for using the csv format
    public static RocketStatus outOf(String input) {
        for (RocketStatus rs : RocketStatus.values()) {
            if (rs.value.equalsIgnoreCase(input.trim())) {
                return rs;
            }
        }
        throw new IllegalArgumentException("No rocket status found for: " + input);
    }
}
