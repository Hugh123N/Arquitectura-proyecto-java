package com.soft.reclutamiento.entity.mappers.dbo;

import com.soft.reclutamiento.entity.entities.Area;
import com.soft.reclutamiento.dto.dbo.area.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversiones entre Entity Area y sus DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AreaMapper {

    // Entity -> DTOs
    AreaDto toAreaDto(Area entity);
    GetAreaDto toGetAreaDto(Area entity);
    ListAreaDto toListAreaDto(Area entity);
    SearchAreaDto toSearchAreaDto(Area entity);
    SelectAreaDto toSelectAreaDto(Area entity);
    SelectComboAreaDto toSelectComboAreaDto(Area entity);

    // DTOs -> Entity
    Area toEntity(AreaDto dto);
    Area toEntity(CreateAreaDto dto);
    Area toEntity(UpdateAreaDto dto);
    Area toEntity(GetAreaDto dto);
}
