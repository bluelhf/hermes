# Hermes

> *Nevertheless some god has held his hand over me, in that he has  
> sent such a wayfarer as yourself to meet me so opportunely.*

Hermes is a small utility to aid in parsing ProGuard mappings files. It
does no class resolution of any kind, and you do not need to know
where a  mappings file came from in order to parse it with Hermes.


## Performance

[<img align="right" alt="Very Cool Flame Graph" src="https://i.postimg.cc/9FFvbzz7/image.png" width="50%"/>](https://postimg.cc/6Tgj5WrW)

Hermes accepts a Reader, and, during execution, spends most of its
time playing around with the Reader checking if it's found the ` -> `
yet. Readers are great for streamed data, but they're quite sequential
in nature, so Hermes sadly operates on only a single thread. It'll still
read your 5000-class code-base's mappings file in 700 milliseconds
on your school laptop, though.
<br/><br/><br/>
## Usage
The code snippet below documents the use of Hermes. It's so simple you can figure the rest out just by reading its code.
```java
final var result = Hermes.parse("""
# Lines starting with an octothorpe are ignored.
# Types in method and field descriptors are not
# subject to mapping (the return type for fum()
# in the example is 'fee.fi.Fo', not 'b'.)
foo.bar.Baz -> a:
    int myNumber -> a
    6:6:void <init>() -> <init>
    12:13:fee.fi.Fo fum(foo.bar.Baz,double) -> englishman
fee.fi.Fo -> b:
""");

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
```
```shell
# Order wasn't kept. Such is the life of a java.util.Set.
foo.bar.Baz -> a:
    6:6:void <init>() -> <init>
    int myNumber -> a
    12:13:fee.fi.Fo fum(foo.bar.Baz,double) -> englishman
fee.fi.Fo -> b:
```
