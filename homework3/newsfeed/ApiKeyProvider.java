package bg.sofia.uni.fmi.mjt.newsfeed;

//The NewsApi key shouldn't be hardcoded
//It will be provided via environment variable
//AI tools were used for understanding how to set the environment variable
public class ApiKeyProvider {
    private final String apiKey;

    public ApiKeyProvider() {
        this.apiKey = System.getenv("NEWS_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("NEWS_API_KEY environment variable is not set");
        }
    }

    //auto generated getter
    public String getApiKey() {
        return apiKey;
    }
}
