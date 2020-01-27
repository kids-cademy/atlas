package com.kidscademy.atlas.unit;

import com.kidscademy.atlas.model.HTML;

import org.junit.Test;

import java.util.List;

import js.util.Classes;
import js.util.Strings;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class HtmlContentTest {
    @Test
    public void parseParagraph() throws Exception {
        String html = "<p>text</p>";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(1));
        assertThat(elements.get(0), instanceOf(HTML.Paragraph.class));
        assertThat(((HTML.Paragraph) elements.get(0)).getText(), equalTo("text"));
    }

    @Test
    public void parseTwoParagraphs() throws Exception {
        String html = "<p>text1</p><p>text2</p>";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(2));
        assertThat(elements.get(0), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(1), instanceOf(HTML.Paragraph.class));
        assertThat(((HTML.Paragraph) elements.get(0)).getText(), equalTo("text1"));
        assertThat(((HTML.Paragraph) elements.get(1)).getText(), equalTo("text2"));
    }

    @Test
    public void parseListItem() throws Exception {
        String html = "<ul><li>text1</li><li>text2</li></ul>";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(2));

    }

    @Test
    public void parseImage() throws Exception {
        String html = "<img src='accordion.png' />";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(1));
        assertThat(elements.get(0), instanceOf(HTML.Image.class));
        assertThat(elements.get(0).getText(), equalTo("accordion.png"));
    }

    @Test
    public void parseImageWithPathSeparator() throws Exception {
        String html = "<img src='atlas/accordion.png' />";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(1));
        assertThat(elements.get(0), instanceOf(HTML.Image.class));
        assertThat(elements.get(0).getText(), equalTo("atlas/accordion.png"));
    }

    @Test
    public void parseParagraphAndImage() throws Exception {
        String html = "<p>text</p><img src='accordion.png' />";
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(2));
        assertThat(elements.get(0), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(1), instanceOf(HTML.Image.class));
        assertThat(((HTML.Paragraph) elements.get(0)).getText(), equalTo("text"));
        assertThat(elements.get(1).getText(), equalTo("accordion.png"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void parseHtmlContent() throws Exception {
        String html = Strings.load(HTML.class.getResourceAsStream("/content.htm"));
        List<HTML.Element> elements = parse(html);
        assertThat(elements, hasSize(7));

        assertThat(elements.get(0), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(1), instanceOf(HTML.Image.class));
        assertThat(elements.get(2), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(3), instanceOf(HTML.Image.class));
        assertThat(elements.get(4), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(5), instanceOf(HTML.Paragraph.class));
        assertThat(elements.get(6), instanceOf(HTML.Paragraph.class));

        assertThat(((HTML.Paragraph) elements.get(0)).getText(), startsWith("The accordion is a musical"));
        assertThat(elements.get(1).getText(), equalTo("atlas/accordion/piano-accordion.png"));
        assertThat(((HTML.Paragraph) elements.get(2)).getText(), startsWith("The bellows are the section"));
        assertThat(elements.get(3).getText(), equalTo("atlas/accordion/button-accordion.png"));
        assertThat(((HTML.Paragraph) elements.get(4)).getText(), startsWith("The bellows are expanded"));
        assertThat(((HTML.Paragraph) elements.get(5)).getText(), startsWith("The accordion is used"));
        assertThat(((HTML.Paragraph) elements.get(6)).getText(), startsWith("The accordion has also"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void createHtmlObject() throws Exception {
        String content = Strings.load(HTML.class.getResourceAsStream("/content.htm"));
        HTML html = new HTML(content);

        List<HTML.Element> body = html.getElements();
        assertThat(body, hasSize(7));

        assertThat(body.get(0), instanceOf(HTML.Paragraph.class));
        assertThat(body.get(1), instanceOf(HTML.Image.class));
        assertThat(body.get(2), instanceOf(HTML.Paragraph.class));
        assertThat(body.get(3), instanceOf(HTML.Image.class));
        assertThat(body.get(4), instanceOf(HTML.Paragraph.class));
        assertThat(body.get(5), instanceOf(HTML.Paragraph.class));
        assertThat(body.get(6), instanceOf(HTML.Paragraph.class));

        assertThat(((HTML.Paragraph) body.get(0)).getText(), startsWith("The accordion is a musical"));
        assertThat(body.get(1).getText(), equalTo("atlas/accordion/piano-accordion.png"));
        assertThat(((HTML.Paragraph) body.get(2)).getText(), startsWith("The bellows are the section"));
        assertThat(body.get(3).getText(), equalTo("atlas/accordion/button-accordion.png"));
        assertThat(((HTML.Paragraph) body.get(4)).getText(), startsWith("The bellows are expanded"));
        assertThat(((HTML.Paragraph) body.get(5)).getText(), startsWith("The accordion is used"));
        assertThat(((HTML.Paragraph) body.get(6)).getText(), startsWith("The accordion has also"));
    }

    private static List<HTML.Element> parse(String html) throws Exception {
        return Classes.invoke(HTML.class, "parse", html);
    }
}
