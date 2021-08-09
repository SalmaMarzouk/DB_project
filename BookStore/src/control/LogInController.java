package control;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LogInController {
    public PasswordField passwordField;
    public TextField usernameField;
    public Button loginButton;

    @FXML


    public void initialize() {

        loginButton.disableProperty().bind(Bindings.or(passwordField.textProperty().isEmpty(), usernameField.textProperty().isEmpty()));
    }


    private void showErrorAlert() {

        Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong Username or Password", ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Can't Login");
        alert.show();
    }


    public void clearLogInForm() {
        usernameField.setText("");
        passwordField.setText("");
    }


    public void logIn() {
        User user = User.getUser(usernameField.getText(), passwordField.getText());
        if (user == null) {
            showErrorAlert();
            clearLogInForm();
            return;
        }
        try {
            File userInfo = new File("UserInfo.txt");
            userInfo.createNewFile();
            FileWriter myWriter = new FileWriter("UserInfo.txt");
            myWriter.write(user.getUsername());
            myWriter.close();
        } catch (IOException e) {

        }
        Stage newWindow = constructNewWindow(user);

        if (newWindow == null) {
            showErrorAlert();
            clearLogInForm();
            return;
        }
        ((Stage) usernameField.getScene().getWindow()).hide();
        newWindow.show();
    }

    public User getLoggedInUser() {
        User user =null;
        String username = null;
        try {
            File myObj = new File("UserInfo.txt");
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
                username = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {

        }
        if (username != null) {
            user = User.getLoggedUser(username);

        }
        return user;
    }

    public boolean loggedIn() {
        User user = getLoggedInUser();
        if (user == null) {
            return false;
        }
        Stage newWindow = constructNewWindow(user);

        if (newWindow == null) {
            showErrorAlert();
            clearLogInForm();
            return false;
        }
        newWindow.show();
        return true;
    }


    private Stage constructNewWindow(User user) {
        FXMLLoader fxmlLoader = getUI(user);

        if (fxmlLoader == null) {
            return null;
        }
        Stage newWindow = new Stage();
        newWindow.setTitle("Book Store");
        newWindow.setScene(new Scene(fxmlLoader.getRoot()));
        newWindow.setMaximized(true);
        ((UserController) fxmlLoader.getController()).setUser(user, (Stage) loginButton.getScene().getWindow());

        return newWindow;
    }


    private FXMLLoader getUI(User user) {
        FXMLLoader loader = null;
        if (user.getType().equals("customer")) {

            loader = new FXMLLoader(getClass().getResource("../View/customerForm.fxml"));
        } else if (user.getType().equals("manager")) {
            loader = new FXMLLoader(getClass().getResource("../View/managerForm.fxml"));
        }
        if (loader == null) {
            return null;
        }
        try {
            loader.load();
        } catch (IOException e) {
            loader = null;
        }
        return loader;
    }

}
