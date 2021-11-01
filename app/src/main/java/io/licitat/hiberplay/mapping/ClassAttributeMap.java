package io.licitat.hiberplay.mapping;

import java.util.function.BiConsumer;
import java.util.function.Function;

class ClassAttributeMap<FROM, TO, T> implements Mapper<FROM, TO> {

    private final BiConsumer<TO, T> setter;
    private final Function<FROM, T> getter;

    public Runnable arm(FROM source, TO target) {
        return () -> setter.accept(target, getter.apply(source));
    }

    public ClassAttributeMap(BiConsumer<TO, T> setter, Function<FROM, T> getter) {
        this.setter = setter;
        this.getter = getter;
    }
}
