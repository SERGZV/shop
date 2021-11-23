package ru.shop.three_d_print.dao;

import org.springframework.stereotype.Component;
import ru.shop.three_d_print.models.Account;

import java.sql.*;

@Component
public class AccountDAO
{
    private static int ACCOUNT_ID = 0;

    private static final String URL = "jdbc:postgresql://localhost:5432/three_d_shop_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "666999Qw";

    private static Connection connection;

    public AccountDAO()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try
        {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public boolean createAccount(Account account)
    {
        boolean success = true;

        try
        {
            String SQL = "INSERT INTO account(fname, mname, lname, age, sex, email, login, password) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setString(1, account.getFirstName());
            statement.setString(2, account.getMiddleName());
            statement.setString(3, account.getLastName());
            statement.setInt(4, account.getAge());
            statement.setString(5, account.getSex());
            statement.setString(6, account.getEmail());
            statement.setString(7, account.getLogin());
            statement.setString(8, account.getPassword());
            statement.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return success;
    }

    public Account getAccount(int id)
    {
        Account account = null;

        try
        {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM account";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next())
            {
                if(resultSet.getInt("id") == id)
                {
                    account = new Account
                    (
                        id,
                        resultSet.getString("fname"),
                        resultSet.getString("mname"),
                        resultSet.getString("lname"),
                        resultSet.getInt("age"),
                        resultSet.getString("sex"),
                        resultSet.getString("email"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                    );
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return account;
    }
}
