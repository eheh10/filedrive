package com.path;

import com.exception.NullException;
import com.request.HttpRequestPath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestPathTest {

    @Test
    @DisplayName("같은 경로를 가진 인스턴스들이 동일 인스턴스로 구분되는지 테스트")
    void testSamePath() {
        //given
        HttpRequestPath expected = HttpRequestPath.of("/a");

        //when
        HttpRequestPath actual = HttpRequestPath.of("/a");

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/test","/test/../test/../test", "/test/."})
    @DisplayName("중복 경로 제거하여 path 얻는지 테스트")
    void testParsePath(String path) {
        //given
        HttpRequestPath expected = HttpRequestPath.of("/test");

        //when
        HttpRequestPath actual = HttpRequestPath.of(path);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 생성시 에러 테스트")
    void testNullConstructor() {
        Assertions.assertThatThrownBy(()-> new HttpRequestPath(null))
                .isInstanceOf(NullException.class);

        Assertions.assertThatThrownBy(()-> HttpRequestPath.of(null))
                .isInstanceOf(NullException.class);
    }
}