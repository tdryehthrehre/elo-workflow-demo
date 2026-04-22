package de.erikschwob.elodemo.model;

public enum WFStatus {
    INCOMING,
    REVIEW,
    APPROVAL,
    ARCHIVE;

    public boolean canTransitionTo(WFStatus target) {
        return switch (this) {
            case INCOMING  -> target == REVIEW;
            case REVIEW    -> target == APPROVAL || target == INCOMING;
            case APPROVAL  -> target == ARCHIVE  || target == REVIEW;
            case ARCHIVE   -> false;
        };
    }
}
