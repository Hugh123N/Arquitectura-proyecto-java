package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandHandlerBase;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import org.springframework.stereotype.Component;

/**
 * Handler para DeleteAreaCommand
 * Extiende de CommandHandlerBase para validación automática
 */
@Component
public class DeleteAreaCommandHandler extends CommandHandlerBase<DeleteAreaCommand> {

    private final AreaRepository areaRepository;

    public DeleteAreaCommandHandler(
            Messages messages,
            DeleteAreaCommandValidator validator,
            AreaRepository areaRepository
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
    }

    @Override
    protected ResponseDto handleCommand(DeleteAreaCommand command) {
        var response = new ResponseDto();

        // Soft delete (marcar como inactivo)
        var area = areaRepository.findById(command.getId()).get();
        area.setActivo(false);
        areaRepository.save(area);

        response.addOkResult(messages.deleteSuccess());

        return response;
    }
}
