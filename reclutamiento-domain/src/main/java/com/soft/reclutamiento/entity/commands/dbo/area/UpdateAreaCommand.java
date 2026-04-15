package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.CommandWithResponse;
import com.soft.reclutamiento.dto.dbo.area.UpdateAreaDto;
import com.soft.reclutamiento.dto.dbo.area.GetAreaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Command para actualizar Area
 */
@Getter
@Setter
@AllArgsConstructor
public class UpdateAreaCommand extends CommandWithResponse<GetAreaDto> {

    private UpdateAreaDto updateDto;
}
