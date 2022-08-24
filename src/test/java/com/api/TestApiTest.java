package com.api;

import com.parser.KeyParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestApiTest {

    @Test
    @DisplayName("name key 파싱 테스트")
    void testWithNormalValues() {
        //given
        String expected = "안녕하세요 kim님";
        String values = "name=kim";
        TestApi testApi = TestApi.of(values);

        //when
        String actual = testApi.getBody();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("KeyParser가 null일때 런타임에러 발생테스트")
    void testWithNull() {
        //given
        String expected = "KeyParser가 null";
        KeyParser keyParser = null;

        try{
            //when
            TestApi testApi = new TestApi(keyParser);
        }catch (RuntimeException e){

            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }


}