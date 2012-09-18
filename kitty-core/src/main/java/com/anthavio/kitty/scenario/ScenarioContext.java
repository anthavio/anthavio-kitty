/**
 * 
 */
package com.anthavio.kitty.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vanek
 * 
 * Map for data exchange between steps
 *
 */
public class ScenarioContext implements Map<String, List<Object>> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	//public final Scenario scenario;

	public final ScenarioExecution execution;

	private final HashMap<String, List<Object>> map = new HashMap<String, List<Object>>();

	public ScenarioContext(ScenarioExecution execution) {
		this.execution = execution;
		//this.scenario = execution.getScenario();
	}

	public ScenarioExecution getExecution() {
		return execution;
	}

	public Scenario getScenario() {
		return execution.getScenario();
	}

	public void put(Class<?> keyClass, Object value) {
		String key = keyClass.getName();
		put(key, value);
	}

	public void put(Object value) {
		if (value == null) {
			throw new ContextException(execution.getActiveStep(), "Will not store null Object");
		}
		String key = value.getClass().getName();
		put(key, value);
	}

	public void put(String key, Object value) {
		if (key == null) {
			throw new ContextException(execution.getActiveStep(), "Will not store null key");
		}
		if (value == null) {
			throw new ContextException(execution.getActiveStep(), "Will not store null under key " + key);
		}
		// existujici pridat na konec
		if (map.containsKey(key)) {
			map.get(key).add(value);
		} else {
			// neexistujici zalozit
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(value);
			map.put(key, list);
		}
		int position = map.get(key).size();
		log.debug("put key=" + key + ", pos=" + position + ", value=" + value);
	}

	public <T> boolean hasKey(Class<T> expected) {
		try {
			get(expected);
		} catch (ContextException iax) {
			return false;
		}
		return true;
	}

	public boolean hasKey(String key) {
		try {
			int last = count(key);
			get(key, last);
		} catch (ContextException iax) {
			return false;
		}
		return true;
	}

	public boolean hasKey(String key, int position) {
		try {
			get(key, position);
		} catch (ContextException iax) {
			return false;
		}
		return true;
	}

	public <T> boolean hasValue(Class<T> clazz, Object value) {
		try {
			T t = get(clazz);
			return t.equals(value);
		} catch (IllegalArgumentException iax) {
			return false;
		}
	}

	/**
	 * String key is used
	 * @return value is converted to required type
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> expected) throws ContextException {
		int last = count(key);
		return (T) get(key, last);
	}

	/**
	 * Class name - expected.getName() is key
	 * @return value is converted to required type
	 */
	public <T> T get(Class<T> expected) throws ContextException {
		int last = count(expected);
		return get(expected, last);
	}

	public Object get(String key) throws ContextException {
		int last = count(key);
		return get(key, last);
	}

	public Object get(String key, int position) throws ContextException {
		if (key == null) {
			throw new ContextException(execution.getActiveStep(), "Context key is null");
		}
		List<Object> list = map.get(key);
		if (list == null) {
			throw new ContextException(execution.getActiveStep(), "Nothing found with key " + key + " in context: "
					+ map.keySet());
		}
		if (position < 1 || position > list.size()) {
			throw new ContextException(execution.getActiveStep(), "Position " + position + " is not in range <1,"
					+ list.size() + "> in " + list);
		}
		Object ret = list.get(position - 1);
		//log.debug("get key=" + key + ", pos=" + position + ", value=" + ret);
		return ret;
	}

	public <T> T get(Class<T> expected, int position) throws ContextException {
		String key = expected.getName();
		Object value = get(key, position);
		//GenericsFaker<T> xUno = new GenericsFaker(expected, value);
		//T ret = xUno.getValue();
		//log.debug("get key=" + key + ", pos=" + position + ", value=" + ret);
		return (T) value;
	}

	public int count(Class<?> expected) {
		return count(expected.getName());
	}

	public int count(String key) {
		if (!map.containsKey(key)) {
			throw new ContextException(execution.getActiveStep(), "Nothing found with key: " + key + " in in context: "
					+ map.keySet());
		}
		return map.get(key).size();
	}

	static class GenericsFaker<T> {

		private final T value;

		private final Class<T> declaredType;

		public GenericsFaker(Class<T> declaredType, T value) {
			this.value = value;
			this.declaredType = declaredType;
		}

		public T getValue() {
			return value;
		}

		public Class<T> getDeclaredType() {
			return declaredType;
		}

	}

	//java.util.Map interface implementation

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Class<?> clazz) {
		return map.containsKey(clazz.getName());
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public List<Object> get(Object key) {
		if (key == null) {
			throw new ContextException(execution.getActiveStep(), "Context key is null");
		}
		if (!map.containsKey(key)) {
			throw new ContextException(execution.getActiveStep(), "Nothing found with key '" + key + "' in " + map.keySet());
		}
		return map.get(key);
	}

	public List<Object> put(String key, List<Object> value) {
		if (key == null) {
			throw new ContextException(execution.getActiveStep(), "Will not store null key");
		}
		if (value == null) {
			throw new ContextException(execution.getActiveStep(), "Will not store null value under key '" + key + "'");
		}
		return map.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends List<Object>> m) {
		map.putAll(m);
	}

	public List<Object> remove(Object key) {
		if (key == null) {
			throw new ContextException(execution.getActiveStep(), "Context key is null");
		}
		if (!map.containsKey(key)) {
			throw new ContextException(execution.getActiveStep(), "Nothing found with key '" + key + "' in " + map.keySet());
		}
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Object clone() {
		return map.clone();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Collection<List<Object>> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<String, List<Object>>> entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return map.equals(o);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
