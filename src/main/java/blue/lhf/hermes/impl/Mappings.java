package blue.lhf.hermes.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static blue.lhf.hermes.util.ReaderUtils.*;

public class Mappings extends HashSet<ClassMapping> {
    private final Set<ClassMapping> set;

    public Mappings(Set<ClassMapping> set) {
        this.set = set;
    }

    public static Mappings from(final Reader reader) throws IOException {
        Set<ClassMapping> set = new HashSet<>();
        while (hasNext(reader)) {
            if (hasNext(reader, "#")) {
                skipUntil(reader, '\n');
                continue;
            }
            set.add(ClassMapping.from(reader));
        }
        return new Mappings(set);
    }

    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(Object o) {
        return set.contains(o);
    }

    public ClassMapping[] toArray() {
        return set.toArray(new ClassMapping[0]);
    }

    public String toString() {
        return set.toString();
    }

    public Iterator<ClassMapping> iterator() {
        return new Iterator<>() {
            private final Iterator<? extends ClassMapping> i = set.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public ClassMapping next() {
                return i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void forEachRemaining(Consumer<? super ClassMapping> action) {
                // Use backing collection version
                i.forEachRemaining(action);
            }
        };
    }

    @Override
    public boolean add(ClassMapping mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return set.containsAll(coll);
    }

    @Override
    public boolean addAll(Collection<? extends ClassMapping> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    // Override default methods in Collection
    @Override
    public void forEach(Consumer<? super ClassMapping> action) {
        set.forEach(action);
    }

    @Override
    public boolean removeIf(Predicate<? super ClassMapping> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<ClassMapping> spliterator() {
        return set.spliterator();
    }

    @Override
    public Stream<ClassMapping> stream() {
        return set.stream();
    }

    @Override
    public Stream<ClassMapping> parallelStream() {
        return set.parallelStream();
    }
}
