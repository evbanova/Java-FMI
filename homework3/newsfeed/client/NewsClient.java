package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;

public interface NewsClient {
    //this method uses NewsUri to build uri to use for HttpRequest
    //and then to validate and parse a NewsResponse
    NewsResponse getTopHeadlines(NewsRequest request) throws NewsApiException;
}

