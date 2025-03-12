package com.example.dory.userDatabase;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHandler {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private PasswordHandler(){}

    public static String getNewSalt(){
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Arrays.toString(salt);
    }

    public static String hash(String password, String salt){
        char[] passwordChar = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passwordChar, saltBytes, ITERATIONS, KEY_LENGTH);
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        try{
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return Arrays.toString(skf.generateSecret(spec).getEncoded());
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password: " + e.getMessage());
        }
        finally {
            spec.clearPassword();
        }
    }

    public static boolean validatePassword(String password, String salt, String expectedHash){
        char[] passwordChar = password.toCharArray();
        byte[] expectedHashBytes = expectedHash.getBytes();
        byte[] passwordHash = hash(password, salt).getBytes();
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        if (passwordHash.length != expectedHash.length){
            return false;
        }
        for (int i=0; i<passwordHash.length; i++){
            if(passwordHash[i] != expectedHashBytes[i]){
                return false;
            }
        }
        return true;
    }
}
