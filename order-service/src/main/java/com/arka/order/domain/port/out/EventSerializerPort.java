package com.arka.order.domain.port.out;

public interface EventSerializerPort {

    String serialize(Object event);
}
