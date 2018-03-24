package jmdb.tutorial.nettyio.http;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;

import java.net.InetSocketAddress;


public class HttpClientInitialiser extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final InetSocketAddress socksProxy;

    public HttpClientInitialiser(SslContext sslCtx) {
        this(sslCtx, null);
    }

    public HttpClientInitialiser(SslContext sslCtx, InetSocketAddress socksProxy) {
        this.sslCtx = sslCtx;
        this.socksProxy = socksProxy;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        if (socksProxy != null) {
            p.addFirst(new Socks5ProxyHandler(socksProxy));
        }

        // Enable HTTPS if necessary.
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast(new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpContents.
        //p.addLast(new HttpObjectAggregator(1048576));

        p.addLast(new HttpClientHandler());

    }
}
