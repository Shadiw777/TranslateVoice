package leon.android.translatevoice.Model;

public class Language {
    private String titleOfLanguage;
    private String textOfLanguage;

    public Language(String titleOfLanguage, String textOfLanguage) {
        this.titleOfLanguage = titleOfLanguage;
        this.textOfLanguage = textOfLanguage;
    }

    public String getTitleOfLanguage() {
        return titleOfLanguage;
    }

    public void setTitleOfLanguage(String titleOfLanguage) {
        this.titleOfLanguage = titleOfLanguage;
    }

    public String getTextOfLanguage() {
        return textOfLanguage;
    }

    public void setTextOfLanguage(String textOfLanguage) {
        this.textOfLanguage = textOfLanguage;
    }
}
