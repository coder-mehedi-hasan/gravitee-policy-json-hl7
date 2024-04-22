/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.policy.pizza;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.gravitee.apim.gateway.tests.sdk.utils.HttpClientUtils.extractHeaders;
import static io.gravitee.policy.pizza.PizzaPolicy.CREATED;
import static io.gravitee.policy.pizza.PizzaPolicy.NOT_CREATED;
import static io.gravitee.policy.pizza.PizzaPolicy.X_PIZZA_HEADER;
import static io.gravitee.policy.pizza.PizzaPolicy.X_PIZZA_HEADER_TOPPING;
import static io.gravitee.policy.pizza.exceptions.NotStringArrayException.ERROR_BODY_SHOULD_BE_AN_ARRAY_OF_STRINGS;
import static io.gravitee.policy.pizza.exceptions.PineappleForbiddenException.ERROR_PINEAPPLE_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gravitee.apim.gateway.tests.sdk.AbstractPolicyTest;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.apim.gateway.tests.sdk.connector.EndpointBuilder;
import io.gravitee.apim.gateway.tests.sdk.connector.EntrypointBuilder;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.http.MediaType;
import io.gravitee.definition.model.v4.Api;
import io.gravitee.definition.model.v4.flow.Flow;
import io.gravitee.definition.model.v4.flow.step.Step;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.reactor.ReactableApi;
import io.gravitee.plugin.endpoint.EndpointConnectorPlugin;
import io.gravitee.plugin.endpoint.http.proxy.HttpProxyEndpointConnectorFactory;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPlugin;
import io.gravitee.plugin.entrypoint.http.proxy.HttpProxyEntrypointConnectorFactory;
import io.gravitee.policy.pizza.configuration.PizzaPolicyConfiguration;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.HttpClientRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
class PizzaPolicyIntegrationTest {

    public static final String JSON_TO_HL7 = "json-hl7";
    public static final String HL7_TO_JSON = "hl7-json";
    public static final String ACTION_TYPE = "action-type";

    static class TestPreparer extends AbstractPolicyTest<PizzaPolicy, PizzaPolicyConfiguration> {

        @Override
        public void configureEntrypoints(Map<String, EntrypointConnectorPlugin<?, ?>> entrypoints) {
            entrypoints.putIfAbsent("http-proxy", EntrypointBuilder.build("http-proxy", HttpProxyEntrypointConnectorFactory.class));
        }

        @Override
        public void configureEndpoints(Map<String, EndpointConnectorPlugin<?, ?>> endpoints) {
            endpoints.putIfAbsent("http-proxy", EndpointBuilder.build("http-proxy", HttpProxyEndpointConnectorFactory.class));
        }
    }

    @Nested
    @GatewayTest
    @DeployApi({ "/apis/pizza-api.json", "/apis/pizza-pineapple.json" })
    class OnRequest extends TestPreparer {

        ObjectMapper objectMapper = new ObjectMapper();

        @Test
        @DisplayName("Should create pizza when toppings provided from body")
        void should_create_pizza_with_body_toppings_on_request(HttpClient httpClient) {
            try {
                wiremock.stubFor(get("/endpoint").willReturn(ok()));
                JsonNode payloadJson = objectMapper.readTree(new File("src/test/resources/payload.json"));
                String payloadHl7 =
                    "MSH|^~\\&|HL7Soup|Instance1|HL7Soup|Instance2|20240415104425||ORM^001|MSGID20060307110114|P|2.5.1\n" +
                    "PID||81243|12001||Jones^John^^^Mr.||20011025051236|M|||123 West St.^^Denver^CO^80020^USA|||||||\n" +
                    "PV1||O|OP^PAREG||||2342^Jones^Bob|||CAR|||||||||2|||||||||||||||||||||||||20240415105422\n" +
                    "ORC|NW|202404151101\n" +
                    "OBR|1|20060307110114||003038^Urinalysis^L|||20240415110325";
                httpClient
                    .rxRequest(HttpMethod.GET, "/test")
                    .flatMap(httpClientRequest -> {
                        httpClientRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                        httpClientRequest.headers().add(ACTION_TYPE, HL7_TO_JSON);
                        return httpClientRequest.rxSend(payloadHl7.toString());
                    })
                    .flatMap(response -> {
                        assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK_200);
                        System.out.println("Response Body");
                        return response.body();
                    })
                    .test()
                    .awaitDone(10, TimeUnit.SECONDS);
            } catch (Exception err) {
                System.out.println("Error" + err.getMessage());
            }
        }
    }
}
