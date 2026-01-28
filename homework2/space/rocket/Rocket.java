package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

//looking at the CSV file, commas are used for separating
//but also as characters, in which case the whole string field is quoted

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    public static Rocket of(String line) {
        //The regex works by counting how many quotes exist after the comma it just found
        //pairs of quotes - even number
        //regex (?= ) syntax : Match the comma only if the stuff inside follows it
        //regex (?: )* syntax : It looks for a pattern repeated zero or more times
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        int index = 0;

        String id = fields[index++].trim();
        String name = fields[index++].trim().replace("\"", "");

        Optional<String> wiki = (fields.length > index && !fields[index].isBlank())
                ? Optional.of(fields[index].trim())
                : Optional.empty();

        index++;
        Optional<Double> height = (fields.length > index && !fields[index].isBlank())
                ? Optional.of(Double.parseDouble(fields[index].toLowerCase().replace("m", "").trim()))
                : Optional.empty();

        return new Rocket(id, name, wiki, height);
    }
}
