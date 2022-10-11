package com;

import com.exception.InputNullParameterException;
import com.releaser.FileResourceCloser;
import com.releaser.ResourceCloser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class HttpStreamGeneratorTest {

    @Test
    @DisplayName("복수의 InputStream 연결 테스트")
    void testSequenceOf() throws IOException {
        //given
        String txt1 = "Hello";
        String txt2 = "World";
        String expected = txt1 + txt2;
        InputStream is1 = new ByteArrayInputStream(txt1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(txt2.getBytes(StandardCharsets.UTF_8));
        InputStreamGenerator isGenerator1 = InputStreamGenerator.of(is1);
        HttpStreamGenerator generator1 = HttpStreamGenerator.of(isGenerator1);
        InputStreamGenerator isGenerator2 = InputStreamGenerator.of(is2);
        HttpStreamGenerator generator2 = HttpStreamGenerator.of(isGenerator2);

        //then
        HttpStreamGenerator generator = generator1.sequenceOf(generator2);
        StringBuilder actual = new StringBuilder();
        while (generator.hasMoreString()) {
            actual.append(generator.generate());
        }

        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("파일 삭제 releaser 등록시 파일 삭제 테스트")
    void testRegisterReleaser() throws IOException {
        //given
        boolean expected = false;
        File file = Path.of("src","test","resources","test-delete.txt").toFile();
        file.createNewFile();

        ResourceCloser releaser = new FileResourceCloser(file);
        HttpStreamGenerator generator = HttpStreamGenerator.empty();
        generator.registerReleaser(releaser);

        //when
        generator.close();
        boolean deleted = file.exists();

        //then
        Assertions.assertThat(deleted).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> HttpStreamGenerator.of(null))
                .isInstanceOf(InputNullParameterException.class);
    }

    @Test
    @DisplayName("null 로 sequenceOf() 호출시 에러 발생 테스트")
    void testSequenceOfWithNull() {
        //given
        String str = "Hello";
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        InputStreamGenerator isGenerator = InputStreamGenerator.of(is);
        HttpStreamGenerator generator = HttpStreamGenerator.of(isGenerator);

        Assertions.assertThatThrownBy(()-> generator.sequenceOf(null))
                .isInstanceOf(InputNullParameterException.class);
    }

}