package com.mrray.dev.feign;

import com.mrray.dev.entity.vo.RestResponseBody;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by ln on 2017/8/8.
 */
@FeignClient("task-management-server")
public interface TaskManagerFeign {

    @PostMapping("/api/v1/tasks/notice/extract")
    ResponseEntity extract(RestResponseBody restResponseBody);

    @PostMapping("/api/v1/tasks/notice/load")
    ResponseEntity load(RestResponseBody restResponseBody);
}
