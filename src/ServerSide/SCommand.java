package ServerSide;

import com.sun.org.apache.bcel.internal.generic.LADD;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;
import LabWork.*;
import Other.*;
import Other.*;

import java.util.stream.Stream;

public class SCommand {
    ObjectOutputStream toClient;
    ObjectInputStream fromClient;
    Logger logger;
    Connection connection;
    long userid;

    private HashMap<String, String> CommandHelpList = new HashMap<String, String>();


    public SCommand(ObjectOutputStream toClient, ObjectInputStream fromClient, Connection connection, long userid) throws SQLException {
        this.toClient = toClient;
        this.fromClient = fromClient;
        this.connection = connection;
        this.userid = userid;
        System.out.println("USERID5: " + userid);
        connection.setAutoCommit(false);
        CommandHelpList.put("help", "Команда help выведет справку по доступным командам.");
        CommandHelpList.put("info", "Команда info выведет информацию о коллекции.");
        CommandHelpList.put("show", "Команда show выведет все элементы коллекции.");
        CommandHelpList.put("add", "Команда add добавит новый элемент, созданный по указанным параметрам, в коллекцию.");
        CommandHelpList.put("update_by_id", "Команда update id обновит значение элемента коллекции, id которого равен заданному.");
        CommandHelpList.put("remove_by_id", "Команда remove_by_id удалит из коллекции элемент с указанным id.");
        CommandHelpList.put("clear", "Команда clear очистит коллекцию.");
        CommandHelpList.put("execute_script", "Команда execute_script cчитает и исполнит скрипт из указанного файла.");
        CommandHelpList.put("exit","Команда exit завершит работу программы без сохранения файла.");
        CommandHelpList.put("add_if_min", "Команда add_if_min добавит новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента коллекции.");
        CommandHelpList.put("remove_greater", "Команда remove_greater удалит из коллекции все элементы, превышающие заданный.");
        CommandHelpList.put("remove_lower","Команда remove_lower удалит из коллекции все элементы, превышающие заданный.");

        //this.logger = logger;
    }

    public void show(Set<LabWork> collection) throws IOException {
        Response resp = new Response("");
        collection.forEach(o -> resp.addText(o.toString()));

        this.toClient.writeObject(resp);
       // logger.info("Sent response to show command");
    }

    public void info(Set<LabWork> collection) throws IOException {
        this.toClient.writeObject(new Response(">Тип коллекции: " + collection.getClass() + '\n'+
                ">Количество элементов: " +collection.size()+'\n'));
       // logger.info("Sent response to info command");
    }

    public void add(Set<LabWork> collection) throws IOException, ClassNotFoundException, SQLException {
        LabWorkWrap wrap = (LabWorkWrap)fromClient.readObject();


        String sql = "INSERT INTO SOKORKIN_LABS " + "(id,name,coordinates,minimalPoint,personalQualitiesMinimum,description,difficulty,USERID)"+
                "VALUES (NEXTVAL('LABS_SOKORKIN_SEQUENCE'), '" + wrap.getName() + "', '"  + wrap.getCoordinates() + "'," + wrap.getMinimalPoint() +
                ", " + wrap.getPersonalQualitiesMinimum()+ ", '" + wrap.getDescription() + "', '" + wrap.getDifficulty().toString()+"', "+userid +");";
        Statement strm = this.connection.createStatement();
        strm.execute(sql);
        connection.commit();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM SOKORKIN_LABS WHERE ID=CURRVAL('LABS_SOKORKIN_SEQUENCE')" );
        while ( rs.next() ) {
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
            collection.add(temp);
            System.out.println(temp.getName() + " добавлена");
        }

       // logger.info("Added new element to collection");
        toClient.writeObject(new Response(">ЛабораторнаяРабота успешно добавлена в коллекцию"));
       // logger.info("Sent response to add command");
    }

    public void update_by_id(Set<LabWork> collection, long id) throws IOException, ClassNotFoundException, SQLException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            if (collection.stream().filter(o -> o.getId()==id).findFirst().get().getUSERID()!=this.userid) {
                toClient.writeObject(new Response("vse ne ok"));
             //   logger.info("Attempt to change element by id failed");
                return;
            }
            toClient.writeObject(new Response("vse ok"));
            LabWorkWrap wrap = (LabWorkWrap) fromClient.readObject();
            collection.stream().filter(o -> o.getId()==id).findFirst().get().replace(wrap.getName(),
                    wrap.getCoordinates(),
                    wrap.getMinimalPoint(),
                    wrap.getPersonalQualitiesMinimum(),
                    wrap.getDescription(),
                    wrap.getDifficulty());

