package view;

import control.LogInController;
import control.UserController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Book;
import model.Item;
import model.User;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class CustomerController implements UserController {
    public TextField username;
    public PasswordField password;
    public TextField firstName;
    public TextField lastName;
    public TextField email;
    public TextField phone;
    public TextField shippingAddress;
    public TextField type;
    public Button updateInfoButton;
    public ComboBox<String> searchMethod;
    public TextField bookSearch;
    public Button searchButton;
    public TableView<Book> searchResultsTable;
    public TableColumn<Book, String> isbnCol;
    public TableColumn<Book, String> titleCol;
    public TableColumn<Book, String> authorCol;
    public TableColumn<Book, String> priceCol;
    public Button addToCartButton;
    public TableView<Item> cartTable;
    public TableColumn<Item, String> isbnCartCol;
    public TableColumn<Item, String> titleCartCol;
    public TableColumn<Item, String> authorCartCol;
    public TableColumn<Item, String> priceCartCol;
    public Button removeFromCartButton;
    public Button checkoutButton;
    public TextField totalPrice;
    public Button logOutButton;
    User currUsr;
    Stage mainWindow;

    @Override
    public void setUser(User user, Stage mainWindow) {
        currUsr = user;
        this.mainWindow = mainWindow;
        cartTable.getItems().addAll(currUsr.retriveCart());
        refreshInfo();
    }

    @FXML
    void initialize() {
        searchResultsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        isbnCartCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleCartCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCartCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceCartCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        searchButton.disableProperty().bind(bookSearch.textProperty().isEmpty());
        addToCartButton.disableProperty().bind(searchResultsTable.getSelectionModel().selectedItemProperty().isNull());
        removeFromCartButton.disableProperty().bind(cartTable.getSelectionModel().selectedItemProperty().isNull());
        checkoutButton.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(cartTable.getItems());
            }

            @Override
            protected boolean computeValue() {
                return cartTable.getItems() == null || cartTable.getItems().isEmpty();
            }
        });
        totalPrice.textProperty().bind(new StringBinding() {
            {
                super.bind(cartTable.getItems());
            }

            @Override
            protected String computeValue() {
                double sum = 0;
                for (Item b :
                        cartTable.getItems()) {
                    sum += b.getPrice();
                }
                return Double.toString(sum);
            }
        });

    }

    public void refreshInfo() {
        Map<String, String> user = currUsr.retriveUserInfo();
        username.setText(user.get("username"));
        password.setText(user.get("password"));
        firstName.setText(user.get("firstName"));
        lastName.setText(user.get("lastName"));
        email.setText(user.get("email"));
        phone.setText(user.get("phone"));
        shippingAddress.setText(user.get("shippAddress"));
        type.setText(user.get("type"));

    }

    public void updateInfo() {
        if (currUsr.update_info(password.getText(), lastName.getText(), firstName.getText(), email.getText(), phone.getText(), shippingAddress.getText(), type.getText())) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Info Updated");
            a.setHeaderText("Successful");
            a.setContentText("Your data has been updated!");
            a.show();
            refreshInfo();
        }
    }

    public void searchBooks() {
        List<Book> books = currUsr.searchBook(searchMethod.getValue(), bookSearch.getText());
        searchResultsTable.getItems().clear();
        searchResultsTable.getItems().addAll(books);
    }

    public void clearSearchResults() {
        searchResultsTable.getItems().clear();
        bookSearch.setText("");
    }

    public void addToCart() {
        Book b = searchResultsTable.getSelectionModel().getSelectedItem();
        int id = currUsr.addToCart(b);
        cartTable.getItems().add(new Item(id, b.getIsbn(), b.getTitle(), b.getAuthor(), b.getPrice()));
    }

    public void removeFromCart() {
        Item i = cartTable.getSelectionModel().getSelectedItem();
        if (currUsr.removeFromCart(i.getId())) {
            cartTable.getItems().remove(i);
        }
    }

    public void checkout() {
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        Label cardNoL = new Label("Credit Card No.");
        Label expL = new Label("Expiry date (MM-YYYY)");
        TextField cardNo = new TextField();
        TextField exp = new TextField();
        Button out = new Button("Checkout");
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setAlignment(Pos.CENTER);
        grid.add(cardNoL, 1, 1);
        grid.add(expL, 1, 2);
        grid.add(cardNo, 2, 1);
        grid.add(exp, 2, 2);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.add(out, 1, 3, 2, 1);
        out.disableProperty().bind(Bindings.or(exp.textProperty().isEmpty(), cardNo.textProperty().isEmpty()));
        out.setOnAction(event -> {
            stage.close();
            boolean isValid = true;
            if (cardNo.getText().length() == 14) {
                for (int i = 0; i < cardNo.getText().length(); i++) {
                    isValid = isValid && Character.isDigit(cardNo.getText().charAt(i));
                }
            } else {
                isValid = false;
            }
            if (isValid) {
                if (exp.getText().length() == 7) {
                    isValid = (exp.getText().charAt(2) == '-') && (Character.isDigit(exp.getText().charAt(0))) && (Character.isDigit(exp.getText().charAt(1))) && (Character.isDigit(exp.getText().charAt(3))) && (Character.isDigit(exp.getText().charAt(4))) && (Character.isDigit(exp.getText().charAt(5))) && (Character.isDigit(exp.getText().charAt(6)));
                } else {
                    isValid = false;
                }
            }
            if (isValid) {
                int month = Integer.parseInt(exp.getText().substring(0, 2));
                int year = Integer.parseInt(exp.getText().substring(3, 7));
                isValid = month >= 1 && month <= 12 && year >= 2021 && year < 2050;
            }
            if (isValid) {
                currUsr.checkOut();
                cartTable.getItems().clear();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Checked out Successfully");
                a.setHeaderText("Checked out Successfully!");
                a.setContentText("Your order is on its way!!");
                a.showAndWait();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Can't check out!");
                a.setContentText("Please review the entered info!!");
                a.showAndWait();

            }
        });
        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }

    public void logOut() {
        ((Stage) username.getScene().getWindow()).close();
        mainWindow.show();
        currUsr.emptyCart();
        File myObj = new File("UserInfo.txt");
        myObj.delete();
    }
}
