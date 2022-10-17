package com;

import static org.assertj.core.api.Assertions.assertThat;

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

class HttpMessageStreamTest {

    @Test
    @DisplayName("파일 삭제 releaser 등록시 파일 삭제 테스트")
    void testRegisterReleaser() throws IOException {
        //given
        boolean expected = false;
        String str = "Hello";
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        StringStream stringStream = StringStream.of(is);

        File file = Path.of("src","test","resources","test-delete.txt").toFile();
        file.createNewFile();

        ResourceCloser closer = new FileResourceCloser(file);
        HttpMessageStream stream = HttpMessageStream.of(stringStream,closer);

        //when
        stream.close();
        boolean deleted = file.exists();

        //then
        Assertions.assertThat(deleted).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> HttpMessageStream.of(null))
                .isInstanceOf(InputNullParameterException.class);
        Assertions.assertThatThrownBy(()-> HttpMessageStream.of(null,null))
                .isInstanceOf(InputNullParameterException.class);
    }

}