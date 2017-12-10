package de.codemakers.lang;

import de.codemakers.logger.Logger;

/**
 * LanguageReloader
 *
 * @author Paul Hagedorn
 */
public interface LanguageReloader {

    public void reloadLanguage();

    default void reloadLanguage0() {
        try {
            reloadLanguage();
        } catch (Exception ex) {
            Logger.logErr("Error while reloading language", ex);
        }
    }

    default public String getLang(Language language, String key, String defaultValue) {
        return LanguageManager.getLang(language, key, defaultValue);
    }

    default public String getLang(String key, String defaultValue) {
        return LanguageManager.getLang(key, defaultValue);
    }

}
