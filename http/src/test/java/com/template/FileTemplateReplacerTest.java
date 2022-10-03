package com.template;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class FileTemplateReplacerTest {
    private static final Path TEMPLATE_FILE = Path.of("src","test","resources","test-template.txt");
    private static final Path REPLACED_FILE = Path.of("src","test","resources","test-template-replaced.txt");
    private static final TemplateNodes nodes = new TemplateNodes();

    @BeforeAll
    static void registerTemplate(){
        nodes.register("name","kim");
        nodes.register("age","100");
    }

    @AfterEach
    void deleteFile() {
        File file = new File(REPLACED_FILE.toString());
        file.delete();
    }

    private String readFile(Path file) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream is = new FileInputStream(file.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis,StandardCharsets.UTF_8);

        char[] buffer = new char[1024];
        int len = -1;

        while((len=isr.read(buffer)) != -1) {
            content.append(buffer,0,len);
        }

        return content.toString();
    }

    @Test
    @DisplayName("템플릿 대치 테스트")
    void testReplaceTemplate() throws IOException {
        //given
        FileTemplateReplacer replacer = FileTemplateReplacer.of(TEMPLATE_FILE,REPLACED_FILE);
        String expected = readFile(TEMPLATE_FILE);
        expected = expected.replace("<<<name>>>","kim");
        expected = expected.replace("<<<age>>>","100");

        //when
        replacer.replace(nodes,"<<<",">>>");
        String actual = readFile(REPLACED_FILE);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}