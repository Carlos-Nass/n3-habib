import java.io.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigInteger;
import java.util.Base64;

public class Encoder {
	public static void main(String[] args) {
		var arqPublicKey = args[0];
		var arqIn = args[1];
		var arqOut = args[2];

		/*
		ler os arquivos
		converter o texto aberto em b64
		quebrar o texto convertido em chunks (texchunk - chunksize)
		para cada chunk:
			converter para bigint
			criptografar (modpow)
			gravar no arquivo
		fim para
		*/

        try {
            var publicKey = publicKeyReader(arqPublicKey);
            var textIn = Files.readString(Path.of(arqIn));
            var codedText = codedBase64(textIn);
            cryptedRSA(arqOut, codedText, publicKey);

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

	private static void cryptedRSA(String arqOut, String text, BigInteger[] publicKey) throws IOException {
		var e = publicKey[1];
		var n = publicKey[0];

		File fout = new File(arqOut);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		// obter o tamanho do chunk
		int chunkSize = TextChunk.blockSize(n);
		// quebrar o texto em chunks
		String[] chunks = text.split("(?<=\\G.{"+ chunkSize +"})");
		for (String chunk : chunks){
			BigInteger message = new TextChunk(chunk).bigIntValue();
			BigInteger encoded = message.modPow(e, n);
			bw.write(encoded.toString());
			bw.newLine();
		}
		
		bw.close();
	}
}