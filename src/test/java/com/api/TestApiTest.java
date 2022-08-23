package com.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestApiTest {

    @Test
    @DisplayName("name key 파싱 테스트")
    void testWithNormalValues() {
        //given
        String expected = "안녕하세요 kim님";
        TestApi testApi = new TestApi();
        String values = "name=kim";

        //when
        String actual = testApi.getResponseMessage(values);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("value가 null일때 런타임에러 발생테스트")
    void testWithNull() {
        //given
        TestApi testApi = new TestApi();
        String expected = "values가 null";
        String values = null;

        try{
            //when
            String result = testApi.getResponseMessage(values);
        }catch (RuntimeException e){

            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("value에 name key가 없을때 런타임에러 발생테스트")
    void testWithNoName() {
        //given
        TestApi testApi = new TestApi();
        String expected = "name key가 없음";
        String values = "";

        try{
            //when
            String result = testApi.getResponseMessage(values);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

}