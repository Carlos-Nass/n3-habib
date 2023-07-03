import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Decoder {
	public static void main(String[] args) {
		String arqPrivateKey = args[0];
		String arqIn = args[1];
		String arqOut = args[2];

		/*
		ler os arquivos
		para cada linha do arquivo:
			converter para bigint
			descriptografar (modpow)
			converte para string (textchunk)
			append em uma string grande
		fim para
		desfazer base 64
		gravar num arquivo
		*/



        try {
            BigInteger[] privateKey = privateKeyReader(arqPrivateKey);
            List<String> encodedText = readText(arqIn);
            String decryptedText = decrypteRSA(encodedText, privateKey);
            String decodedText = decodedBase64(decryptedText);
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

	private static List<String> readText(String arqIn) throws IOException {
		List<String> lines = new ArrayList<>();
		
        try (BufferedReader reader = new BufferedReader(new FileReader(arqIn))){
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines;
	}

	private static String decodedBase64(String codedText) {
		byte[] decodedBytes = Base64.getDecoder().decode(codedText);
		return new String(decodedBytes);
	}

	private static String decrypteRSA(List<String> encodedText, BigInteger[] privateKey) {
		BigInteger d = privateKey[1];
		BigInteger n = privateKey[0];
		StringBuilder b64encoded = new StringBuilder();
		
		for (String line : encodedText){
			BigInteger encodedChunk = new BigInteger(line);
			BigInteger originalChunk = encodedChunk.modPow(d, n);
			b64encoded.append(new TextChunk(originalChunk).toString());
		}

		// foreach linha
			//BigInteger encodedChunk = new BigInteger(encodedText);
			//BigInteger originalChunk = encodedChunk.modPow(d, n);
			// textchunk (original).toString
			// b64.append(textchunk)
		return b64encoded.toString();
	}

	private static void writeText(String arqOut, String decodedText) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(arqOut))){
			bw.write(decodedText);
        	System.out.println(decodedText);
		}	
	}
}