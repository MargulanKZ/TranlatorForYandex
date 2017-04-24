package kz.sdu.margulan.trdr;

/**
 * Created by margulan on 4/13/17.
 */

public class TrHistory {
    int id;
    String originalText;
    String afterTranslateText;
    String favStatus;
    String langToLang;

    public TrHistory() {
    }

    public TrHistory(String original_text, String after_translate_text, String fav_status, String lang_to_lang) {
        super();
        this.originalText = original_text;
        this.afterTranslateText = after_translate_text;
        this.favStatus = fav_status;
        this.langToLang = lang_to_lang;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getAfterTranslateText() {
        return afterTranslateText;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public String getLangToLang() {
        return langToLang;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }


}
