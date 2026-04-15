package com.soft.reclutamiento.dto.dbo.area;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchAreaFilterDto extends AreaFilterDto {

    private OffsetDateTime fechaDesde;
    private OffsetDateTime fechaHasta;
}
