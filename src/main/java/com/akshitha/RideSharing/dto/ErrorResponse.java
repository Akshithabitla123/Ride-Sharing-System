package com.akshitha.RideSharing.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timeStamp,
    int status,
    String message
) {
    
}
