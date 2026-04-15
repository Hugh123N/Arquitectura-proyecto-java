package com.soft.reclutamiento.application.dbo;

import an.awesome.pipelinr.Pipeline;
import com.soft.reclutamiento.application.abstractions.dbo.IAreaApplication;
import com.soft.reclutamiento.application.base.ApplicationBase;
import com.soft.reclutamiento.entity.commands.dbo.area.CreateAreaCommand;
import com.soft.reclutamiento.entity.commands.dbo.area.DeleteAreaCommand;
import com.soft.reclutamiento.entity.commands.dbo.area.UpdateAreaCommand;
import com.soft.reclutamiento.entity.queries.dbo.area.*;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaApplication extends ApplicationBase implements IAreaApplication {

    public AreaApplication(Pipeline mediator) {
        super(mediator);
    }

    @Override
    public ResponseDto<GetAreaDto> create(CreateAreaDto createDto) {
        return mediator.send(new CreateAreaCommand(createDto));
    }

    @Override
    public ResponseDto<GetAreaDto> update(UpdateAreaDto updateDto) {
        return mediator.send(new UpdateAreaCommand(updateDto));
    }

    @Override
    public ResponseDto delete(Integer id) {
        return mediator.send(new DeleteAreaCommand(id));
    }

    @Override
    public ResponseDto<GetAreaDto> get(Integer id) {
        return mediator.send(new GetAreaQuery(id));
    }

    @Override
    public ResponseDto<List<ListAreaDto>> list() {
        return mediator.send(new ListAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SearchAreaDto>> search(SearchParamsDto<SearchAreaFilterDto> searchParams) {
        return mediator.send(new SearchAreaQuery(searchParams));
    }

    @Override
    public ResponseDto<List<SelectComboAreaDto>> selectCombo() {
        return mediator.send(new SelectComboAreaQuery());
    }

    @Override
    public ResponseDto<SearchResultDto<SelectAreaDto>> select(SearchParamsDto<SelectAreaFilterDto> searchParams) {
        return mediator.send(new SelectAreaQuery(searchParams));
    }
}
