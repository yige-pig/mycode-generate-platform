package com.yige.mycodegenerateplatform.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yige.mycodegenerateplatform.model.dto.app.AppAddRequest;
import com.yige.mycodegenerateplatform.model.dto.app.AppQueryRequest;
import com.yige.mycodegenerateplatform.model.entity.App;
import com.yige.mycodegenerateplatform.model.entity.User;
import com.yige.mycodegenerateplatform.model.vo.app.AppVO;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

/**
 * 应用 服务层。
 *
 * @author lly
 */
public interface AppService extends IService<App> {

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    String deployApp(Long appId, User loginUser);

    boolean removeById(Serializable id);

    void generateAppScreenshotAsync(Long appId, String appUrl);

    Long createApp(AppAddRequest appAddRequest, User loginUser);
}
