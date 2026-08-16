package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja vest ili servisni savet preuzet sa interneta putem Jsoup biblioteke.
 *
 * @author Vukasin Miljkovic
 */
public class NewsArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Naslov vesti ili saveta */
    private String title;

    /** Kratak sažetak / opis */
    private String summary;

    /** URL link do originalnog članka */
    private String url;

    /** Kategorija članka (npr. Saveti za vozače, Održavanje) */
    private String category;

    public NewsArticle() {
    }

    public NewsArticle(String title, String summary, String url, String category) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "[" + category + "] " + title;
    }
}
