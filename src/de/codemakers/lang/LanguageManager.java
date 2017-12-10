package de.codemakers.lang;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.logger.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * LanguageManager
 *
 * @author Paul Hagedorn
 */
public class LanguageManager {

    public static final AdvancedFile LANGUAGE_FOLDER = new AdvancedFile("de", "codemakers", "lang");
    private static final AdvancedFile LANGUAGE_INFO_FILE = new AdvancedFile(LANGUAGE_FOLDER, "language-codes-full_csv.csv");
    private static final String LANGUAGE_INFO_SEPARATOR = ",";
    private static final String COMMENT = "#";
    private static final List<LanguageReloader> LANGUAGE_RELOADERS = new ArrayList<>();
    private static final List<Language> LANGUAGES = new ArrayList<>();
    private static Language UNDETERMINED_LANGUAGE = null;
    private static Language DEFAULT_LANGUAGE = null;
    public static Language ACTIVE_LANGUAGE = null;
    public static Pattern LANGUAGE_FILE_PATTERN = Pattern.compile("lang_(.*)");
    public static Predicate<String> LANGUAGE_FILE_PREDICATE = (name) -> name != null && (LANGUAGE_FILE_PATTERN == null || LANGUAGE_FILE_PATTERN.matcher(name).matches());
    protected static final Map<String, String> DEFAULT_MAP = new HashMap<>();
    protected static boolean COLLECTING = false;

    static {
        loadLanguageInfo(LANGUAGE_INFO_FILE);
        /*
        if (LANGUAGE_FOLDER.exists()) {
            ofFolder(LANGUAGE_FOLDER, true, false); //FIXME
        } else {
            Logger.logErr("The standard path \"%s\" for the languages does not exist", null, LANGUAGE_FOLDER);
        }
         */
        setLanguage("EN");
    }

    private static final void loadLanguageInfo(AdvancedFile language_info_file) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(language_info_file.createInputStream(), StandardCharsets.UTF_8));
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

    public static final void setLanguage(String text) {
        ACTIVE_LANGUAGE = ofString(text);
    }

    public static final String getLang(String key, String defaultValue) {
        if (ACTIVE_LANGUAGE == null) {
            return getLang(DEFAULT_LANGUAGE, key, defaultValue);
        }
        return getLang(ACTIVE_LANGUAGE, key, defaultValue);
    }

    public static final String getLang(Language language, String key, String defaultValue) {
        if (language == null) {
            return null;
        }
        if (COLLECTING) {
            DEFAULT_MAP.put(key, defaultValue);
        }
        return language.getLanguageContainer().getLang(key, defaultValue);
    }

    protected static final void reloadLanguages() {
        LANGUAGE_RELOADERS.forEach(LanguageReloader::reloadLanguage0);
    }

    public static final boolean addLanguageReloader(LanguageReloader languageReloader) {
        return LANGUAGE_RELOADERS.add(languageReloader);
    }

    public static final boolean removeLanguageReloader(LanguageReloader languageReloader) {
        return LANGUAGE_RELOADERS.remove(languageReloader);
    }

    public static final Map<String, String> collectDefaultLanguageKeys() {
        DEFAULT_MAP.clear();
        COLLECTING = true;
        reloadLanguages();
        COLLECTING = false;
        return DEFAULT_MAP;
    }

    public static final String fileToLanguageCode(AdvancedFile file) {
        if (file == null) {
            return null;
        }
        final Matcher matcher = LANGUAGE_FILE_PATTERN.matcher(file.getName());
        if (!matcher.matches()) {
            return null;
        }
        String restName = matcher.group(1);
        int indexPoint = restName.indexOf(".");
        if (indexPoint != -1) {
            restName = restName.substring(0, indexPoint);
        }
        return restName.toUpperCase();
    }

    public static final void ofResource(String path, boolean overwrite, boolean append) {
        if (path == null || path.isEmpty()) {
            Logger.logErr("Error while loading language from resource, path is invalid", null);
            return;
        }
        ofFile(new AdvancedFile(path), overwrite, append);
    }

    public static final void ofResources(String path, boolean overwrite, boolean append) {
        if (path == null) {
            Logger.logErr("Error while loading languages from resources, path is invalid", null);
            return;
        }
        ofFolder(new AdvancedFile(path), overwrite, append);
    }

    public static final void ofFile(AdvancedFile file, boolean overwrite, boolean append) {
        if (file == null) {
            throw new NullPointerException("The file must not be null");
        } else if (!file.exists()) {
            Logger.logErr("Error while loading language from file, file does not exist", null);
            return;
        } else if (!file.isFile()) {
            ofFolder(file, overwrite, append);
            return;
        }
        try {
            final String languageCode = fileToLanguageCode(file);
            final Map<String, String> map = loadFromInputStream(file.createInputStream());
            final Language language = ofString(languageCode);
            if (language == null) {
                Logger.logErr("Error while loading language from resource, language was not found", null);
                return;
            }
            if (!append) {
                language.getLanguageContainer().clear();
            }
            language.getLanguageContainer().add(map, overwrite);
            Logger.log("Loaded language \"%s\" from resource", languageCode);
        } catch (Exception ex) {
            Logger.logErr("Error while loading languages from file", ex);
        }
    }

    public static final void ofFolder(AdvancedFile folder, boolean overwrite, boolean append) {
        if (folder == null) {
            throw new NullPointerException("The folder must not be null");
        } else if (!folder.exists()) {
            Logger.logErr("Error while loading language from folder, folder does not exist", null);
            return;
        } else if (!folder.isDirectory()) {
            ofFile(folder, overwrite, append);
            return;
        }
        try {
            folder.listAdvancedFiles((parent, name) -> LANGUAGE_FILE_PREDICATE.test(name)).forEach((file) -> ofFile(file, overwrite, append));
        } catch (Exception ex) {
            Logger.logErr("Error while loading languages from folder", ex);
        }
    }

    public static final Map<String, String> loadFromInputStream(InputStream inputStream) {
        try {
            final Properties map = new Properties();
            map.load(inputStream);
            return map.entrySet().stream().collect(Collectors.toMap((entry) -> "" + entry.getKey(), (entry) -> "" + entry.getValue()));
        } catch (Exception ex) {
            Logger.logErr("Error while loading languages from InputStream", ex);
            return null;
        }
    }

}
