package org.mydotey.codec.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.mydotey.codec.CodecException;
import org.mydotey.codec.probotuf.ProtobufCodec;

import com.google.protobuf.DiscardUnknownFieldsParser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtoMessageCodec extends ProtobufCodec {

    public static final ProtoMessageCodec DEFAULT = new ProtoMessageCodec();

    private ConcurrentHashMap<Class, Parser> _parserMap = new ConcurrentHashMap<>();
    private Function<Class, Parser> _parserCreator = c -> {
        try {
            Method method = c.getMethod("parser");
            Parser parser = (Parser) method.invoke(null);
            return DiscardUnknownFieldsParser.wrap(parser);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "clazz must be a Google PB Message & has an auto generated public static method parser()", e);
        }
    };

    @Override
    protected byte[] doEncode(Object obj) {
        Message message = (Message) obj;
        return message.toByteArray();
    }

    @Override
    protected <T> T doDecode(byte[] is, Class<T> clazz) {
        Parser<T> parser = getParser(clazz);
        try {
            return (T) parser.parseFrom(is);
        } catch (InvalidProtocolBufferException e) {
            throw new CodecException(e);
        }
    }

    @Override
    protected void doEncode(OutputStream os, Object obj) {
        try {
            Message message = (Message) obj;
            message.writeTo(os);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Override
    protected <T> T doDecode(InputStream is, Class<T> clazz) {
        Parser<T> parser = getParser(clazz);
        try {
            return (T) parser.parseFrom(is);
        } catch (InvalidProtocolBufferException e) {
            throw new CodecException(e);
        }
    }

    protected <T> Parser<T> getParser(Class<T> clazz) {
        return _parserMap.computeIfAbsent(clazz, _parserCreator);
    }

}
