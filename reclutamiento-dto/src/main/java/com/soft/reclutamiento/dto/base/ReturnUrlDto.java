package com.soft.reclutamiento.dto.base;

/**
 * DTO para URL de retorno
 * Equivalente a ReturnUrlDto.cs del proyecto .NET
 */
public class ReturnUrlDto {
    private String returnUrl;

    public ReturnUrlDto() {
    }

    public ReturnUrlDto(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
