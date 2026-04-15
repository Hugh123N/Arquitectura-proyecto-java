package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandValidatorBase;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Validator para UpdateAreaCommand
 * Extiende de CommandValidatorBase para funcionalidad común
 */
@Component
@RequiredArgsConstructor
public class UpdateAreaCommandValidator extends CommandValidatorBase<UpdateAreaCommand> {

    private final Validator validator;
    private final AreaRepository areaRepository;

    @Override
    public List<String> validate(UpdateAreaCommand command) {
        var errors = createErrorList();

        if (command.getUpdateDto() == null) {
            errors.add(messages.informationRequired());
            return errors;
        }

        // Validar usando Jakarta Bean Validation
        Set<ConstraintViolation<Object>> violations = validator.validate(command.getUpdateDto());
        for (ConstraintViolation<Object> violation : violations) {
            errors.add(violation.getMessage());
        }

        // Validar que el Area exista
        if (command.getUpdateDto().getIdArea() != null) {
            var existingArea = areaRepository.findById(command.getUpdateDto().getIdArea());
            if (existingArea.isEmpty()) {
                errors.add(messages.recordNotFound());
            }
        }

        return errors;
    }
}
