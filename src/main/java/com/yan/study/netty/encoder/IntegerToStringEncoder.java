package com.yan.study.netty.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 专业用法请看 ProtobufEncoder, 它处理了由 Google 的 Protocol Buffers 规范所定义的数据格式
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {

    }

}
