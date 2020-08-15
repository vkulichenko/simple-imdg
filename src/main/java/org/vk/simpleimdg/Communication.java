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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.vk.simpleimdg.command.Command;

public class Communication {
    private ServerSocket serverSocket;

    public int init() {
        int port = 4000;

        while (true) {
            try {
                serverSocket = new ServerSocket(port);

                break;
            }
            catch (IOException e) {
                port++;
            }
        }

        return port;
    }

    public void listen(Storage storage) throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                try {
                    ObjectInput in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutput out = new ObjectOutputStream(socket.getOutputStream());

                    while (true) {
                        Command request = (Command)in.readObject();

                        out.writeObject(request.handle(storage));
                    }
                }
                catch (EOFException ignored) {}
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public String execute(Command command, int port) {
        try (Socket socket = new Socket("127.0.0.1", port)) {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(command);

            return (String)new ObjectInputStream(socket.getInputStream()).readObject();
        }
        catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
