package com.soft.reclutamiento.entity.queries.base;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.dto.base.ResponseDto;

/**
 * Query base con respuesta específica
 *
 * @param <TResponse> Tipo de respuesta
 */
public abstract class Query<TResponse> implements Command<ResponseDto<TResponse>> {
}
