package parser.services;


import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HtmlParserServiceTest {

    @Autowired
    public HTMLParserService parserService;

    @Before
    @Test
    public void testSetLink() throws Exception{
        String s = parserService.setLinkToSiteWithTeams("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");
        System.out.println(s);
        assert s.equals("https://en.wikipedia.org/wiki/List_of_football_clubs_in_Spain");

    }

    String host = "localhost"
            ,port = "8083"
            ,request = "team"
            ;
    @Before
    @Test
    public void testSetConnectionParams() throws Exception{
        assert parserService.setConnectionParams(host,port).equals(String.format("http://%s:%s/",host,port));
    }

    String userName = "str1k6r"
            ,password = "that'sME"
            ;
    @Before
    @Test
    public void testCredentials() throws Exception {
        UsernamePasswordCredentials credentials = parserService.setUsernamePasswordCredentials(userName, password);

        assert (credentials.getPassword().equals(password)&&credentials.getUserName().equals(userName));
    }

    @Test
    public void testJsonResult() throws Exception{
        System.out.println(parserService.getPlayersInJsonFormat(parserService.getPlayersStringBySiteWithTeamList()));
    }

}
