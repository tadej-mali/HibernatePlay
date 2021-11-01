package io.licitat.hiberplay.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

public class ClassMapper<FROM, TO> {

    private List<Mapper<FROM, TO>> mappings = new ArrayList<>();

    public <T> ClassMapper<FROM, TO> map(Function<FROM, T> getter, BiConsumer<TO, T> setter) {
        mappings.add(new ClassAttributeMap<FROM, TO, T>(setter, getter));
        return this;
    }

    public <F, T> ClassMapper<FROM, TO> mapCollection(
        Function<FROM, List<F>> source,
        BiConsumer<TO, List<T>> target, Supplier<T> createTarget,
        Consumer<ClassMapper<F, T>> configure
    ) {
        ListMapper<FROM, F, TO, T> listMapper = new ListMapper<FROM, F, TO, T>(source, target, createTarget);
        configure.accept(listMapper.getMemberMapper());
        mappings.add(listMapper);
        return this;
    }

    public Runnable arm(FROM source, TO target) {
        List<Runnable> ops = mappings.stream().map(m -> m.arm(source, target)).collect(toList());
        return () -> ops.forEach(Runnable::run);
    }

    public void map(FROM source, TO target) {
        arm(source, target).run();
    }
}
