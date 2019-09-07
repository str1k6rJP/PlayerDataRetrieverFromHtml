package parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import parser.database.tables.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import parser.services.HTMLParserService;

import java.util.List;

/**
 * @author str1k6rJP
 * @version 1.0.0
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static boolean consoleWriterMode;
    @Autowired
    private HTMLParserService htmlParserService;

    public static void main(String[] args) {

        SpringApplication.run(Application.class,args);

    }

    @Override
    public void run(String... args) throws Exception {
        try {
            consoleWriterMode = (args[0].equals("true") || args[0].equals("console")) || (Integer.parseInt(args[0]) == 1) ? true : false;
        } catch (ArrayIndexOutOfBoundsException e){
            consoleWriterMode = false;
            System.out.println("\n\n\n\n\nNo arguments were specified so consoleWriterMode set to false by default\n\n\n\n\n\n");
        }
        // System.out.println(parser.HTMLParser.getWebDoc("https://en.wikipedia.org/wiki/Deportivo_Alav%C3%A9s"));
if (consoleWriterMode){
    System.out.println("\n\n\n\n\n\n\n\n\n\nConsoleWriterMode set to true\n" +
            "You will be able to see intermediate and while-processing data\n\n\n\n\n\n\n\n\n\n");
}
        List<Player> playersSetToDataBase = htmlParserService.getPlayersBySiteWithTeamList("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");
        if (consoleWriterMode) {
            System.out.println("Such players were retrieved from html pages and set to databases");
            for (Player player : playersSetToDataBase
            ) {
                System.out.println(player.getSurname() + "  " + player.getRole() + "  " + player.getTeamId());
            }
        }
    }
}
