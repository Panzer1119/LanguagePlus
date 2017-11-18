package de.codemakers.lang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * LanguageManager
 *
 * @author Paul Hagedorn
 */
public class LanguageManager {

    private static final String LANGUAGE_INFO_FILE = "/de/codemakers/lang/language-codes-full_csv.csv";
    private static final String LANGUAGE_INFO_SEPARATOR = ",";
    private static final String COMMENT = "#";
    private static final List<Language> LANGUAGES = new ArrayList<>();
    private static Language UNDETERMINED_LANGUAGE = null;
    private static Language DEFAULT_LANGUAGE = null;

    static {
        loadLanguageInfo(LANGUAGE_INFO_FILE);
    }

    private static final void loadLanguageInfo(String language_info_file) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(LanguageManager.class.getResourceAsStream(language_info_file), StandardCharsets.UTF_8));
            LANGUAGES.clear();
            bufferedReader.lines().filter((line) -> line.contains(LANGUAGE_INFO_SEPARATOR) && !line.startsWith(COMMENT)).map((line) -> split(line, LANGUAGE_INFO_SEPARATOR)).filter((info) -> info.length == 6).map((info) -> new Language(info[0], info[1], info[2], info[3], info[4], info[5])).forEach(LANGUAGES::add);
            bufferedReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LANGUAGES.stream().forEach((language) -> {
            final List<Locale> locales = language.getLocales();
            Arrays.asList(Locale.getAvailableLocales()).stream().filter((locale) -> language.equals(locale.getLanguage())).forEach(locales::add);
        });
    }

    public static final List<Language> getLanguages() {
        return new ArrayList<>(LANGUAGES);
    }

    public static final Language getUndeterminedLanguage() {
        if (UNDETERMINED_LANGUAGE == null) {
            UNDETERMINED_LANGUAGE = ofString("und");
        }
        return UNDETERMINED_LANGUAGE;
    }

    public static final Language getDefaultLanguage() {
        if (DEFAULT_LANGUAGE == null) {
            DEFAULT_LANGUAGE = ofLocale(Locale.getDefault());
        }
        return DEFAULT_LANGUAGE;
    }

    public static final Language ofLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        return LANGUAGES.stream().filter((language) -> language.equals(locale)).findFirst().orElse(null);
    }

    public static final Language ofString(String text) {
        if (text == null) {
            return null;
        }
        final Language language = LANGUAGES.stream().filter((language_) -> language_.equals(text)).findFirst().orElse(null);
        return language != null ? language : ofExtra(text);
    }

    private static final Language ofExtra(String extra) {
        if (extra == null) {
            return null;
        }
        return ofLocale(Locale.forLanguageTag(extra));
    }

    private static final String[] split(String toSplit, String delimiter) {
        if (toSplit == null || delimiter == null || delimiter.isEmpty()) {
            return new String[0];
        }
        if (!toSplit.contains(delimiter)) {
            return new String[]{toSplit};
        }
        final List<String> temp = new ArrayList<>();
        int index = -1;
        while ((index = toSplit.indexOf(delimiter)) != -1) {
            temp.add(toSplit.substring(0, index));
            toSplit = toSplit.substring(index + delimiter.length());
        }
        temp.add(toSplit);
        return temp.toArray(new String[temp.size()]);
    }

}
