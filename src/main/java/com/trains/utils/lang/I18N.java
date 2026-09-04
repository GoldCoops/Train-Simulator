package com.trains.utils.lang;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class I18N {

	private static Locale locale;
	private static final List<Runnable> listeners = new CopyOnWriteArrayList<>():
	public static final Set<Locale> availableLocales = Set.of(
			Locale.of("en", "AU")
	);
	private static final Locale defaultLocale = Locale.of("en", "AU");

	static {
		locale = getDefaultLocale();
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
		return locale;
	}

	public static void setLocale(Locale newLocale) {
		locale = newLocale;
		for (Runnable listener : listeners) {
			listener.run();
		}

	}

	public static void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public static void removeListener(Runnable listener) {
		listeners.remove(listener);
	}

	public static String getString(String key, Object... params) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("com.trains.lang.messages", getLocale());
			return MessageFormat.format(bundle.getString(key), params);
		} catch (MissingResourceException e) {
			return key;
		}
	}

}
