package org.example.app;

public class User {

    private String username;
    private String name;
    private String age;

    public User(String username, String name, String age) {
        this.username = username;
        this.name = name;
        this.age = age;
    }

    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
