package com.trains.utils.lang;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public final class I18N {
	private static final ObjectProperty<Locale> locale;
	public static final Set<Locale> availableLocales = Set.of(
			Locale.of("en", "AU")
	);
	private static final Locale defaultLocale = Locale.of("en", "AU");

	static {
		locale = new SimpleObjectProperty<>(getDefaultLocale());
	}

	private I18N() {
	}

	private static Locale getDefaultLocale() {
		Locale locale = Locale.getDefault();
		return isLocaleSupported(locale) ? locale : defaultLocale;
	}

	private static boolean isLocaleSupported(Locale locale) {
		return availableLocales.contains(locale);
	}

	public static Locale getLocale() {
		return locale.get();
	}

	public static void setLocale(Locale newLocale) {
		locale.set(newLocale);

	}

	public static String getString(String key, Object... params) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("com.trains.lang.messages", getLocale());
			return MessageFormat.format(bundle.getString(key), params);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static StringBinding createStringBinding(String key, Object... params) {
		return Bindings.createStringBinding(() -> getString(key, params), locale);
	}
}
