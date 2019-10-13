package com.kidscademy.atlas.unit;

import com.kidscademy.atlas.model.CharTree;
import com.kidscademy.atlas.model.SearchIndex;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import js.core.Factory;
import js.json.Json;
import js.lang.GType;
import js.util.Classes;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CharTreeTest {
    private CharTree<SearchIndex> charTree;

    @Before
    public void beforeTest() {
        charTree = new CharTree<>();
    }

    @Test
    public void loadIndex() throws IOException {
        Json json = Factory.getInstance(Json.class);
        List<SearchIndex> indices = json.parse(Classes.getResourceAsReader("search-index.json"), new GType(List.class, SearchIndex.class));
        CharTree<SearchIndex> charTree = new CharTree<>(indices);

//        indices = charTree.find("a");
//        assertNotNull(indices);
//        assertEquals(10, indices.getObjectsCount());

        indices = charTree.find("per");
        assertThat(indices, notNullValue());
        assertThat(indices, hasSize(1));
    }

    @Test
    public void subwords() {
        List<SearchIndex> indices = new ArrayList<>();
        indices.add(new SearchIndex("indian", 0, 1, 2));
        indices.add(new SearchIndex("india", 1, 2, 3));
        CharTree<SearchIndex> charTree = new CharTree<>(indices);
        charTree.dump();

        indices = charTree.find("i");
        assertThat(indices, notNullValue());
        assertThat(indices, hasSize(2));
    }

    @Test
    public void search() {
        List<SearchIndex> indices = new ArrayList<>();
        indices.add(new SearchIndex("accordion", 0, 1, 2));
        indices.add(new SearchIndex("alphorh", 1, 2, 3));
        indices.add(new SearchIndex("bansuri", 2, 3, 4));

        CharTree<SearchIndex> charTree = new CharTree<>(indices);

        indices = charTree.find("ban");
        assertThat(indices, notNullValue());
        assertThat(indices, not(empty()));
    }

    @Test
    public void searchEmptyTree() {
        List<SearchIndex> indices = charTree.find("ban");
        assertThat(indices, notNullValue());
        assertThat(indices, empty());
    }

    @Test
    public void serialization() {
        List<SearchIndex> indices = new ArrayList<>();
        indices.add(new SearchIndex("accordion", 0, 1, 2));
        indices.add(new SearchIndex("alphorh", 1, 2, 3));
        indices.add(new SearchIndex("bansuri", 2, 3, 4));

        Json json = Classes.loadService(Json.class);
        String indicesJson = json.stringify(indices);
        System.out.println(indicesJson);

        List<SearchIndex> loadedIndices = json.parse(indicesJson, new GType(List.class, SearchIndex.class));
        CharTree<SearchIndex> charTree = new CharTree<>(loadedIndices);

        indices = charTree.find("ban");
        assertThat(indices, notNullValue());
        assertThat(indices, not(empty()));
    }
}
