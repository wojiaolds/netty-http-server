package com.farsunset.httpserver.configuration;

import com.farsunset.httpserver.netty.iohandler.InterceptorHandler;
import com.farsunset.httpserver.netty.iohandler.FilterLogginglHandler;
import com.farsunset.httpserver.netty.iohandler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 实现了ApplicationListener用于监听ApplicationStartedEvent事件，
 * 应用加载完，会发布ApplicationStartedEvent事件
 *
 */
//@Configuration
@Component
public class NettyHttpServer implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServer.class);

    @Value("${server.port}")
    private int port;

    @Autowired
    private InterceptorHandler interceptorHandler;
    
    @Autowired
    private HttpServerHandler httpServerHandler;

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
    
        LOGGER.info ("---NettyHttpServer(ApplicationStartedEvent) start---");
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childOption(NioChannelOption.TCP_NODELAY, true)
				.childOption(NioChannelOption.SO_REUSEADDR,true)
				.childOption(NioChannelOption.SO_KEEPALIVE,false)
				.childOption(NioChannelOption.SO_RCVBUF, 2048)
				.childOption(NioChannelOption.SO_SNDBUF, 2048)
				.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ch.pipeline().addLast("codec", new HttpServerCodec()); //in out
					ch.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));//in
					ch.pipeline().addLast("logging", new FilterLogginglHandler());//in out
					ch.pipeline().addLast("interceptor", interceptorHandler); //in
					ch.pipeline().addLast("bizHandler", httpServerHandler); //in
					/**
					 * 执行顺序 codec->aggregator->logging->interceptor->bizHandler
					 *         ->logging->codec
					 *
					 *  在使用Handler的过程中，需要注意：
					 
					 　	1、ChannelInboundHandler之间的传递，通过调用 ctx.fireChannelRead(msg) 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler
					 
					 　　2、ctx.write()方法执行后，需要调用flush()方法才能令它立即执行。
					 
					 　　3、ChannelOutboundHandler 在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler
					 
					 　　　（流水线pipeline中outhander不能放到最后，否则不生效）
					 
					 　　4、Handler的消费处理放在最后一个处理。
					 */
					
            }
        })
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).syncUninterruptibly().addListener(future -> {
            String logBanner = "\n\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "*                   Netty Http Server started on port {}.                         *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
            LOGGER.info(logBanner, port);
        });
        channelFuture.channel().closeFuture().addListener(future -> {
            LOGGER.info("Netty Http Server Start Shutdown ............");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });
    }

}