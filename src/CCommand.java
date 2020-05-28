import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class CCommand {
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;
    public CCommand(ObjectInputStream fromServer, ObjectOutputStream toServer){
        this.toServer = toServer;
        this.fromServer = fromServer;
    }

    public void show() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("show", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void help(String command) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command(command, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void info() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("info", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void add() throws IOException, ClassNotFoundException {

        Scanner in = new Scanner(System.in);
        System.out.println(">Введите название LabWork:");
        String name = in.nextLine();
        while (true) {
            System.out.println(name);
            if (!name.isEmpty()) break;
            System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
            name = in.nextLine();
        }

        System.out.println(">Введите координаты LabWork в целых числах через пробел");
        double x;
        float y;
        String[] coords;
        String coordinates;
        while (true) {
            try {
                coordinates = in.nextLine();
                coords = coordinates.split(" ", 2);
                x = Double.parseDouble(coords[0]);
                y = Float.parseFloat(coords[1]);
                break;
            } catch (Exception e) {
                System.out.println(">Что-то пошло не так. Повторите ввод координат");
            }
        }

        Coordinates newcoords = new Coordinates(x, y);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(formatter);

        System.out.println(">Введите Минимальный Балл за LabWork(целое число от 0 до 10) : ");
        long minimal_point;
        while (true) {
            try {
                minimal_point = Long.parseLong(in.nextLine());
                if (minimal_point <= 0 || minimal_point >= 10) minimal_point = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Неверное значение. Попробуйте еще раз");
            }
        }

        System.out.println(">Введите Минимальный балл Личностных качеств для LabWork (целое число от 0 до 10): ");
        long personalQualitiesMinimum;

        while (true) {
            try {
                personalQualitiesMinimum = Integer.parseInt(in.nextLine());
                if (personalQualitiesMinimum <= 0 || personalQualitiesMinimum >= 10) personalQualitiesMinimum = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то не так. Вводите число согласно указаниям");
            }
        }
        System.out.println("Введите Описание для LabWork");
        String description = in.nextLine();
        while (true) {
            if (description.isEmpty()) {
                System.out.println("Описание не может быть пустым. Введите ещё раз^");
                description = in.nextLine();
            } else break;
        }

        System.out.println(">Введите Сложность для LabWork(NORMAL,HARD,INSANE,HOPELESS,TERRIBLE");

        Difficulty difficulty = Difficulty.NORMAL;
        while (true) {
            try {
                String buffer_string = in.nextLine().toUpperCase();
                if (buffer_string.isEmpty()) break;
                difficulty = Difficulty.valueOf(buffer_string);
                break;
            } catch (Exception e) {
                System.out.println(">Такой сложности нет. Попробуйте снова");
            }
        }
        System.out.println("Введите Название Предмета для LabWork");
        String str_discipline = in.nextLine();
        while (true) {
            if (str_discipline.isEmpty()) {
                System.out.println("Название Предмета не может быть пустым. Введите ещё раз.");
                str_discipline = in.nextLine();
            }
            else break;
        }

        LabWorkWrap lab = new LabWorkWrap(name, newcoords, minimal_point, personalQualitiesMinimum, description, difficulty);

        toServer.writeObject(new Command("add", true));
        toServer.writeObject(lab);
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void update_by_id(long id) throws IOException, ClassNotFoundException {

        toServer.writeObject(new Command("update_by_id " + id, true));

        if ((fromServer.readObject()).toString().equals("vse ok")) {

            Scanner in = new Scanner(System.in);
            System.out.println(">Введите название LabWork:");
            String name = in.nextLine();
            while (true) {
                System.out.println(name);
                if (!name.isEmpty()) break;
                System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
                name = in.nextLine();
            }

            System.out.println(">Введите координаты LabWork в целых числах через пробел");
            double x;
            float y;
            String[] coords;
            String coordinates;
            while (true) {
                try {
                    coordinates = in.nextLine();
                    coords = coordinates.split(" ", 2);
                    x = Double.parseDouble(coords[0]);
                    y = Float.parseFloat(coords[1]);
                    break;
                } catch (Exception e) {
                    System.out.println(">Что-то пошло не так. Повторите ввод координат");
                }
            }

            Coordinates newcoords = new Coordinates(x, y);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = now.format(formatter);

            System.out.println(">Введите Минимальный Балл за LabWork(целое число от 0 до 10) : ");
            long minimal_point;
            while (true) {
                try {
                    minimal_point = Long.parseLong(in.nextLine());
                    if (minimal_point <= 0 || minimal_point >= 10) minimal_point = 1 / 0;
                    break;
                } catch (Exception e) {
                    System.out.println(">Неверное значение. Попробуйте еще раз");
                }
            }

            System.out.println(">Введите Минимальный балл Личностных качеств для LabWork (целое число от 0 до 10): ");
            long personalQualitiesMinimum;

            while (true) {
                try {
                    personalQualitiesMinimum = Integer.parseInt(in.nextLine());
                    if (personalQualitiesMinimum <= 0 || personalQualitiesMinimum >= 10)
                        personalQualitiesMinimum = 1 / 0;
                    break;
                } catch (Exception e) {
                    System.out.println(">Что-то не так. Вводите число согласно указаниям");
                }
            }
            System.out.println("Введите Описание для LabWork");
            String description = in.nextLine();
            while (true) {
                if (description.isEmpty()) {
                    System.out.println("Описание не может быть пустым. Введите ещё раз^");
                    description = in.nextLine();
                } else break;
            }

            System.out.println(">Введите Сложность для LabWork(NORMAL,HARD,INSANE,HOPELESS,TERRIBLE");

            Difficulty difficulty = Difficulty.NORMAL;
            while (true) {
                try {
                    String buffer_string = in.nextLine().toUpperCase();
                    if (buffer_string.isEmpty()) break;
                    difficulty = Difficulty.valueOf(buffer_string);
                    break;
                } catch (Exception e) {
                    System.out.println(">Такой сложности нет. Попробуйте снова");
                }
            }
            System.out.println("Введите Название Предмета для LabWork");
            String str_discipline = in.nextLine();
            while (true) {
                if (str_discipline.isEmpty()) {
                    System.out.println("Название Предмета не может быть пустым. Введите ещё раз.");
                    str_discipline = in.nextLine();
                } else break;
            }
        }
    }

    public void remove_by_id(Long id) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("remove_by_id "+ id, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void clear() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("clear", false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void add_if_min() throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command("add_if_min", true));

        Scanner in = new Scanner(System.in);
        System.out.println(">Введите название LabWork:");
        String name = in.nextLine();
        while (true) {
            System.out.println(name);
            if (!name.isEmpty()) break;
            System.out.println(">Имя не может быть пустой строкой. Введите имя ещё раз");
            name = in.nextLine();
        }

        System.out.println(">Введите координаты LabWork в целых числах через пробел");
        double x;
        float y;
        String[] coords;
        String coordinates;
        while (true) {
            try {
                coordinates = in.nextLine();
                coords = coordinates.split(" ", 2);
                x = Double.parseDouble(coords[0]);
                y = Float.parseFloat(coords[1]);
                break;
            } catch (Exception e) {
                System.out.println(">Что-то пошло не так. Повторите ввод координат");
            }
        }

        Coordinates newcoords = new Coordinates(x, y);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(formatter);

        System.out.println(">Введите Минимальный Балл за LabWork(целое число от 0 до 10) : ");
        long minimal_point;
        while (true) {
            try {
                minimal_point = Long.parseLong(in.nextLine());
                if (minimal_point <= 0 || minimal_point >= 10) minimal_point = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Неверное значение. Попробуйте еще раз");
            }
        }

        System.out.println(">Введите Минимальный балл Личностных качеств для LabWork (целое число от 0 до 10): ");
        long personalQualitiesMinimum;

        while (true) {
            try {
                personalQualitiesMinimum = Integer.parseInt(in.nextLine());
                if (personalQualitiesMinimum <= 0 || personalQualitiesMinimum >= 10) personalQualitiesMinimum = 1 / 0;
                break;
            } catch (Exception e) {
                System.out.println(">Что-то не так. Вводите число согласно указаниям");
            }
        }
        System.out.println("Введите Описание для LabWork");
        String description = in.nextLine();
        while (true) {
            if (description.isEmpty()) {
                System.out.println("Описание не может быть пустым. Введите ещё раз^");
                description = in.nextLine();
            } else break;
        }

        System.out.println(">Введите Сложность для LabWork(NORMAL,HARD,INSANE,HOPELESS,TERRIBLE");

        Difficulty difficulty = Difficulty.NORMAL;
        while (true) {
            try {
                String buffer_string = in.nextLine().toUpperCase();
                if (buffer_string.isEmpty()) break;
                difficulty = Difficulty.valueOf(buffer_string);
                break;
            } catch (Exception e) {
                System.out.println(">Такой сложности нет. Попробуйте снова");
            }
        }
        System.out.println("Введите Название Предмета для LabWork");
        String str_discipline = in.nextLine();
        while (true) {
            if (str_discipline.isEmpty()) {
                System.out.println("Название Предмета не может быть пустым. Введите ещё раз.");
                str_discipline = in.nextLine();
            }
            else break;
        }

        LabWorkWrap lab = new LabWorkWrap(name, newcoords, minimal_point, personalQualitiesMinimum, description, difficulty);

        toServer.writeObject(lab);
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void remove_greater(String name) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command ("remove_greater " + name, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }

    public void remove_lower(String name) throws IOException, ClassNotFoundException {
        toServer.writeObject(new Command ("remove_lower " + name, false));
        System.out.println(((Response) fromServer.readObject()).content);
    }
}