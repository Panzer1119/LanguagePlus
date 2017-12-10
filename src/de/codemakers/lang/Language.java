package de.codemakers.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Language
 *
 * @author Paul Hagedorn
 */
public class Language {

    private final String alpha3_b;
    private final String alpha3_t;
    private final String alpha2;
    private final String english;
    private final String french;
    private final String german;
    private final List<Locale> locales = new ArrayList<>();
    private final List<String> aliases = new ArrayList<String>() {
        @Override
        public boolean addAll(int index, Collection<? extends String> aliases) {
            if (aliases == null) {
                return false;
            }
            return super.addAll(index, aliases.stream().filter(Objects::nonNull).map((alias) -> Arrays.asList(alias, alias.toLowerCase(), alias.toUpperCase())).flatMap(List::stream).collect(Collectors.toList()));
        }

        @Override
        public boolean addAll(Collection<? extends String> aliases) {
            if (aliases == null) {
                return false;
            }
            return super.addAll(aliases.stream().filter(Objects::nonNull).map((alias) -> Arrays.asList(alias, alias.toLowerCase(), alias.toUpperCase())).flatMap(List::stream).collect(Collectors.toList()));
        }

        @Override
        public void add(int index, String alias) {
            if (alias == null) {
                return;
            }
            super.add(index, alias);
            super.add(index, alias.toLowerCase());
            super.add(index, alias.toUpperCase());
        }

        @Override
        public boolean add(String alias) {
            if (alias == null) {
                return false;
            }
            final boolean done_1 = super.add(alias);
            final boolean done_2 = super.add(alias.toLowerCase());
            final boolean done_3 = super.add(alias.toUpperCase());
            return done_1 && done_2 && done_3;
        }

        @Override
        public boolean contains(Object object) {
            if (super.contains(object)) {
                return true;
            }
            if (object instanceof String) {
                final String alias = (String) object;
                if (super.contains(alias) || super.contains(alias.toLowerCase()) || super.contains(alias.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    };
    private final LanguageContainer languageContainer = new LanguageContainer(this);

    public Language(String alpha3_b, String alpha3_t, String alpha2, String english, String french, String german) {
        this.alpha3_b = alpha3_b;
        this.alpha3_t = alpha3_t;
        this.alpha2 = alpha2;
        this.english = english;
        this.french = french;
        this.german = german;
    }

    public final String getAlpha3_b() {
        return alpha3_b;
    }

    public final String getAlpha3_t() {
        return alpha3_t;
    }

    public final String getAlpha2() {
        return alpha2;
    }

    public final String getEnglish() {
        return english;
    }

    public final String getFrench() {
        return french;
    }

    public final String getGerman() {
        return german;
    }

    public final List<Locale> getLocales() {
        return locales;
    }

    public final List<String> getAliases() {
        return aliases;
    }

    public final LanguageContainer getLanguageContainer() {
        return languageContainer;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Language) {
            final Language language = (Language) object;
            if (!((alpha3_b != null && language.alpha3_b != null && !Objects.equals(alpha3_b, language.alpha3_b)) || !Objects.equals(locales, language.locales))) {
                return true;
            }
        } else if (object instanceof String) {
            final String text = (String) object;
            if (text.isEmpty()) {
                return false;
            }
            if (text.equalsIgnoreCase(alpha2) || text.equalsIgnoreCase(alpha3_b) || text.equalsIgnoreCase(alpha3_t) || text.equalsIgnoreCase(english) || text.equalsIgnoreCase(french) || text.equalsIgnoreCase(german) || aliases.contains(text) || locales.stream().map(Locale::toString).anyMatch((lang) -> text.equalsIgnoreCase(lang))) {
                return true;
            }
        } else if (object instanceof Locale) {
            final Locale locale_ = (Locale) object;
            if (locales.contains(locale_)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final String toString() {
        return "Language{" + "alpha3_b=" + alpha3_b + ", alpha3_t=" + alpha3_t + ", alpha2=" + alpha2 + ", english=" + english + ", french=" + french + ", german=" + german + ", locales=" + locales + ", aliases=" + aliases + '}';
    }

}
