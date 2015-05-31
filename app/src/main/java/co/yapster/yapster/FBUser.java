package co.yapster.yapster;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gurkarangulati on 5/9/15.
 */
public class FBUser extends Object {

    Integer id;
    String name;
    String firstName;
    String lastName;
    String email;
    String dateOfBirth;
    String gender;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        Integer numberOfWords = countWords(this.name);
        if (numberOfWords == 1){

        }else{
            if (numberOfWords == 2){
                String arr[] = name.split(" ", 2);
                this.firstName = arr[0];
                this.lastName = arr[1];
            } else if (numberOfWords == 3){
                String arr[] = name.split(" ", 3);
                this.firstName = arr[0];
                this.lastName = arr[2];
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private Integer countWords(String s){
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }
}
