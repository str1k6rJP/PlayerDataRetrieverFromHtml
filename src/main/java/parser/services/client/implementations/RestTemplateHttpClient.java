package parser.services.client.implementations;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;

public class RestTemplateHttpClient implements HttpClient {


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
        return false;
    }

    @Override
    public UsernamePasswordCredentials getCredentials() {
        return null;
    }
}
