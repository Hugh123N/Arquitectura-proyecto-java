package com.soft.reclutamiento.entity.commands.base;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.dto.base.ResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Clase base para handlers de Commands sin respuesta específica
 * Maneja automáticamente la validación antes de ejecutar el comando
 */
public abstract class CommandHandlerBase<TCommand extends BaseCommand>
        implements Command.Handler<TCommand, ResponseDto> {

    protected final Messages messages;
    protected final CommandValidatorBase<TCommand> validator;

    protected CommandHandlerBase(Messages messages, CommandValidatorBase<TCommand> validator) {
        this.messages = messages;
        this.validator = validator;
    }

    @Override
    @Transactional
    public ResponseDto handle(TCommand command) {
        var response = new ResponseDto();

        try {
            // Validación automática si está habilitada
            if (command.isValidate() && validator != null && validator.isEnabled()) {
                List<String> validationErrors = validator.validate(command);
                if (!validationErrors.isEmpty()) {
                    validationErrors.forEach(response::addErrorResult);
                    return response;
                }
            }

            // Ejecutar el comando
            return handleCommand(command);

        } catch (Exception e) {
            addExceptionCommandResult(response, e);
            return response;
        }
    }

    /**
     * Método abstracto que debe implementar cada handler específico
     * Aquí va la lógica de negocio del comando
     */
    protected abstract ResponseDto handleCommand(TCommand command);

    /**
     * Agrega información de excepción al response
     */
    protected void addExceptionCommandResult(ResponseDto response, Exception exception) {
        var innerException = getInnerException(exception);
        response.addErrorResult("Error al procesar el comando: " + innerException.getMessage());
    }

    /**
     * Obtiene la excepción más interna
     */
    private Exception getInnerException(Exception ex) {
        int limit = 10;
        int iteration = 0;
        Exception exception = ex;

        while (exception.getCause() != null && iteration < limit) {
            exception = (Exception) exception.getCause();
            iteration++;
        }

        return exception;
    }
}
