import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigInteger;
import java.util.Base64;

public class encoder {
	public static void main(String[] args) {
		var arqPublicKey = args[0];
		var arqIn = args[1];
		var arqOut = args[2];

        try {
            var publicKey = publicKeyReader(arqPublicKey);
            var textIn = Files.readString(Path.of(arqIn));
            var codedText = codedBase64(textIn);
            var cryptedText = cryptedRSA(codedText, publicKey);

            Files.writeString(Path.of(arqOut), cryptedText.toString());

        } catch (IOException e) {
            System.out.println(e);
        } 
	}

	private static BigInteger[] publicKeyReader(String arqPublicKey) throws IOException {
		var line = Files.readAllLines(Path.of(arqPublicKey));
		var e = new BigInteger(line.get(0));
		var n = new BigInteger(line.get(1));
		return new BigInteger[] { e, n };
	}

	private static String codedBase64(String text) {
		var bytes = text.getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(bytes);
	}

	private static BigInteger cryptedRSA(String text, BigInteger[] publicKey) {
		var e = publicKey[0];
		var n = publicKey[1];
		var message = new TextChunk(text).bigIntValue();
		return message.modPow(e, n);
	}
}