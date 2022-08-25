package com.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestApiTest {

    @Test
    @DisplayName("name이 주어졌을때 getBody() 리턴값 정상 출력 테스트")
    void testWithNormalValues() {
        //given
        String expected = "안녕하세요 kim님";
        String name = "kim";
        TestApi testApi = new TestApi(name);

        //when
        String actual = testApi.getBody();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("name이 null일때 런타임에러 발생테스트")
    void testWithNull() {
        //given
        String expected = "name이 null";

        try{
            //when
            TestApi testApi = new TestApi(null);
        }catch (RuntimeException e){

            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }


}