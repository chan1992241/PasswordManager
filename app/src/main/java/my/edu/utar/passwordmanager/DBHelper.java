package my.edu.utar.passwordmanager;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "PasswordManager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String USER_TABLE_NAME = "User";
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_PASSWORD = "password";
    private static final String USER_USERNAME = "username";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String createUserTableQuery =
                "CREATE TABLE " + USER_TABLE_NAME + " (" + USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + USER_USERNAME + " VARCHAR(1000), " + USER_PASSWORD + " VARCHAR(1000));";
        db.execSQL(
                createUserTableQuery
        );
        db.execSQL(
                "create table Password (id INTEGER primary key autoincrement, userID int not null, " +
                        "siteName VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, FOREIGN KEY(userID) REFERENCES User(id));");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS Password");
        onCreate(db);
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_USERNAME + " = '" + username + "';", null);
        if (res.moveToFirst() == true) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        password = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println(password);
        contentValues.put("password", password);
        long result = db.insert("User", null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;
    }


    @SuppressLint("Range")
    public int checkUserPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id , password from User where username = '" + username + "'", null);
        if (res.moveToFirst()) {
            //Check is equal to input password
            String hashedPassword = res.getString(res.getColumnIndex("password"));
            System.out.println(hashedPassword);
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
            System.out.println(result.verified == true);
            if (result.verified == true) {
                // if correct password return userID
                return Integer.parseInt(res.getString(res.getColumnIndex("id")));
            }
        }
        return -1;
    }

    @SuppressLint("Range")
    public boolean checkUserPasswordwithUserId(String username, String password, String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        /* Cursor res = db.rawQuery("select id , password from User " +
                "where username = '" + username + "' " +
                "and id = " + userID + " and password = '"
                + password + "'", null); */
        Cursor res = db.rawQuery("select id , password from User " +
                "where username = '" + username + "' " +
                "and id = " + userID + "", null);
        if (res.moveToFirst()) {
            String hashedPassword = res.getString(res.getColumnIndex("password"));
            System.out.println(hashedPassword);
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
            if (result.verified == true){
                //Check is equal to input password
                return true;
            }
            return false;
        }
        return false;
    }

    @SuppressLint("Range")
    public boolean insertNewPassword(String userID, String siteName, String username, String password) {
        //TODO encrypt password
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor userInfo = db.rawQuery("select password from User where id = " + userID, null);
        String encryptionPassword = null;
        if (userInfo.moveToFirst()) {
            //Check is equal to input password
            encryptionPassword = userInfo.getString(userInfo.getColumnIndex("password"));
        }
        try{
            // Encrypt password
            password = encrypt(password, encryptionPassword);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }
        // System.out.println(password);
        ContentValues contentValues = new ContentValues();
        contentValues.put("userID", userID);
        contentValues.put("siteName", siteName);
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.insert("password", null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;
    }

    @SuppressLint("Range")
    public Cursor getPasswordList(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Password where userID=" + userID + ";", null);
        return res;
    }

    @SuppressLint("Range")
    public HashMap getPassword(String passwordID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select u.password as encryptionPassword,p.password as password, p.siteName as siteName, p.username as username " +
                "from Password p inner join User u on p.userID = u.id" +
                " where p.id = " + passwordID + ";", null);
        String siteName, username, password, encryptedPass, oriPassword;
        siteName = username = password = encryptedPass = oriPassword = null;
        if (res.moveToFirst()){
            siteName = res.getString(res.getColumnIndex("siteName"));
            username = res.getString(res.getColumnIndex("username"));
            password = res.getString(res.getColumnIndex("password"));
            encryptedPass = res.getString(res.getColumnIndex("encryptionPassword"));
        }
        System.out.println(password);
        try{
            oriPassword = decrypt(password,encryptedPass);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        HashMap<String, String> resVal = new HashMap<String, String>();
        resVal.put("siteName", siteName);
        resVal.put("username", username);
        resVal.put("password", oriPassword);
        return resVal;
    }

    @SuppressLint("Range")
    public boolean updatePassword(String passwordID, String userID, String siteName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor userInfo = db.rawQuery("select password from User where id = " + userID, null);
        String encryptionPassword = null;
        if (userInfo.moveToFirst()) {
            //Check is equal to input password
            encryptionPassword = userInfo.getString(userInfo.getColumnIndex("password"));
        }
        try{
            // Encrypt password
            password = encrypt(password, encryptionPassword);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("userID", userID);
        contentValues.put("siteName", siteName);
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.update("password", contentValues, "id = ?", new String[]{passwordID});
        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean deletePassword(String passwordID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("password", " id = ? ", new String[]{passwordID});
        if (result == -1) {
            return false;
        }
        return true;
    }

    // Encrypt decrypt test
    private String encrypt (String passwordToEncrypt, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(passwordToEncrypt.getBytes());
        String encryptedVal = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedVal;
    }

    private String decrypt (String cipherTxt, String password) throws Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] cipherTxtBase64 = Base64.decode(cipherTxt, Base64.DEFAULT);
        byte[] decryptedVal = c.doFinal(cipherTxtBase64);
        String decryptedValStr = new String(decryptedVal);
        return decryptedValStr;
    }

    private SecretKeySpec generateKey (String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}