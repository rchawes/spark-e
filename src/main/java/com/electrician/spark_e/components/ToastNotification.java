package com.electrician.spark_e.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Toast notification model for frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToastNotification {
    
    public enum Type {
        SUCCESS("success", "check-circle"),
        ERROR("error", "times-circle"),
        WARNING("warning", "exclamation-triangle"),
        INFO("info", "info-circle");
        
        private final String cssClass;
        private final String icon;
        
        Type(String cssClass, String icon) {
            this.cssClass = cssClass;
            this.icon = icon;
        }
        
        public String getCssClass() { return cssClass; }
        public String getIcon() { return icon; }
    }
    
    private String id;
    private Type type;
    private String title;
    private String message;
    private Integer duration;
    private Boolean persistent;
    private String actionText;
    private String actionUrl;
    
    public static ToastNotification success(String title, String message) {
        return new ToastNotification(
            java.util.UUID.randomUUID().toString(),
            Type.SUCCESS,
            title,
            message,
            5000, // 5 seconds
            false,
            null,
            null
        );
    }
    
    public static ToastNotification error(String title, String message) {
        return new ToastNotification(
            java.util.UUID.randomUUID().toString(),
            Type.ERROR,
            title,
            message,
            8000, // 8 seconds
            true, // persistent for errors
            null,
            null
        );
    }
    
    public static ToastNotification warning(String title, String message) {
        return new ToastNotification(
            java.util.UUID.randomUUID().toString(),
            Type.WARNING,
            title,
            message,
            6000, // 6 seconds
            false,
            null,
            null
        );
    }
    
    public static ToastNotification info(String title, String message) {
        return new ToastNotification(
            java.util.UUID.randomUUID().toString(),
            Type.INFO,
            title,
            message,
            4000, // 4 seconds
            false,
            null,
            null
        );
    }
    
    public static ToastNotification successWithAction(String title, String message, String actionText, String actionUrl) {
        return new ToastNotification(
            java.util.UUID.randomUUID().toString(),
            Type.SUCCESS,
            title,
            message,
            10000, // 10 seconds for action notifications
            true,
            actionText,
            actionUrl
        );
    }
}
