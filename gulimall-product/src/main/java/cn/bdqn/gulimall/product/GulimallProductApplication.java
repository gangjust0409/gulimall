package cn.bdqn.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 逻辑删除
 * 	1、
 */
@EnableFeignClients(basePackages = "cn.bdqn.gulimall.product.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallProductApplication.class, args);
	}

}
