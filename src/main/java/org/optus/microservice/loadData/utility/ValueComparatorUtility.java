package org.optus.microservice.loadData.utility;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueComparatorUtility {

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, long count ) {
	    return map.entrySet()
	              .stream()
	              /*.sorted(Map.Entry.comparingByValue())*/ //For Asscending Order
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder())) //For Descending Order
	              .limit(count)
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
}
