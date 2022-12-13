package com.template;

import com.http.template.FileTemplateReplacer;
import com.http.template.TemplateFileStream;
import com.http.template.TemplateNodes;
import com.http.template.TemplateText;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Disabled
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
        InputStream is = new FileInputStream(TEMPLATE_FILE.toFile());
        FileTemplateReplacer replacer = FileTemplateReplacer.of(TemplateFileStream.of(is),REPLACED_FILE);
        String start = TemplateText.ERROR_TEMPLATE.getStart();
        String end = TemplateText.ERROR_TEMPLATE.getEnd();
        String expected = readFile(TEMPLATE_FILE);
        expected = expected.replace(start+"name"+end,"kim");
        expected = expected.replace(start+"age"+end,"100");

        //when
        replacer.replace(nodes, TemplateText.ERROR_TEMPLATE);
        String actual = readFile(REPLACED_FILE);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}