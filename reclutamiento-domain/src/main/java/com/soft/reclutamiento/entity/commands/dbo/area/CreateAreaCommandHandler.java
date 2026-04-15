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
 * Handler para CreateAreaCommand
 * Extiende de CommandHandlerBaseWithResponse para validación automática
 */
@Component
public class CreateAreaCommandHandler extends CommandHandlerBaseWithResponse<CreateAreaCommand, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public CreateAreaCommandHandler(
            Messages messages,
            CreateAreaCommandValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleCommand(CreateAreaCommand command) {
        var response = new ResponseDto<GetAreaDto>();

        // Mapear y guardar
        Area area = areaMapper.toEntity(command.getCreateDto());
        area = areaRepository.save(area);

        // Mapear respuesta
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);
        response.addOkResult(messages.createSuccess());

        return response;
    }
}
