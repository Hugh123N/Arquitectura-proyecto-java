package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandHandlerBaseWithResponse;
import com.soft.reclutamiento.entity.mappers.dbo.AreaMapper;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.entity.entities.Area;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.dbo.area.GetAreaDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import org.springframework.stereotype.Component;

/**
 * Handler para UpdateAreaCommand
 * Extiende de CommandHandlerBaseWithResponse para validación automática
 */
@Component
public class UpdateAreaCommandHandler extends CommandHandlerBaseWithResponse<UpdateAreaCommand, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public UpdateAreaCommandHandler(
            Messages messages,
            UpdateAreaCommandValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleCommand(UpdateAreaCommand command) {
        var response = new ResponseDto<GetAreaDto>();

        // Mapear y guardar
        Area area = areaMapper.toEntity(command.getUpdateDto());
        area = areaRepository.save(area);

        // Retornar respuesta
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);
        response.addOkResult(messages.updateSuccess());

        return response;
    }
}
