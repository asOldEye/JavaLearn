package com.OldEye.database;

import javax.validation.constraints.NotNull;
import java.sql.*;

//выполняет операции над базой данных
public class SQLDatabaseHolder {
    private String url, serverName, portNumber, databaseName;
    private boolean integratedSecurity;
    private Connection connection;
    private Statement stmt;

    public SQLDatabaseHolder(String url, String serverName, String portNumber, String databaseName, boolean integratedSecurity)
            throws SQLException, ClassNotFoundException {
        this.url = url;
        this.serverName = serverName;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        this.integratedSecurity = integratedSecurity;
        //Подгружаем драйвер, если требуется
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        //Создаем подключение
        stmt = (connection = DriverManager.getConnection(connectionUrl())).createStatement();
    }

    public String connectionUrl() {
        return url + serverName + ":" + portNumber + ";databaseName=" + databaseName + ";integratedSecurity=" + integratedSecurity;
    }

    public String getUrl() {
        return url;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isIntegratedSecurity() {
        return integratedSecurity;
    }

    //выборка (не успеваю реализовать)
    public ResultSet select(@NotNull String select) throws SQLException {
        return stmt.executeQuery(select);
    }

    //заполнение
    public void insert(@NotNull String[] insert) throws SQLException {
        for (String sql : insert) stmt.execute(sql);
    }

    //закрывает соединение
    public void close() throws SQLException {
        stmt.close();
        connection.close();
    }
}