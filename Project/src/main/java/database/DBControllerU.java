package database;

import application.CurrentUser;
import application.Encryptor;
import entities.User;
import network.DBNetwork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class DBControllerU extends DBController {
    public static User loginCheck(String username, String password, Connection conn, int permission){
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME = '"+ username + "'" +
                    " AND PASSWORD = '"+ Encryptor.encrypt(password) +"'");
            if(ps.execute()) {
                ResultSet rs = ps.getResultSet();
                rs.next();
                User curr = new User(rs.getString("USERID"),rs.getString("USERNAME"),rs.getInt("PERMISSION"));
                if(curr.getPermissions() == permission){
                    return curr;
                }else {
                    return null;
                }
            }else{
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static User loginWithID(String ID, Connection conn){
        User u = null;
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * from USERS where WPIID = ?");
            ps.setString(1,ID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                u =  new User(rs.getString("USERID"),rs.getString("USERNAME"),rs.getInt("PERMISSION"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return u;
    }

    public static User getGuestUser(Connection conn){
        User guestUser;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * from USERS where PERMISSION = 1024");
            ResultSet rs = ps.executeQuery();
            rs.next();
            guestUser = new User(rs.getString("USERID"),rs.getString("USERNAME"),rs.getInt("PERMISSiON"));
        } catch (SQLException e) {
            e.printStackTrace();
            guestUser = null;
        }


        return guestUser;
    }

    public static LinkedList<User> getUser(Connection conn){
        LinkedList<User> listOfUsers = new LinkedList<User>();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT * from USERS");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                listOfUsers.add(new User(rs.getString("USERID"),rs.getString("USERNAME"),rs.getString("PASSWORD"),rs.getInt("PERMISSION")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfUsers;
    }

    /**
     * UpdateUser
     *
     *
     */
    public static void updateUser(String ID, User user, Connection conn){
        try {
            //System.out.println(user);
            if(!(ID == null  || ID == "")){
                PreparedStatement ps = conn.prepareStatement("UPDATE USERS " +
                        "SET USERID ='"+user.getUserID()+"'," +
                        " PERMISSION = "+ user.getPermissionsNumber() +"," +
                        " USERNAME = '"+ user.getUsername() +"', PASSWORD = " +
                        "'"+Encryptor.encrypt(user.getPassword())+"' where USERID = '"+ID +"'");
                ps.execute();
                CurrentUser.network.sendUserPacket(DBNetwork.UPDATE_USER, user);
            } else {
                addUser(user,conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * AddUser
     *
     *
     */
    public static void addUser(User user,Connection conn){
        try {
            //System.out.println(user);
            PreparedStatement s = conn.prepareStatement("insert into USERS (userid, permission, username, password) \n" +
                    "values ('"+ user.getUserID() +"',"+ user.getPermissionsNumber()+",'"+user.getUsername()+"','"+Encryptor.encrypt(user.getPassword())+"')");
            s.execute();
            CurrentUser.network.sendUserPacket(DBNetwork.ADD_USER, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public static void deleteUser(User user, Connection conn){
        try {
            PreparedStatement ps = conn.prepareStatement("Delete from USERS where USERID = ?");
            ps.setString(1, user.getUserID());
            ps.execute();
            CurrentUser.network.sendUserPacket(DBNetwork.DELETE_USER, user);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void teamID(String userID, String wpiID, Connection conn){
       try {
           PreparedStatement ps = conn.prepareStatement("update USERS set WPIID = ? where USERID = ?");
           ps.setString(1,wpiID);
           ps.setString(2,userID);
           ps.execute();
       }catch (SQLException e){
           e.printStackTrace();
       }
    }

    public static void loadTeam(Connection conn){
        addUser(new User("TM0001","jon","jon",4095),conn); // adding Jon
        addUser(new User("TM0002","joe","joe",4095),conn); // adding Joe
        addUser(new User("TM0003","ryan","ryan",4095),conn); // adding Ryan
        addUser(new User("TM0004","shiyi","shiyi",4095),conn); // adding Shiyi
        addUser(new User("TM0005","nicole","nicole",4095),conn); // adding Nicole
        addUser(new User("TM0006","dimitri","dimitri",4095),conn); // adding Dimitri
        addUser(new User("TM0007","rakesh","rakesh",4095),conn); // adding Rakesh
        addUser(new User("TM0008","henry","henry",4095),conn); // adding Henry
        addUser(new User("TM0009","p","p",4095),conn); // adding Panos
        addUser(new User("TM0010","isabel","isabel",4095),conn); // adding Isabel
    }

}
