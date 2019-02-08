package leon.android.translatevoice.model;


public class Language {

    private String titleOfFirstLanguage;
    private String titleOfSecondLanguage;
    private String textOfFirstLanguage;
    private String textOfSecondLanguage;

    public Language(String titleOfFirstLanguage, String titleOfSecondLanguage) {
        this.titleOfFirstLanguage = titleOfFirstLanguage;
        this.titleOfSecondLanguage = titleOfSecondLanguage;
    }

    public Language(String titleOfFirstLanguage, String titleOfSecondLanguage, String textOfFirstLanguage, String textOfSecondLanguage) {
        this.titleOfFirstLanguage = titleOfFirstLanguage;
        this.titleOfSecondLanguage = titleOfSecondLanguage;
        this.textOfFirstLanguage = textOfFirstLanguage;
        this.textOfSecondLanguage = textOfSecondLanguage;
    }

    public Language() {
    }

    public String getTitleOfFirstLanguage() {
        return titleOfFirstLanguage;
    }

    public void setTitleOfFirstLanguage(String titleOfFirstLanguage) {
        this.titleOfFirstLanguage = titleOfFirstLanguage;
    }

    public String getTitleOfSecondLanguage() {
        return titleOfSecondLanguage;
    }

    public void setTitleOfSecondLanguage(String titleOfSecondLanguage) {
        this.titleOfSecondLanguage = titleOfSecondLanguage;
    }

    public String getTextOfFirstLanguage() {
        return textOfFirstLanguage;
    }

    public void setTextOfFirstLanguage(String textOfFirstLanguage) {
        this.textOfFirstLanguage = textOfFirstLanguage;
    }

    public String getTextOfSecondLanguage() {
        return textOfSecondLanguage;
    }

    public void setTextOfSecondLanguage(String textOfSecondLanguage) {
        this.textOfSecondLanguage = textOfSecondLanguage;
    }
}
