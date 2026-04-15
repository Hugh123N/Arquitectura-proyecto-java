package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.Query;
import com.soft.reclutamiento.dto.dbo.area.ListAreaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Query para listar Areas
 */
@Getter
@Setter
@AllArgsConstructor
public class ListAreaQuery extends Query<List<ListAreaDto>> {
}
