package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandValidatorBase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Validator para CreateAreaCommand
 * Extiende de CommandValidatorBase para funcionalidad común
 */
@Component
@RequiredArgsConstructor
public class CreateAreaCommandValidator extends CommandValidatorBase<CreateAreaCommand> {

    private final Validator validator;

    @Override
    public List<String> validate(CreateAreaCommand command) {
        var errors = createErrorList();

        // Validar que CreateDto no sea null
        if (command.getCreateDto() == null) {
            errors.add(messages.informationRequired());
            return errors;
        }

        // Validar usando Jakarta Bean Validation
        Set<ConstraintViolation<Object>> violations = validator.validate(command.getCreateDto());
        for (ConstraintViolation<Object> violation : violations) {
            errors.add(violation.getMessage());
        }

        // Aquí se pueden agregar validaciones de negocio adicionales
        // Por ejemplo: verificar que no exista un Area con el mismo nombre

        return errors;
    }
}
