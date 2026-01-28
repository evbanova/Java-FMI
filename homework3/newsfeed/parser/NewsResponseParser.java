package bg.sofia.uni.fmi.mjt.newsfeed.parser;

import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import com.google.gson.Gson;

//just takes json data and parses it as NewsResponse
public class NewsResponseParser {
    private Gson gson;

    public NewsResponseParser() {
        this.gson = new Gson();
    }

    public NewsResponse parse(String json) {
        return gson.fromJson(json, NewsResponse.class);
    }
}

