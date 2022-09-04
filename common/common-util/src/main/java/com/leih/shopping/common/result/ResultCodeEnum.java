package com.leih.shopping.common.result;

import lombok.Getter;

/**
 * Result code enum
 *
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"Success"),
    FAIL(201, "Failed"),
    SERVICE_ERROR(202, "Server is busy"),
    ILLEGAL_REQUEST( 204, "Illegal request"),
    Payment_being_processed(205, "Transactions are being processed"),

    LOGIN_AUTH(208, "Please log in first"),
    PERMISSION(209, "No permission"),
    DEAL_NO_START(210, "The deal has not started yet"),
    DEAL_NO_EXIST(210, "The deal does not exist"),
    QUEUE_WAITING(211, "You are in the queue, please wait."),
    DEAL_NO_PAID_ORDER(212, "Your order is not paid"),
    SOLD_OUT(213, "The item is out of the stock"),
    DEAL_END(214, "The deal ends"),
    DEAL_ILLEGAL(217, "Illegal request"),
    DEAL_ORDER_SUCCESS(218, "Order Successful"),
    COUPON_GOT(220, "You have already claimed your coupon"),
    COUPON_NO_MORE(221, "Sorry, no coupon left"),
    USER_LIMITED(222,"You are not able to purchase this item"),
    USERNAME_PASSWORD_INVALID(222,"Invalid username or password")
    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
