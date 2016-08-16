package code.google.nfs.rpc.netty4.server;
/**
 * nfs-rpc
 *   Apache License
 *
 *   http://code.google.com/p/nfs-rpc (c) 2011
 */

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty4 Server Handler
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ServerHandler2 extends ChannelInboundHandlerAdapter {

    private static final Log LOGGER = LogFactory.getLog(Netty4ServerHandler2.class);

    public Netty4ServerHandler2() {
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {
        if (!(e.getCause() instanceof IOException)) {
            // only log
            LOGGER.error("catch some exception not IOException", e.getCause());
        }
    }

    @SuppressWarnings("rawtypes")
    public void channelRead(ChannelHandlerContext ctx, Object message)
            throws Exception {
        if (message instanceof RequestWrapper) {
            run(ctx, message);
        } else if (message instanceof List) {
            List messages = (List) message;
            for (Object messageObject : messages) {
                run(ctx, messageObject);
            }
        } else {
            LOGGER.error("receive message error,only support RequestWrapper || List");
            throw new Exception("receive message error,only support RequestWrapper || List");
        }
    }

    private static final ChannelFutureListener listener = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOGGER.error("server write response error");
            }
        }
    };

    private void run(ChannelHandlerContext ctx, Object message) {
        RequestWrapper request = (RequestWrapper) message;
        long beginTime = System.currentTimeMillis();
        ResponseWrapper responseWrapper = ProtocolFactory.getServerHandler(request.getProtocolType()).handleRequest(request);
        final int id = request.getId();
        // already timeout,so not return
        if ((System.currentTimeMillis() - beginTime) >= request.getTimeout()) {
            LOGGER.warn("timeout,so give up send response to client,requestId is:"
                    + id
                    + ",client is:"
                    + ctx.channel().remoteAddress() + ",consumetime is:" + (System.currentTimeMillis() - beginTime) + ",timeout is:" + request.getTimeout());
            return;
        }
        ChannelFuture wf = ctx.writeAndFlush(responseWrapper);
        wf.addListener(listener);
    }

}
