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

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;

public class Discovery {
    private final UUID id = UUID.randomUUID();

    private final Map<UUID, Integer> topology = new LinkedHashMap<>();

    public void join(Integer localPort) throws Exception {
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();

        synchronized (topology) {
            for (KeyValue kv : client.getKVClient().get(bs("nodes/"), GetOption.newBuilder().withPrefix(bs("nodes/")).build()).get().getKvs()) {
                UUID id = UUID.fromString(kv.getKey().toString(StandardCharsets.UTF_8).substring("nodes/".length()));
                int port = Integer.parseInt(kv.getValue().toString(StandardCharsets.UTF_8));

                topology.put(id, port);
            }
        }

        client.getWatchClient().watch(bs("nodes/"), WatchOption.newBuilder().withPrefix(bs("nodes/")).build(), res -> {
            synchronized (topology) {
                for (WatchEvent event : res.getEvents()) {
                    if (event.getEventType() == WatchEvent.EventType.PUT) {
                        UUID id = UUID.fromString(event.getKeyValue().getKey().toString(StandardCharsets.UTF_8).substring("nodes/".length()));
                        int port = Integer.parseInt(event.getKeyValue().getValue().toString(StandardCharsets.UTF_8));

                        topology.put(id, port);
                    }
                }

                System.out.println("Topology:");

                for (Map.Entry<UUID, Integer> entry : topology.entrySet()) {
                    System.out.println("   " + entry.getKey() + " -> 127.0.0.1:" + entry.getValue());
                }
            }
        });

        if (localPort != null)
            client.getKVClient().put(bs("nodes/" + id), bs(String.valueOf(localPort))).get();
    }

    public Map<UUID, Integer> topology() {
        synchronized (topology) {
            return new LinkedHashMap<>(topology);
        }
    }

    private static ByteSequence bs(String str) {
        return ByteSequence.from(str, StandardCharsets.UTF_8);
    }
}
