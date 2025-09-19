package com.unblu.middleware.outboundrequests.entity;

public record OutboundRequestType(String type) {
    public static OutboundRequestType outboundRequestType(String type) {
        return new OutboundRequestType(type);
    }
}
