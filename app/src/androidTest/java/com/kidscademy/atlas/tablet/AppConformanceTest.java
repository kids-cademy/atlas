package com.kidscademy.atlas.tablet;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.activity.MainActivity;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.SearchIndex;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.onDisplayView;
import static com.kidscademy.atlas.Util.sleep;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withIdPath;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppConformanceTest {
    @Rule
    public ActivityTestRule<MainActivity> activityLauncher = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void beforeTest() {
        // explorer pages save current page; ensure test starts always from first page
        App.instance().flags().setExplorerPosition(0);
    }

    @After
    public void afterTest() {
        //waitView(withId(R.id.main_close)).perform(click());
    }

    @Test
    public void openActivitiesFromCoverPage() {
        // there is a mysterious behaviour when testing trademark actions:
        // when perform click on 'no ads' view the action is actually executed on 'we play' action
        // while 'we play' action is not found

        // since on manual testing everything seems correct i tend to beleive is something on testing framework

        // no ads manifesto activity
        onView(withId(R.id.action_no_ads)).perform(click());
        // go back to cover page with back arrow from action bar
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

/*
        // we play experience activity
        sleep(500);
        onView(withId(R.id.action_we_play)).perform(click());
        // go back to cover page with back arrow from action bar
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());
*/
        // main menu activity
        sleep(500);
        onView(withId(R.id.action_forward)).perform(click());
        // go back to cover page with back arrow from action bar
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());
    }

    @Test
    public void openActivitiesFromMainMenu() {
        onView(withId(R.id.action_forward)).perform(click());

        // atlas reader activity
        sleep(500);
        onView(withId(R.id.menu_reader)).perform(click());
        // go back to main menu with back arrow from action bar
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

        sleep(1000);
        waitView(withId(R.id.menu_reader)).perform(click());
        // go back to main menu with tablet back button
        sleep(500);
        pressBack();

        // search activity
        sleep(1000);
        onView(withId(R.id.menu_search)).perform(click());
        // need a back to hide keyboard
        sleep(500);
        pressBack();
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

        sleep(1000);
        onView(withId(R.id.menu_search)).perform(click());
        sleep(500);
        pressBack();
        sleep(500);
        pressBack();

        // index activity
        sleep(1000);
        onView(withId(R.id.menu_index)).perform(click());
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

        sleep(1000);
        onView(withId(R.id.menu_index)).perform(click());
        sleep(500);
        pressBack();

        // about activity
        sleep(1000);
        onView(withId(R.id.menu_about)).perform(click());
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

        sleep(1000);
        onView(withId(R.id.menu_about)).perform(click());
        sleep(500);
        pressBack();

        // share activity
        sleep(1000);
        onView(withId(R.id.menu_share)).perform(click());
        sleep(500);
        onView(withId(R.id.action_back)).perform(click());

        sleep(1000);
        onView(withId(R.id.menu_share)).perform(click());
        sleep(500);
        pressBack();
    }

    @Test
    public void searchAtlasObject() {
        List<SearchIndex> indices = App.instance().repository().getSearchIndices();
        String keyword = indices.get(0).getKeyword();

        onView(withId(R.id.action_forward)).perform(click());

        // open search activity
        sleep(500);
        onView(withId(R.id.menu_search)).perform(click());

        // type keyword to search input
        sleep(500);
        onView(withId(R.id.search_input)).perform(typeText(keyword));

        // wait for keywords to load and click on item displaying that keyword
        sleep(500);
        onDisplayView(allOf(isDescendantOfA(withId(R.id.search_keywords)), withText(keyword))).perform(click());

        // press on first item from result
        sleep(500);
        onView(childAtPosition(withId(R.id.search_result_objects), 0)).perform(click());

        // wait for reader page to load then close it; reader page loaded is signaled by the presence of the action back icon
        sleep(500);
        onView(withId(R.id.action_menu)).perform(click());

        sleep(500);
        onView(withId(R.id.action_back)).perform(click());
    }

    @Test
    public void selectAtlasObjectFromIndex() {
        onView(withId(R.id.action_forward)).perform(click());

        sleep(500);
        onView(withId(R.id.menu_index)).perform(click());

        sleep(500);
        onView(withIdPath(R.id.index_list_view, 0, R.id.group_list_item_objects, 0)).perform(click());

        // wait for reader page to load then close it; reader page loaded is signaled by the presence of the action back icon
        sleep(500);
        onView(withId(R.id.action_menu)).perform(click());

        sleep(500);
        onView(withId(R.id.action_back)).perform(click());
    }
}
