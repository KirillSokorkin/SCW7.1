import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
//import java.util.logging.Logger;
import java.util.stream.Stream;

public class SCommand {
    ObjectOutputStream toClient;
    ObjectInputStream fromClient;
    Logger logger;
    private HashMap<String, String> CommandHelpList = new HashMap<String, String>();

    public SCommand(ObjectOutputStream toClient, ObjectInputStream fromClient, Logger logger){
        this.toClient = toClient;
        this.fromClient = fromClient;
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

        this.logger = logger;
    }

    public void show(TreeSet<LabWork> collection) throws IOException {
        Response resp = new Response("");
        collection.forEach(o -> resp.addText(o.toString()));

        this.toClient.writeObject(resp);
        logger.info("Sent response to show command");
    }

    public void info(TreeSet<LabWork> collection, String collection_creation_date) throws IOException {
        this.toClient.writeObject(new Response(">Тип коллекции: " + collection.getClass() + '\n'+
                ">Дата создания: " + collection_creation_date +'\n'+
                ">Количество элементов: " +collection.size()+'\n'));
        logger.info("Sent response to info command");
    }

    public void add(TreeSet<LabWork> collection) throws IOException, ClassNotFoundException {
        LabWorkWrap wrap = (LabWorkWrap) fromClient.readObject();
        LabWork temp = new LabWork(wrap.getName(),
                wrap.getCoordinates(),
                wrap.getMinimalPoint(),
                wrap.getPersonalQualitiesMinimum(),
                wrap.getDescription(),
                wrap.getDifficulty());

        collection.add(temp);
        logger.info("Added new element to collection");
        toClient.writeObject(new Response(">Работа успешно добавлена в коллекцию"));
        logger.info("Sent response to add command");
    }

    public void update_by_id(TreeSet<LabWork> collection, long id) throws IOException, ClassNotFoundException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            toClient.writeObject(new Response("vse ok"));
            LabWorkWrap wrap = (LabWorkWrap) fromClient.readObject();
            collection.stream().filter(o -> o.getId()==id).findFirst().get().replace(wrap.getName(),
                    wrap.getCoordinates(),
                    wrap.getMinimalPoint(),
                    wrap.getPersonalQualitiesMinimum(),
                    wrap.getDescription(),
                    wrap.getDifficulty());

            System.out.println(collection.stream().filter(o -> o.getId()==id).findFirst().get().toString());

            toClient.writeObject(new Response(">Элемент по id "+ id + " успешно обновлен"));
            logger.info("Element with id " + id + " updated successfully");
        }
        else  {
            toClient.writeObject(new Response("vse ne ok"));
            logger.info("Attempt to change element by id failed");
        }

    }

    public void remove_by_id(long id, TreeSet<LabWork> collection) throws IOException {

        Stream stream = collection.stream().filter(o -> o.getId()==id);
        if (stream.count()==1) {
            collection.remove(collection.stream().filter(o -> o.getId()==id).findFirst().get());
            toClient.writeObject(new Response(">Элемент с заданным id успешно удален"));
            logger.info("Element with id "+ + id + " removed successfully");
        }
        else {
            toClient.writeObject(new Response(">Элемента с таким id не существует"));
            logger.info("Attempt to remove element by id failed");
        }
    }

    public void clear(TreeSet<LabWork> collection) throws IOException {
        collection.clear();
        toClient.writeObject(new Response(">Коллекция успешно очищена"));
        logger.info("Collection was cleared");

    }

    public void add_if_min(TreeSet<LabWork> collection) throws IOException, ClassNotFoundException {
        LabWorkWrap wrap = (LabWorkWrap) fromClient.readObject();
        LabWork temp = new LabWork(wrap.getName(),
                wrap.getCoordinates(),
                wrap.getMinimalPoint(),
                wrap.getPersonalQualitiesMinimum(),
                wrap.getDescription(),
                wrap.getDifficulty());


        if (collection.stream().filter(o -> o.compareTo(temp)<0).count()==0)
        {
            collection.add(temp);
            toClient.writeObject(new Response(">Элемент минимален \n" + temp.getName() +" - успешно добавлен в коллекцию"));
            logger.info("New element was successfully added");
        } else {
            toClient.writeObject(new Response(">Элемент " + temp.getName() + " не является минимальным - данный метод не может добавить его в коллекцию"));
            logger.info("Element wasn't added as it isn't minimal");
        }

    }

    public void remove_greater(TreeSet<LabWork> collection, String name) throws IOException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)>0).toArray())
                collection.remove((LabWork) o);
            toClient.writeObject(new Response(">Объекты, бОльшие чем " + name + " ,удалены"));
            logger.info("Organizations were deleted");
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
            logger.info("No organizations were deleted as there isn't any element with name " + name);
        }

    }

    public void remove_lower(TreeSet<LabWork> collection, String name) throws IOException {

        if (collection.stream().filter(o-> o.getName().toLowerCase().equals(name)).count()>0){
            for (Object o: collection.stream().filter(o -> o.getName().toLowerCase().compareTo(name)<0).toArray())
                collection.remove((LabWork) o);
            toClient.writeObject(new Response(">Объекты, меньшие чем " + name + " ,удалены"));
            logger.info("Organizations were deleted");
        }
        else{
            toClient.writeObject(new Response(">Не найдено организации с названием " + name));
            logger.info("No organizations were deleted as there isn't any element with name " + name);
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
        logger.info("A hand of help were given");
    }
}