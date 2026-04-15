package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandValidatorBase;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator para DeleteAreaCommand
 * Extiende de CommandValidatorBase para funcionalidad común
 */
@Component
@RequiredArgsConstructor
public class DeleteAreaCommandValidator extends CommandValidatorBase<DeleteAreaCommand> {

    private final AreaRepository areaRepository;

    @Override
    public List<String> validate(DeleteAreaCommand command) {
        var errors = createErrorList();

        // Validar que el ID no sea null
        if (command.getId() == null) {
            errors.add(messages.identifierRequired());
            return errors;
        }

        // Validar que el Area exista
        var existingArea = areaRepository.findById(command.getId());
        if (existingArea.isEmpty()) {
            errors.add(messages.deleteRecordNotFound());
        }

        return errors;
    }
}
