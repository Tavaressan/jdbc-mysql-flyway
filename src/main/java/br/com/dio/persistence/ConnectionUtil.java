package br.com.dio.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionUtil {
    @Getter
    private static final String DB_URL =  "jdbc:mysql://localhost:3307/jdbc-sample";
    @Getter
    private static final String DB_USER =  "root";
    @Getter
    private static final String DB_PASSWORD =  "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    }
}
