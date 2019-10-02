package parser.services.client.implementations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import parser.beans.Team;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

@Service
public class RestTemplateClient implements HttpClient {

    private static final String invalidInputErrorCustomAdviceMessageForConnectionParams = "\nThe input should be in format <hostname>:<port>";
    private static final Pattern stringNamePattern = Pattern.compile("\"team_?(?i)n(?-i)ame=\"(?<teamName>[^\",]{1,50}+)\"");
    private String host, port;
    private String username, password;
    private String requestForSaveTeam = "team/", requestForSavePlayers = "player/add";
    private UsernamePasswordCredentials credentials;
    private String prebuiltConnectionParams;
    private RestTemplate template = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public boolean savePlayers(String jsonString) throws IOException, AuthenticationException {

        HttpEntity<String> playersListToSet = new HttpEntity<>(jsonString, getAuthHeader());
        template.postForObject(prebuiltConnectionParams + requestForSavePlayers, playersListToSet, String.class);
        return false;
    }

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException {

        String teamName = stringNamePattern.matcher(jsonStringWithName).group("teamName");
        if (teamName != null && teamName.length() != 0) {
            URL url = new URL(prebuiltConnectionParams + requestForSaveTeam + teamName);
            return template.postForObject(url.toString()
                    , new HttpEntity<String>("body", getAuthHeader()), Team.class).getId();
        }
        System.err.println(teamName + "isn't a correct team name");
        return -1;
    }

    @Override
    public void setConnectionParams(String host, String port) throws InvalidInputError {
        try {
            Integer.parseInt(port);
            if (host.matches(forbiddenHostPartsRegexSet)) {
                throw new InvalidInputError("It's ot possible for the hostname to contain any of " + forbiddenHostPartsRegexSet + " symbols");
            }
            ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new InvalidInputError("Wrong input for port value!! It MUSTN'T contain any symbols except digits : "
                    + e.getMessage());
        }
        this.host = host;
        this.port = port;

        prebuiltConnectionParams = "http://" + host + ":" + port + "/";
    }

    @Override
    public String getConnectionParams() {
        return prebuiltConnectionParams;
    }

    @Override
    public boolean setCredentials(String username, String password) {
        credentials = new UsernamePasswordCredentials(this.username = username, this.password = password);
        return credentials.getUserName().equals(this.username) && credentials.getPassword().equals(password);
    }

    @Override
    public UsernamePasswordCredentials getCredentials() {
        return credentials;
    }

    private HttpHeaders getAuthHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(credentials.getUserName(), credentials.getPassword());
        return httpHeaders;
    }

}
