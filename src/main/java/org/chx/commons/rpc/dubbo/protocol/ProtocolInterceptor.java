package org.chx.commons.rpc.dubbo.protocol;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.SPI;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author chenxi
 * @date 2019-10-04
 */
@SPI
public interface ProtocolInterceptor {

    /**
     * before protocol exports service
     *
     * @param protocol
     * @param invoker
     * @param <T>
     */
    <T> void beforeExport(Protocol protocol, Invoker<T> invoker);

    /**
     * after protocol exports service
     *
     * @param protocol
     * @param invoker
     * @param exporter
     * @param <T>
     * @return
     * @throws RpcException
     */
    <T> void afterExport(Protocol protocol, Invoker<T> invoker, Exporter<T> exporter) throws RpcException;

    /**
     * before protocol refers service
     *
     * @param protocol
     * @param type
     * @param url
     * @param <T>
     */
    <T> void beforeRefer(Protocol protocol, Class<T> type, URL url);

    /**
     * after protocol refers service
     *
     * @param protocol
     * @param type
     * @param url
     * @param invoker
     * @param <T>
     * @return
     */
    <T> void afterRefer(Protocol protocol, Class<T> type, URL url, Invoker<T> invoker);

}
