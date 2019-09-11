package parser.services.client.implementations;


import org.junit.After;
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
    @Test
    public void testConnectionParams() throws Exception{
        apacheHttpClient.setConnectionParams(host,port,request);
        String connectionParams;
        System.out.println(connectionParams=apacheHttpClient.getConnectionParams());
        assert(connectionParams.equals(host+":"+port+"/"+request));
    }

    String userName = "str1k6r"
            ,password = "that'sMe"
            ;
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

}
