package org.chx.commons.rpc.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.chx.commons.serialize.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dubbo consumer rpc exception filter
 *
 * @author chenxi
 * @date 2019-09-03
 */
@Activate(group = Constants.CONSUMER, order = 1)
public class RpcResultCheckFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(RpcResultCheckFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String refer = invoker.getInterface().getName() + "." + invocation.getMethodName();

        long s = System.currentTimeMillis();
        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                log.error("refer={}||params={}", refer, JsonHelper.toJson(invocation.getArguments()), result.getException());
                /* 抛出自定义异常 throw new SystemException(StatusCode.RPC_REQUEST_ERROR, result.getException().getMessage()); */
                throw new RuntimeException(result.getException());
            }

            log.warn("refer={}||params={}||result={}||proc_time={}", refer, JsonHelper.toJson(invocation.getArguments()), JsonHelper.toJson(result.getValue()), System.currentTimeMillis() - s);
            return result;
        } catch (RpcException e) {
            log.error("refer={}||params={}", refer, JsonHelper.toJson(invocation.getArguments()), e);
            /* 抛出自定义异常 throw new SystemException(StatusCode.RPC_REQUEST_ERROR, e.getMessage());*/
            throw new RuntimeException(e);
        }
    }

}
