package com.cloud.business.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderDTO {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "购买数量不能为空")
    private Integer quantity;
}
