package co.com.pragma.consumer;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class ClientValidationRestConsumerTest {

    private MockWebServer server;
    private ClientValidationRestConsumer consumer;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient wc = WebClient.builder().baseUrl(server.url("/").toString()).build();
        consumer = new ClientValidationRestConsumer(wc);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void returnsTrueWhenBackendSaysTrue() {
        server.enqueue(new MockResponse().setBody("true").addHeader("Content-Type", "text/plain"));

        StepVerifier.create(consumer.validateUserByEmailAndDocument("a@b.com","123"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void returnsFalseWhenBackendSaysFalse() {
        server.enqueue(new MockResponse().setBody("false").addHeader("Content-Type", "text/plain"));

        StepVerifier.create(consumer.validateUserByEmailAndDocument("a@b.com","123"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void whenConnectionDrops_pipelineErrorsBecauseNoAOP() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        StepVerifier.create(consumer.validateUserByEmailAndDocument("a@b.com","123"))
                .expectError()
                .verify();
    }
}
