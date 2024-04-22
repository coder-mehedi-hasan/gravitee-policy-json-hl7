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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gravitee.common.http.MediaType;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.reactive.api.context.HttpExecutionContext;
import io.gravitee.gateway.reactive.api.policy.Policy;
import io.gravitee.policy.pizza.configuration.PizzaPolicyConfiguration;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class PizzaPolicy implements Policy {

    public static final String JSON_TO_HL7 = "json-hl7";
    public static final String HL7_TO_JSON = "hl7-json";
    public static final String ACTION_TYPE = "action-type";
    public static final String X_PIZZA_HEADER_TOPPING = "x-pizza-topping";
    public static final String PIZZA_ERROR_KEY = "PIZZA_ERROR";
    public static final String ERROR_PROCESSING_PIZZA = "Error processing pizza";
    public static final String X_PIZZA_HEADER = "X-Pizza";
    public static final String NOT_CREATED = "not-created";
    public static final String CREATED = "created";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final methods methods;
    private final PizzaPolicyConfiguration configuration;

    @Override
    public String id() {
        return "pizza-factory";
    }

    public PizzaPolicy(PizzaPolicyConfiguration configuration) throws URISyntaxException {
        this.configuration = configuration;
        this.methods = new methods();
    }

    private static void setContentHeaders(final HttpHeaders headers, final Buffer buffer, String headerType, String actionType) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, headerType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(buffer.length()));
        headers.set(ACTION_TYPE, actionType);
    }

    @Override
    public Completable onRequest(HttpExecutionContext ctx) {
        return ctx
            .request()
            .onBody(maybeBody ->
                maybeBody
                    // If no body, then use an empty buffer
                    .defaultIfEmpty(Buffer.buffer())
                    // Create a pizza from body and headers
                    .flatMapMaybe(body -> {
                        System.out.println("payload body");
                        System.out.println(body);
                        Maybe<Buffer> str = createPizza(body, ctx.request().headers());
                        System.out.println("payload response");
                        str.subscribe(buffer -> System.out.println(buffer.toString()));
                        //                        System.out.println("Response header");
                        //                        System.out.println(ctx.request().headers().get("test"));
                        return str;
                    })
                    // If no pizza has been created, then handle the case
                    .switchIfEmpty(handleNoPizza(ctx.request().headers(), ctx))
                    .doOnComplete(() -> {
                        System.out.println("Response logged successfully.");
                    })
            );
    }

    /**
     * Create a pizza according to toppings from body and headers. Crust and sauce are coming from configuration of the policy
     * @param body the request or response body
     * @param headers the request or response headers
     * @return a Maybe.empty() if no topping provided, a Maybe.just(createdPizza) if there are toppings.
     * @throws IOException or RuntimeException that will be managed by the caller.
     */
    private Maybe<Buffer> createPizza(Buffer body, HttpHeaders headers) throws IOException {
        String bodyString = body.toString();
        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        String actionType = headers.get(ACTION_TYPE);
        if (contentType != null && contentType.equals(MediaType.APPLICATION_JSON) && actionType != null && actionType.equals(JSON_TO_HL7)) {
            String hl7Str = jsonToHl7(bodyString);
            Buffer hl7Buffer = Buffer.buffer(hl7Str);
            if (hl7Str != null) {
                setContentHeaders(headers, hl7Buffer, MediaType.TEXT_PLAIN, actionType);
            }
            System.out.println("HL7STR:->  " + hl7Str);
            return Maybe.just(hl7Buffer);
        } else if (
            contentType != null && contentType.equals(MediaType.TEXT_PLAIN) && actionType != null && actionType.equals(HL7_TO_JSON)
        ) {
            ObjectNode json = hl7ToJson(bodyString);
            System.out.println("JSON:->" + json.toString());
            Buffer jsonBuffer = Buffer.buffer(json.toString());
            if (json != null) {
                setContentHeaders(headers, jsonBuffer, MediaType.APPLICATION_JSON, actionType);
            }
            return Maybe.just(jsonBuffer);
        } else {
            return Maybe.just(body);
        }
    }

    public String jsonToHl7(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder message = new StringBuilder();
        try {
            JsonNode fields = objectMapper.readTree(methods.fieldsString);
            JsonNode payload = objectMapper.readTree(data);
            ArrayList<String> payloadKeys = methods.objectKeys(payload);
            System.out.println("fields");
            System.out.println(fields);
            System.out.println("payload");
            System.out.println(payload);
            for (int i = 0; i < payloadKeys.size(); i++) {
                String payloadKey = payloadKeys.get(i);
                JsonNode field = methods.find(fields, payloadKey);
                JsonNode fieldElements = field.get("elements");
                if (field != null && fieldElements.size() != 0) {
                    String line = field.get("init").asText() + "|";
                    int in = 0;
                    for (JsonNode element : fieldElements) {
                        JsonNode subElements = element.get("sub");
                        String elementName = element.get("name").textValue();
                        String fieldName = field.get("name").asText();
                        JsonNode fieldObject = payload.get(fieldName);
                        if (elementName == null) {
                            if (in != fieldElements.size() - 1) {
                                line += "|";
                            }
                        } else if (subElements != null) {
                            int k = 0;
                            for (JsonNode subElement : subElements) {
                                String subElementName = subElement.get("name").textValue();
                                if (subElementName == null) {
                                    line += "^";
                                } else {
                                    JsonNode elementValue = fieldObject.get(elementName);
                                    JsonNode value = elementValue.get(subElement.get("name").textValue());
                                    if (value != null) {
                                        line += value.textValue();
                                    } else if (subElement.get("value") != null) {
                                        line += subElement.get("value").textValue();
                                    } else {
                                        line += subElement.get("name").textValue();
                                    }
                                    if (k != subElements.size() - 1) {
                                        line += "^";
                                    }
                                }
                                k++;
                            }
                            if (in != fieldElements.size() - 1) {
                                line += "|";
                            }
                        } else {
                            if (elementName != null) {
                                JsonNode value = fieldObject.get(elementName);
                                if (value != null) {
                                    line += value.textValue();
                                } else if (element.get("value").textValue() != null) {
                                    line += element.get("value").textValue();
                                } else {
                                    line += element.get("name").textValue();
                                }

                                if (in != fieldElements.size() - 1) {
                                    line += "|";
                                }
                            }
                        }
                        in++;
                    }
                    message.append(line).append("\n");
                }
            }
            return message.toString();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ObjectNode hl7ToJson(String hl7Msg) {
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode fieldsHl7 = objectMapper.createArrayNode();
        ObjectNode fieldObj = objectMapper.createObjectNode();

        try {
            JsonNode fields = objectMapper.readTree(methods.fieldsString);
            if (fields.isArray()) {
                for (JsonNode field : fields) {
                    String init = field.get("init").textValue();
                    boolean found = hl7Msg.contains(init);
                    if (found) {
                        int startAt = hl7Msg.indexOf(init);
                        ((ObjectNode) field).put("startAt", startAt);
                        fieldsHl7.add(field);
                    }
                }
            }

            ArrayNode fieldsWithMessage = objectMapper.createArrayNode();
            for (int i = 0; i < fieldsHl7.size(); i++) {
                JsonNode field = fieldsHl7.get(i);
                if (field.has("startAt")) {
                    int startAt = field.get("startAt").asInt();
                    String msg = "";
                    if (fieldsHl7.size() == i + 1) {
                        msg = hl7Msg.substring(startAt, hl7Msg.length());
                    } else {
                        JsonNode nextField = fieldsHl7.get(i + 1);
                        int nextStart = nextField.get("startAt").asInt();
                        msg = hl7Msg.substring(startAt, nextStart);
                    }
                    ((ObjectNode) field).put("msg", msg);
                    fieldsWithMessage.add(field);
                }
            }

            for (JsonNode field : fieldsWithMessage) {
                String msgs = field.get("msg").textValue();
                String[] partMsg = msgs.split("\\|");
                ObjectNode msgObj = objectMapper.createObjectNode();
                for (int index = 0; index < partMsg.length; index++) {
                    String msg = partMsg[index];
                    if (index != 0 && !msg.isEmpty()) {
                        JsonNode elements = field.get("elements");
                        if (elements.isArray()) {
                            JsonNode element = elements.get(index - 1);
                            JsonNode subElements = element.get("sub");
                            String elementName = element.get("name").textValue();
                            if ((subElements == null || subElements.isNull()) && elementName != null) {
                                msgObj.put(elementName, msg);
                            } else if (elementName != null && subElements.isArray()) {
                                ObjectNode obj = objectMapper.createObjectNode();
                                String[] subMsgs = msg.split("\\^");
                                for (int i = 0; i < subMsgs.length; i++) {
                                    String subMsg = subMsgs[i];
                                    if (subMsg != null && !subMsg.isEmpty()) {
                                        JsonNode subElement = subElements.get(i);
                                        if (subElement != null) {
                                            String subElementName = subElement.get("name").textValue();
                                            if (subElementName != null && !subElementName.isEmpty()) {
                                                obj.put(subElementName, subMsg);
                                            }
                                        }
                                    }
                                }
                                msgObj.put(elementName, obj);
                            }
                        }
                    }
                }
                String fieldName = field.get("name").textValue();
                if (fieldName != null && !fieldName.isEmpty()) {
                    fieldObj.put(fieldName, msgObj);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fieldObj;
    }

    /**
     * If no pizza created, then return an empty buffer and add a particular header.
     * ⚠️ The header is added as part of the reactive chain
     * @param headers
     * @return
     */
    private static Maybe<Buffer> handleNoPizza(HttpHeaders headers, HttpExecutionContext ctx) {
        System.out.println("Pizza is empty");

        return Maybe.fromCallable(() -> {
            headers.add(X_PIZZA_HEADER, NOT_CREATED);
            return Buffer.buffer();
        });
    }
}
