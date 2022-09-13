package com.api;

import com.exception.NotAllowedHttpMethodException;
import com.exception.NotFoundHttpPathException;
import com.exception.NullException;
import com.method.HttpRequestMethod;
import com.path.HttpRequestPath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHandlersTest {

    @Test
    @DisplayName("접근 가능한 path,method 로 바인딩 객체를 찾는 경우 테스트")
    void testFindHandler() {
        //given
        HttpRequestPath path = HttpRequestPath.of("/test");
        HttpRequestMethod method = HttpRequestMethod.POST;
        HttpRequestHandlers handlers = new HttpRequestHandlers();

        //when
        HttpRequestHandler actual = handlers.find(path,method);

        //then
        Assertions.assertThat(actual).isInstanceOf(HttpRequestBodyFileCreator.class);
    }

    @Test
    @DisplayName("접근 불가능한 path 로 바인딩 객체를 찾는 경우 테스트")
    void testFindHandlerWithNotFoundHttpPath() {
        //given
        HttpRequestPath path = HttpRequestPath.of("/wrongPath");
        HttpRequestMethod method = HttpRequestMethod.POST;
        HttpRequestHandlers handlers = new HttpRequestHandlers();

        //when
        Assertions.assertThatThrownBy(()->handlers.find(path,method))
                .isInstanceOf(NotFoundHttpPathException.class);
    }

    @Test
    @DisplayName("접근 불가능한 method 로 바인딩 객체를 찾는 경우 테스트")
    void testFindHandlerWithNotAllowedHttpMethod() {
        //given
        HttpRequestPath path = HttpRequestPath.of("/test");
        HttpRequestMethod method = HttpRequestMethod.DELETE;
        HttpRequestHandlers handlers = new HttpRequestHandlers();

        //when
        Assertions.assertThatThrownBy(()->handlers.find(path,method))
                .isInstanceOf(NotAllowedHttpMethodException.class);
    }

    @Test
    @DisplayName("path 값이 null 인데 바인딩 객체를 찾는 경우 테스트")
    void testFindHandlerWithNullPath() {
        //given
        HttpRequestMethod method = HttpRequestMethod.GET;
        HttpRequestHandlers handlers = new HttpRequestHandlers();

        //when
        Assertions.assertThatThrownBy(()->handlers.find(null,method))
                .isInstanceOf(NullException.class);
    }

    @Test
    @DisplayName("method 값이 null 인데 바인딩 객체를 찾는 경우 테스트")
    void testFindHandlerWithNullMethod() {
        //given
        HttpRequestPath path = HttpRequestPath.of("/test");
        HttpRequestHandlers handlers = new HttpRequestHandlers();

        //when
        Assertions.assertThatThrownBy(()->handlers.find(path,null))
                .isInstanceOf(NullException.class);
    }


}