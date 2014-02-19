package edu.teco.dnd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Provides utility methods for dealing with {@link Map}s.
 */
public class MapUtil {
	/**
	 * Private constructor as this should not be instantiated.
	 */
	private MapUtil() {
	}

	/**
	 * Inverts a map. This creates a new Map that maps from value to key in regards to the old map. Uses a Set for value
	 * as multiple keys can have the same value in the original map.
	 * 
	 * @param map
	 *            the Map to invert
	 * @return an inverted version of the Map
	 */
	public static <A, B> Map<B, Set<A>> invertMap(final Map<A, B> map) {
		final Map<B, Set<A>> result = new HashMap<B, Set<A>>(map.size());
		for (final Entry<A, B> entry : map.entrySet()) {
			final B value = entry.getValue();
			Set<A> set = result.get(value);
			if (set == null) {
				set = new HashSet<A>();
				result.put(value, set);
			}
			set.add(entry.getKey());
		}
		return result;
	}

	/**
	 * Generates a transitive Mapping. That is, given a Map from type <code>A</code> to <code>Collection&lt;B&gt;</code>
	 * and a Map from <code>B</code> to <code>Collection&lt;C&gt;</code> it creates a Map from <code>A</code> to
	 * <code>Collection&lt;C&gt;</code> where the value contains all elements of all values from the second Map where
	 * the key is in the value of the first Map for <code>A</code>.
	 * 
	 * @param mapA
	 *            the first Map
	 * @param mapB
	 *            the second Map
	 * @return a Map from the key of mapA to the values of mapB
	 */
	public static <A, B, C> Map<A, Collection<C>> transitiveMapCollection(Map<A, Collection<B>> mapA,
			Map<B, Collection<C>> mapB) {
		final Map<A, Collection<C>> result = new HashMap<A, Collection<C>>(mapA.size());
		for (final Entry<A, Collection<B>> entry : mapA.entrySet()) {
			final A key = entry.getKey();
			Collection<C> collection = result.get(key);
			if (collection == null) {
				collection = new ArrayList<C>();
				result.put(key, collection);
			}
			for (final B b : entry.getValue()) {
				final Collection<C> newItems = mapB.get(b);
				if (newItems != null) {
					collection.addAll(newItems);
				}
			}
		}
		return result;
	}

	/**
	 * The same as {@link #transitiveMapCollection(Map, Map)}, but with Sets instead of Collections.
	 * 
	 * @param mapA
	 *            the first Map
	 * @param mapB
	 *            the second Map
	 * @return a Map from the key of mapA to the values of mapB
	 */
	public static <A, B, C> Map<A, Set<C>> transitiveMapSet(Map<A, Set<B>> mapA, Map<B, Set<C>> mapB) {
		final Map<A, Set<C>> result = new HashMap<A, Set<C>>(mapA.size());
		for (final Entry<A, Set<B>> entry : mapA.entrySet()) {
			final A key = entry.getKey();
			Set<C> collection = result.get(key);
			if (collection == null) {
				collection = new HashSet<C>();
				result.put(key, collection);
			}
			for (final B b : entry.getValue()) {
				final Set<C> newItems = mapB.get(b);
				if (newItems != null) {
					collection.addAll(newItems);
				}
			}
		}
		return result;
	}
}
