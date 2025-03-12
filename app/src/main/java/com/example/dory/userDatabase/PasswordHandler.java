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

    public static byte[] getNewSalt(){
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] hash(char[] password, byte[] salt){
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try{
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password: " + e.getMessage());
        }
        finally {
            spec.clearPassword();
        }
    }

    public static boolean validatePassword(char[] password, byte[] salt, byte[] expectedHash){
        byte[] passwordHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        if (passwordHash.length != expectedHash.length){
            return false;
        }
        for (int i=0; i<passwordHash.length; i++){
            if(passwordHash[i] != expectedHash[i]){
                return false;
            }
        }
        return true;
    }
}
