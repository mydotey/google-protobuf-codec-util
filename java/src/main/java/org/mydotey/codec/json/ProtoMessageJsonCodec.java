package org.mydotey.codec.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mydotey.codec.CodecException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import com.google.protobuf.util.JsonFormat.Printer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtoMessageJsonCodec extends JsonCodec {

    public static final ProtoMessageJsonCodec DEFAULT = new ProtoMessageJsonCodec();

    private Parser _parser;
    private Printer _printer;

    public ProtoMessageJsonCodec() {
        _parser = newParser();
        _printer = newPrinter();
    }

    @Override
    protected byte[] doEncode(Object obj) {
        try {
            Message message = (Message) obj;
            String json = _printer.print(message);
            return json.getBytes(getCharset());
        } catch (InvalidProtocolBufferException | UnsupportedEncodingException e) {
            throw new CodecException(e);
        }
    }

    @Override
    protected <T> T doDecode(byte[] is, Class<T> clazz) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(is)) {
            return decode(stream, clazz);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Override
    protected void doEncode(OutputStream os, Object obj) {
        try {
            byte[] bytes = encode(obj);
            os.write(bytes);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Override
    protected <T> T doDecode(InputStream is, Class<T> clazz) {
        Message.Builder builder = newBuilder(clazz);
        try {
            _parser.merge(new InputStreamReader(is, getCharset()), builder);
            return (T) builder.build();
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    protected Message.Builder newBuilder(Class clazz) {
        try {
            Method method = clazz.getMethod("newBuilder");
            return (Message.Builder) method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "clazz must be a Google Proto Message with an auto generated public static method newBuilder", e);
        }
    }

    protected String getCharset() {
        return "UTF-8";
    }

    protected Parser newParser() {
        return JsonFormat.parser().ignoringUnknownFields();
    }

    protected Printer newPrinter() {
        return JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames()
                .omittingInsignificantWhitespace();
    }

}
