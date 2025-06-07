package thc.util;

public class StringUtils {
    public static boolean isAlphabeticallyEqual(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private static String normalize(String input) {
        return input.chars()
                .filter(Character::isLetter)
                .map(Character::toLowerCase)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }
}
