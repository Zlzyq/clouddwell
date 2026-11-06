package com.cloud.common.base;

import com.cloud.common.domain.PageResult;
import com.cloud.common.domain.Result;

/**
 * Controller 抽象基类：只做统一响应封装，不写业务逻辑。
 *
 * @param <D> 入参模型（DTO）
 * @param <V> 出参模型（VO）
 */
public abstract class BaseController<D, V> {

    protected <T> Result<T> ok() {
        return Result.ok();
    }

    protected <T> Result<T> ok(T data) {
        return Result.ok(data);
    }

    protected <T> Result<PageResult<T>> okPage(PageResult<T> page) {
        return Result.ok(page);
    }
}
