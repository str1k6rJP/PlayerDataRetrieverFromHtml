package parser.services.client.implementations;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApacheHttpClientTest {

    @Autowired
    public ApacheHttpClient apacheHttpClient;

    String host = "localhost"
            ,port = "8083"
            ,request = "team"
            ;
    @Before
    public void setConnectionParams(){
        apacheHttpClient.setConnectionParams(host,port);
    }
    @Test
    public void testConnectionParams() throws Exception{
        String connectionParams;
        System.out.println(connectionParams=apacheHttpClient.getConnectionParams(request));
        assert(connectionParams.equals("http://"+host+":"+port+"/"+request));
    }

    String userName = "str1k6r"
            ,password = "that'sME"
            ;
    @Before
    @Test
    public void testCredentials() throws Exception{
        assert (apacheHttpClient.setCredentials(userName,password));
        System.out.println(apacheHttpClient.getCredentials().getUserName()+"\n"+apacheHttpClient.getCredentials().getPassword());
        assert (apacheHttpClient.getCredentials().getUserName().equals(userName)&&apacheHttpClient.getCredentials().getPassword().equals(password));
    }

    String jsonStringWithTeam = "[{teamName:reallyBadTeamName}]"
            ;
    @Test
    public void testSaveTeam() throws Exception{
        int teamId = apacheHttpClient.saveTeam(jsonStringWithTeam);
        System.out.println(teamId);
        assert (teamId>0);
    }

    String jsonPlayersString =
            "[{\"surname\":\"reallySurname\",\"role\":\"midfielder\",\"teamId\":\"1\"},{\"surname\":\"secondSurname\",\"role\":\"hz\",\"teamId\":\"1\"}]"
            ;
    @Test
    public void testSavePlayers() throws Exception{
        apacheHttpClient.setConnectionParams(host,port);
        assert apacheHttpClient.savePlayers(jsonPlayersString);
    }


}
