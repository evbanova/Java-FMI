package bg.sofia.uni.fmi.mjt.newsfeed.client;

import bg.sofia.uni.fmi.mjt.newsfeed.ApiKeyProvider;
import bg.sofia.uni.fmi.mjt.newsfeed.model.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.parser.NewsResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.uri.NewsUri;
import bg.sofia.uni.fmi.mjt.newsfeed.validator.NewsResponseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsClientImplTest {

    @Mock private HttpClient httpClient;
    @Mock private NewsResponseParser parser;
    @Mock private NewsResponseValidator validator;
    @Mock private NewsUri newsUriBuilder;
    @Mock private ApiKeyProvider apiKeyProvider;
    @Mock private HttpResponse<String> httpResponse;

    private NewsClientImpl newsClient;

    @BeforeEach
    void setUp() {
        newsClient = new NewsClientImpl(httpClient, parser, validator, newsUriBuilder, apiKeyProvider);
    }

    @Test
    void testGetTopHeadlinesFlow() throws Exception {
        NewsRequest request = mock(NewsRequest.class);
        URI mockUri = URI.create("https://newsapi.org/v2/top-headlines?q=test");
        NewsResponse mockNewsResponse = new NewsResponse();

        when(newsUriBuilder.buildUriFromRequest(request)).thenReturn(mockUri);
        when(apiKeyProvider.getApiKey()).thenReturn("fake-key");
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn("{ \"status\": \"ok\" }");
        when(parser.parse(anyString())).thenReturn(mockNewsResponse);

        NewsResponse result = newsClient.getTopHeadlines(request);

        assertNotNull(result);
        verify(validator).validate(httpResponse);
        verify(validator).validateAfterParsing(mockNewsResponse);
    }
}