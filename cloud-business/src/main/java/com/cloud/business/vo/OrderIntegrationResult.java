package com.cloud.business.vo;

import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.business.feign.dto.UserRemoteVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Handler 编排后的远程快照（用户 + 商品）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderIntegrationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserRemoteVO user;
    private ProductRemoteVO product;


}
