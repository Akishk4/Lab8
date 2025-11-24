/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BackEnd;
import java.util.regex.Pattern;

public class AuthService {
    private final JsonDatabaseManager db;

    public AuthService(JsonDatabaseManager db) { 
        this.db = db;
    }

    public boolean signupStudent(String username, String email, String passwordPlain) {
        if (username == null || email == null || passwordPlain == null) {
            return false;
        }
        if (!isValidEmail(email) || username.isBlank() || passwordPlain.length() < 6) {
            return false;
        }
        
        String hash = HashUtil.sha256(passwordPlain);
        Student s = new Student(username, email, hash);
        return db.addUser(s);
    }

    public boolean signupInstructor(String username, String email, String passwordPlain) {
        if (username == null || email == null || passwordPlain == null) {
            return false;
        }
        if (!isValidEmail(email) || username.isBlank() || passwordPlain.length() < 6) {
            return false;
        }
        
        String hash = HashUtil.sha256(passwordPlain);
        Instructor i = new Instructor(username, email, hash);
        return db.addUser(i);
    }

    public User login(String email, String passwordPlain) {
        if (email == null || passwordPlain == null) {
            return null;
        }
        
        User u = db.findUserByEmail(email);
        if (u == null) {
            return null;
        }
        
        String hashed = HashUtil.sha256(passwordPlain);
        
        if (hashed.equals(u.getPasswordHash())) {
            return u;
        } else {
            return null;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return Pattern.matches("^\\S+@\\S+\\.\\S+$", email);
    }
}