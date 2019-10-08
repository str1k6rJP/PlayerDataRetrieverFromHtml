package parser.services.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import parser.beans.Player;
import parser.errors.InvalidInputError;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface HttpClient {

    String forbiddenHostPartsRegexSet = "[:;/'`\\]}{)(*&?%^$@!~\\[\"\\\\\\s]";
    String invalidInputErrorCustomAdviceMessageForConnectionParams = "\nThe input should be in format <hostname>:<port>";

    /**
     * This regular expression implies that received string would whether or not contain domain http:// or https://
     * , would necessarily contain host name without any slashes, colons, semicolons or question marks in it
     * , then ':' and four-digit only port followed by slash. All the conditions should be held or input string won't match otherwise
     */
    Pattern hostAndPortPattern = Pattern.compile("^(https?://)?(?<host>[^/:?]+):(?<port>\\d{4})/?");


    /**
     * Receives templates of objects to be set, returns list of <code>Player</code> which were set
     *
     * @param jsonString
     * @return <code>boolean</code>
     */
    //boolean savePlayers(String  jsonString) throws IOException, AuthenticationException;

    boolean savePlayers(List<Player> jsonString) throws IOException, AuthenticationException;

    /**
     * Receives <code>Team</code> prefab to save in, returns id of team saved
     * If save was failed, returns -1
     *
     * @param jsonStringWithName team entity prefab
     * @return <code>Team.id</code> field value
     */
    int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException;

    /**
     * Sets host and port parameters of application responsive for database handling
     *
     * @param host host name
     * @param port port number
     * @throws InvalidInputError if input was incorrect
     * @see #setConnectionParams(String)
     */
    void setConnectionParams(String host, String port) throws InvalidInputError;

    /**
     * Does the same thing as {@link #setConnectionParams(String, String)}, but accepts parameters in form of <code><host>:<port></code>
     *
     * @param singleLineConnectionParams host and port
     * @return <code>String</code> templated as http[s]://<host>:<port>/
     * @throws InvalidInputError if input was incorrect
     * @see #setConnectionParams(String, String)
     */
    default String setConnectionParams(String singleLineConnectionParams) throws InvalidInputError {
        Matcher matcher = hostAndPortPattern.matcher(singleLineConnectionParams);
        if (matcher.find()) {
            System.out.println(matcher.group("host"));
            System.out.println(matcher.group("port"));
            setConnectionParams(matcher.group("host"), matcher.group("port"));
            return getConnectionParams();
        } else {
            throw new InvalidInputError(singleLineConnectionParams + "isn't valid input");
        }
    }


    /**
     * Returns <code>String</code> in form of http[s]://<host>:<port>/
     *
     * @return URL prefab as <code>String</code>
     */
    String getConnectionParams();

    /**
     * Returns result of {@link #getConnectionParams()} and appends /<request> to it
     *
     * @param request request to the applications' API
     * @return prebuilt request in full form as <code>String</code>
     */
    default String getConnectionParams(String request) {
        return getConnectionParams() + request;
    }

    /**
     * Sets credentials (login and password) to define access permissions in the environment of API of application you're trying to connect to
     *
     * @param username
     * @param password
     * @return if credentials were set without any issues
     */
    boolean setCredentials(String username, String password);

    /**
     * Returns credentials in form defined by {@link UsernamePasswordCredentials} from the spring boot security lib
     *
     * @return <code>UsernamePasswordCredentials</code>  entity stored within the implementation class in the current moment
     */
    UsernamePasswordCredentials getCredentials();

    default <T> String getInstanceInJsonFormat(T instance) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(instance);
    }

}
