package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

//looking at the CSV file, commas are used for separating
//but also as characters, in which case the whole string field is quoted

public record Mission(String id, String company, String location, LocalDate date,
                      Detail detail, RocketStatus rocketStatus, Optional<Double> cost,
                      MissionStatus missionStatus) {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.ENGLISH);

    //The regex works by counting how many quotes exist after the comma it just found
    //pairs of quotes - even number
    //regex (?= ) syntax : Match the comma only if the stuff inside follows it
    //regex (?: )* syntax : It looks for a pattern repeated zero or more times
    public static Mission of(String line) {
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        int index = 0;

        String id = fields[index++].trim();
        String company = fields[index++].trim().replace("\"", "");
        String location = fields[index++].trim().replace("\"", "");

        String dateRaw = fields[index++].replace("\"", "").trim();
        LocalDate date = LocalDate.parse(dateRaw, DATE_FORMATTER);

        Detail detail = Detail.parse(fields[index++].trim());
        RocketStatus rocketStatus = RocketStatus.outOf(fields[index++].trim());

        Optional<Double> cost = (fields.length > index && !fields[index].isBlank())
                ? Optional.of(Double.parseDouble(fields[index].toLowerCase()
                    .replace("\"", "").replace(",", "").trim()))
                : Optional.empty();

        index++;
        MissionStatus missionStatus = MissionStatus.outOf(fields[index].trim());

        return new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);
    }
}
