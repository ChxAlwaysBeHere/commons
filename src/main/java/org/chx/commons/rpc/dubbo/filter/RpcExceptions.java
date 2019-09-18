package org.chx.commons.rpc.dubbo.filter;

/**
 * rpc exception enums
 *
 * @author chenxi
 * @date 2019-09-18
 */
public enum RpcExceptions {

    // example
    SERVICE1_EXCEPTION1() {
        @Override
        public boolean matches(Throwable throwable) {
            if (!RuntimeException.class.isInstance(throwable)) {
                return false;
            }

            // extra judge
            return true;
        }

        @Override
        public String getMessage(Throwable throwable) {
            // custom error message
            return throwable.getMessage();
        }

    };

    public abstract boolean matches(Throwable throwable);

    public abstract String getMessage(Throwable throwable);

    public static RpcExceptions findMatch(Throwable throwable) {
        for (RpcExceptions exception : RpcExceptions.values()) {
            if (exception.matches(throwable)) {
                return exception;
            }
        }
        return null;
    }

}
