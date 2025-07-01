package tw.ispan.librarysystem.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClientBuilder.RequestConfigCallback requestConfigCallback = requestConfigBuilder -> 
            requestConfigBuilder
                .setConnectTimeout(5000)
                .setSocketTimeout(60000);

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = httpClientBuilder -> {
            // 如果有設定認證資訊，則加入認證
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, 
                    new UsernamePasswordCredentials(username, password));
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
            return httpClientBuilder;
        };

        RestClient restClient = RestClient.builder(new HttpHost(host, port))
            .setRequestConfigCallback(requestConfigCallback)
            .setHttpClientConfigCallback(httpClientConfigCallback)
            .build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
