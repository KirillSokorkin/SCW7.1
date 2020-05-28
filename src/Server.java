import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
//import java.util.logging.LogManager;
//import java.util.logging.Logger;

public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        final Logger logger = LogManager.getLogger();

        String env_name = "LAB_PROG_ENV_NAME";
        if (System.getenv(env_name) == null)
            System.out.println("Переменная окружения не задана. Для считывания файла добавьте переменную среды " + env_name);
        else {

            String file_name = System.getenv(env_name);

            System.out.println("\nСчитано: " + env_name + "   " + file_name);

            File file = new File(file_name);

            TreeSet<LabWork> labs = readFromFile(file);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String collection_creation_date = now.format(formatter);

            logger.info("Got collection with "+ labs.size() + " elements");

            logger.info("We are running server");

            try {
                try (ServerSocket ss = new ServerSocket(2605)) {
                    System.out.println("Ожидание подключения...");
                    try (Socket socket = ss.accept()) {
                        System.out.println("Connection from " + socket + "!");
                        logger.info("Connection from " + socket + "!");
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                            objectOutputStream.writeObject("Соединение установлено");
                            ServerCommandReader reader = new ServerCommandReader(objectOutputStream, objectInputStream, logger);

                            logger.info("Start messages exchange");

                            reader.start_listening(labs, collection_creation_date, file);
//                            while (true) {
//
////                            Command cmessage = (Command) objectInputStream.readObject();
////                            System.out.println("Received [" + cmessage.description + "] from: " + socket);
////                            objectOutputStream.writeObject(new Response(cmessage.description));
//                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                logger.info("Connection were terminated");
                SaveToFile.saveToFile(labs, file);
                logger.info("Collection was saved in " + file.getPath());
            }
        }
    }
    public static TreeSet<LabWork> readFromFile(File file)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            String text = new String();

            while((line=reader.readLine())!=null)  text+=line+'\n';

            TreeSet<LabWork> labs = new TreeSet<LabWork>();

            JSONArray lab1s = new JSONArray(text);


            for (int i=0; i<lab1s.length(); i++)
            {
                JSONObject a = lab1s.getJSONObject(i);

                String name = a.getString("name");

                long id = a.getLong("id");

                JSONObject c = a.getJSONObject("coordinates");
                Coordinates coords = new Coordinates(c.getLong("x"), c.getInt("y"));

                long    minimalPoint = a.getLong("minimalPoint");

                long personalQualitiesMinimum = a.getLong("personalQualitiesMinimum");

                String  description = a.getString("description");


                Difficulty difficulty;
                if (a.isNull("diffivulty"))  difficulty = null;
                else difficulty = Difficulty.valueOf(a.getString("diffivulty"));

                LabWork laba = new LabWork(name,coords, minimalPoint,personalQualitiesMinimum,description,difficulty);

                labs.add(laba);
            }

            return labs;
        }
        catch (Exception e)
        {
            System.out.println("Беда с файлом. Создана пустая коллекция");
            System.out.println(e.getMessage());
            return new TreeSet<LabWork>();
        }
    }
}