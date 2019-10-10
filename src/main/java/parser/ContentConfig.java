package parser;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import parser.services.HTMLParserService;
import parser.services.client.HttpClient;
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
    @Value("${http-client.host:localhost:8080}")
    public String connectionString;
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
    public ApacheHttpClient httpClientApache() {
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        System.err.println("apache: " + apacheHttpClient.getClass());
        return setDefaultConnectionConfig(apacheHttpClient);
    }

    @Bean(name = "httpClientRestTemplate")
    public RestTemplateClient httpClientRestTemplate() {
        RestTemplateClient restTemplateClient = new RestTemplateClient();
        System.err.println("restTemplate: " + restTemplateClient.getClass());
        return setDefaultConnectionConfig(restTemplateClient);
    }

    @Bean
    public HttpClient httpClient() {
        if (httpClientType.equals("httpClientRestTemplate")) {
            return httpClientRestTemplate();
        } else {
            return httpClientApache();
        }
    }

    @Bean(name = "configuredHtmlParserService")
    public HTMLParserService htmlParserService() {
        HTMLParserService htmlParserService = new HTMLParserService();
        htmlParserService.setLinkToSiteWithTeams(defaultLink);
        return htmlParserService;
    }

    @NotNull
    private <T extends AbstractHttpClient> T setDefaultConnectionConfig(T httpClient) {
        httpClient.setInitialConnectionPath(connectionString);
        httpClient.setCredentials(username, password);
        return httpClient;
    }
}
