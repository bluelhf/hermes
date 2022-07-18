package blue.lhf.hermes.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static blue.lhf.hermes.util.ReaderUtils.readUntil;
import static blue.lhf.hermes.util.ReaderUtils.readWhitespace;
import static java.util.Collections.unmodifiableSet;

public record ClassMapping(String originalName, String obfuscatedName, Set<Mapping> members) implements Mapping, Iterable<Mapping> {
    public static ClassMapping from(final Reader reader) throws IOException {
        final String originalName = readUntil(reader, " -> ");
        final String obfuscatedName = readUntil(reader, ':');
        final Set<Mapping> members = new HashSet<>();
        while (readWhitespace(reader).length() >= 4) {
            final String line = readUntil(reader, '\n');
            final Reader sub = new StringReader(line);
            if (MethodMapping.matches(line)) {
                members.add(MethodMapping.from(sub));
            } else if (FieldMapping.matches(line)) {
                members.add(FieldMapping.from(sub));
            } else {
                throw new IllegalArgumentException("Unknown input: " + line);
            }
        }

        return new ClassMapping(originalName, obfuscatedName, unmodifiableSet(members));
    }

    @Override
    public Iterator<Mapping> iterator() {
        return members().iterator();
    }
}
