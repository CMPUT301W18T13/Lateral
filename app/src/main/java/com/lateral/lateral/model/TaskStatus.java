package com.lateral.lateral.model;

/**
 * Indicates the status of a task
 */
public enum TaskStatus {
    Requested,
    Bidded,
    Assigned,
    Done;

    public static String getFormattedEnum(TaskStatus value){
        switch (value){
            case Requested: return "[requested]";
            case Bidded: return "[bidded]";
            case Assigned: return "[assigned]";
            case Done: return "[done]";
            default: return null;
        }
    }
}
