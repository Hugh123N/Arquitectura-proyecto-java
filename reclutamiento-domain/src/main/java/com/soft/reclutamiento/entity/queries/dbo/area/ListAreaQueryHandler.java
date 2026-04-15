package com.soft.reclutamiento.entity.queries.dbo.area;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.entity.mappers.dbo.AreaMapper;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.dbo.area.ListAreaDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler para ListAreaQuery
 */
@Component
@RequiredArgsConstructor
public class ListAreaQueryHandler implements Command.Handler<ListAreaQuery, ResponseDto<List<ListAreaDto>>> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    @Override
    public ResponseDto<List<ListAreaDto>> handle(ListAreaQuery query) {
        var response = new ResponseDto<List<ListAreaDto>>();

        try {
            var areas = areaRepository.findAll();
            List<ListAreaDto> areaDtos = areas.stream()
                    .map(areaMapper::toListAreaDto)
                    .collect(Collectors.toList());

            response.updateData(areaDtos);

        } catch (Exception e) {
            response.addErrorResult("Error al listar Areas: " + e.getMessage());
        }

        return response;
    }
}
