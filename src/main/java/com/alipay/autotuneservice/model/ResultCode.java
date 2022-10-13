/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model;

/**
 * @author dutianze
 * @version ResultCode.java, v 0.1 2022年05月16日 15:37 dutianze
 */
public enum ResultCode {

    OK(200, "OK"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "Unauthorized"),

    FORBIDDEN(403, "Resource Access Forbidden"),

    NOT_FOUND(404, "Resource Not Found"),

    APP_TAG_NOT_FOUND(4041, "App Tag Not Found"),

    APP_NOT_FOUND(4042, "App Not Found"),

    PLAN_NOT_FOUND(4043, "can't find tunePlans by appId"),

    EXPERT_KNOWLEDGE_NOT_FOUND(4044, "Not Found Corresponding Expert Knowledge"),

    EXPERT_KNOWLEDGE_NOT_RECOMMEND(4047, "Not Recommend Expert Knowledge, Please check jvmDefault"),

    HEALTH_CHECK_NOT_FOUND(4045, "Not Found Corresponding Health Check"),

    POD_NOT_FOUND(4046, "Not Found Pod"),

    POD_ATTACH_NOT_FOUND(4046, "Not Found PodAttach"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "internal system error"),

    ALREADY_HAVE_RUNNING_PLAN(5001, "Already Have Running Plan"),

    SUBMIT_PLAN_TOO_FAST(5003, "Submit Plan Too Fast"),

    DUPLICATE_ATTACH(5003, "Duplicate Attach Pod"),

    BUILD_URL_ERROR(5004, "Build Url Error"),

    UNSUPPORTED_OPERATOR_ERROR(5005, "Unsupported operator"),

    NO_CONTAINER_FOR_POD_ERROR(5006, "No container for pod"),

    NO_DATA_IN_DB(5007, "No Data in DB"),

    ;

    private final Integer code;
    private final String  message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}