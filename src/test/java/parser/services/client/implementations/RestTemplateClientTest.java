package parser.services.client.implementations;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import parser.services.client.ClientTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RestTemplateClientTest extends ClientTest { //NOSONAR
    //because all the tests implemented in the parent, and this class' purpose is only to configure tests for the right aervice

    @Autowired
    @Qualifier("httpClientRestTemplate")
    public AbstractHttpClient httpClient;

    @Override
    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    @Before
    public void setup() {
        stubFor(
                post(urlPathMatching(postSingleTeam))
                        .withBasicAuth("str1k6r", "that'sME")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withBody(teamSaveResponse)));
        stubFor(post(urlEqualTo(postAllPlayers)).withHeader("Accept", equalTo("application/json"))
                .withHeader("content-type", equalTo("application/json"))
                .withBasicAuth("str1k6r", "that'sME")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

    }
}
