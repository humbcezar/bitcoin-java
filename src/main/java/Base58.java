import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.util.Arrays.copyOfRange;

public class Base58 {
    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static String encode(byte[] key) {
        int zeroCount = 0;
        for (byte b : key) {
            if (b == 0) {
                zeroCount++;
            } else {
                break;
            }
        }
        BigInteger keyNumber = new BigInteger(1, key);
        String prefix = "1".repeat(zeroCount);
        String result = "";
        while (keyNumber.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divideAndRemainder = keyNumber.divideAndRemainder(valueOf(58));
            keyNumber = divideAndRemainder[0];
            int remainder = divideAndRemainder[1].intValueExact();
            result = BASE58_ALPHABET.substring(remainder, remainder + 1).concat(result);
        }
        return prefix.concat(result);
    }

    public static String encodeWithChecksum(byte[] key) throws NoSuchAlgorithmException {
        byte[] checksum = Hash256.hash(key);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.writeBytes(key);
        byteArrayOutputStream.writeBytes(new byte[]{checksum[0], checksum[1], checksum[2], checksum[3]});
        return encode(byteArrayOutputStream.toByteArray());
    }

    public static byte[] decodeWithChecksum(String key) throws NoSuchAlgorithmException {
        BigInteger number = ZERO;
        for (Character c : key.toCharArray()) {
            number = number.multiply(valueOf(58));
            number = number.add(valueOf(BASE58_ALPHABET.chars().mapToObj(ch -> (char) ch).collect(Collectors.toList()).indexOf(c)));
        }
        byte[] combined = BigIntegers.asUnsignedByteArray(25, number);
        byte[] checksum = copyOfRange(combined, 21, 25);
        if (!isValidAddress(combined, checksum)) {
            throw new RuntimeException("Bad address");
        }
        return copyOfRange(combined, 1, 21);
    }

    public static String decodeWithChecksumToHex(String key) throws NoSuchAlgorithmException {
        return Hex.toHexString(decodeWithChecksum(key));
    }

    private static boolean isValidAddress(byte[] combined, byte[] checksum) throws NoSuchAlgorithmException {
        return Arrays.equals(copyOfRange(Hash256.hash(copyOfRange(combined, 0, 21)), 0, 4), checksum);
    }

    public static String encodeFromHex(String key) {
        return encode(Hex.decodeStrict(key));
    }
}
