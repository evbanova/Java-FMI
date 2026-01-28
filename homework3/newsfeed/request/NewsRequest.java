package bg.sofia.uni.fmi.mjt.newsfeed.request;

public class NewsRequest {
    private final String keywords;
    private final String category;
    private final String country;
    private final int page;
    private final int pageSize;

    public NewsRequest(NewsRequestBuilder builder) {
        this.keywords = builder.keywords;
        this.category = builder.category;
        this.country = builder.country;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    //auto generated getters
    public String getKeywords() {
        return keywords;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
