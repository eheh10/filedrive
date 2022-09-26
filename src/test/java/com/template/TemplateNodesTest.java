package com.template;


import com.exception.NotFoundTemplateException;
import com.exception.NullException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TemplateNodesTest {

    @Test
    @DisplayName("템플릿 등록 테스트")
    void testRegisterTemplateNode() {
        //given
        TemplateNodes nodes = new TemplateNodes();
        String templateTxt1 = "a";
        String replaceTxt1 = "apple";
        String templateTxt2 = "b";
        String replaceTxt2 = "banana";
        int expectedMaxLength = Math.max(templateTxt1.length(),templateTxt2.length());

        //when
        nodes.register(templateTxt1,replaceTxt1);
        nodes.register(templateTxt2,replaceTxt2);

        String actual1 = nodes.replace(templateTxt1);
        String actual2 = nodes.replace(templateTxt2);
        int actualMaxLength = nodes.getTemplateMaxLength();

        //then
        Assertions.assertThat(actual1).isEqualTo(replaceTxt1);
        Assertions.assertThat(actual2).isEqualTo(replaceTxt2);
        Assertions.assertThat(actualMaxLength).isEqualTo(expectedMaxLength);
    }

    @Test
    @DisplayName("디폴트값을 지정하여 대체 텍스트 검색 테스트")
    void testReplaceWithDefault() {
        //given
        TemplateNodes nodes = new TemplateNodes();
        String expected = "hello";

        //when
        String actual = nodes.replaceWithDefault("NotRegisterTemplate",expected);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 템플릿 등록시 에러 발생 테스트")
    void testRegisterTemplateNodeWithNull() {
        //given
        TemplateNodes nodes = new TemplateNodes();

        Assertions.assertThatThrownBy(()->
                nodes.register(null,"hello")
        ).isInstanceOf(NullException.class);
        Assertions.assertThatThrownBy(()->
                nodes.register("world",null)
        ).isInstanceOf(NullException.class);
    }

    @Test
    @DisplayName("null 로 대체 텍스트 검색시 에러 발생 테스트")
    void testReplaceWithNull() {
        //given
        TemplateNodes nodes = new TemplateNodes();
        nodes.register("hello","world");

        Assertions.assertThatThrownBy(()->
                nodes.replace(null)
        ).isInstanceOf(NullException.class);
    }

    @Test
    @DisplayName("등록하지 않은 템플릿 문구로 대체 텍스트 검색시 에러 발생 테스트")
    void testReplaceWithNotRegisterTemplate() {
        //given
        TemplateNodes nodes = new TemplateNodes();

        Assertions.assertThatThrownBy(()->
                nodes.replace("hello")
        ).isInstanceOf(NotFoundTemplateException.class);
    }

}