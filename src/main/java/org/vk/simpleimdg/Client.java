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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private final Discovery discovery = new Discovery();

    private final Mapper mapper = new Mapper();

    public void start() throws Exception {
        discovery.join(null);
    }

    public void put(String key, String value) throws Exception {
        InetSocketAddress address = mapper.map(key.hashCode() % 10, discovery.topology());

        execute(new PutRequest(key, value), address);
    }

    public String get(String key) throws Exception {
        InetSocketAddress address = mapper.map(key.hashCode() % 10, discovery.topology());

        return execute(new GetRequest(key), address);
    }

    private String execute(Command command, InetSocketAddress address) throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(address);

            new ObjectOutputStream(socket.getOutputStream()).writeObject(command);

            return (String)new ObjectInputStream(socket.getInputStream()).readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        client.start();

        client.put("key1", "value1");

        System.out.println(client.get("key1"));
    }
}
