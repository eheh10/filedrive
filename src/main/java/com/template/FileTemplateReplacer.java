package com.template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileTemplateReplacer {
    private final InputStreamReader isr;
    private final OutputStreamWriter osw;

    private FileTemplateReplacer(InputStreamReader isr, OutputStreamWriter osw) {
        if (isr == null || osw == null) {
            new NullPointerException();
        }
        this.isr = isr;
        this.osw = osw;
    }

    public static FileTemplateReplacer of(Path templateFileName, Path replacedFileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(templateFileName.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis,StandardCharsets.UTF_8);

        OutputStream os = new FileOutputStream(replacedFileName.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os, 8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos,StandardCharsets.UTF_8);

        return new FileTemplateReplacer(isr,osw);
    }

    public void replace(TemplateNodes templateNodes, String startTemplate, String endTemplate) throws IOException {
        StringBuilder templateTxt = new StringBuilder();
        char[] buffer = new char[1024];
        int len = -1;

        int txtMaxLength = templateNodes.getTemplateMaxLength();
        int templateLength = startTemplate.length() + endTemplate.length();

        while((len=isr.read(buffer)) != -1) {
            if (templateTxt.length() != 0) {
                if (templateTxt.length() < txtMaxLength+templateLength) {
                    templateTxt.append(buffer,0,len);
                    continue;
                }

                String replace = replaceTemplate(templateTxt,startTemplate,endTemplate,0,txtMaxLength,templateNodes);

                int nextStartIdx = templateTxt.indexOf(startTemplate.substring(0,1));
                if (nextStartIdx != -1) {
                    osw.write(replace.substring(0,nextStartIdx));
                    templateTxt.setLength(0);
                    templateTxt.append(replace.substring(nextStartIdx));
                    templateTxt.append(buffer,0,len);
                    continue;
                }

                osw.write(replace);
                templateTxt.setLength(0);
            }

            int startIdx= findIndexOf(startTemplate.charAt(0),buffer);
            if (startIdx == -1) {
                osw.write(buffer,0,len);
                continue;
            }

            osw.write(buffer,0,startIdx);
            templateTxt.append(buffer,startIdx,len-startIdx);
        }

        if (templateTxt.length() != 0) {
            String replace = replaceTemplate(templateTxt,startTemplate,endTemplate,0,txtMaxLength,templateNodes);
            osw.write(replace);
        }

        osw.flush();
        osw.close();
    }

    private String replaceTemplate(StringBuilder templateTxt, String startTemplate, String endTemplate, int initialIdx, int max, TemplateNodes replaceTxt) {
        int templateStartIdx = templateTxt.indexOf(startTemplate,initialIdx);

        if (templateStartIdx == -1) {
            return templateTxt.toString();
        }

        int templateEndIdx = templateTxt.indexOf(endTemplate,templateStartIdx+startTemplate.length());

        if (templateEndIdx == -1) {
            return templateTxt.toString();
        }

        String template = templateTxt.substring(templateStartIdx+startTemplate.length(),templateEndIdx);
        if (template.length() > max) {
            return replaceTemplate(templateTxt,startTemplate,endTemplate,templateEndIdx+2,max,replaceTxt);
        }

        String replace = replaceTxt.replaceWithDefault(template,template);
        templateTxt.replace(templateStartIdx,templateEndIdx+endTemplate.length(),replace);

        return replaceTemplate(templateTxt,startTemplate,endTemplate,templateStartIdx+replace.length(),max,replaceTxt);
    }

    private int findIndexOf(char target, char[] buffer, int fromIdx) {
        for(int i=fromIdx; i< buffer.length; i++) {
            if (buffer[i] == target) {
                return i;
            }
        }
        return -1;
    }

    private int findIndexOf(char target, char[] buffer) {
        return findIndexOf(target,buffer,0);
    }
}
