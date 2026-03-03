package org.example.businessprocessservice.service.status;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.StatusHistoryEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.StatusHistoryRepository;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.ChangeStatusRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CaseStatusService {

    private final CaseRepository caseRepository;
    private final StatusHistoryRepository historyRepository;

    public CaseStatusService(CaseRepository caseRepository, StatusHistoryRepository historyRepository) {
        this.caseRepository = caseRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public CaseResponse changeStatus(Long caseId, ChangeStatusRequest req) {

        // ✅ защита от пустого тела запроса (иначе NPE на req.getNewStatus())
        if (req == null) throw new IllegalArgumentException("Request body is required");

        CaseEntity c = caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        CaseStatus from = c.getStatus();
        CaseStatus to = req.getNewStatus();

        if (to == null) throw new IllegalArgumentException("newStatus is required");

        // 1) Запрещённые переходы (граф переходов)
        if (!TransitionRules.canMove(from, to)) {
            throw new ForbiddenStatusTransitionException("Forbidden transition: " + from + " -> " + to);
        }

        // 2) Обязательные условия (каркас: допишем позже)
        checkPreconditions(c, from, to, req);

        // 3) Применяем изменения
        c.setStatus(to);

        // endDate ставим только один раз (не перезаписываем)
        if (to == CaseStatus.COMPLETED && c.getEndDate() == null) {
            c.setEndDate(LocalDateTime.now());
        }

        // 4) Сохраняем кейс
        CaseEntity saved = caseRepository.save(c);

        // 5) Пишем историю
        StatusHistoryEntity h = new StatusHistoryEntity();
        h.setCaseId(saved.getId());
        h.setPreviousStatus(from.name());
        h.setNewStatus(to.name());
        h.setChangedAt(LocalDateTime.now());
        h.setInitiatedBy(req.getInitiatedBy());

        historyRepository.save(h);

        return toResponse(saved);
    }

    /**
     * Обязательные условия для перехода статуса.
     * Сюда добавим проверки вроде:
     * - нельзя завершить без обязательных документов
     * - нельзя перевести в PROCEDURE_RUNNING без участников
     * и т.д.
     */
    private void checkPreconditions(CaseEntity c, CaseStatus from, CaseStatus to, ChangeStatusRequest req) {
        // TODO: пример будущей логики:
        // if (to == CaseStatus.COMPLETED) {
        //     boolean ok = documentRepository.existsRequiredForCase(c.getId());
        //     if (!ok) throw new IllegalStateException("Cannot complete without required documents");
        // }
    }

    private static CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());
        r.setProcedureType(e.getProcedureType());
        r.setStatus(e.getStatus());
        r.setStartDate(e.getStartDate());
        r.setEndDate(e.getEndDate());
        return r;
    }
}