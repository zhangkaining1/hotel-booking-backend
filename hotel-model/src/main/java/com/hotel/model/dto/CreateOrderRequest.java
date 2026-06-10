package com.hotel.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Hotel id is required")
    private Long hotelId;

    @NotNull(message = "Room type id is required")
    private Long roomTypeId;

    @NotNull(message = "Checkin date is required")
    @FutureOrPresent(message = "Checkin date cannot be in the past")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkinDate;

    @NotNull(message = "Checkout date is required")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    @NotNull(message = "Room count is required")
    @Min(value = 1, message = "Room count must be at least 1")
    private Integer roomCount;

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotBlank(message = "Guest phone is required")
    private String guestPhone;

    private String specialRequest;
}
