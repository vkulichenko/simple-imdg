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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.vk.simpleimdg.request.GetRequest;
import org.vk.simpleimdg.request.PartitionsRequest;
import org.vk.simpleimdg.request.PutRequest;
import org.vk.simpleimdg.request.Request;

public class Client {
    private final Communication communication = new Communication();

    private final Discovery discovery = new Discovery(topology -> {});

    private final Mapper mapper = new Mapper();

    public void start() throws Exception {
        discovery.join(null, null);
    }

    public void put(String key, String value) {
        execute(key, new PutRequest(key, value));
    }

    public String get(String key) {
        return execute(key, new GetRequest(key));
    }

    public Map<UUID, Collection<Integer>> partitions() {
        return broadcast(new PartitionsRequest());
    }

    private <R> R execute(String key, Request<R> request) {
        Map<UUID, Integer> topology = discovery.topology();

        UUID id = mapper.map(key.hashCode() % 10, topology);

        return communication.execute(request, topology.get(id));
    }

    private <R> Map<UUID, R> broadcast(Request<R> request) {
        Map<UUID, R> results = new HashMap<>();

        for (Map.Entry<UUID, Integer> e : discovery.topology().entrySet()) {
            results.put(e.getKey(), communication.execute(request, e.getValue()));
        }

        return results;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        client.start();

        for (int i = 0; i < 50; i++)
            client.put("key" + i, "value" + i);

        while (true) {
            System.in.read();

            Map<UUID, Collection<Integer>> partitions = client.partitions();

            for (Map.Entry<UUID, Collection<Integer>> e : partitions.entrySet()) {
                System.out.println(e.getKey());
                System.out.println("    " + e.getValue().stream().map(Object::toString).collect(Collectors.joining(", ")));
            }

            System.out.println();
        }
    }
}
