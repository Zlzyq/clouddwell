package com.cloud.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "提交订单入参")
public class SubmitOrderDTO implements Serializable {


    @NotNull
    @Schema(description = "购买人用户ID，须与网关解析的登录UID一致")
    private Long buyerUserId;

    @NotBlank
    @Schema(description = "商品编码", example = "SKU-DEMO")
    private String productCode;

    @NotNull
    @Min(value = 1, message = "数量至少为1")
    @Max(value = 10)
    @Schema(description = "数量")
    private Integer quantity;

}
