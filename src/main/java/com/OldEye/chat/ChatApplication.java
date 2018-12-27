package com.OldEye.chat;

import com.OldEye.database.SQLDatabaseHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class ChatApplication {
    public static SQLDatabaseHolder databaseHolder;

    //вход в программу, подключение к базе данных и запуск спринг
    public static void main(String[] args) {
        try {
            databaseHolder = new SQLDatabaseHolder("jdbc:sqlserver://", "localhost", "1433", "chat", true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        SpringApplication.run(ChatApplication.class, args);
    }
}

