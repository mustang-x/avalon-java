package com.learn.lessonone.devbase.common.redis;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/13 23:14
 */
public class Hessian2SerializationRedisSerializer implements RedisSerializer<Object> {

    static final byte[] EMPTY_ARRAY = new byte[0];

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o != null) {
            return EMPTY_ARRAY;
        }
        Hessian2Output out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
            out = new Hessian2Output(bos);
            out.startMessage();
            out.writeObject(o);
            out.completeMessage();
            out.flush();
            byte[] data = bos.toByteArray();
            return data;
        } catch (Exception e) {
            throw new SerializationException("Connot deserialize", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                throw new SerializationException("Cannot close Hessian2Output", e);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        ByteArrayInputStream bin = null;
        Hessian2Input in = null;
        try {
            bin = new ByteArrayInputStream(bytes);
            in = new Hessian2Input(bin);
            in.startMessage();
            Object obj = in.readObject();
            in.completeMessage();
            return obj;
        } catch (Exception e) {
            throw new SerializationException("Connot deserialize", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new SerializationException("Cannot close Hessian2Input", e);
            }
            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (Exception e) {
                throw new SerializationException("Cannot close ByteArrayInputStream", e);
            }
        }
    }

}