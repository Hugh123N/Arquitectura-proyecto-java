package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.mappers.dbo.AreaMapper;
import com.soft.reclutamiento.entity.queries.base.QueryHandlerBase;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.dbo.area.GetAreaDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import org.springframework.stereotype.Component;

/**
 * Handler para GetAreaQuery
 * Extiende de QueryHandlerBase para validación automática
 */
@Component
public class GetAreaQueryHandler extends QueryHandlerBase<GetAreaQuery, GetAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public GetAreaQueryHandler(
            Messages messages,
            GetAreaQueryValidator validator,
            AreaRepository areaRepository,
            AreaMapper areaMapper
    ) {
        super(messages, validator);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<GetAreaDto> handleQuery(GetAreaQuery query) {
        var response = new ResponseDto<GetAreaDto>();

        // Obtener y mapear
        var area = areaRepository.findById(query.getId()).get();
        GetAreaDto areaDto = areaMapper.toGetAreaDto(area);
        response.updateData(areaDto);

        return response;
    }
}
