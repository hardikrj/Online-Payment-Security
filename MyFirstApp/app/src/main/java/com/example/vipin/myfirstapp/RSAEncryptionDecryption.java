package com.example.vipin.myfirstapp;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class RSAEncryptionDecryption {

    static Context context;

    private static final String PUBLIC_KEY_FILE = "Public";

    public RSAEncryptionDecryption(Context c){
        context =c;
    }

     byte[] encryptData(byte dataToEncrypt[]) throws IOException {

        byte[] encryptedData = null;
        try {

            InputStream ins=context.getResources().openRawResource(R.raw.key);
            PublicKey pubKey = readPublicKeyFromFile(ins);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
         return encryptedData;
    }

    public PublicKey readPublicKeyFromFile(InputStream fis) throws IOException{
        ObjectInputStream ois = null;
        try {
            //fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey;

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