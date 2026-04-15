package com.soft.reclutamiento.entity.commands.base;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.dto.base.ResponseDto;

/**
 * Comando base sin respuesta específica
 */
public abstract class BaseCommand implements Command<ResponseDto> {

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
