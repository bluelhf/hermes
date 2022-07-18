package blue.lhf.hermes;

import blue.lhf.hermes.impl.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

import static java.lang.System.out;

class DeserialisationTests {
    @Test
    void roundTrip() {
        final String input = """
                ti.ts.CoolClass -> a:
                    double field -> j
                    6:9:void <init>() -> <init>
                    4:20:ti.ts.Tit method() -> c
                bo.obs.OtherClass -> b:
                    ti.ts.CoolClass reference -> p
                    8008:135:ti.ts.CoolClass methodTwo() -> ijk
                                """;

        final var result = Hermes.parse(input);
        assert result.contains(new ClassMapping(
                "ti.ts.CoolClass",
                "a",
                Set.of(
                        new FieldMapping("double", "field", "j"),
                        new MethodMapping(6, 9, "void", "<init>", new String[0], "<init>"),
                        new MethodMapping(4, 20, "ti.ts.Tit", "method", new String[0], "c")
                ))) : "Result was wrong";

        assert result.contains(new ClassMapping(
                "bo.obs.OtherClass",
                "b",
                Set.of(
                        new FieldMapping("ti.ts.CoolClass", "reference", "p"),
                        new MethodMapping(8008, 135, "ti.ts.CoolClass", "methodTwo", new String[0], "ijk")
                )
        )) : "Result was wrong";
        for (ClassMapping clazz : result) {
            out.printf("Now in %s, which was renamed to %s%n",
                    clazz.originalName(), clazz.obfuscatedName());
            for (Mapping member : clazz.members()) {
                out.print("    ");
                if (member instanceof MethodMapping method) {
                    out.printf("Method %s %s(%s) (lines %d-%d) was renamed to %s%n",
                            method.returnType(),
                            method.originalName(),
                            String.join(", ", method.parameters()),
                            method.start(),
                            method.end(),
                            method.obfuscatedName());
                } else if (member instanceof FieldMapping field) {
                    out.printf("Field %s %s was renamed to %s%n",
                            field.type(),
                            field.originalName(),
                            field.obfuscatedName());
                }
            }
        }
    }

    @Test
    void reconstructionTest() {
        final var result = Hermes.parse("""
                # Lines starting with an octothorpe are ignored.
                # Types in method and field descriptors are not
                # subject to mapping (the return type for fum()
                # in the example is 'fee.fi.Fo', not 'b'.)
                foo.bar.Baz -> a:
                    int myNumber -> a
                    6:6:void <init>() -> <init>
                    12:13:fee.fi.Fo fum(foo.bar.Baz,double) -> englishman
                fee.fi.Fo -> b:""");

        for (final var clazz : result) {
            out.printf("%s -> %s:%n",
                    clazz.originalName(),
                    clazz.obfuscatedName());

            for (final var member : clazz) {
                // I miss Rust's pattern matching
                if (member instanceof MethodMapping method) {
                    out.printf("    %d:%d:%s %s(%s) -> %s%n",
                            method.start(), method.end(),
                            method.returnType(),
                            method.originalName(),
                            String.join(",", method.parameters()),
                            method.obfuscatedName());
                } else if (member instanceof FieldMapping field) {
                    out.printf("    %s %s -> %s%n",
                            field.type(),
                            field.originalName(),
                            field.obfuscatedName());
                }
            }
        }
    }

    @Test
    void needntTrailingNewline() {
        final String input = "foo.bar -> a:";
        assert Hermes.parse(input).equals(Hermes.parse(input + "\n")):
                "Meaningful trailing newline";
    }

    @Test
    void speedTest() throws IOException {
        final URL resource = getClass().getResource("/speed_test.txt");
        assert resource != null : "Corrupt test suite";
        final var connection = resource.openConnection();
        final long length = connection.getContentLengthLong();
        out.println(length);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()), 131072)) {
            final long start = System.nanoTime();
            Mappings mappings = Hermes.parse(reader);
            final long end = System.nanoTime();
            out.printf("Parsed mappings for %d classes in %.6f ms%n", mappings.size(), (end - start) / 1E6);
            out.printf("%.6f bytes/s%n", (length * 1E9) / (end - start));
        }
    }
}