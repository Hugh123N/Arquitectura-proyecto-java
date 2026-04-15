package com.soft.reclutamiento.application.abstractions.dbo;

import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;

import java.util.List;

public interface IAreaApplication {

    ResponseDto<GetAreaDto> create(CreateAreaDto createDto);

    ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto);

    ResponseDto delete(Integer id);

    ResponseDto<GetAreaDto> get(Integer id);

    ResponseDto<List<ListAreaDto>> list();

    ResponseDto<SearchResultDto<SearchAreaDto>> search(SearchParamsDto<SearchAreaFilterDto> searchParams);

    ResponseDto<List<SelectComboAreaDto>> selectCombo();

    ResponseDto<SearchResultDto<SelectAreaDto>> select(SearchParamsDto<SelectAreaFilterDto> searchParams);
}
