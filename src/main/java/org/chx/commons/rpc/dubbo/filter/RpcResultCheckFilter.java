package org.chx.commons.rpc.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.chx.commons.serialize.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

        Throwable throwable = null;
        Result result = null;
        long s = System.currentTimeMillis();
        try {
            result = invoker.invoke(invocation);
            if (result.hasException()) {
                throwable = result.getException();
            }
        } catch (RpcException e) {
            throwable = e;
        }

        if (Objects.nonNull(throwable)) {
            log.error("refer={}||params={}", refer, JsonHelper.toJson(invocation.getArguments()), throwable);

            /* 抛出自定义异常 throw new SystemException(StatusCode.RPC_REQUEST_ERROR, result.getException().getMessage()); */
            return new RpcResult(new RuntimeException(throwable));
        } else {
            log.warn("refer={}||params={}||result={}||proc_time={}", refer, JsonHelper.toJson(invocation.getArguments()), JsonHelper.toJson(result.getValue()), System.currentTimeMillis() - s);
            return result;
        }
    }

}
