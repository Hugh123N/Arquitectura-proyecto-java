package com.soft.reclutamiento.api.controllers.dbo;

import com.soft.reclutamiento.application.abstractions.dbo.IAreaApplication;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController implements IAreaApplication {

    private final IAreaApplication areaApplication;

    @PostMapping
    @Override
    public ResponseDto<GetAreaDto> create(@RequestBody CreateAreaDto createDto) {
        return areaApplication.create(createDto);
    }

    @PutMapping
    @Override
    public ResponseDto<GetAreaDto> update(@RequestBody UpdateAreaDto updateDto) {
        return areaApplication.update(updateDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseDto delete(@PathVariable Integer id) {
        return areaApplication.delete(id);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseDto<GetAreaDto> get(@PathVariable Integer id) {
        return areaApplication.get(id);
    }

    @GetMapping("/list")
    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return areaApplication.list();
    }

    @PostMapping("/search")
    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(@RequestBody SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return areaApplication.search(searchParams);
    }

    @GetMapping("/selectcombo")
    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return areaApplication.selectCombo();
    }

    @PostMapping("/select")
    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(@RequestBody SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return areaApplication.select(searchParams);
    }
}
