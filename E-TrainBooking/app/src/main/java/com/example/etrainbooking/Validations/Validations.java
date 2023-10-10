package com.example.etrainbooking.Validations;

public class Validations {

    public static boolean isEmailValid(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+";
        return email.matches(emailPattern);
    }

    public static boolean isNICValid(String nic) {
        // NIC should contain only numbers (0-9) or numbers with 'V' or 'v'
        return nic.matches("[0-9Vv]+") && (nic.length() >= 10 && nic.length() <= 12);
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        // Phone number should contain only numbers (0-9)
        return phoneNumber.matches("[0-9]+") && phoneNumber.length() == 10;
    }

    public static boolean arePasswordsMatching(String password, String confirmPassword) {
        // Check if the passwords match
        return password.equals(confirmPassword);
    }

    public static boolean isPasswordCorrect(String password){

        return password.length() >= 8 &&
                password.matches(".*\\d.*") ||                // At least one digit
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?\\\\|].*") || // At least one special character
                password.matches(".*[A-Z].*") ||             // At least one uppercase letter
                password.matches(".*[a-z].*");             // At least one lowercase letter
    }
}
