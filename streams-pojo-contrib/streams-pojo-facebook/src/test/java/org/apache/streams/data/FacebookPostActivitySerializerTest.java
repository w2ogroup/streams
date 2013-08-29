/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.streams.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FacebookPostActivitySerializerTest {

    @Before
    public void setup() throws IOException { }

    @Test
    public void discover() {
        JsonNode node = getJsonNode();
        Node root = new Node(null, "root");
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                mapNode(root, item);
            }
        }
        printTree(root, "");
    }

    private void printTree(Node node, String spacer) {
        System.out.println(String.format("%s %s (%s)", spacer, node.getName(), node.getType()));
        for(Node child : node.getChildren().values()) {
            printTree(child, spacer + "      ");
        }
    }

    private void mapNode(Node parent, JsonNode jsonNode) {
        for (Iterator<Map.Entry<String, JsonNode>> iter = jsonNode.fields(); iter.hasNext(); ) {
            Map.Entry<String, JsonNode> property = iter.next();
            Node current;
            String key = property.getKey();
            JsonNode value = property.getValue();
            if (!parent.getChildren().containsKey(key)) {
                current = new Node(null, key);
                current.setType(value.getNodeType().toString());
                parent.getChildren().put(key, current);
            } else {
                current = parent.getChildren().get(key);
            }
            if(!value.isArray() && value.isObject()){
                mapNode(current, value);
            }
        }
    }

    private JsonNode getJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory(); // since 2.1 use mapper.getFactory() instead

        JsonNode node = null;
        try {
            JsonParser jp = factory.createJsonParser(this.getClass().getResourceAsStream("Facebook.json"));
            node = mapper.readTree(jp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    private static class Node {
        Node parent;
        String name;
        String type;
        Map<String, Node> children = new HashMap<String, Node>();

        private Node(Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        private Node getParent() {
            return parent;
        }

        private void setParent(Node parent) {
            this.parent = parent;
        }

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private Map<String, Node> getChildren() {
            return children;
        }

        private void setChildren(Map<String, Node> children) {
            this.children = children;
        }

        private String getType() {
            return type;
        }

        private void setType(String type) {
            this.type = type;
        }
    }

}
