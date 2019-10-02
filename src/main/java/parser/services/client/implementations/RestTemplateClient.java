package parser.services.client.implementations;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;
import java.util.regex.Pattern;

public class RestTemplateClient implements HttpClient {
    private Pattern hostAndPortPattern= Pattern.compile("^(https?//)?(?<host>):(?<port>)");
    private static final String invalidInputErrorCustomAdviceMessageForConnectionParams = "\nThe input should be in format <hostname>:<port>";
    private String host, port;
    private String username, password;
    private String requestForSaveTeam = "team", requsetForSavePlayers = "player/add";
    private UsernamePasswordCredentials credentials;
    private String prebuiltConnectionParams;
    @Override
    public boolean savePlayers(String jsonString) throws IOException, AuthenticationException {
        return false;
    }

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException {
        return 0;
    }

    @Override
    public void setConnectionParams(String host, String port) throws InvalidInputError {
        try {
            Integer.parseInt(port);
            if (host.matches(forbiddenHostPartsRegexSet)){
                throw new InvalidInputError("It's ot possible for the hostname to contain any of "+forbiddenHostPartsRegexSet+" symbols");
            };
        } catch (NumberFormatException e){
            e.printStackTrace();
            throw new InvalidInputError("Wrong input for port value!! It MUSTN'T contain any symbols except digits : "
                    +e.getMessage());
        }
        this.host=host;
        this.port=port;

        prebuiltConnectionParams = prebuiltConnectionParams = "http://" + host + ":" + port + "/";
    }

    @Override
    public String setConnectionParams(String singleLineConnectionParams) throws InvalidInputError {
        return null;
    }

    @Override
    public String getConnectionParams() {
        return null;
    }

    @Override
    public String getConnectionParams(String request) {
        return null;
    }

    @Override
    public boolean setCredentials(String username, String password) {
        credentials = new UsernamePasswordCredentials(this.username=username, this.password=password);
        return credentials.getUserName().equals(this.username) && credentials.getPassword().equals(password);
    }

    @Override
    public UsernamePasswordCredentials getCredentials() {
       return credentials;
    }
}
