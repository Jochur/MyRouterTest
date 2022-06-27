package com.grechur.router_compiler.utils;

import java.util.Collection;
import java.util.Map;

public class Utils {
    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(final Map<?,?> coll) {
        return coll == null || coll.isEmpty();
    }
}
