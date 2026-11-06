package com.cloud.booking.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.booking.converter.BookingConverter;
import com.cloud.booking.dto.BookingSaveDTO;
import com.cloud.booking.entity.BookingEntity;
import com.cloud.booking.service.BookingService;
import com.cloud.booking.vo.BookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "预订管理")
@RestController
@RequestMapping("/api/bookings")
public class BookingController extends BaseController<BookingSaveDTO, BookingVO> {
    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    public BookingController(BookingService bookingService, BookingConverter bookingConverter) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
    }
    @Operation(summary = "预订列表")
    @GetMapping
    public Result<List<BookingVO>> list() { return ok(bookingConverter.toVO(bookingService.list())); }
    @Operation(summary = "预订详情")
    @GetMapping("/{id}")
    public Result<BookingVO> get(@PathVariable Long id) { return ok(bookingConverter.toVO(bookingService.getById(id))); }
    @Operation(summary = "新增预订")
    @PostMapping
    public Result<BookingVO> save(@RequestBody BookingSaveDTO dto) {
        BookingEntity entity = bookingConverter.toEntity(dto);
        return ok(bookingConverter.toVO(bookingService.save(entity)));
    }
    @Operation(summary = "修改预订")
    @PutMapping("/{id}")
    public Result<BookingVO> update(@PathVariable Long id, @RequestBody BookingSaveDTO dto) {
        BookingEntity entity = bookingConverter.toEntity(dto);
        return ok(bookingConverter.toVO(bookingService.updateById(id, entity)));
    }
    @Operation(summary = "删除预订")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { bookingService.deleteById(id); return ok(); }
}
