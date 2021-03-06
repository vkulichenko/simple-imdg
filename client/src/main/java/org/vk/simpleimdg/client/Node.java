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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Node {
    private final UUID id;

    private final boolean isNew;

    private final List<Partition> partitions = new ArrayList<>();

    public Node(UUID id, boolean isNew) {
        this.id = id;
        this.isNew = isNew;
    }

    public void addPartition(Partition partition) {
        partitions.add(partition);
    }

    public UUID getId() {
        return id;
    }

    public boolean isNew() {
        return isNew;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }
}
