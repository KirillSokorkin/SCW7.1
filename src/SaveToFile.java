import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.TreeSet;

public class SaveToFile {
    public static void saveToFile(TreeSet<LabWork> collection, File file) throws FileNotFoundException {

        JSONArray laba = new JSONArray();

        for (LabWork a: collection) {
            JSONObject one = new JSONObject();

            one.put("id", a.getId());
            one.put("name", a.getName());
            JSONObject coords = new JSONObject();
            coords.put("x",a.getCoordinates().getX());
            coords.put("y",a.getCoordinates().getY());
            one.put("coordinates", coords);
            one.put("annualTurnover", a.getMinimalPoint());
            one.put("employeesCount", a.getPersonalQualitiesMinimum());
            one.put("писани", a.getDescription());

            if (a.getDifficulty()==null)
                one.put("type", JSONObject.NULL);
            else
                one.put("type", a.getDifficulty());

            laba.put(one);
        }

        PrintWriter writer = new PrintWriter(file);
        writer.write(laba.toString());
        writer.close();
        System.out.println("Коллекция успешно сохранена в файл " + file);
    }
}