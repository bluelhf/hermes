package blue.lhf.hermes.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import static java.lang.Character.isWhitespace;

// Our loops have side effects in their predicates
@SuppressWarnings({"StatementWithEmptyBody", "ResultOfMethodCallIgnored"})
public class ReaderUtils {
    private ReaderUtils() {
    }

    public static void skipUntil(final Reader reader, final char target) throws IOException {
        for (int chr; (chr = reader.read()) != -1 && chr != target;);
    }

    public static String readUntil(final Reader reader, final char target) throws IOException {
        final var sb = new StringBuilder();
        for (int chr; (chr = reader.read()) != -1;) {
            if (chr == target) {
                return sb.toString();
            }

            sb.append((char) chr);
        }
        return sb.toString();
    }

    public static String readUntil(final Reader reader, final String target) throws IOException {
        final var sb = new StringBuilder();
        for (int chr; (chr = reader.read()) != -1;) {
            sb.append((char) chr);

            final int index = sb.lastIndexOf(target);
            if (index > -1) {
                return sb.substring(0, index);
            }

        }
        return sb.toString();
    }

    public static void skipUntil(final Reader reader, final String target) throws IOException {
        final int len = target.length();
        final CharBuffer data = CharBuffer.allocate(len);
        final char[] arr = data.array();
        for (int chr; (chr = reader.read()) != -1;) {
            System.arraycopy(arr, 1, arr, 0, len - 1);
            arr[len - 1] = (char) chr;
            if (target.contentEquals(data))
                return;
        }
    }

    public static boolean hasNext(final Reader reader) throws IOException {
        return peek(reader) != -1;
    }

    public static String readWhitespace(final Reader reader) throws IOException {
        final var sb = new StringBuilder();
        for (int chr; (chr = peek(reader)) != -1 && isWhitespace(sb.append((char) chr).charAt(sb.length() - 1));)
            reader.skip(1);
        return sb.substring(0, sb.length());
    }

    public static int peek(final Reader reader) throws IOException {
        reader.mark(1);
        final int result = reader.read();
        reader.reset();
        return result;
    }

    public static boolean hasNext(final Reader reader, final String target) throws IOException {
        reader.mark(target.length());
        final CharBuffer buffer = CharBuffer.allocate(target.length());
        reader.read(buffer);
        buffer.flip();
        final boolean match = target.contentEquals(buffer);
        reader.reset();
        return match;
    }
}
