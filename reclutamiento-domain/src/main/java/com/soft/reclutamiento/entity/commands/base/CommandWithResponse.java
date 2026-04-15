package com.soft.reclutamiento.entity.commands.base;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.dto.base.ResponseDto;

/**
 * Comando base con respuesta específica
 *
 * @param <TResponse> Tipo de respuesta
 */
public abstract class CommandWithResponse<TResponse> implements Command<ResponseDto<TResponse>> {

    private boolean validate = true;

    public boolean isValidate() {
        return validate;
    }

    public void enableValidation() {
        this.validate = true;
    }

    public void disableValidation() {
        this.validate = false;
    }
}
