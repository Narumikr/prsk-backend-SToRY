package com.example.untitled.common.util;

import java.util.function.Consumer;

public class EntityHelper {

    private EntityHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * @param value : 更新する値
     * @param setter : セッター
     */
    public static <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if(null != value) {
            setter.accept(value);
        }
    }
}
