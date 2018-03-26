//package com.lateral.lateral;
//import org.junit.Test;
//
//import com.lateral.lateral.service.UserLoginTools;
//
//import junit.framework.Assert;
//
//import java.util.Arrays;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
///**
// * Class for testing various login functions
// */
//public class LoginTest {
//
//    /**
//     * Test the byteToHex function from UserLoginTools
//     */
//    @Test
//    public void testByteToHex(){
//        byte[] bytes = new byte[32];
//        for(int b = 0; b < bytes.length; b++){
//            bytes[b] = (byte) b;
//        }
//        assertFalse(UserLoginTools.bytesToHex(bytes).equals("0123456789ABCDEF101112131415161718191A1B1C1D1E1F"));
//        assertTrue(UserLoginTools.bytesToHex(bytes).equals("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F"));
//
//        bytes = new byte[16];
//        for(int b = 0; b < bytes.length; b++){
//            bytes[b] = (byte) (240 + b);
//        }
//        assertTrue(UserLoginTools.bytesToHex(bytes).equals("F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF"));
//    }
//
//    /**
//     * Test the hexToByte function from UserLoginTools
//     */
//    @Test
//    public void testHexToByte(){
//        String hexString = "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF";
//        byte[] bytes = new byte[16];
//        for(int b = 0; b < bytes.length; b++){
//            bytes[b] = (byte) (240 + b);
//        }
//        assertTrue(Arrays.equals(UserLoginTools.hexToBytes(hexString), bytes));
//
//        hexString = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";
//        bytes = new byte[32];
//        for(int b = 0; b < bytes.length; b++){
//            bytes[b] = (byte) b;
//        }
//        assertTrue(Arrays.equals(UserLoginTools.hexToBytes(hexString), bytes));
//
//        try {
//            hexString = "0123456";
//            UserLoginTools.hexToBytes(hexString);
//            Assert.fail("Should have thrown a StringIndexOutOfBoundsException");
//        }
//        catch (StringIndexOutOfBoundsException e){
//            assertTrue(true);
//        }
//    }
//
//    /**
//     * Test the hashPassword function from UserLoginTools using different salts
//     */
//    @Test
//    public void testNoCollisionOnDifferentSalt(){
//        String password = "VerifiableTestPassword";
//        String salt1 = UserLoginTools.randomBytes(64);
//        String salt2 = UserLoginTools.randomBytes(64);
//        assertFalse(salt1.equals(salt2));
//
//        String hashedPassword1 = UserLoginTools.hashPassword(password, salt1);
//        String hashedPassword2 = UserLoginTools.hashPassword(password, salt2);
//        assertFalse(hashedPassword1.equals(hashedPassword2));
//    }
//
//    /**
//     * Test the hashPassword function from UserLoginTools using different passwords
//     */
//    @Test
//    public void testNoCollisionOnDifferentPassword(){
//        String password1 = "VerifiableTestPassword1";
//        String password2 = "VerifiableTestPassword2";
//        String salt = UserLoginTools.randomBytes(64);
//        assertFalse(password1.equals(password2));
//
//        String hashedPassword1 = UserLoginTools.hashPassword(password1, salt);
//        String hashedPassword2 = UserLoginTools.hashPassword(password2, salt);
//        assertFalse(hashedPassword1.equals(hashedPassword2));
//    }
//
//    /**
//     * Test the hashPassword function from UserLoginTools using the same password and salt
//     */
//    @Test
//    public void testHashMatches(){
//        String password = "VerifiableTestPassword";
//        String salt = UserLoginTools.randomBytes(64);
//
//        String hashedPassword1 = UserLoginTools.hashPassword(password, salt);
//        String hashedPassword2 = UserLoginTools.hashPassword(password, salt);
//        assertTrue(hashedPassword1.equals(hashedPassword2));
//    }
//}
