package leon.android.translatevoice.model;

public class Language {
    private String titleOfFirstLanguage;
    private String titleOfSecondLanguage;

    public Language(String titleOfFirstLanguage, String titleOfSecondLanguage) {
        this.titleOfFirstLanguage = titleOfFirstLanguage;
        this.titleOfSecondLanguage = titleOfSecondLanguage;
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
}
