package com.xlfc.serialization.serializtion;

import com.xlfc.common.annotion.SPI;


@SPI
public interface Serializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
