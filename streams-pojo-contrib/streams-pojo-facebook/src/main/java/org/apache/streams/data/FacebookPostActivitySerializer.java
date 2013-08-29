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

import org.apache.streams.pojo.Activity;
import org.apache.streams.pojo.Generator;
import org.apache.streams.pojo.Provider;

/**
 * Serializes activity posts
 */
public class FacebookPostActivitySerializer implements ActivitySerializer {
    @Override
    public String serializationFormat() {
        return "facebook_post_json_v1";
    }

    @Override
    public String serialize(Activity deserialized) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Activity deserialize(String serialized) {
        return null;
    }
}
