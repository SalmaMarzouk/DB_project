package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Item {
    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty isbn;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleDoubleProperty price;


    public Item(int id, String isbn, String title, String author, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.isbn = new SimpleStringProperty(isbn);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getIsbn() {
        return isbn.get();
    }

    public SimpleStringProperty isbnProperty() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn.set(isbn);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getAuthor() {
        return author.get();
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(int price) {
        this.price.set(price);
    }
}
