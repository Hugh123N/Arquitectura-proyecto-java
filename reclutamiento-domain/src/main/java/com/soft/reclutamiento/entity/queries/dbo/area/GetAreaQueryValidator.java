package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.QueryValidatorBase;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator para GetAreaQuery
 * Extiende de QueryValidatorBase para funcionalidad común
 */
@Component
@RequiredArgsConstructor
public class GetAreaQueryValidator extends QueryValidatorBase<GetAreaQuery> {

    private final AreaRepository areaRepository;

    @Override
    public List<String> validate(GetAreaQuery query) {
        var errors = createErrorList();

        // Validar que el ID no sea null
        if (query.getId() == null) {
            errors.add(messages.identifierRequired());
            return errors;
        }

        // Validar que el Area exista
        var existingArea = areaRepository.findById(query.getId());
        if (existingArea.isEmpty()) {
            errors.add(messages.recordNotFound());
        }

        return errors;
    }
}
