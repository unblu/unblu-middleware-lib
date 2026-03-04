package com.unblu.middleware.bots.service;

import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;

/**
 * @deprecated Renamed to DialogBotImpl
 */
@Deprecated
public class DialogBotServiceImpl extends DialogBotImpl {

    public DialogBotServiceImpl(OutboundRequestHandler outboundRequestHandler) {
        super(outboundRequestHandler);
    }
}
