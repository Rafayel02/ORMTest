package org.example.app;

import org.example.orm.Orm;
import java.util.Scanner;

public class App {

    private static Scanner scanner;
    private static Orm orm;

    public static void main(String[] args) throws Exception{
        Orm orm = new Orm("postgresql", "localhost", "5432", "test", "postgres", "qwerty");

        //Create table if not exists
        orm.register(User.class);

        scanner = new Scanner(System.in);
        LOOP: while(true) {
            System.out.print("Choose command (save/delete/getAll): ");
            String command = scanner.next();
            switch (command) {
                case "save" :{
                    User user = getUserFromConsole();
                    orm.save(user);
                    break;
                }
                case "delete" :{
                    System.out.print("Input userName to delete: ");
                    String userName = scanner.next();
                    orm.deleteByParam("username",userName, User.class);
                    break;
                }
                case "getAll" :{
                    orm.getAll(User.class);
                    break;
                }
                case "exit" :{
                    System.out.println("Thank you, good bye!");
                    break LOOP;
                }
                default:{
                    System.out.println("Inserted wrong command, try another!");
                }
            }
        }
    }

    private static User getUserFromConsole() {

        System.out.print("Input username: ");
        String username = scanner.next();

        System.out.print("Input name: ");
        String name = scanner.next();

        System.out.print("Input age: ");
        String age = scanner.next();

        return new User(username, name, age);
    }

}
