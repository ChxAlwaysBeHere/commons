package org.chx.commons.rpc.dubbo.protocol;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcException;

import java.util.Set;

/**
 * pre/post handle when protocol exports/refers
 *
 * @author chenxi
 * @date 2019-09-30
 * @see com.alibaba.dubbo.rpc.Protocol
 */
public class ProtocolInterceptorWrapper implements Protocol {

    private Protocol protocol;

    public ProtocolInterceptorWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == null");
        }
        this.protocol = protocol;
    }

    @Override
    public int getDefaultPort() {
        return protocol.getDefaultPort();
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        ExtensionLoader<ProtocolInterceptor> loader = ExtensionLoader.getExtensionLoader(ProtocolInterceptor.class);
        Set<String> extensionNames = loader.getSupportedExtensions();

        if (!extensionNames.isEmpty()) {
            extensionNames.forEach(extensionName -> loader.getExtension(extensionName).beforeExport(protocol, invoker));
        }

        Exporter<T> exporter = protocol.export(invoker);

        if (!extensionNames.isEmpty()) {
            extensionNames.forEach(extensionName -> loader.getExtension(extensionName).afterExport(protocol, invoker, exporter));
        }

        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        ExtensionLoader<ProtocolInterceptor> loader = ExtensionLoader.getExtensionLoader(ProtocolInterceptor.class);
        Set<String> extensionNames = loader.getSupportedExtensions();

        if (!extensionNames.isEmpty()) {
            extensionNames.forEach(extensionName -> loader.getExtension(extensionName).beforeRefer(protocol, type, url));
        }

        Invoker<T> invoker = protocol.refer(type, url);


        if (!extensionNames.isEmpty()) {
            extensionNames.forEach(extensionName -> loader.getExtension(extensionName).afterRefer(protocol, type, url, invoker));
        }

        return invoker;
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }

}
