package edu.buffalo.cse.cse486586.simpledht;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by imransay on 3/28/15.
 */
public class Tester {
    public static void main(String [] args){

        String em5554 = "5554";
        String em5556 = "5556";
        String em5558 = "5558";
        String em5560 = "5560";
        String em5562 = "5562";
        System.out.println(genHash(em5556));
        //System.out.println(genHash("fcKSZYfYyq1lLLwousbOh3mRyc0UDNkE"));
        System.out.println("sa "+genHash(em5554).compareTo(genHash("tE2N1oK6OuGJX2mZS1415GiVPelk6Blq")));


    }

    private static String genHash(String input) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
