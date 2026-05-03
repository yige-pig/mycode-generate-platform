package com.yige.mycodegenerateplatform.service.impl;

import com.yige.mycodegenerateplatform.innerservice.InnerScreenshotService;
import com.yige.mycodegenerateplatform.service.ScreenshotService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        return screenshotService.generateAndUploadScreenshot(webUrl);
    }
}