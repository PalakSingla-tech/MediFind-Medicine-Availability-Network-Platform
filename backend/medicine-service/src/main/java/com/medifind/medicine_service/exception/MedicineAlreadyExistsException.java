package com.medifind.medicine_service.exception;

public class MedicineAlreadyExistsException extends RuntimeException {
    public MedicineAlreadyExistsException(String message) {
        super(message);
    }
}
