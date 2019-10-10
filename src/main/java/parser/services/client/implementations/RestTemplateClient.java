package parser.services.client.implementations;

import lombok.extern.slf4j.Slf4j;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RestTemplateClient extends AbstractHttpClient {

    private RestTemplate template = new RestTemplate();

    public RestTemplateClient() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        template.setMessageConverters(messageConverters);
    }

    @Override
    public boolean savePlayers(List<Player> playerList) {
        HttpEntity<List<Player>> playersListToSet = new HttpEntity<>(playerList, getRequestHeaders());
        ResponseEntity<List<Player>> rateResponse = template.exchange(getConnectionPathTo(REQUEST_SAVE_PLAYERS)
                , HttpMethod.POST
                , playersListToSet
                , new ParameterizedTypeReference<List<Player>>() {
                });
        log.error("Status code" + (rateResponse.getStatusCode().value()));

        return true;
    }

    @Override
    public Team saveTeam(Team team) {

        if (team.getTeamName() != null && team.getTeamName().length() != 0) {
            URL url = null;
            try {
                url = new URL(getConnectionPathTo(REQUEST_SAVE_TEAM) + team.getTeamName());
            } catch (MalformedURLException e) {
                log.error("Failed to build valid url!!", e.getMessage(), e);
                return null;
            }
            return template.postForObject(url.toString()
                    , new HttpEntity<>(null, getRequestHeaders()), Team.class);
        }
        log.error(team.getTeamName() + "isn't a correct team name");
        return null;
    }

    HttpHeaders getRequestHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(getUsername(), getPassword());
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Content-Type", "text/plain");
        httpHeaders.add("Accept", "application/json");
        return httpHeaders;
    }

}
