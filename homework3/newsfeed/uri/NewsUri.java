package bg.sofia.uni.fmi.mjt.newsfeed.uri;

import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//this class is used to build a specific url by keywords, country and category from NewsRequest
//and then for the HttpRequest
public class NewsUri {
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines";

    public URI buildUriFromRequest(NewsRequest request) {
        StringBuilder url = new StringBuilder(BASE_URL);

        url.append("?q=").append(encode(request.getKeywords()));

        //optional
        if (request.getCategory() != null) {
            url.append("&category=").append(encode(request.getCategory()));
        }
        if (request.getCountry() != null) {
            url.append("&country=").append(encode(request.getCountry()));
        }

        url.append("&page=").append(request.getPage());
        url.append("&pageSize=").append(request.getPageSize());

        return URI.create(url.toString());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
