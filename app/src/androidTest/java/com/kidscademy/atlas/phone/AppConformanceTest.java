package com.kidscademy.atlas.phone;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.activity.MainActivity;
import com.kidscademy.atlas.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.onDisplayView;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withIdPath;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppConformanceTest {
    @Rule
    public ActivityTestRule<MainActivity> activityLauncher = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void beforeTest() {
        // explorer pages save current page; ensure test starts always from first page
        App.instance().flags().setExplorerPosition(0);

        onView(withId(R.id.main_picture)).perform(click());
    }

    @After
    public void afterTest() {
        waitView(withId(R.id.menu_close)).perform(click());
    }

    @Test
    public void openActivities() {
        // atlas reader activity
        onView(withId(R.id.menu_reader)).perform(click());
        onView(withId(R.id.activity_reader_fab_control)).perform(click());
        onView(withId(R.id.activity_reader_fab_main_menu)).perform(click());

        onView(withId(R.id.menu_reader)).perform(click());
        pressBack();

        // search activity
        onView(withId(R.id.menu_search)).perform(click());
        // need a back to hide keyboard
        pressBack();
        waitView(withId(R.id.search_fab_control)).perform(click());
        onView(withId(R.id.search_fab_main_menu)).perform(click());

        onView(withId(R.id.menu_search)).perform(click());
        pressBack();
        pressBack();

        // index activity
        onView(withId(R.id.menu_index)).perform(click());
        onView(withId(R.id.index_fab_control)).perform(click());
        onView(withId(R.id.index_fab_main_menu)).perform(click());

        onView(withId(R.id.menu_index)).perform(click());
        pressBack();

        // about activity
        onView(withId(R.id.menu_about)).perform(click());
        onView(withId(R.id.action_back)).perform(click());

        onView(withId(R.id.menu_about)).perform(click());
        pressBack();

        // share activity
        onView(withId(R.id.menu_share)).perform(click());
        onView(withId(R.id.action_back)).perform(click());

        onView(withId(R.id.menu_share)).perform(click());
        pressBack();
    }

    @Test
    public void searchAtlasObject() {
        // open search activity
        onView(withId(R.id.menu_search)).perform(click());

        // type letter 'a' to search input
        onView(withId(R.id.search_input)).perform(typeText("accord"));

        // wait for keywords to load and click on item 'accordion'
        onDisplayView(withText("accordion")).perform(click());

        // press on first item from result
        onView(childAtPosition(withId(R.id.search_result_objects), 0)).perform(click());

        // wait for explorer page to loaded then close it; explorer page loaded is signaled by the presence of the fab menu
        onView(withId(R.id.activity_reader_fab_control)).perform(click());
        onView(withId(R.id.activity_reader_fab_main_menu)).perform(click());
    }

    @Test
    public void selectAtlasObjectFromIndex() {
        onView(withId(R.id.menu_index)).perform(click());

        onView(withIdPath(R.id.index_list_view, 0, R.id.group_list_item_objects, 0)).perform(click());

        // wait for reader page to loaded then close it; reader page loaded is signaled by the presence of the fab menu
        onView(withId(R.id.activity_reader_fab_control)).perform(click());
        onView(withId(R.id.activity_reader_fab_main_menu)).perform(click());
    }
}
