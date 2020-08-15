package org.vk.simpleimdg.command;

import java.io.Serializable;
import org.vk.simpleimdg.Storage;

public interface Command extends Serializable {
    String handle(Storage storage);
}
