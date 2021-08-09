import control.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader l = new FXMLLoader(getClass().getResource("view/LogInForm.fxml"));
        Parent root = l.load();
        primaryStage.setTitle("Book Store");
        primaryStage.setScene(new Scene(root, 300, 275));
        if(!((LogInController)l.getController()).loggedIn()){
            primaryStage.show();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
