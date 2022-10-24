package cn.bdqn.gulimall.gateway.config;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySentinelConfig {

    public GatewaySentinelConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                final R r = R.error(BizExceptionCode.NO_MANY_REQUEST.getCode(), BizExceptionCode.NO_MANY_REQUEST.getMsg());
                final String jsonString = JSON.toJSONString(r);
                final Mono<ServerResponse> responseMono = ServerResponse.ok().body(Mono.just(jsonString), String.class);
                return responseMono;
            }
        });
    }

}
