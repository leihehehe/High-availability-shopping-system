package com.leih.commonutil.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deal {
    private Long id;

    private String name;

    private Long productId;

    private BigDecimal originalPrice;

    private BigDecimal dealPrice;

    private Integer dealStatus;

    private Date startTime;

    private Date endTime;

    private Long totalStock;

    private Integer availableStock;

    private Long lockStock;

}