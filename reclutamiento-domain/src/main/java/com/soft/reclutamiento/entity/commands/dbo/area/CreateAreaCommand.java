package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandWithResponse;
import com.soft.reclutamiento.dto.dbo.area.CreateAreaDto;
import com.soft.reclutamiento.dto.dbo.area.GetAreaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Command para crear Area
 */
@Getter
@Setter
@AllArgsConstructor
public class CreateAreaCommand extends CommandWithResponse<GetAreaDto> {

    private CreateAreaDto createDto;
}
