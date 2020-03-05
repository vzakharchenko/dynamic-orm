package com.github.vzakharchenko.dynamic.orm.core.helper;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 20:58
 */
public abstract class FormatHelper {

    public static <T> T transformObjectValue(Object value, Class<T> valueClass) {
        try {
            T ret;
            if (value instanceof String && !valueClass.equals(String.class)) {
                Constructor<T> constructor = valueClass.getConstructor(String.class);
                ret = constructor.newInstance(value);
            } else if (!(value instanceof String) && valueClass.equals(String.class)) {
                ret = (T) Objects.toString(value);
            } else {
                ret = (T) value;
            }
            return ret;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
