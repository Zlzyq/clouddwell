package com.cloud.business.controller;

import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.service.OrderService;
import com.cloud.business.service.impl.OrderAsyncNotifyService;
import com.cloud.business.vo.OrderVO;
import com.cloud.common.constant.MdcKeys;
import com.cloud.common.context.UserContext;
import com.cloud.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderAsyncNotifyService orderAsyncNotifyService;

    @PostMapping("/submit")
    @Operation(summary = "提交订单")
    public Result<OrderVO> submit(@Valid @RequestBody SubmitOrderDTO dto) {
        return Result.ok(orderService.submitOrder(dto));
    }

    @PostMapping("/pay/{orderNo}")
    @Operation(summary = "模拟支付")
    public Result<Void> pay(@PathVariable String orderNo) {
        Long uid = UserContext.getUid();
        if (uid == null) return Result.fail(401, "请先登录");
        String traceId = MDC.get(MdcKeys.TRACE_ID);
        orderAsyncNotifyService.notifyOrderCreated(orderNo, uid, traceId);
        return Result.ok();
    }

    @PostMapping("/cancel/{orderNo}")
    @Operation(summary = "取消订单")
    public Result<Void> cancel(@PathVariable String orderNo) {
        Long uid = UserContext.getUid();
        if (uid == null) return Result.fail(401, "请先登录");
        orderService.cancelOrder(uid, orderNo);
        return Result.ok();
    }

    @PostMapping("/refund/{orderNo}")
    @Operation(summary = "退款")
    public Result<Void> refund(@PathVariable String orderNo) {
        Long uid = UserContext.getUid();
        if (uid == null) return Result.fail(401, "请先登录");
        orderService.refund(uid, orderNo);
        return Result.ok();
    }
}
