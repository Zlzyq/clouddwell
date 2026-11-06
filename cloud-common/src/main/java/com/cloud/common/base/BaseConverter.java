package com.cloud.common.base;

/**
 * Converter 抽象基类：手写 set/get 完成对象转换。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public abstract class BaseConverter<S, T> {

    /**
     * 单对象转换。
     */
    public abstract T convert(S source);

    /**
     * List 分页等场景的模板方法钩子；默认返回 null，子类按需实现。
     */
    public T convertNullable(S source) {
        if (source == null) {
            return null;
        }
        return convert(source);
    }
}
