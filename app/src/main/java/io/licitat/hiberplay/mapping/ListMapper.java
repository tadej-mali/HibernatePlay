package io.licitat.hiberplay.mapping;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ListMapper<FROM, F, TO, T> implements Mapper<FROM, TO> {

    private final ClassMapper<F, T> memberMapper = new ClassMapper<F, T>();

    private Function<FROM, List<F>> source;
    private BiConsumer<TO, List<T>> target;
    private Supplier<T> createTarget;

    private Stream<F> sourceToStream(FROM source) {
        if (this.source == null) { return Stream.empty(); }
        Collection<F> theSource = this.source.apply(source);
        if (theSource == null) { return Stream.empty(); }
        return theSource.stream();
    }

    public Runnable arm(FROM source, TO target) {
        return () -> {
            List<T> mapped = sourceToStream(source)
                .map(src -> {
                    T tgt = createTarget.get();
                    memberMapper.arm(src, tgt).run();
                    return tgt;
                })
                .collect(toList());

            this.target.accept(target, mapped);
        };
    }

    public ListMapper(Function<FROM, List<F>> source, BiConsumer<TO, List<T>> target, Supplier<T> createTarget) {
        this.source = source;
        this.target = target;
        this.createTarget = createTarget;
    }

    public ClassMapper<F, T> getMemberMapper() {
        return memberMapper;
    }
}
