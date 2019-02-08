package leon.android.translatevoice.database;

import android.provider.BaseColumns;

public class LanguageContract {
    private LanguageContract(){}

    public static final class LanguageEntry implements BaseColumns{
        public static final String TABLE_NAME = "language";
        public static final String COLUMN_CHOOSEN_LANGUAGE = "choosen_language";
        public static final String COLUMN_CHOOSEN_LANGUAGE_TEXT = "choosen_language_text";
        public static final String COLUMN_TRANSALATED_LANGUAGE = "translated_language";
        public static final String COLUMN_TRANSLATED_LANGUAGE_TEXT = "translated_language_text";
    }
}
