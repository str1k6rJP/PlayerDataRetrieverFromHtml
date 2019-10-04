package parser.services.client.implementations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import parser.beans.Player;
import parser.beans.Team;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RestTemplateClient implements HttpClient {

    private static final String invalidInputErrorCustomAdviceMessageForConnectionParams = "\nThe input should be in format <hostname>:<port>";
    private static final Pattern stringNamePattern = Pattern.compile("teamName\\\"?[=:]\\\"?(?<teamName>[^\\\",}]{1,50}+)\\\"?[,}]");
    private String host, port;
    private String username, password;
    private String requestForSaveTeam = "team/", requestForSavePlayers = "player/add";
    private UsernamePasswordCredentials credentials;
    private String prebuiltConnectionParams;
    private RestTemplate template = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));

        messageConverters.add(converter);
        template.setMessageConverters(messageConverters);
    }

//    @Override
//    public boolean savePlayers(String jsonString) throws IOException, AuthenticationException {
//        System.err.println(false);
//     return false;
//    }

    @Override
    public boolean savePlayers(List<Player> jsonString) throws IOException, AuthenticationException {
        HttpEntity<List<Player>> playersListToSet = new HttpEntity<>(jsonString, getAuthHeader());
        ResponseEntity<List<Player>> rateResponse = template.exchange(prebuiltConnectionParams + requestForSavePlayers
                , HttpMethod.POST
                , playersListToSet
                , new ParameterizedTypeReference<List<Player>>() {
                });
        System.err.println("Status code" + (rateResponse.getStatusCode().value()));/*
        System.out.println(template
                .postForObject(prebuiltConnectionParams + requestForSavePlayers
                        , playersListToSet
                        , HttpEntity.class));*/
        System.err.println(true);
        return true;
    }

    @Override
    public int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException {
        Matcher m = stringNamePattern.matcher(jsonStringWithName);
        String teamName = null;
        if (m.find()) {
            teamName = m.group("teamName");
        } else {
            throw new IOException("Can't parse json " + jsonStringWithName);
        }
        if (teamName != null && teamName.length() != 0) {
            URL url = new URL(prebuiltConnectionParams + requestForSaveTeam + teamName);
            return template.postForObject(url.toString()
                    , new HttpEntity<>(null, getAuthHeader()), Team.class).getId();
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
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Content-Type", "text/plain");

        httpHeaders.add("Accept", "application/json");
        return httpHeaders;
    }

}
