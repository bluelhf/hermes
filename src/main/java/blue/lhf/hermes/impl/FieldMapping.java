package blue.lhf.hermes.impl;

import java.io.IOException;
import java.io.Reader;

import static blue.lhf.hermes.util.ReaderUtils.readUntil;

public record FieldMapping(String type, String originalName, String obfuscatedName) implements Mapping {
    public static FieldMapping from(Reader reader) throws IOException {
        return new FieldMapping(
                readUntil(reader, ' '),
                readUntil(reader, " -> "),
                readUntil(reader, '\n')
        );
    }

    public static boolean matches(String line) {
        return !line.contains(":");
    }
}
