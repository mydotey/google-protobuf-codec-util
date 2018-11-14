package org.mydotey.codec.protobuf;

import org.mydotey.codec.TestMessage;

/**
 * @author koqizhao
 *
 * Nov 8, 2018
 */
public abstract class ProtoMessageCodecTest extends ProtobufCodecTest {

    protected Object newObject() {
        return TestMessage.newBuilder().setId(1).setData("hello").build();
    }

}
