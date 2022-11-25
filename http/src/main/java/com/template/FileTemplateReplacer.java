package com.template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileTemplateReplacer implements Closeable {
    private final TemplateFileStream templateFileStream;
    private final OutputStreamWriter osw;

    private FileTemplateReplacer(TemplateFileStream templateFileStream, OutputStreamWriter osw) {
        if (templateFileStream == null || osw == null) {
            new NullPointerException();
        }
        this.templateFileStream = templateFileStream;
        this.osw = osw;
    }

    public static FileTemplateReplacer of(TemplateFileStream templateFileStream, Path replacedFileName) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(replacedFileName.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        BufferedOutputStream bos = new BufferedOutputStream(os, 8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos,StandardCharsets.UTF_8);

        return new FileTemplateReplacer(templateFileStream,osw);
    }

    public void replace(TemplateNodes templateNodes, TemplateText template) {
        try {
            StringBuilder foundTemplateText = new StringBuilder();

            String startTemplate = template.getStart();
            String endTemplate = template.getEnd();

            int txtMaxLength = templateNodes.getTemplateMaxLength();
            int templateLength = startTemplate.length() + endTemplate.length();

            while (templateFileStream.hasMoreFileContent()) {
                String fileContent = templateFileStream.generate();

                if (foundTemplateText.length() != 0) {
                    if (foundTemplateText.length() < txtMaxLength + templateLength) {
                        foundTemplateText.append(fileContent);
                        continue;
                    }

                    String replace = replaceTemplate(foundTemplateText, startTemplate, endTemplate, 0, txtMaxLength, templateNodes);

                    int nextStartIdx = foundTemplateText.indexOf(startTemplate.substring(0, 1));
                    if (nextStartIdx != -1) {
                        osw.write(replace.substring(0, nextStartIdx));
                        foundTemplateText.setLength(0);
                        foundTemplateText.append(replace.substring(nextStartIdx));
                        foundTemplateText.append(fileContent);
                        continue;
                    }

                    osw.write(replace);
                    foundTemplateText.setLength(0);
                }

                int startIdx = fileContent.indexOf(startTemplate.charAt(0));
                if (startIdx == -1) {
                    osw.write(fileContent);
                    continue;
                }

                osw.write(fileContent.substring(0, startIdx));
                foundTemplateText.append(fileContent.substring(startIdx));
            }

            if (foundTemplateText.length() != 0) {
                String replace = replaceTemplate(foundTemplateText, startTemplate, endTemplate, 0, txtMaxLength, templateNodes);
                osw.write(replace);
            }

            osw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public void close() {
        templateFileStream.close();

        try {
            osw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
