package com.mZone.epro.client.utility;

import java.util.Random;

public class GenerateKey {
    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int PASSWORD_LENGTH = 6;

    public String generateKey()
    {
        Random rnd = new Random();
        //gen random 3 word
        StringBuilder sb = new StringBuilder( );
       for( int i = 0; i < PASSWORD_LENGTH; i++ ) 
          sb.append( SYMBOLS.charAt( rnd.nextInt(SYMBOLS.length()) ) );
       return sb.toString();
    }
}
