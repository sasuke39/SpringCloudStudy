package com.geekbang.coupon.customer.loadbalance;

import com.alibaba.cloud.nacos.NacosServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.geekbang.coupon.customer.constant.Constant.TRAFFIC_VERSION;

// 可以将这个负载均衡策略单独拎出来，作为一个公共组件提供服务
@Slf4j
public class ClusterFirstRule implements ReactorServiceInstanceLoadBalancer {

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;
    private Environment environment;

    // 定义一个轮询策略的种子
    final AtomicInteger position;

    public ClusterFirstRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                      String serviceId,Environment environment) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        position = new AtomicInteger(new Random().nextInt(1000));
        this.environment= environment;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, request));
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            List<ServiceInstance> serviceInstances,
            Request request) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, request);

        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        // 注册中心无可用实例 抛出异常
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("No instance available {}", serviceId);
            return new EmptyResponse();
        }
        return getSameClusterService(request, instances);
    }

    private Response<ServiceInstance> getSameClusterService(Request request, List<ServiceInstance> instances) {
        String clusterName = environment.resolvePlaceholders("${spring.cloud.nacos.discovery.cluster-name:}");
        List<ServiceInstance> instanceList = instances.stream().filter(v -> {
            Map<String, String> metadata = v.getMetadata();
            String serviceClusterName = metadata.get("nacos.cluster");
            return clusterName.equals(serviceClusterName);
        }).collect(Collectors.toList());
        //有同集群下服务 RoundRobin算法挑选
        if (CollectionUtils.isNotEmpty(instanceList)){
            return getRoundRobinInstance(instanceList);
        }else {
            return getRoundRobinInstance(instances);
        }
    }

    // 使用轮训机制获取节点
    private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
        // 如果没有可用节点，则返回空
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + serviceId);
            return new EmptyResponse();
        }

        int pos = Math.abs(this.position.incrementAndGet());
        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }

}
