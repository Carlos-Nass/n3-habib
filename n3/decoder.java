import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class decoder {
	public static void main(String[] args) {
		String arqPrivateKey = args[0];
		String arqIn = args[1];
		String arqOut = args[2];

        try {
            BigInteger[] privateKey = privateKeyReader(arqPrivateKey);
            String encodedText = readText(arqIn);
            BigInteger decryptedText = decrypteRSA(encodedText, privateKey);
            String decodedText = decodedBase64(decryptedText.toString());

            writeText(arqOut, decodedText);

        } catch (IOException e) {
            System.out.println(e);
        }
	}

	private static BigInteger[] privateKeyReader(String arqPrivateKey) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(arqPrivateKey))){
			BigInteger d = new BigInteger(reader.readLine());
			BigInteger n = new BigInteger(reader.readLine());
			
			return new BigInteger[] { d, n };
		}
	}

	private static String readText(String arqIn) throws IOException {
		StringBuilder builder = new StringBuilder();
		
        try (BufferedReader reader = new BufferedReader(new FileReader(arqIn))){
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
		return builder.toString();
	}

	private static String decodedBase64(String codedText) {
		BigInteger n = new BigInteger(codedText);
		TextChunk chunk = new TextChunk(n);
		return chunk.toString();
	}

	private static BigInteger decrypteRSA(String encodedText, BigInteger[] privateKey) {
		BigInteger d = privateKey[0];
		BigInteger n = privateKey[1];
		BigInteger encodedChunk = new BigInteger(encodedText);
		BigInteger originalChunk = encodedChunk.modPow(d, n);
		return originalChunk;
	}

	private static void writeText(String arqOut, String decodedText) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(arqOut))){
			bw.write(decodedText);
        	System.out.println(decodedText);
		}	
	}
}