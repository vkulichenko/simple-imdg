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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.vk.simpleimdg.request.RebalanceRequest;

public class Storage {
    private static final int PARTITIONS = 10;

    private final Map<Integer, Map<String, String>> partitions = new ConcurrentHashMap<>();

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

    public Set<String> keySet(int partition) {
        return partitions.get(partition).keySet();
    }

    public Collection<Integer> partitions() {
        List<Integer> list = new ArrayList<>(partitions.keySet());

        Collections.sort(list);

        return list;
    }

    public void onPartitionReceived(int partition, Map<String, String> data) {
        partitions.put(partition, data);
    }

    public void remap(Map<UUID, Integer> topology) {
        for (int i = 0; i < PARTITIONS; i++) {
            UUID id = mapper.map(i, topology);

            if (id.equals(localId)) {
                partitions.putIfAbsent(i, new ConcurrentHashMap<>());
            }
            else {
                Map<String, String> partition = partitions.remove(i);

                if (partition != null)
                    communication.execute(new RebalanceRequest(i, partition), topology.get(id));
            }
        }

        System.out.println("Remapping completed.");
    }

    private Map<String, String> partition(String key) {
        return partitions.get(key.hashCode() % PARTITIONS);
    }
}
