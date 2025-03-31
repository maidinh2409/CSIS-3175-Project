package com.example.dory.userDatabase;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A static utility class to generate salts, hash passwords, and check passwords with existing hashes.
 * The algorithm used is PBKDF2. The hashed value is 256 bits.
 * The class has been configured in a way so that all input and output values are Strings,
 * external classes will not need to use char[] or byte[] for the passwords, salts, and hashes.
 */
public class PasswordHandler {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String OTP_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Static utility class. No constructors needed to use any of it's methods.
     */
    private PasswordHandler(){}

    /**
     * Generate a random salt used to hash a password.
     * @return a 16 byte random salt converted to a string.
     */
    public static String getNewSalt(){
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return new String(salt);
    }

    /**
     * Hashes the given password with the provided salt and the PBKDF2WithHmacSHA1 algorithm.
     * @param password the password to be hashed
     * @param salt a 16 bytes salt as a string, ideally obtained from the getNewSalt() method
     * @return a 256 bit salted and hashed password, converted to a string.
     */
    public static String hash(String password, String salt){
        char[] passwordChar = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passwordChar, saltBytes, ITERATIONS, KEY_LENGTH);
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        try{
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return new String(skf.generateSecret(spec).getEncoded());
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password: " + e.getMessage());
        }
        finally {
            spec.clearPassword();
        }
    }

    /**
     * Compares a password and salt to an existing hash, returning true if they're the same and false otherwise.
     * @param password the password to be validated
     * @param salt the salt used to hash the password
     * @param expectedHash the expected hash value of the password
     * @return true if the password and salt matched the hash and false otherwise
     */
    public static boolean validatePassword(String password, String salt, String expectedHash){
        char[] passwordChar = password.toCharArray();
        byte[] expectedHashBytes = expectedHash.getBytes();
        byte[] passwordHash = hash(password, salt).getBytes();
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        if (passwordHash.length != expectedHash.getBytes().length){
            return false;
        }
        for (int i=0; i<passwordHash.length; i++){
            if(passwordHash[i] != expectedHashBytes[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a random OTP
     * @param length the length of the OTP code;
     * @return a string with random characters of the specified length
     */
    public static String generateOtp(int length){
        StringBuilder otp = new StringBuilder();
        for(int i=0; i<length; i++){
           otp.append(OTP_CHARACTERS.charAt(RANDOM.nextInt(OTP_CHARACTERS.length())));
        }
        return otp.toString();
    }
}
