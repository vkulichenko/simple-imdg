package org.vk.simpleimdg.request;

import java.io.Serializable;
import org.vk.simpleimdg.Storage;

public interface Request extends Serializable {
    String handle(Storage storage);
}
