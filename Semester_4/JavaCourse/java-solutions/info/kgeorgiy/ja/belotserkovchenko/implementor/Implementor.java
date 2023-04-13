package info.kgeorgiy.ja.belotserkovchenko.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link JarImpler} interface generating classes with methods returning default values.
 *
 * @author Andrey Belocerkovchenko
 */
public class Implementor implements JarImpler {
    /**
     * Prefix for temporary directory with {@code .class} files.
     */
    private final static String TMP_PREFIX = "jimpl-";

    /**
     * The size of the {@link BufferedInputStream} buffer used to read {@code .class} files.
     */
    private final static int BUFF_SIZE = 1024;

    /**
     * System-dependent line separator string.
     */
    private final static String LS = System.lineSeparator();

    /**
     * Generates an implementation of specified class or interface according to passed arguments.
     * The method can take one command line argument - the full name of class/interface for which you want to
     * generate an implementation, in the format:
     * <pre>
     *     {@code java <class name>}
     * </pre>
     *
     * Use {@code -jar} to generate {@code .jar} file with implementation of specified class. Use the following format:
     * <pre>
     *     {@code java -jar <class name> <jar-file>}
     * </pre>
     *
     * For generation, the {@link #implement(Class, Path)} and {@link #implementJar(Class, Path)} methods are used
     * respectively.
     * Displays an error message if the class cannot be found or {@link ImplerException} was thrown while during the
     * call of {@link #implement(Class, Path)} and {@link #implementJar(Class, Path)} methods.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                new Implementor().implement(Class.forName(args[0]), Paths.get("."));
                return;
            }

            if (args.length == 3 && args[0].equals("-jar") && args[2].endsWith(".jar")) {
                new Implementor().implementJar(Class.forName(args[0]), Paths.get(args[2]));
                return;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find class: " + e.getMessage());
        } catch (ImplerException e) {
            System.out.println("Implementor error occurred: " + e.getMessage());
        }

        System.out.println("Usage:");
        System.out.println("\tjava <class name>");
        System.out.println("\tjava -jar <class name> <jar-file>");
    }

    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        try {
            Path root = jarFile.getParent();
            Files.createDirectories(root);
            try {
                Path tempDir = Files.createTempDirectory(root, TMP_PREFIX);
                implement(token, tempDir);

                compile(token, tempDir);

                try (JarOutputStream jarOutputStream = new JarOutputStream(
                        new BufferedOutputStream(Files.newOutputStream(jarFile)))) {

                    JarEntry packageEntry = new JarEntry(getImplName(token).replace(".", "/") + ".class");
                    jarOutputStream.putNextEntry(packageEntry);
                    try (BufferedInputStream fileReader =
                                 new BufferedInputStream(Files.newInputStream(getFile(tempDir, token, ".class")))) {
                        byte[] buff = new byte[BUFF_SIZE];
                        int len;
                        while ((len = fileReader.read(buff)) != -1) {
                            jarOutputStream.write(buff, 0, len);
                        }
                    } catch (IOException | SecurityException e) {
                        throw new ImplerException("Unable to write class file to jar: " + e.getMessage(), e);
                    }
                    jarOutputStream.closeEntry();
                } catch (IOException | SecurityException e) {
                    throw new ImplerException("Unable to create jar file: " + e.getMessage(), e);
                }
                deleteDir(tempDir);
            } catch (IOException | SecurityException e) {
                throw new ImplerException("Unable to create temp dir: " + e.getMessage(), e);
            }
        } catch (IOException | SecurityException e) {
            throw new ImplerException("Unable to create root dir: " + e.getMessage(), e);
        }
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkToken(token);

        try {
            String tokenName = token.getSimpleName();
            String className = token.getPackageName().replace(".", File.separator)
                    + File.separator + tokenName + "Impl.java";
            Path fullPath = root.resolve(className);
            Files.createDirectories(fullPath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(fullPath.toAbsolutePath(), StandardCharsets.UTF_8)) {
                writeClass(token, tokenName + "Impl", bw);
            } catch (IOException | SecurityException e) {
                throw new ImplerException("Unable to write to the output file: " + e.getMessage(), e);
            }
        } catch (IOException | SecurityException e) {
            throw new ImplerException("Unable to create root directory: " + e.getMessage(), e);
        }
    }

    /**
     * Recursively deletes a folder with all its contents.
     *
     * @param directory the path to folder to delete
     * @throws ImplerException if {@link IOException} occurs while deleting files
     */
    private static void deleteDir(Path directory) throws ImplerException {
        try {
            FileVisitor<Path> clearVisitor = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            };
            Files.walkFileTree(directory, clearVisitor);
        } catch (IOException | SecurityException e) {
            throw new ImplerException("Unable to delete temp dir: " + e.getMessage(), e);
        }
    }

    /**
     * Compiles the specified class and places to the given directory.
     *
     * @param token type token of class we need to compile.
     * @param root directory we need to place result {@code .class} file to.
     * @throws ImplerException if exit code is not equal to 0 during compilation.
     */
    private static void compile(Class<?> token, final Path root) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final String classpath = root + File.pathSeparator + getClassPath(token);
        String file = getFile(root, token, ".java").toString();
        final String[] args = Stream.of(file, "-cp", classpath, "-encoding", StandardCharsets.UTF_8.name())
                .toArray(String[]::new);
        int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Exit code = " + exitCode + " != 0");
        }
    }

    /**
     * Returns a {@link String} representation of the absolute classpath.
     *
     * @param token type token to get classpath for.
     * @return classpath to specified type token
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns the {@link Path} to the class being implemented.
     *
     * @param root the directory where the class implementation should be
     * @param clazz type token of the class being implemented
     * @param suffix file suffix, for example {@code .class} or {@code .java}
     * @return {@link Path} to the file with specified suffix
     */
    private static Path getFile(final Path root, final Class<?> clazz, String suffix) {
        return root.resolve(getImplName(clazz).replace(".", File.separator) + suffix).toAbsolutePath();
    }

    /**
     * Returns canonical name of class implementation.
     * Name of result class always ends with <var>Impl</var> suffix
     *
     * @param token type token of the class being implemented
     * @return canonical name of implementation.
     */
    private static String getImplName(final Class<?> token) {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

    /**
     * Checks if a class can be implemented.
     * In case the class is primitive, private, final, enum or class without available constructors,
     * the {@link ImplerException} will be thrown.
     *
     * @param token type token of verifiable class
     * @throws ImplerException if class can't be implemented
     */
    private void checkToken(Class<?> token) throws ImplerException {
        int modifiers = token.getModifiers();
        String tokenName = token.getSimpleName();
        if (Modifier.isPrivate(modifiers)) {
            throw new ImplerException("You can't implement private interface: " + tokenName);
        }
        if (Modifier.isFinal(modifiers)) {
            throw new ImplerException("You can't extend final class: " + tokenName);
        }
        if (token.isPrimitive() || token.isArray() || token.equals(Enum.class)) {
            throw new ImplerException("You can't implement primitive type or array: " + tokenName);
        }
        if (!token.isInterface() &&
                Arrays.stream(token.getDeclaredConstructors()).allMatch(c -> Modifier.isPrivate(c.getModifiers()))) {
            throw new ImplerException("You can't implement class without available constructors: " + tokenName);
        }
    }

    /**
     * Returns the result of executing function on array elements separated by commas.
     *
     * @param arr array of processed objects
     * @param function function applied on array elements
     * @return {@link String} with results
     * @param <T> type of processed objects
     */
    private static <T> String joining(T[] arr, Function<T, String> function) {
        return Arrays.stream(arr).map(function).collect(Collectors.joining(", "));
    }

    /**
     * Writes class to specified {@link BufferedWriter}. Including package, class modifiers, constructors and methods.
     *
     * @param token type token of the class being implemented
     * @param className implementation class name
     * @param bw output {@link BufferedWriter}
     * @throws IOException if I/O Exception occurs while writing to bw
     */
    private void writeClass(Class<?> token, String className, BufferedWriter bw) throws IOException {
        bw.write("package " + token.getPackageName() + ";" + LS + LS);
        bw.write("public class " + className + " ");
        if (token.isInterface()) {
            bw.write("implements");
        } else {
             bw.write("extends");
        }

        bw.write(" " + token.getCanonicalName() + " {" + LS);
        for (Constructor<?> c : token.getDeclaredConstructors()) {
            writeConstructor(c, className, bw);
        }

        for (Method m : token.getMethods()) {
            writeMethod(m, bw);
        }

        for (Method m : token.getDeclaredMethods()) {
            if (Modifier.isProtected(m.getModifiers())) {
                writeMethod(m, bw);
            }
        }
        bw.write("}");
    }

    /**
     * Writes method with given attributes to specified {@link BufferedWriter}.
     *
     * @param modifiers set of modifiers represented as integer
     * @param returnType method return type
     * @param methodName method name
     * @param args method arguments
     * @param exceptions method exceptions
     * @param body the inner part of the method, for example {@code return new Int[0][][];} or {@code super();}
     * @param bw output {@link BufferedWriter}
     * @throws IOException if I/O Exception occurs while writing to bw
     * @see Modifier
     */
    private void writeMethodImpl(
            int modifiers, Class<?> returnType, String methodName, Parameter[] args, Class<?>[] exceptions,
            String body, BufferedWriter bw) throws IOException {
        bw.write("\t" + Modifier.toString(modifiers & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT));
        if (returnType != null) {
            bw.write(" " + returnType.getCanonicalName());
        }
        bw.write(" " + methodName + "(");
        bw.write(joining(args, p -> p.getType().getCanonicalName() + " " + p.getName()));
        bw.write(")");

        if (exceptions.length != 0) {
            bw.write(" throws ");
            bw.write(joining(exceptions, Class::getCanonicalName));
        }

        bw.write(" {" + LS);
        bw.write("\t\t" + body);
        bw.write(LS + "\t}" + LS + LS);
    }

    /**
     * Writes given constructor to specified {@link BufferedWriter}.
     * The result constructor only contains a call to the superclass constructor.
     *
     * @param c class constructor
     * @param className the name of the implemented class
     * @param bw output {@link BufferedWriter}
     * @throws IOException if I/O Exception occurs while writing to bw
     */
    private void writeConstructor(Constructor<?> c, String className, BufferedWriter bw) throws IOException {
        if (Modifier.isPrivate(c.getModifiers())) {
            return;
        }

        Parameter[] args = c.getParameters();
        String body = "super(" + joining(args, Parameter::getName) + ");";
        writeMethodImpl(c.getModifiers(), null, className, args,
                c.getExceptionTypes(), body, bw);
    }

    /**
     * Writes given method to specified {@link BufferedWriter}.
     * The result method only contains return of default value for method return type: null for Objects, empty arrays
     * for arrays, false for boolean, 0 for numerical types and nothing for void.
     *
     * @param m class method
     * @param bw output {@link BufferedWriter}
     * @throws IOException if I/O Exception occurs while writing to bw
     */
    private void writeMethod(Method m, BufferedWriter bw) throws IOException {
        if (!Modifier.isAbstract(m.getModifiers())) {
            return;
        }

        String result;
        String body;
        Class<?> rType = m.getReturnType();
        String typeName = rType.getCanonicalName();
        if (rType != void.class) {
            if (rType.isArray()) {
                result = "new " + typeName.replaceFirst("\\[", "[0");
            } else if (rType.equals(boolean.class)) {
                result = "false";
            } else if (rType.isPrimitive()) {
                result = "0";
            } else {
                result = "null";
            }
            body = "return " + result + ";";
        } else {
            body = "";
        }

        writeMethodImpl(m.getModifiers(), rType, m.getName(), m.getParameters(),
                m.getExceptionTypes(), body, bw);
    }
}
