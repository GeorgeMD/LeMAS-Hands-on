package base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implements a simple key-value knowledge base. There can be multiple values for the same key.
 * <p>
 * Added values expire after some time, set as a multiple of the atomic time grain set at construction.
 * 
 * @author andreiolaru
 */
public class KB {
	/**
	 * The underlying storage. For each key, there is a set of values; for each value, there is an expiration time (the
	 * system time after which the value is no longer valid.
	 */
	protected Map<String, Map<String, Long>> store = new HashMap<>();

	/**
	 * Atomic time grain for expiration times.
	 */
	protected int timeUnit = 1000;                // 1 second

	/**
	 * Default constructor.
	 */
	public KB() {
		// nothing to do
	}

	/**
	 * Constructor that sets the time grain for expiration times.
	 *
	 * @param timeGrain - the atomic time grain.
	 */
	public KB(int timeGrain) {
		timeUnit = timeGrain;
	}

	/**
	 * Cleans up expired values.
	 */
	protected void cleanUp() {
		long cTime = System.currentTimeMillis();
		for (Iterator<String> keyIt = store.keySet().iterator(); keyIt.hasNext(); ) {
			// remove expired values
			String key = keyIt.next();
			for (Iterator<String> valueIt = store.get(key).keySet().iterator(); valueIt.hasNext(); ) {
				String value = valueIt.next();
				if (store.get(key).get(value).longValue() < cTime)
					valueIt.remove();
			}
			// remove empty keys
			if (store.get(key).isEmpty())
				keyIt.remove();
		}
	}

	/**
	 * Adds a key-value association.
	 *
	 * @param key         - the key.
	 * @param value       - the value associated with the key.
	 * @param unitsToLive - time to live for this value, as multiplier for the atomic time grain.
	 */
	public void add(String key, String value, int unitsToLive) {
		cleanUp();
		if (!store.containsKey(key))
			store.put(key, new HashMap<String, Long>());
		store.get(key).put(value, new Long(System.currentTimeMillis() + unitsToLive * timeUnit));
	}

	/**
	 * Retrieves all values associated with a key.
	 *
	 * @param key - the key to retrieve.
	 * @return a {@link List} of values associated with the key.
	 */
	public Collection<String> get(String key) {
		cleanUp();
		if (store.containsKey(key)) {
			return store.get(key).keySet();
		} else {
			return null;
		}
	}

	/**
	 * @return the complete set of keys in the KB.
	 */
	public Collection<String> getKeys() {
		cleanUp();
		return store.keySet();
	}

	/**
	 * Removes all values associated with a key.
	 *
	 * @param key - the key to remove.
	 */
	public void remove(String key) {
		cleanUp();
		store.remove(key);
	}

	/**
	 * Removes the value from the list of values associated with the key.
	 *
	 * @param key   - the key.
	 * @param value - the value to remove.
	 */
	public void remove(String key, String value) {
		cleanUp();
		if (store.containsKey(key)) {
			store.get(key).remove(value);
		}
	}

	/**
	 * Removes all data in the KB.
	 */
	public void clear() {
		store.clear();
	}
}