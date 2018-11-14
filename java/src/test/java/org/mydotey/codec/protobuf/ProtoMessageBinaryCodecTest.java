package org.mydotey.codec.protobuf;

import org.mydotey.codec.Codec;

/**
 * @author koqizhao
 *
 * Nov 8, 2018
 */
public class ProtoMessageBinaryCodecTest extends ProtoMessageCodecTest {

    @Override
    protected Codec getCodec() {
        return ProtoMessageCodec.DEFAULT;
    }

}
