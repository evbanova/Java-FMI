package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsClient;
import bg.sofia.uni.fmi.mjt.newsfeed.client.NewsClientImpl;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequestBuilder;

public class Main {
    public static void main(String[] args) {
        NewsClient client = new NewsClientImpl();

        try {
            NewsRequest request = new NewsRequestBuilder("money")
                    .country("us").category("business")
                    .page(1).pageSize(5).build();

            NewsResponse response = client.getTopHeadlines(request);

            if (response.getArticles() != null && !response.getArticles().isEmpty()) {
                System.out.println("Top Headlines for 'java':");
                response.getArticles().forEach(article ->
                        System.out.printf("- %s (by %s)%n", article.getTitle(), article.getAuthor()));
            } else {
                System.out.println("No articles found for the given keywords.");
            }

        } catch (NewsApiException e) {
            System.err.println("API Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
