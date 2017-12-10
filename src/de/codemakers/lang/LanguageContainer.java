package de.codemakers.lang;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * LanguageContainer
 *
 * @author Paul Hagedorn
 */
public class LanguageContainer {

    private final Language language;
    private final Properties map = new Properties();

    public LanguageContainer(Language language) {
        this.language = language;
    }

    public final Language getLanguage() {
        return language;
    }

    public final Properties getMap() {
        return map;
    }

    public final LanguageContainer clear() {
        map.clear();
        return this;
    }

    public final LanguageContainer add(Map<String, String> map, boolean overwrite) {
        if (overwrite) {
            this.map.putAll(map);
        } else {
            this.map.putAll(map.entrySet().stream().filter((entry) -> !map.containsKey(entry.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
        }
        return this;
    }

    public final String getLang(String key, String defaultValue) {
        if (LanguageManager.COLLECTING) {
            LanguageManager.DEFAULT_MAP.put(key, defaultValue);
        }
        return map.getProperty(key, defaultValue);
    }

}
