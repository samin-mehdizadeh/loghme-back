package ie.repository;
import ie.domain.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
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
        String hashPass = Base64.getEncoder().encodeToString(newClient.getPassword().getBytes());
        pStatement.setString(7,hashPass);
        pStatement.addBatch();
        pStatement.executeBatch();
        pStatement.close();
        userSearchStatement.close();
        restaurantResult.close();
        connection.commit();
        connection.close();
        return 1;
    }

    public int getCredit(String username){
        int credit=0;
        try{
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet Result = stmt.executeQuery("select credit from User WHERE username = \"" + username + "\" ");
            while (Result.next()){
                credit=Result.getInt("credit");
            }
            stmt.close();
            Result.close();
            connection.close();


        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return credit;
    }

    public void addCredit(String username, int credit) {
        int c=0;
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            Statement updateQuery = connection.createStatement();
            updateQuery.executeUpdate("UPDATE User SET credit = credit+"+credit+" WHERE username = \"" + username + "\"");
            updateQuery.close();
            connection.close();
        }
        catch (SQLException e){
            System.out.println(e);
        }

    }

    public Client selectUser(String username, String password,int firstTime) throws SQLException {
        Client loggedInClient = new Client();
        Connection connection = ConnectionPool.getInstance().getConnection();
        PreparedStatement userSearchStatement;
        ResultSet rs = null;
        if(firstTime==1){
            String query = "select * from User where User.username = ? and User.password = ? ";
            userSearchStatement = connection.prepareStatement(query);
            userSearchStatement.setString(1,username);
            String hashPass = Base64.getEncoder().encodeToString(password.getBytes());
            userSearchStatement.setString(2,hashPass);
            rs = userSearchStatement.executeQuery();
        }
        else{
            String query = "select * from User where User.username = ? ";
            userSearchStatement = connection.prepareStatement(query);
            userSearchStatement.setString(1,username);
            rs = userSearchStatement.executeQuery();
        }

        boolean empty = true;
        while( rs.next() ) {
            loggedInClient.setCredit(rs.getInt("credit"));
            loggedInClient.setUsername(rs.getString("username"));
            loggedInClient.setName(rs.getString("name"));
            loggedInClient.setLastName(rs.getString("lastName"));
            loggedInClient.setEmailAddress(rs.getString("emailAddress"));
            loggedInClient.setPhoneNumber(rs.getString("phoneNumber"));
            loggedInClient.setPassword(rs.getString("password"));
            empty = false;
        }

        if( empty ) {
            userSearchStatement.close();
            rs.close();
            connection.close();
            return null;
        }

        userSearchStatement.close();
        rs.close();
        connection.close();
        return loggedInClient;
    }

    public void deleteUserBasket(String username){
        try {
            Connection connection = ConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);
            Statement remove = connection.createStatement();
            remove.addBatch("DELETE from CurrentOrdinary where username =\"" + username+"\"");
            remove.addBatch("DELETE from CurrentDiscount where username =\"" + username+"\"");
            remove.executeBatch();
            connection.commit();
            remove.close();
            connection.close();
        } catch (SQLException e) {
                System.out.println(e.getMessage());
        }

    }

    public Client selectUserByEmail(String email)throws SQLException {
        Client loggedInClient = new Client();
        Connection connection = ConnectionPool.getInstance().getConnection();
        PreparedStatement userSearchStatement = connection.prepareStatement("select * from User where User.emailAddress = ?");
        userSearchStatement.setString(1,email);

        ResultSet rs = userSearchStatement.executeQuery();


        boolean empty = true;
        while( rs.next() ) {
            loggedInClient.setCredit(rs.getInt("credit"));
            loggedInClient.setUsername(rs.getString("username"));
            loggedInClient.setName(rs.getString("name"));
            loggedInClient.setLastName(rs.getString("lastName"));
            loggedInClient.setEmailAddress(rs.getString("emailAddress"));
            loggedInClient.setPhoneNumber(rs.getString("phoneNumber"));
            loggedInClient.setPassword(rs.getString("password"));
            empty = false;
        }

        if( empty ) {
            userSearchStatement.close();
            rs.close();
            connection.close();
            return null;
        }

        userSearchStatement.close();
        rs.close();
        connection.close();
        return loggedInClient;
    }


}
