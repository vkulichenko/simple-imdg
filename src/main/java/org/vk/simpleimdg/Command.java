package org.vk.simpleimdg;

import java.io.Serializable;

public interface Command extends Serializable {
    String handle(Storage storage);
}
