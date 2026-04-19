package com.arka.order.infrastructure.adapter.out.serialization;

import org.springframework.stereotype.Component;

import com.arka.order.domain.port.out.EventSerializerPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JacksonEventSerializerAdapter implements EventSerializerPort {

    private final ObjectMapper objectMapper;

    @Override
    public String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize event", e);
        }
    }
}
