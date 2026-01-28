package bg.sofia.uni.fmi.mjt.newsfeed.model;

import java.util.List;

//fields are as stated in the newsApi
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;
    private String code;
    private String message;

    public NewsResponse() {

    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
