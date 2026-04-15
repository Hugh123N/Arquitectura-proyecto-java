package com.soft.reclutamiento.application.base;

import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;

public abstract class ApplicationBase {

    protected final Pipeline mediator;

    public ApplicationBase(Pipeline mediator) {
        this.mediator = mediator;
    }
}
