package com.medifind.medicine_service.exception;

public class MedicineInUseException extends RuntimeException {
    public MedicineInUseException(String message) {
        super(message);
    }
}
