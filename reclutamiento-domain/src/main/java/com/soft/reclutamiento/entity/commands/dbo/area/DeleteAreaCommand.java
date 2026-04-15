package com.soft.reclutamiento.entity.commands.dbo.area;

import com.soft.reclutamiento.entity.commands.base.BaseCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Command para eliminar Area
 */
@Getter
@Setter
@AllArgsConstructor
public class DeleteAreaCommand extends BaseCommand {

    private Integer id;
}
