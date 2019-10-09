package parser.services.client.implementations;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import parser.services.client.ClientTest;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApacheHttpClientTest extends ClientTest {

    @Autowired
    @Qualifier("httpClientApache")
    public AbstractHttpClient httpClient;


    @Override
    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }

}
