package ie.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserMapper {
    List<Client> users;
    private static UserMapper instance;
    private UserMapper() {
    }

    public static UserMapper getInstance(){
        if(instance == null){
            instance = new UserMapper();
        }
        return instance;
    }



    public List<Client> getUsers() throws SQLException {
        users = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement usersStatement = connection.createStatement();
        ResultSet usersResult = usersStatement.executeQuery(
                "select * from User ");
        while (usersResult.next()) {
            Client client = new Client();
            client.setName(usersResult.getString("name"));
            client.setLastName(usersResult.getString("lastName"));
            client.setPhoneNumber(usersResult.getString("phoneNumber"));
            client.setCredit(usersResult.getInt("credit"));
            client.setUsername(usersResult.getString("username"));
            client.setEmailAddress(usersResult.getString("emailAddress"));
            client.setPassword(usersResult.getString("password"));
            users.add(client);
        }
        usersStatement.close();
        usersResult.close();
        connection.close();
        return users;
    }

    public int insertUser(Client newClient) throws SQLException {
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement userSearchStatement = connection.createStatement();
        ResultSet restaurantResult = userSearchStatement.executeQuery(
                "select * from User where User.username = \"" + newClient.getUsername() + "\""
                        + " or User.emailAddress = \"" + newClient.getEmailAddress() + "\"" +
                        " or User.phoneNumber = \"" + newClient.getPhoneNumber() + "\"");

        boolean empty = true;
        while( restaurantResult.next() ) {
            empty = false;
        }

        if( !empty ) {
            userSearchStatement.close();
            restaurantResult.close();
            connection.close();
            return 0;
        }

        connection.setAutoCommit(false);
        PreparedStatement pStatement = connection.prepareStatement(
                "insert ignore into User (username, name, lastName, phoneNumber, emailAddress, credit, password) values (?, ?, ?, ?, ?, ?, ?)");
        pStatement.setString(1, newClient.getUsername());
        pStatement.setString(2, newClient.getName());
        pStatement.setString(3, newClient.getLastName());
        pStatement.setString(4, newClient.getPhoneNumber());
        pStatement.setString(5, newClient.getEmailAddress());
        pStatement.setInt(6,newClient.getCredit());
        pStatement.setString(7,newClient.getPassword());
        pStatement.addBatch();
        pStatement.executeBatch();
        pStatement.close();
        userSearchStatement.close();
        restaurantResult.close();
        connection.commit();
        connection.close();
        return 1;
    }

    public void addCredit(Client client, int credit) throws SQLException {
        Connection connection = ConnectionPool.getInstance().getConnection();

        String sqlQuery =
                "update User " +
                        "set credit = ?" +
                        " where username = ?";
        PreparedStatement updateQuery  = connection.prepareStatement(sqlQuery);
        updateQuery.setInt(1,credit);
        updateQuery.setString(2,client.getUsername());
        int success = updateQuery.executeUpdate();
        updateQuery.close();
        connection.close();
        System.out.println(success);

    }

    public Client selectUser(String username, String password) throws SQLException {
        Client loggedInClient = new Client();
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement userSearchStatement = connection.createStatement();
        ResultSet restaurantResult = userSearchStatement.executeQuery(
                "select * from User where User.username = \"" + username + "\""
                        + " and USER.password = \"" + password + "\"");

        boolean empty = true;
        while( restaurantResult.next() ) {
            loggedInClient.setCredit(restaurantResult.getInt("credit"));
            loggedInClient.setUsername(restaurantResult.getString("username"));
            loggedInClient.setName(restaurantResult.getString("name"));
            loggedInClient.setLastName(restaurantResult.getString("lastName"));
            loggedInClient.setEmailAddress(restaurantResult.getString("emailAddress"));
            loggedInClient.setPhoneNumber(restaurantResult.getString("phoneNumber"));
            loggedInClient.setPassword(restaurantResult.getString("password"));
            empty = false;
        }

        if( empty ) {
            userSearchStatement.close();
            restaurantResult.close();
            connection.close();
            return null;
        }

        userSearchStatement.close();
        restaurantResult.close();
        connection.close();
        return loggedInClient;
    }


}
