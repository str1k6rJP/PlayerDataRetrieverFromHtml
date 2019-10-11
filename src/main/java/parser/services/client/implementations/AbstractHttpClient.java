package parser.services.client.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import parser.services.client.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public abstract class AbstractHttpClient implements HttpClient {
    static final Pattern protocolPattern = Pattern.compile("^[a-z.]+://");
    static final String REQUEST_SAVE_TEAM = "team/";
    static final String REQUEST_SAVE_PLAYERS = "player/add";
    private static final int PORT_LIMITER = 65535;
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Pattern connectionPattern = Pattern.compile("^.*(://)?(?<host>.+):(?<port>\\d+)$");
    private URL initialConnPath;
    private String username;
    private String password;


    /**
     * Sets path to the target service. All the further requests will contain this path followed by request
     *
     * @param connection path to the target service
     * @return true in case URL is valid
     */
    @Override
    public boolean setInitialConnectionPath(String connection) {
        Matcher matcher = connectionPattern.matcher(connection);

        if (matcher.find()) {
            if (Integer.parseInt(matcher.group("port")) <= PORT_LIMITER) {
                if (connectionPattern.matcher(connection).find()) {
                    return setConnectionURLFromValidString(connection);
                }
                log.error("URL specified missing a protocol");
                return false;
            }
            log.error(String.format("%s isn't valid port number!!!%n It should be natural number less than %s", matcher.group("port"), PORT_LIMITER));
            return false;
        }
        log.error(String.format("%s isn't valid value for service path", connection));
        return false;
    }

    boolean setConnectionURLFromValidString(String connection) {
        try {
            this.initialConnPath = new URL(connection);
            return true;
        } catch (MalformedURLException e) {
            log.error("Failed to set valid link!!!", e.getMessage(), e);
        }
        return false;
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
        return String.format("%s/%s", getInitialConnectionPath().toString(), request);
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
        return objectMapper.writeValueAsString(instance);
    }
}
