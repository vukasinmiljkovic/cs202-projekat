package rs.autoservice.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rs.autoservice.model.NewsArticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servis koji koristi Jsoup biblioteku za preuzimanje aktuelnih vesti i servisnih saveta sa interneta.
 * Sadrži i rezervni mehanizam (fallback) ukoliko tokom demonstracije nema internet konekcije.
 *
 * @author Vukasin Miljkovic
 */
public class ScraperService {

    private static final String TARGET_URL = "https://www.polovniautomobili.com/auto-vesti/saveti";
    private static final int TIMEOUT_MS = 4000;

    /**
     * Preuzima listu saveta i vesti o održavanju automobila sa interneta pomoću Jsoup biblioteke.
     *
     * @return Lista objekata {@link NewsArticle}
     */
    public static List<NewsArticle> fetchArticles() {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            // Povezujemo se na sajt i preuzimamo HTML dokument
            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(TIMEOUT_MS)
                    .get();

            // Pronalazimo elemente članaka
            Elements elements = doc.select("article, .article-item, .news-item, .blog-item");

            for (Element el : elements) {
                String title = el.select("h2, h3, .title, a.title").text();
                String summary = el.select("p, .lead, .description").text();
                String link = el.select("a").attr("abs:href");

                if (!title.isEmpty() && articles.size() < 8) {
                    if (summary.isEmpty()) {
                        summary = "Kliknite na link za detaljnije informacije o ovom servisnom savetu.";
                    }
                    articles.add(new NewsArticle(title, summary, link, "Saveti za održavanje"));
                }
            }

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[ScraperService] Internet konekcija nije dostupna ili je došlo do timeout-a. Učitavam lokalne savete.");
        }

        // Ako scraper nije vratio vesti (npr. nema neta ili se promenio layout), popunjavamo korisne servisne savete
        if (articles.isEmpty()) {
            articles.addAll(getFallbackArticles());
        }

        return articles;
    }

    /**
     * Rezervna lista profesionalnih saveta za održavanje automobila.
     */
    private static List<NewsArticle> getFallbackArticles() {
        List<NewsArticle> fallback = new ArrayList<>();

        fallback.add(new NewsArticle(
                "Kada je pravo vreme za mali i veliki servis?",
                "Mali servis se radi na 10.000 - 15.000 km ili jednom godišnje. Veliki servis podrazumeva zamenu zupčastog kaiša i radi se na 60.000 - 120.000 km.",
                "https://www.polovniautomobili.com/auto-vesti/saveti/mali-i-veliki-servis",
                "Periodično održavanje"
        ));

        fallback.add(new NewsArticle(
                "Kako prepoznati dotrajale kočione pločice i diskove?",
                "Škripanje pri kočenju, propadanje pedale ili vibracije na volanu ukazuju na hitnu potrebu za proverom kočionog sistema.",
                "https://www.polovniautomobili.com/auto-vesti/saveti/kocioni-sistem-zamena",
                "Bezbednost"
        ));

        fallback.add(new NewsArticle(
                "Značaj redovne provere nivoa i kvaliteta motornog ulja",
                "Nedostatak ulja može prouzrokovati havariju motora. Preporučuje se provera šipke za ulje na svakih 1.000 pređenih kilometara.",
                "https://www.polovniautomobili.com/auto-vesti/saveti/motorno-ulje-provera",
                "Motor"
        ));

        fallback.add(new NewsArticle(
                "Priprema klima uređaja i ventilacije za letnju sezonu",
                "Dezinfekcija sistema i zamena filtera kabine (polen filtera) sprečavaju neprijatne mirise, vlagu i razvoj bakterija.",
                "https://www.polovniautomobili.com/auto-vesti/saveti/servis-auto-klime",
                "Klima & Udobnost"
        ));

        fallback.add(new NewsArticle(
                "Pravilno održavanje i skladištenje pneumatika",
                "Proveravajte pritisak u gumama barem jednom mesečno. Nepravilan pritisak povećava potrošnju goriva i habanje gazećeg sloja.",
                "https://www.polovniautomobili.com/auto-vesti/saveti/odrzavanje-guma",
                "Pneumatici"
        ));

        return fallback;
    }
}
