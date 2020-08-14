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

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class Mapper {
    public InetSocketAddress map(int partition, Map<UUID, Integer> topology) {
        long maxHash = Long.MIN_VALUE;
        int port = -1;

        for (Map.Entry<UUID, Integer> entry : topology.entrySet()) {
            long hash = Math.abs(murmur3(partition, entry.getKey()));

            if (hash > maxHash) {
                maxHash = hash;
                port = entry.getValue();
            }
        }

        return new InetSocketAddress("127.0.0.1", port);
    }

    private long murmur3(int partition, UUID nodeId) {
        Hasher hasher = Hashing.murmur3_128().newHasher();

        hasher.putInt(partition);
        hasher.putLong(nodeId.getMostSignificantBits());
        hasher.putLong(nodeId.getLeastSignificantBits());

        return hasher.hash().asLong();
    }
}