            String sql =
                    "UPDATE SOKORKIN_LABS SET" + "(id,name,coordinates,minimalPoint,personalQualitiesMinimum,description,difficulty,USERID)"+
                            "= ('" + wrap.getName() + "', '"  +wrap.getCoordinates() + "', " + wrap.getMinimalPoint() +
                            ", " + wrap.getPersonalQualitiesMinimum()+ ", '" + wrap.getDescription() + "', '" +wrap.getDifficulty().toString()+"', "+userid +") " +
                            "WHERE ID="+ id +";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            toClient.writeObject(new Response(">Элемент по id "+ id + " успешно обновлен"));
          //  logger.info("Element with id " + id + " updated successfully");
        }
        else  {
            toClient.writeObject(new Response("vse ne ok"));
           // logger.info("Attempt to change element by id failed");
        }

    }

    public void remove_by_id(long id, Set<LabWork> collection) throws IOException, SQLException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            if (collection.stream().filter(o -> o.getId()==id).findFirst().get().getUSERID()!=this.userid) {
                toClient.writeObject(new Response(">Нет доступа к элементу с таким id"));
               // logger.info("Attempt to remove element by id failed");
                return;
            }
            collection.remove(collection.stream().filter(o -> o.getId()==id).findFirst().get());

            String sql = "DELETE FROM SOKORKIN_LABS WHERE ID="+ id+";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            toClient.writeObject(new Response(">Элемент с заданным id успешно удален"));
           // logger.info("Element with id "+ + id + " removed successfully");
        }
        else {
            toClient.writeObject(new Response(">Элемента с таким id не существует"));
           // logger.info("Attempt to remove element by id failed");
        }
    }

    public void clear(Set<LabWork> collection) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getUSERID()==userid).count()>0) {
            for (Object o : collection.stream().filter(o-> o.getUSERID()==userid).toArray())
                collection.remove((LabWork) o);
            String sql = "DELETE FROM SOKORKIN_LABS WHERE USERID="+ userid+";";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();
            toClient.writeObject(new Response(">Коллекция успешно очищена"));
           // logger.info("Collection was cleared");
        }
        else
        {
            toClient.writeObject(new Response(">У вас нет элементов в этой коллекции"));
           // logger.info("Collection wasn't cleared");
        }

    }

    public void add_if_min(Set<LabWork> collection) throws IOException, ClassNotFoundException, SQLException {
        LabWorkWrap wrap = (LabWorkWrap) fromClient.readObject();
        LabWork temp = new LabWork(-1,wrap.getName(),
                wrap.getCoordinates(),
                wrap.getMinimalPoint(),
                wrap.getPersonalQualitiesMinimum(),
                wrap.getDescription(),
                wrap.getDifficulty(),(int)this.userid);

        if (collection.stream().filter(o -> o.compareTo(temp)<0).count()==0)
        {
            String sql = "INSERT INTO SOKORKIN_LABS " + "(id,name,coordinates,minimalPoint,personalQualitiesMinimum,description,difficulty,USERID)"+
                    "VALUES (NEXTVAL('LABS_SOKORKIN_sequence'), '" + wrap.getName() + "', '" + wrap.getCoordinates() + "'," + wrap.getMinimalPoint() +
                    ", " + wrap.getPersonalQualitiesMinimum()+ ", '" + wrap.getDescription() + "', '" +wrap.getDifficulty().toString()+"', "+userid +");";
            Statement strm = this.connection.createStatement();
            strm.execute(sql);
            connection.commit();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM SOKORKIN_LABS WHERE ID=CURRVAL('LABS_SOKORKIN_sequence')" );
            while ( rs.next() ) {
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
                LabWork temp1 = new LabWork(id,name,coords,minimalPoint,personalQualitiesMinimum,description,type,USERID);
                collection.add(temp1);
                System.out.println(temp.getName() + " добавлена");
            }

            toClient.writeObject(new Response(">Элемент минимален \n" + temp.getName() +" - успешно добавлен в коллекцию"));
           // logger.info("New element was successfully added");
        } else {
            toClient.writeObject(new Response(">Элемент " + temp.getName() + " не является минимальным - данный метод не может добавить его в коллекцию"));
           // logger.info("Element wasn't added as it isn't minimal");
        }
    }

    public void remove_greater(Set<LabWork> collection, String name) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)>0 && o.getUSERID()==userid).toArray())
            {
                collection.remove((LabWork) o);
                String sql = "DELETE FROM SOKORKIN_LABS WHERE ID="+ ((LabWork) o).getId()+";";
                Statement strm = this.connection.createStatement();
                strm.execute(sql);
                connection.commit();
            }
            toClient.writeObject(new Response(">Объекты, бОльшие чем " + name + " ,удалены"));
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
        }

    }

    public void remove_lower(Set<LabWork> collection, String name) throws IOException, SQLException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name) && o.getUSERID()==userid).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)<0 && o.getUSERID()==userid).toArray())
            {
                collection.remove((LabWork) o);
                String sql = "DELETE FROM SOKORKIN_LABS WHERE ID="+ ((LabWork) o).getId()+";";
                Statement strm = this.connection.createStatement();
                strm.execute(sql);
                connection.commit();
            }
            toClient.writeObject(new Response(">Объекты, меньшие чем " + name + " ,удалены"));
          //  logger.info("Organizations were deleted");
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
        }

    }

    void help(String [] commandParts) throws IOException {
        if (commandParts.length==1)
        {
            toClient.writeObject(new Response(">Список доступных команд:\n"  + CommandHelpList.keySet()));
        }
        else
        {
            if (CommandHelpList.containsKey(commandParts[1])) {
                toClient.writeObject(new Response(CommandHelpList.get(commandParts[1])));
            }
            else
                toClient.writeObject(new Response(">Такая команда не найдена"));
        }
       // logger.info("A hand of help were given");
    }
}