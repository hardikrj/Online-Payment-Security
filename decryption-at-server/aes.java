
import java.security.SecureRandom;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.*;
import java.nio.charset.StandardCharsets;
//import apache.commons.codec.binary.base64;

public class aes{

	private static final String PRIVATE_KEY_FILE = "Private";
	static final String seedValue = "This Is MySecure";
	static String details=null,Skey=null,Bkey=null;
	static byte b[];

	public static void input(){
		try {
			
		BufferedReader br = new BufferedReader(new FileReader("details.txt"));
		details = br.readLine();
		br.readLine();
		Bkey = br.readLine();
		Bkey=Bkey+br.readLine();
		Bkey=Bkey+br.readLine();
		Bkey=Bkey+br.readLine();
		Bkey=Bkey+br.readLine();		
		b = Base64.decode(Bkey,Base64.DEFAULT);
	
		String ans = decrypt(seedValue, details);
		System.out.println(ans);
		br.close();

		} 
		catch (Exception e){}
	}
	
  
    public static String decrypt(String seed, String encrypted) throws Exception {
		byte[] rawKey = decryptData(b);
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }
	
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }
	
	static byte[] decryptData(byte[] data) throws IOException {
	  byte[] decryptedData = null;

	  try {
	   PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
	   Cipher cipher = Cipher.getInstance("RSA");
	   cipher.init(Cipher.DECRYPT_MODE, privateKey);
	   decryptedData = cipher.doFinal(data);
	   
	  } catch (Exception e) {
	   e.printStackTrace();
	  } 
	  
	  return decryptedData;
	 }
	  
	  
	static public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException{
	  FileInputStream fis = null;
	  ObjectInputStream ois = null;
	  try {
	   fis = new FileInputStream(new File(fileName+".key"));
	   ois = new ObjectInputStream(fis);
	   
	   BigInteger modulus = (BigInteger) ois.readObject();
		  BigInteger exponent = (BigInteger) ois.readObject();
	   
		  //Get Private Key
		  RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
		  KeyFactory fact = KeyFactory.getInstance("RSA");
		  PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);
				
		  return privateKey;
		  
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	  finally{
	   if(ois != null){
		ois.close();
		if(fis != null){
		 fis.close();
		}
	   }
	  }
	  return null;
	 }
	 
    }
