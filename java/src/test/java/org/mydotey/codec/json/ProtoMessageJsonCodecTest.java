package org.mydotey.codec.json;

import org.mydotey.codec.Codec;
import org.mydotey.codec.protobuf.ProtoMessageCodecTest;

/**
 * @author koqizhao
 *
 * Nov 8, 2018
 */
public class ProtoMessageJsonCodecTest extends ProtoMessageCodecTest {

    @Override
    protected Codec getCodec() {
        return ProtoMessageJsonCodec.DEFAULT;
    }

}
