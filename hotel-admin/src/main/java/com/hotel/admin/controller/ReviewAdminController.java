package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.entity.Review;
import com.hotel.model.vo.PageVO;
import com.hotel.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
public class ReviewAdminController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public Result<PageVO<Review>> getReviews(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Review> page = reviewService.getReviewPage(hotelId, status, pageNum, pageSize);
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        reviewService.updateReviewStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/reply")
    public Result<Void> replyReview(@PathVariable Long id, @RequestParam String reply) {
        reviewService.replyReview(id, reply);
        return Result.success();
    }
}
