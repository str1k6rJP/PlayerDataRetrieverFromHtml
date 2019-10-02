package parser.services.client;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import parser.errors.InvalidInputError;

import java.io.IOException;

public interface HttpClient {

    String serviceUrl = "localhost:8083/";
    String forbiddenHostPartsRegexSet= "[:;/'`\\]}{)(*&?%^$@!~\\[\"\\\\\\s]";
    String invalidInputErrorCustomAdviceMessageForConnectionParams = "\nThe input should be in format <hostname>:<port>";


    /**
     * Receives templates of objects to be set, returns list of <code>Player</code> which were set
     *
     * @param jsonString
     * @return <code>boolean</code>
     */
    boolean savePlayers(String jsonString) throws IOException, AuthenticationException;

    /**
     * Receives <code>Team</code> prefab to save in, returns entity of <code>Team</code> which was saved.
     * If save was failed, returns an empty <code>Team</code>
     *
     * @param jsonStringWithName
     * @return <code>Team</code>
     */
    int saveTeam(String jsonStringWithName) throws IOException, AuthenticationException;

    /**
     * Sets host and port parameters of application responsive for database handling
     *
     * @param host host name
     * @param port port number
     * @throws InvalidInputError if input was incorrect
     *
     * @see #savePlayers(String)
     */
    void setConnectionParams(String host, String port) throws InvalidInputError;

    /**
     * Does the same thing as {@link #setConnectionParams(String, String)}, but accepts parameters in form of <code><host>:<port></code>
     *
     * @param singleLineConnectionParams host and port
     * @return <code>String</code> templated as http[s]://<host>:<port>/
     * @throws InvalidInputError if input was incorrect
     *
     * @see #setConnectionParams(String, String)
     */
    default String setConnectionParams(String singleLineConnectionParams) throws InvalidInputError{
        String[] s = singleLineConnectionParams.trim().split(":");
        try {
            setConnectionParams(s[0], s[1]);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputError(invalidInputErrorCustomAdviceMessageForConnectionParams +" : "+ e.getMessage());
        }
        return getConnectionParams();
    };

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
    String getConnectionParams(String request);

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

}
