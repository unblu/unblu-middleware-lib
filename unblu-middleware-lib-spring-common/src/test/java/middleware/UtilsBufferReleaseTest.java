package middleware;

import com.unblu.middleware.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilsBufferReleaseTest {

    private final NettyDataBufferFactory factory = new NettyDataBufferFactory(UnpooledByteBufAllocator.DEFAULT);

    @Test
    void joinedNettyBuffersAreReleasedAfterBodyIsCopied() {
        var first = nettyBuffer("{\"conversation\":");
        var second = nettyBuffer("\"42\"}");
        ByteBuf firstNative = first.getNativeBuffer();
        ByteBuf secondNative = second.getNativeBuffer();

        var rawRequest = Utils.toLibHttpRequest(Flux.just(first, second), new HttpHeaders()).block();

        assertNotNull(rawRequest);
        assertEquals("{\"conversation\":\"42\"}", new String(rawRequest.body(), StandardCharsets.UTF_8));
        assertEquals(0, firstNative.refCnt(), "first ByteBuf must be fully released after the body copy");
        assertEquals(0, secondNative.refCnt(), "second ByteBuf must be fully released after the body copy");
    }

    @Test
    void emptyBodyYieldsEmptyRawRequest() {
        var rawRequest = Utils.toLibHttpRequest(Flux.empty(), new HttpHeaders()).block();

        assertNotNull(rawRequest);
        assertEquals(0, rawRequest.body().length);
    }

    private NettyDataBuffer nettyBuffer(String content) {
        DataBuffer buffer = factory.allocateBuffer(64);
        buffer.write(content.getBytes(StandardCharsets.UTF_8));
        return (NettyDataBuffer) buffer;
    }
}
