package com.kidscademy.atlas.unit;

import com.kidscademy.atlas.model.AtlasObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import js.json.Json;
import js.util.Classes;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.io.FileMatchers.anExistingDirectory;
import static org.junit.Assert.*;

@Ignore
public class RepositoryObjectTest {
    @Before
    public void beforeTest() {
    }

    @After
    public void afterTest() {
    }

    @Test
    public void loadObjects() throws IOException {
        Json json = Classes.loadService(Json.class);

        File collectionDir = new File("src/main/assets/atlas");
        assertThat(collectionDir, anExistingDirectory());

        List<AtlasObject> objects = new ArrayList<>();
        for (File objectDir : collectionDir.listFiles()) {
            if (!objectDir.isDirectory()) {
                continue;
            }
            assertThat(objectDir, anExistingDirectory());
            AtlasObject object = json.parse(new FileReader(new File(objectDir, "object_en.json")), AtlasObject.class);
            System.out.println(json.stringify(object));
            objects.add(object);
        }

        assertThat(objects, not(empty()));
    }
}