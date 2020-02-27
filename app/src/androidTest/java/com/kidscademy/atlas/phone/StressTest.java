package com.kidscademy.atlas.phone;

import android.util.Log;

import androidx.test.rule.ActivityTestRule;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.MainActivity;
import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.RelatedObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.nestedScrollTo;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withPageId;

public class StressTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private AtlasRepository repository;

    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void beforeTest() {
        repository = App.instance().repository();
        // explorer pages save current page; ensure test starts always from first page
        App.instance().flags().setExplorerPosition(0);

        onView(withId(R.id.main_picture)).perform(click());
        onView(withId(R.id.menu_reader)).perform(click());
    }

    @After
    public void afterTest() {
        onView(withId(R.id.activity_reader_fab_control)).perform(click());
        waitView(withId(R.id.activity_reader_fab_main_menu)).perform(click());
        //onView(withId(R.id.main_close)).perform(pressBack());
    }


    @Test
    public void relatedObjectsNavigation() {
        final Random random = new Random();
        AtlasObject atlasObject = repository.getObjectByIndex(0);

        for (int i = 0, position = 0; i < 500; ++i) {
            Log.d("relatedObjectsNavigation", "iteration #" + i);
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(nestedScrollTo());

            waitView(childAtPosition(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_objects), position)).perform(click());

            RelatedObject relatedObject = atlasObject.getRelated()[position];
            atlasObject = repository.getObjectByIndex(relatedObject.getIndex());
            position = random.nextInt(Math.min(atlasObject.getRelated().length, 5));
        }
    }
}
