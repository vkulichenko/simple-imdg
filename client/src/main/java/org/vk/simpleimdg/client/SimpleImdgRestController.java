/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vk.simpleimdg.client;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class SimpleImdgRestController {
    private final SimpleImdgClient imdgClient;

    public SimpleImdgRestController(SimpleImdgClient imdgClient) {
        this.imdgClient = imdgClient;
    }

    @GetMapping("/nodes")
    public List<Node> nodes() {
        return Arrays.asList(
            new Node(UUID.randomUUID(), Arrays.asList(1, 2, 3)),
            new Node(UUID.randomUUID(), Arrays.asList(4, 5, 6))
        );

//        return imdgClient
//            .partitions()
//            .entrySet()
//            .stream()
//            .map(e -> new Node(e.getKey(), e.getValue()))
//            .collect(Collectors.toList());
    }
}