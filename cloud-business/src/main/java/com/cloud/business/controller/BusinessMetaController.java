package com.cloud.business.controller;

import com.cloud.common.properties.DemoDataProperties;
import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/business")
@Tag(name = "业务元数据")
public class BusinessMetaController extends BaseController<Object, Object> {

    private final DemoDataProperties demoDataProperties;

    public BusinessMetaController(DemoDataProperties demoDataProperties) {
        this.demoDataProperties = demoDataProperties;
    }

    @GetMapping("/storage-path")
    @Operation(summary = "JSON 数据存储根目录")
    public Result<String> storagePath() {
        return ok(Path.of(demoDataProperties.getDataDir()).toAbsolutePath().normalize().toString());
    }
}
