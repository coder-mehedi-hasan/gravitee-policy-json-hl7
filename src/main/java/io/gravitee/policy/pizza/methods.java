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
package io.gravitee.policy.pizza;/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class methods {

    public final String fieldsString =
        "[\n" +
        "    {\n" +
        "        \"elements\": [\n" +
        "            {\n" +
        "                \"name\": \"encoding_charecter\",\n" +
        "                \"value\": \"^~\\\\&\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"sending_application\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"sending_facility\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"receiving_application\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"receiving_facility\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"date_time_message\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"message_type\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"message_code\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"trigger_event\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"message_control_id\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"processing_id\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"version_id\"\n" +
        "            }\n" +
        "        ],\n" +
        "        \"name\": \"messageHeader\",\n" +
        "        \"init\": \"MSH\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"elements\": [\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"patient_id\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"identifier_list\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"patient_name\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"family_name\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"given_name\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": null\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": null\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"prefix\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"date_time_birth\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"sex\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"address\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"street_adress\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": null\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"city\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"state\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"postal_code\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"country\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            }\n" +
        "        ],\n" +
        "        \"name\": \"patientIdentification\",\n" +
        "        \"init\": \"PID\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"elements\": [\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"patient_class\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"patient_location\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"point_care\",\n" +
        "                        \"value\": \"care\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"room\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"attending_doctor\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"id\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"family_name\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"given_name\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"hospital_service\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"visit_number\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"admit_date_time\"\n" +
        "            }\n" +
        "        ],\n" +
        "        \"name\": \"patientVisit\",\n" +
        "        \"init\": \"PV1\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"elements\": [\n" +
        "            {\n" +
        "                \"name\": \"order_control\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"place_order_number\"\n" +
        "            }\n" +
        "        ],\n" +
        "        \"name\": \"commonOrder\",\n" +
        "        \"init\": \"ORC\"\n" +
        "    },\n" +
        "    {\n" +
        "        \"elements\": [\n" +
        "            {\n" +
        "                \"name\": \"set_id\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"place_order_number\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"universal_service\",\n" +
        "                \"sub\": [\n" +
        "                    {\n" +
        "                        \"name\": \"identifier\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"text\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"name\": \"coding_system\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": null\n" +
        "            },\n" +
        "            {\n" +
        "                \"name\": \"observation_date_time\"\n" +
        "            }\n" +
        "        ],\n" +
        "        \"name\": \"observationRequest\",\n" +
        "        \"init\": \"OBR\"\n" +
        "    }\n" +
        "]\n";

    public int jsonLength(JsonNode node) {
        if (node.isArray()) {
            return node.size();
        } else {
            return -1;
        }
    }

    public ArrayList<String> objectKeys(JsonNode node) {
        ArrayList<String> keys = new ArrayList<>();
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                keys.add(fieldName);
            }
        }
        return keys;
    }

    public JsonNode find(JsonNode fields, String key) {
        JsonNode field = NullNode.getInstance();
        for (JsonNode element : fields) {
            String fieldValue = element.get("name").asText();
            if (fieldValue.equals(key)) {
                field = element;
            }
        }
        return field;
    }
}
