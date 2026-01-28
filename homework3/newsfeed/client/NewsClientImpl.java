package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.ApiKeyProvider;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.parser.NewsResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.uri.NewsUri;
import bg.sofia.uni.fmi.mjt.newsfeed.validator.NewsResponseValidator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewsClientImpl implements NewsClient {
    private final HttpClient httpClient;
    private final NewsResponseParser parser;
    private final NewsResponseValidator validator;
    private final NewsUri newsUriBuilder;
    private final ApiKeyProvider apiKeyProvider;

    public NewsClientImpl(HttpClient httpClient, NewsResponseParser parser,
                          NewsResponseValidator validator, NewsUri newsUriBuilder,
                          ApiKeyProvider apiKeyProvider) {
        this.httpClient = httpClient;
        this.parser = parser;
        this.validator = validator;
        this.newsUriBuilder = newsUriBuilder;
        this.apiKeyProvider = apiKeyProvider;
    }

    public NewsClientImpl() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                          .build();
        this.parser = new NewsResponseParser();
        this.validator = new NewsResponseValidator();
        this.newsUriBuilder = new NewsUri();
        this.apiKeyProvider = new ApiKeyProvider();
    }

    @Override
    public NewsResponse getTopHeadlines(NewsRequest request) throws NewsApiException {
        HttpResponse<String> response = sendRequest(newsUriBuilder.buildUriFromRequest(request));

        validator.validate(response);
        NewsResponse news = parser.parse(response.body());
        validator.validateAfterParsing(news);

        return news;
    }

    private HttpResponse<String> sendRequest(URI uri) throws NewsApiException {

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri)
                                  .header("X-Api-Key", apiKeyProvider.getApiKey())
                                  .GET().build();
        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new NewsApiException("HTTP request failed");
        }
    }
}

