package com.databinder.scrapping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CardmarketPriceParser {

//    public CardmarketPriceData parse(String html) {
//        Document doc = Jsoup.parse(html);
//        BigDecimal fromPrice = null;
//        BigDecimal priceTrend = null;
//
//        for (Element dt : doc.select("dt")) {
//            String label = dt.text().trim().toLowerCase();
//            Element dd = dt.nextElementSibling();
//            if (dd == null || !dd.tagName().equals("dd")) continue;
//
//            BigDecimal value = parseSafe(clean(dd.text()));
//
//            if (label.equals("from")) {
//                fromPrice = value;
//            } else if (label.contains("price trend")) {
//                priceTrend = value;
//            }
//        }
//
//        return new CardmarketPriceData(fromPrice, priceTrend);
//    }

    private String clean(String raw) {
        return raw
            .replace("€", "")
            .replace(",", ".")
            .replace("\u00A0", " ")
            .replaceAll("[^0-9.]", "")
            .trim();
    }

    private BigDecimal parseSafe(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return new BigDecimal(raw);
        } catch (Exception e) {
            return null;
        }
    }
}