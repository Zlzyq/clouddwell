package com.cloud.business.service.impl

import com.cloud.business.converter.OrderConverter
import com.cloud.business.dto.SubmitOrderDTO
import com.cloud.business.feign.dto.ProductRemoteVO
import com.cloud.business.feign.dto.UserRemoteVO
import com.cloud.business.handler.OrderHandler
import com.cloud.business.persistence.OrderPersistence

import com.cloud.business.vo.OrderIntegrationResult
import com.cloud.business.vo.OrderVO
import com.cloud.common.context.UserContext
import com.cloud.common.exception.GlobalException
import org.slf4j.MDC
import spock.lang.Specification

class OrderServiceImplSpec extends Specification {

    def handler = Mock(OrderHandler)
    def converter = Stub(OrderConverter)
    def persistence = Mock(OrderPersistence)
    def async = Mock(OrderAsyncNotifyService)

    def service = new OrderServiceImpl(handler, converter, persistence, async)

    def setup() {
        UserContext.clear()
        MDC.clear()
    }

    def cleanup() {
        UserContext.clear()
        MDC.clear()
    }

    def "登录用户必须与买家一致否则拒绝"() {
        given:
        UserContext.setUid(99L)
        def dto = new SubmitOrderDTO()
        dto.buyerUserId = 1L
        dto.productCode = "SKU-DEMO"
        dto.quantity = 1

        when:
        service.submitOrder(dto)

        then:
        thrown(GlobalException)
        0 * handler.execute(_)
    }

    def "成功下单并异步通知一次"() {
        given:
        UserContext.setUid(1L)
        MDC.put("traceId", "trace-demo")

        def dto = new SubmitOrderDTO()
        dto.buyerUserId = 1L
        dto.productCode = "SKU-DEMO"
        dto.quantity = 3

        def bundle = new OrderIntegrationResult()
        bundle.user = new UserRemoteVO(1L, "张三")
        def p = new ProductRemoteVO()
        p.productCode = "SKU-DEMO"
        p.unitPrice = new BigDecimal("2.00")
        bundle.product = p

        def entityStub = new com.cloud.business.entity.OrderEntity()

        handler.execute(dto) >> bundle
        converter.calculateTotal(p, 3) >> new BigDecimal("6.00")
        converter.toNewEntity(_, dto, p, _, new BigDecimal("6.00")) >> entityStub
        persistence.insert(entityStub) >> {
            entityStub.orderId = 88001L
        }
        def vo = new OrderVO()
        vo.orderNo = "ORDX"
        converter.convert(entityStub) >> vo

        when:
        def out = service.submitOrder(dto)

        then:
        out.orderNo == "ORDX"
        entityStub.orderId == 88001L
        1 * async.notifyOrderCreated(_, 1L, "trace-demo")
    }
}
