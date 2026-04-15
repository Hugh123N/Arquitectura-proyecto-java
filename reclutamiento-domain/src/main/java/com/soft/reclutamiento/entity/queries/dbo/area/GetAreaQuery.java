package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.Query;
import com.soft.reclutamiento.dto.dbo.area.GetAreaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Query para obtener Area por ID
 */
@Getter
@Setter
@AllArgsConstructor
public class GetAreaQuery extends Query<GetAreaDto> {

    private Integer id;
}
