package leon.android.translatevoice.translate;


import android.content.Context;
import android.os.SystemClock;

public class JNIEX {
    public JNIEX(){
        double a = Math.random() * 4000;
        SystemClock.sleep(Math.round(a));
    }

    /**
     * @param inputLanguage код языка из 2-3 символов (en, ru, fr, ro...)
     * @param outputLanguage код языка из 2-3 символов (en, ru, fr, ro...)
     */
    public String translate(Context context, String inputLanguage, String outputLanguage, String inputText) {
        double a = Math.random() * 2000 + 1000;
        SystemClock.sleep(Math.round(a));
        return outputLanguage.equals("ru") ? "Поздравляю! Перевод работает на ура" : "Hey. My congratulations! This is your translated text.";
    }
}
