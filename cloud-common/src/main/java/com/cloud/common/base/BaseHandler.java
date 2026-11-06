package com.cloud.common.base;

/**
 * Handler 抽象基类：统一收口远程调用 / 异步 / 第三方网关。
 *
 * @param <Req> 调用入参
 * @param <Resp> 调用出参
 */
public abstract class BaseHandler<Req, Resp> {

    /**
     * 同步编排入口骨架，子类实现具体链路。
     */
    public abstract Resp execute(Req request);

    /**
     * Feign / 异步任务前后的模板扩展点，可选覆盖。
     */
    protected void beforeRemote(Req request) {
    }

    protected void afterRemote(Req request, Resp response) {
    }
}
