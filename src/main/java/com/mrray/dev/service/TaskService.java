package com.mrray.dev.service;

import com.mrray.dev.entity.domain.Task;
import com.mrray.dev.entity.dto.ExtractDto;
import com.mrray.dev.entity.dto.LoadDto;
import com.mrray.dev.entity.vo.RestResponseBody;

/**
 * Created by ln on 2017/8/1.
 */
public interface TaskService {

    RestResponseBody extract(ExtractDto extractDto);

    String save(Task task);

    RestResponseBody load(LoadDto loadDto);

    RestResponseBody deleteTable(String id);
}
