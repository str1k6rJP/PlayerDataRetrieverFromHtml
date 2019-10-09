package parser.services.client.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import parser.errors.InvalidInputError;
import parser.services.client.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

@Service
@Slf4j
public abstract class AbstractHttpClient implements HttpClient {

    static final String REQUEST_SAVE_TEAM = "team/";
    static final String REQUEST_SAVE_PLAYERS = "player/add";
    static final Pattern NAME_PATTERN = Pattern.compile("teamName\\\"?[=:]\\\"?(?<teamName>[^\\\",}]{1,50}+)\\\"?[,}]");
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private URL initialConnPath;
    private String username;
    private String password;

    /**
     * Sets path to the target service. All the further requests will contain this path followed by request
     *
     * @param host
     * @param port
     * @return true in case URL is valid
     * @throws InvalidInputError if input was incorrect
     */
    public boolean setInitialConnPath(String host, String port) {
        if (host.matches("[:;/\\\\]")) {
            throw new InvalidInputError(String.format("Error while validation of acceptable symbols in host: %s", host));
        }
        if (port.matches("[^0-9]")) {
            throw new InvalidInputError(String.format("Invalid port value: %s\nPort should contain only digits", port));
        }
        try {
            this.initialConnPath = new URL(String.format("http://%s:%s/", host, port));
            return true;
        } catch (MalformedURLException e) {
            log.error("Failed to set valid link!!!", e.getMessage(), e);
            return false;
        }
    }

    public boolean setInitialConnPath(String host, int port) {
        return setInitialConnPath(host, port + "");
    }


    /**
     * Returns <code>String</code> in form of http[s]://<host>:<port>/
     *
     * @return URL prefab as <code>String</code>
     */
    public URL getInitialConnectionPath() {
        return initialConnPath;
    }

    /**
     * Returns result of {@link #getInitialConnectionPath()} and appends /<request> to it
     *
     * @param request request to the applications' API
     * @return prebuilt request in full form as <code>String</code>
     */
    public String getConnectionPathTo(String request) {
        return getInitialConnectionPath() + request;
    }

    /**
     * Sets credentials (login and password) to define access permissions in the environment of API of application you're trying to connect to
     *
     * @param username
     * @param password
     * @return if credentials were set without any issues
     */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public <T> String getInstanceInJsonFormat(T instance) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(instance);
    }
}
