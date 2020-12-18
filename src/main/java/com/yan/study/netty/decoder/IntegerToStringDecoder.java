package com.yan.study.netty.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

/**
 * 把 Integer 参数转换为 String
 * 更复杂的例子可以研究 HttpObjectAggregator, 它扩展了 MessageToMessageDecoder<HttpObject>
 *
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }

}
