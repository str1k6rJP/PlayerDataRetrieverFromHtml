package parser;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import parser.services.HTMLParserService;
import parser.services.client.implementations.AbstractHttpClient;
import parser.services.client.implementations.ApacheHttpClient;
import parser.services.client.implementations.RestTemplateClient;

import javax.validation.constraints.NotNull;

@Configuration
public class ContentConfig {

    @NotNull
    @Value("${http-client.type:httpClientApache}")
    public String httpClientType;

    @NotNull
    @Value("${http-client.host:localhost}")
    public String hostName;

    @NotNull
    @Value("${http-client.port:8080}")
    public Integer port;
    @Nullable
    @Value("${html-parser.default-link}")
    public String defaultLink;
    @NotNull
    @Value("${http-client.login.username:user}")
    private String username;
    @NotNull
    @Value("${http-client.login.password:user}")
    private String password;

    @Bean(name = "httpClientApache")
    public AbstractHttpClient httpClientApache() {
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        System.err.println("apache: " + apacheHttpClient.getClass());
        return setDefaultConnectionConfig(apacheHttpClient);
    }

    @Bean(name = "httpClientRestTemplate")
    public AbstractHttpClient httpClientRestTemplate() {
        RestTemplateClient restTemplateClient = new RestTemplateClient();
        System.err.println("restTemplate: " + restTemplateClient.getClass());
        return setDefaultConnectionConfig(restTemplateClient);
    }

    @Bean(name = "configuredHtmlParserService")
    public HTMLParserService htmlParserService() {
        HTMLParserService htmlParserService = new HTMLParserService();
        System.err.println(httpClientType);
        if (httpClientType.equals("httpClientRestTemplate")) {
            htmlParserService.setHttpClient(httpClientRestTemplate());
        } else {
            htmlParserService.setHttpClient(httpClientApache());
        }
        htmlParserService.setLinkToSiteWithTeams(defaultLink);
        return htmlParserService;
    }

    @NotNull
    private AbstractHttpClient setDefaultConnectionConfig(AbstractHttpClient httpClient) {
        httpClient.setInitialConnPath(hostName, port);
        httpClient.setCredentials(username, password);
        return httpClient;
    }

}
