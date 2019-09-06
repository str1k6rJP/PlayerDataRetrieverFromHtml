package parser;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private HTMLParserService htmlParserService;

    public static void main(String[] args) {

        SpringApplication.run(Application.class,args);

    }

    @Override
    public void run(String... args) throws Exception {
        // System.out.println(parser.HTMLParser.getWebDoc("https://en.wikipedia.org/wiki/Deportivo_Alav%C3%A9s"));

        List<Player> playersSetToDataBase = htmlParserService.getPlayersBySiteWithTeamList("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");

        for (Player player: playersSetToDataBase
        ) {
            System.out.println(player.getSurname()+ "  "+player.getRole()+"  "+player.getTeamId());
        }
    }
}
