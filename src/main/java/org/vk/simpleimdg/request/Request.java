package org.vk.simpleimdg.request;

import java.io.Serializable;
import org.vk.simpleimdg.Storage;

public interface Request<R> extends Serializable {
    R handle(Storage storage);
}
