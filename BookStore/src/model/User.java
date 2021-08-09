package model;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    final String username;
    final String type;

    private User(String username, String userType) {
        this.username = username;
        this.type = userType;
    }


    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    private static String authenticate(String userName, String userPassword) {
        String type = null;
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call Authenticate(?, ?, ?);");
            cStmt.setString(1, userName);
            cStmt.setString(2, userPassword);
            cStmt.registerOutParameter(3, Types.VARCHAR);
            cStmt.execute();
            type = cStmt.getString(3);
            cStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOperator.closeResources(connection, null, null);
        return type;
    }

    private static String authenticateLogged(String userName) {
        String type = null;
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call get_type(?, ?);");
            cStmt.setString(1, userName);
            cStmt.registerOutParameter(2, Types.VARCHAR);
            cStmt.execute();
            type = cStmt.getString(2);
            cStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOperator.closeResources(connection, null, null);
        return type;
    }

    public static User getUser(String username, String userPassword) {
        String type = authenticate(username, userPassword);
        if (type == null) {
            return null;
        }
        return new User(username, type);
    }
    public static User getLoggedUser(String username) {
        String type = authenticateLogged(username);
        if (type == null) {
            return null;
        }
        return new User(username, type);
    }
    public List<Book> searchBook(String method, String key) {
        List<Book> books = new ArrayList<>();
        Connection c = null;
        CallableStatement st = null;
        String query = null;
        switch (method) {
            case "ISBN":
                query = "{call Search_by_isbn(?)}";
                break;
            case "Author":
                query = "{call Search_by_author(?)}";
                break;
            case "Publisher":
                query = "{call Search_by_publisher(?)}";
                break;
            case "Category":
                query = "{call Search_by_category(?)}";
                break;
        }

        try {
            c = DBOperator.getOperator().getConnection();
            st = c.prepareCall(query);
            st.setString(1, key);
            boolean hadResults = st.execute();
            while (hadResults) {
                ResultSet rs = st.getResultSet();
                while (rs.next()) {
                    books.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4)));
                }
                rs.close();
                hadResults = st.getMoreResults();
            }

        } catch (SQLException e) {
        }
        DBOperator.closeResources(c, st, null);
        return books;
    }

    public int addToCart(Book b) {
        int id = -1;
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call Insert_into_cart(?, ?, ?);");
            cStmt.setString(1, username);
            cStmt.setString(2, b.getIsbn());
            cStmt.registerOutParameter(3, Types.INTEGER);
            cStmt.execute();
            id = cStmt.getInt(3);
            cStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOperator.closeResources(connection, null, null);
        return id;
    }

    public boolean removeFromCart(int id) {
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call Remove_from_cart(?);");
            cStmt.setInt(1, id);
            cStmt.execute();
            cStmt.close();
        } catch (SQLException e) {
            DBOperator.closeResources(connection, null, null);
            return false;
        }
        return true;
    }

    public boolean emptyCart() {
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call Empty_cart(?);");
            cStmt.setString(1, username);
            cStmt.execute();
            cStmt.close();
        } catch (SQLException e) {
            DBOperator.closeResources(connection, null, null);
            return false;
        }
        return true;
    }

    public void decBook(String isbn) {
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call Dec_Book(?);");
            cStmt.setString(1, isbn);
            cStmt.execute();
            cStmt.close();
        } catch (SQLException e) {
            DBOperator.closeResources(connection, null, null);
        }
    }

    public boolean checkOut() {
        Connection c = null;
        CallableStatement st = null;
        try {
            c = DBOperator.getOperator().getConnection();
            st = c.prepareCall("{call retrive_cart(?)}");
            st.setString(1, username);
            boolean hadResults = st.execute();
            while (hadResults) {
                ResultSet rs = st.getResultSet();
                while (rs.next()) {
                    decBook(rs.getString(2));
                }
                rs.close();
                hadResults = st.getMoreResults();
            }

        } catch (SQLException e) {
            DBOperator.closeResources(c, st, null);

            return false;
        }
        DBOperator.closeResources(c, st, null);
        emptyCart();
        return true;
    }

    public List<Item> retriveCart() {
        List<Item> items = new ArrayList<>();
        Connection c = null;
        CallableStatement st = null;
        String query = "{CALL retrive_cart(?)}";
        try {
            c = DBOperator.getOperator().getConnection();
            st = c.prepareCall(query);
            st.setString(1, username);
            boolean hadResults = st.execute();
            while (hadResults) {
                ResultSet rs = st.getResultSet();
                while (rs.next()) {
                    items.add(new Item(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),rs.getDouble(5)));
                }
                rs.close();
                hadResults = st.getMoreResults();
            }

        } catch (SQLException e) {
        }
        DBOperator.closeResources(c, st, null);
        return items;
    }

    public Map<String,String> retriveUserInfo(){
        Map<String,String> user = new HashMap<>();
        Connection c = null;
        CallableStatement st = null;
        String query = "{CALL retrive_user_info(?)}";

        try {
            c = DBOperator.getOperator().getConnection();
            st = c.prepareCall(query);
            st.setString(1, username);
            boolean hadResults = st.execute();
            while (hadResults) {
                ResultSet rs = st.getResultSet();
                while (rs.next()) {
                    user.put("username",rs.getString(1));
                    user.put("password",rs.getString(2));
                    user.put("lastName", rs.getString(3));
                    user.put("firstName",rs.getString(4));
                    user.put("email",rs.getString(5));
                    user.put("phone",rs.getString(6));
                    user.put("shippAddress",rs.getString(7));
                    user.put("type",rs.getString(8));
                }
                rs.close();
                hadResults = st.getMoreResults();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOperator.closeResources(c, st, null);
        return user;

    }
    public boolean update_info(String password, String lastname, String firstname, String email, String phone, String shippAddress, String type) {
        Connection connection = null;
        try {
            connection = DBOperator.getOperator().getConnection();
            CallableStatement cStmt = connection.prepareCall("call update_user_info(?, ?, ?,?,?,?,?,?);");
            cStmt.setString(1, username);
            cStmt.setString(2, password);
            cStmt.setString(3, lastname);
            cStmt.setString(4, firstname);
            cStmt.setString(5, email);
            cStmt.setString(6, phone);
            cStmt.setString(7, shippAddress);
            cStmt.setString(8, type);
            cStmt.execute();
            cStmt.close();
        } catch (SQLException e) {
            DBOperator.closeResources(connection, null, null);
            return false;
        }
        DBOperator.closeResources(connection, null, null);
        return true;
    }


}
