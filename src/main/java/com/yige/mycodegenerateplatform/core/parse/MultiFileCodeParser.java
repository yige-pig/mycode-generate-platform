package com.yige.mycodegenerateplatform.core.parse;

import com.yige.mycodegenerateplatform.ai.model.MultiFileCodeResult;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多文件代码解析器（HTML + CSS + JS）
 *
 * @author yige
 */
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult> {

    public MultiFileCodeResult parseCode(String codeContent) {
        try {
            // 直接JSON转对象，格式绝对稳定
            JSONObject json = new JSONObject(codeContent);
            String html = json.optString("html", codeContent);
            String css = json.optString("css", "");
            String js = json.optString("javascript", "");

            MultiFileCodeResult result = new MultiFileCodeResult();
            result.setHtmlCode(html);
            result.setCssCode(css);
            result.setJsCode(js);
            return result;
        } catch (Exception e) {
            // 兜底：解析失败就直接返回全文
            MultiFileCodeResult result = new MultiFileCodeResult();
            result.setHtmlCode(codeContent);
            return result;
        }
    }

    /*private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    @Override
    public MultiFileCodeResult parseCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取各类代码
        String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);
        // 设置HTML代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        }
        // 设置CSS代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        }
        // 设置JS代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        }
        return result;
    }

    *//**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码
     *//*
    private String extractCodeByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }*/
}
