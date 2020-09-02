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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class SimpleImdgRestController {
    private final SimpleImdgClient imdgClient;

    private Map<UUID, Node> nodes;

    private Map<UUID, List<Integer>> prevPartitions;

    public SimpleImdgRestController(SimpleImdgClient imdgClient) {
        this.imdgClient = imdgClient;
    }

    @GetMapping("/nodes")
    public Map<UUID, Node> nodes() {
        Map<UUID, List<Integer>> partitions = imdgClient.partitions();

        if (prevPartitions == null) {
            nodes = new LinkedHashMap<>();

            for (Map.Entry<UUID, List<Integer>> e : partitions.entrySet()) {
                UUID id = e.getKey();
                Node node = new Node(id, false);

                for (Integer num : e.getValue()) {
                    node.addPartition(new Partition(num));
                }

                nodes.put(id, node);
            }
        }
        else if (!prevPartitions.keySet().equals(partitions.keySet())) {
            nodes = new LinkedHashMap<>();

            for (Map.Entry<UUID, List<Integer>> e : partitions.entrySet()) {
                UUID id = e.getKey();
                List<Integer> prev = prevPartitions.get(id);
                List<Integer> curr = e.getValue();

                Node node;

                if (prev != null) {
                    node = new Node(id, false);

                    int prevIdx = 0;
                    int currIdx = 0;

                    while (prevIdx < prev.size() || currIdx < curr.size()) {
                        if (prevIdx >= prev.size()) {
                            node.addPartition(new Partition(curr.get(currIdx++)).added());
                        }
                        else if (currIdx >= curr.size()) {
                            node.addPartition(new Partition(prev.get(prevIdx++)).removed());
                        }
                        else {
                            int prevNum = prev.get(prevIdx);
                            int currNum = curr.get(currIdx);

                            if (prevNum < currNum) {
                                node.addPartition(new Partition(prevNum).removed());

                                prevIdx++;
                            }
                            else if (prevNum > currNum) {
                                node.addPartition(new Partition(currNum).added());

                                currIdx++;
                            }
                            else { // prevNum == currNum
                                node.addPartition(new Partition(prevNum));

                                prevIdx++;
                                currIdx++;
                            }
                        }
                    }
                }
                else {
                    node = new Node(id, true);

                    for (Integer num : curr) {
                        node.addPartition(new Partition(num).added());
                    }
                }

                nodes.put(id, node);
            }
        }

        prevPartitions = partitions;

        return nodes;
    }
}
