package ServerSide;

import java.net.ServerSocket;
import java.sql.*;
import java.util.*;
import LabWork.*;

public class Server {

    public static final String DB_URL = "jdbc:postgresql://pg/studs";
    public static String login;
    public static String password;
    public static void main(String[] args) {

        System.out.println("Hello, введите логин для подключения к БД");
        Scanner in = new Scanner(System.in);
        login = in.nextLine();
        System.out.println("Введите пароль для подключения к БД");
        password = in.nextLine();
        TreeSet<LabWork> empty_set = new TreeSet<LabWork>();
        Set<LabWork> labs = Collections.synchronizedSet(empty_set);

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, login, password);
            labs = update_collection(connection);
            System.out.println("1");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC драйвер для СУБД не найден!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL!");
        }

        System.out.println("2");

        try {
            System.out.println("3");
            try (ServerSocket ss = new ServerSocket(2095)) {
                System.out.println("ServerSocket awaiting connections...");
                while (true)
                {
                    Worker w = new Worker(ss.accept(), connection, labs);
                    new Thread(w).start();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Set<LabWork> update_collection (Connection connection) throws SQLException{

        TreeSet<LabWork> labss = new TreeSet<LabWork>();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM SOKORKIN_LABS;" );
        while (rs.next()) {
            int id = rs.getInt("id");
            String  name = rs.getString("name");
            String coords = rs.getString("coordinates");
            Difficulty type = null;
            if (rs.getString("difficulty")!=null)
                type= Difficulty.valueOf(rs.getString("difficulty"));
            long    minimalPoint = rs.getLong("minimalPoint");
            long personalQualitiesMinimum = rs.getLong("personalQualitiesMinimum");
            String  description = rs.getString("description");
            int USERID = rs.getInt("userid");
            LabWork temp = new LabWork(id,name,coords,minimalPoint,personalQualitiesMinimum,description,type,USERID);
            labss.add(temp);
            System.out.println(temp.getName() + " добавлена");
        }
        System.out.println("В локальную коллекцию успешно добавлены " + labss.size() + " лабораторных работ");
        rs.close();
        stmt.close();
        return labss;
    }
}