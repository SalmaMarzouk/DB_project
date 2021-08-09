package model;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBOperator {


    private static final BasicDataSource src;

    private DBOperator() {

    }

    static {
        Properties DBConfig = new Properties();
        try {
            DBConfig.load(DBOperator.class.getResourceAsStream("DBConfig.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't find DB configuration file");
        }
        src = new BasicDataSource();
        src.setUrl(DBConfig.getProperty("url"));
        src.setUsername(DBConfig.getProperty("username"));
        src.setPassword(DBConfig.getProperty("password"));
    }

    public static BasicDataSource getOperator() {
        return src;
    }

    public static void closeResources(Connection c, CallableStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {

            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {

            }
        }
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {

            }
        }
    }
}



