package com.example.myemailapp.network;

public class LoginResponse {
    private String message;
    private String token;
    private User userJson;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUserJson() {
        return userJson;
    }

    public static class User {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String profilePic;
        private String phoneNumber;
        private String birthDate;
        private String gender;

        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getProfilePic() { return profilePic; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getBirthDate() { return birthDate; }
        public String getGender() { return gender; }
    }
}
