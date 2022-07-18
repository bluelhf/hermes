package blue.lhf.hermes;

import blue.lhf.hermes.impl.Mappings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused") // API
public class Hermes {
    public static Mappings parse(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            return Mappings.from(reader);
        }
    }

    public static Mappings parse(BufferedReader reader) throws IOException {
        return Mappings.from(reader);
    }

    public static Mappings parse(String string) {
        try {
            return Mappings.from(new StringReader(string));
        } catch (IOException e) {
            throw new IllegalStateException("Threw I/O exception when it should've been impossible", e);
        }
    }
}