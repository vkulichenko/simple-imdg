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

package org.vk.simpleimdg.request;

import java.util.Map;
import org.vk.simpleimdg.Storage;

public class RebalanceRequest implements Request<Void> {
    private final int partition;

    private final Map<String, String> data;

    public RebalanceRequest(int partition, Map<String, String> data) {
        this.partition = partition;
        this.data = data;
    }

    @Override
    public Void handle(Storage storage) {
        storage.onPartitionReceived(partition, data);

        return null;
    }
}
