package com.soft.reclutamiento.entity.queries.dbo.area;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.entity.mappers.dbo.AreaMapper;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.dbo.area.SelectComboAreaDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler para SelectComboAreaQuery
 */
@Component
@RequiredArgsConstructor
public class SelectComboAreaQueryHandler implements Command.Handler<SelectComboAreaQuery, ResponseDto<List<SelectComboAreaDto>>> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    @Override
    public ResponseDto<List<SelectComboAreaDto>> handle(SelectComboAreaQuery query) {
        var response = new ResponseDto<List<SelectComboAreaDto>>();

        try {
            // Solo areas activas para combos
            var areas = areaRepository.findAll().stream()
                    .filter(a -> a.getActivo() != null && a.getActivo())
                    .collect(Collectors.toList());

            List<SelectComboAreaDto> areaDtos = areas.stream()
                    .map(areaMapper::toSelectComboAreaDto)
                    .collect(Collectors.toList());

            response.updateData(areaDtos);

        } catch (Exception e) {
            response.addErrorResult("Error al obtener combo de Areas: " + e.getMessage());
        }

        return response;
    }
}
