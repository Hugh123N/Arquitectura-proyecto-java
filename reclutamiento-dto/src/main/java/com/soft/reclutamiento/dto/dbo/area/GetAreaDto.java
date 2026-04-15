package com.soft.reclutamiento.dto.dbo.area;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAreaDto extends AreaDto {

    private Integer idArea;
    private Boolean activo;
}
