package org.example.businessprocessservice.service.status;

import org.example.businessprocessservice.domain.enums.CaseStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public final class TransitionRules {

    private TransitionRules() {}

    private static final EnumMap<CaseStatus, Set<CaseStatus>> ALLOWED = new EnumMap<>(CaseStatus.class);

    static {
        ALLOWED.put(CaseStatus.CREATED, EnumSet.of(CaseStatus.IN_PROGRESS));
        ALLOWED.put(CaseStatus.IN_PROGRESS, EnumSet.of(CaseStatus.PROCEDURE_RUNNING, CaseStatus.ARCHIVED));
        ALLOWED.put(CaseStatus.PROCEDURE_RUNNING, EnumSet.of(CaseStatus.COMPLETED, CaseStatus.ARCHIVED));
        ALLOWED.put(CaseStatus.COMPLETED, EnumSet.of(CaseStatus.ARCHIVED));
        ALLOWED.put(CaseStatus.ARCHIVED, EnumSet.noneOf(CaseStatus.class));
    }

    public static boolean canMove(CaseStatus from, CaseStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }
}