package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.Query;
import com.soft.reclutamiento.dto.dbo.area.SelectComboAreaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Query para obtener combo de Areas
 */
@Getter
@Setter
@NoArgsConstructor
public class SelectComboAreaQuery extends Query<List<SelectComboAreaDto>> {
}
