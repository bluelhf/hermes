package blue.lhf.hermes.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

import static blue.lhf.hermes.util.ReaderUtils.readUntil;
import static blue.lhf.hermes.util.ReaderUtils.skipUntil;

public record MethodMapping(int start, int end, String returnType, String originalName, String[] parameters, String obfuscatedName) implements Mapping {
    public static MethodMapping from(final Reader reader) throws IOException {
        final int start = Integer.parseUnsignedInt(readUntil(reader, ':'));
        final int end = Integer.parseUnsignedInt(readUntil(reader, ':'));
        final String returnType = readUntil(reader, ' ');
        final String originalName = readUntil(reader, '(');

        final String[] parameterTypes;
        { // None of the split modes return empty array for blank inputs
            final String parameterString = readUntil(reader, ')');
            parameterTypes = parameterString.isBlank()
                    ? new String[0]
                    : parameterString.split(",");
        }
        skipUntil(reader, " -> ");
        final String obfuscatedName = readUntil(reader, '\n');
        return new MethodMapping(start, end, returnType, originalName, parameterTypes, obfuscatedName);
    }

    public static boolean matches(String line) {
        return line.contains(":");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMapping that = (MethodMapping) o;
        return start == that.start && end == that.end && returnType.equals(that.returnType) && originalName.equals(that.originalName) && Arrays.equals(parameters, that.parameters) && obfuscatedName.equals(that.obfuscatedName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(start, end, returnType, originalName, obfuscatedName);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
}
