package com.yige.mycodegenerateplatform.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yige.mycodegenerateplatform.ai.model.HtmlCodeResult;
import com.yige.mycodegenerateplatform.exception.BusinessException;
import com.yige.mycodegenerateplatform.exception.ErrorCode;
import com.yige.mycodegenerateplatform.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 *
 * @author yige
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
