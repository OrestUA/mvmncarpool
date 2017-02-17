package x.mvmn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CollectionsUtil {

	@SafeVarargs
	public static <A, B> Map<A, B> toHashMap(Pair<A, B>... pairs) {
		Map<A, B> result = new HashMap<>();

		for (Pair<A, B> pair : pairs) {
			result.put(pair.getLeft(), pair.getRight());
		}

		return result;
	}

	@SafeVarargs
	public static <A, B> Map<A, B> toTreeMap(Pair<A, B>... pairs) {
		Map<A, B> result = new TreeMap<>();

		for (Pair<A, B> pair : pairs) {
			result.put(pair.getLeft(), pair.getRight());
		}

		return result;
	}

	public static <A, B> Pair<A, B> pair(A left, B right) {
		return new ImmutablePair<A, B>(left, right);
	}
}
