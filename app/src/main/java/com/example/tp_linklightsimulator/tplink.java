package com.example.tp_linklightsimulator;

public class tplink {
    public static String decrypt(byte[] input) {
        byte firstKey = (byte) 0xAB;
        byte key = firstKey;
        byte nextKey;
        String output = "";
        for(int i = 0; i < input.length; i++)
        {
            nextKey = input[i];
            input[i] = (byte) (input[i] ^ key);
            key = nextKey;
        }
        return new String(input);
    }
    public static byte[] encrypt(String input) {
        byte[] byteInput = input.getBytes();
        byte firstKey = (byte) 0xAB;
        byte key = firstKey;
        for(int i = 0; i < byteInput.length; i++)
        {
            byteInput[i] = (byte) (byteInput[i] ^ key);
            key = key = byteInput[i];
        }
        return byteInput;
    }
}
