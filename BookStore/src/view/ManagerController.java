package view;

import control.LogInController;
import control.UserController;
import javafx.stage.Stage;
import model.User;

public class ManagerController implements UserController {
    User currUsr;
    Stage mainWindow;


    @Override
    public void setUser(User user, Stage mainWindow) {
        currUsr = user;
        this.mainWindow = mainWindow;
    }
}
