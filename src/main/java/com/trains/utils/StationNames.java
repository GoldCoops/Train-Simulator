package com.trains.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StationNames {
	private static final List<String> NSW_NAMES = List.of(
			"Parramatta", "Newcastle", "Wollongong", "Bathurst", "Orange",
			"Dubbo", "Wagga Wagga", "Tamworth", "Albury", "Goulburn",
			"Lithgow", "Nowra", "Coffs Harbour", "Griffith", "Broken Hill",
			"Armidale", "Maitland", "Cessnock", "Queanbeyan", "Bowral"
	);

	private StationNames() {
	}

	/**
	 * Returns a random selection of NSW place names, in random order.
	 * @param count how many names to return
	 * @throws IllegalArgumentException if count exceeds the available pool
	 */
	public static List<String> randomNames(int count) {
		if (count > NSW_NAMES.size()) {
			throw new IllegalArgumentException(
					"Only " + NSW_NAMES.size() + " names available, requested " + count);
		}
		List<String> shuffled = new ArrayList<>(NSW_NAMES);
		Collections.shuffle(shuffled);
		return shuffled.subList(0, count);
	}
}