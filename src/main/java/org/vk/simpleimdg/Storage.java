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

package org.vk.simpleimdg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.vk.simpleimdg.command.RebalanceRequest;

public class Storage {
    private final Map<Integer, Map<String, String>> partitions = new HashMap<>();

    private final Mapper mapper = new Mapper();

    private final UUID localId;

    private final Communication communication;

    public Storage(UUID localId, Communication communication) {
        this.localId = localId;
        this.communication = communication;
    }

    public void put(String key, String value) {
        partition(key).put(key, value);
    }

    public String get(String key) {
        return partition(key).get(key);
    }

    public void onPartitionReceived(int partition, Map<String, String> data) {
        partitions.put(partition, data);
    }

    private Map<String, String> partition(String key) {
        return partitions.get(key.hashCode() % 10);
    }

    public void remap(Map<UUID, Integer> topology) {
        for (int i = 0; i < 10; i++) {
            UUID id = mapper.map(i, topology);

            if (id.equals(localId)) {
                if (!partitions.containsKey(i))
                    partitions.put(i, new ConcurrentHashMap<>());
            }
            else {
                Map<String, String> partition = partitions.remove(i);

                communication.execute(new RebalanceRequest(i, partition), topology.get(id));
            }
        }
    }
}
