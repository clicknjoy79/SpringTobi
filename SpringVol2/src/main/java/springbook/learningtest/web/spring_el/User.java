package springbook.learningtest.web.spring_el;

import javax.validation.constraints.Size;

public class User {
    int id;

    @Size(min = 2, max = 10)
    String name;
    String[] interests;
    UserController.Type userType;
    UserController.Local userLocal;


    public User(int id, String name, String[] interests) {
        this.id = id;
        this.name = name;
        this.interests = interests;
    }

    public User() {}

    public UserController.Type getUserType() {
        return userType;
    }

    public void setUserType(UserController.Type userType) {
        this.userType = userType;
    }

    public UserController.Local getUserLocal() {
        return userLocal;
    }

    public void setUserLocal(UserController.Local userLocal) {
        this.userLocal = userLocal;
    }

    public String[] getInterests() {
        return interests;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "USER [id = " + id + ", name = " + name + ", interests = "
                + interests.length + ", userType = " + userType + ", userLocal =  " + userLocal + "]";
    }
}
