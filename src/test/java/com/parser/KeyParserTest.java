package com.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KeyParserTest {

    @Test
    @DisplayName("null로 생성했을때 런타임에러 발생테스트")
    void testConstructWithNull() {
        //given
        String expected = "values가 null";
        String values = null;

        try{
            //when
            KeyParser keyParser = KeyParser.of(values);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("getValue() 테스트")
    void testGetValue() {
        //given
        String expected1 = "apple";
        String expected2 = "banana";
        String values = "a=apple&b=banana";

        //when
        KeyParser keyParser = KeyParser.of(values);
        String actual1 = keyParser.getValue("a");
        String actual2 = keyParser.getValue("b");

        //then
        Assertions.assertThat(actual1).isEqualTo(expected1);
        Assertions.assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    @DisplayName("getValue()에서 null로 key값을 받았을때 런타임에러 발생 테스트")
    void testGetValueWithNull() {
        //given
        String expected = "key는 null일 수 없음";
        String values = "a=apple&b=banana";

        try{
            //when
            KeyParser keyParser = KeyParser.of(values);
            String value = keyParser.getValue(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("getValue()에서 존재하지 않는 key값을 받았을때 런타임에러 발생 테스트")
    void testNotFoundKey() {
        //given
        String expected = "존재하지 않는 key";
        String values = "a=apple&b=banana";

        try{
            //when
            KeyParser keyParser = KeyParser.of(values);
            String value = keyParser.getValue("c");
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }
}