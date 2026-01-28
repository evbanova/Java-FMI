package bg.sofia.uni.fmi.mjt.newsfeed.request;

public class NewsRequestBuilder {
    protected String keywords;
    protected String category;
    protected String country;
    protected int page = 1;
    protected int pageSize; //= 20;

    public NewsRequestBuilder(String keywords) {
        if (keywords == null || keywords.isBlank()) {
            throw new IllegalArgumentException("Keywords are required");
        }
        this.keywords = keywords;
    }

    public NewsRequestBuilder category(String category) {
        this.category = category;
        return this;
    }

    public NewsRequestBuilder country(String country) {
        this.country = country;
        return this;
    }

    public NewsRequestBuilder page(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("Pages must be >= 1");
        }
        this.page = page;
        return this;
    }

    public NewsRequestBuilder pageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("Invalid page size");
        }
        this.pageSize = pageSize;
        return this;
    }

    public NewsRequest build() {
        return new NewsRequest(this);
    }
}
