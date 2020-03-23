package com.kidscademy.atlas.phone;

import android.os.Looper;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.kidscademy.atlas.Util;
import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.activity.MainActivity;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.RelatedObject;
import com.kidscademy.atlas.view.ReaderObjectLayout;
import com.kidscademy.atlas.view.ReaderPage;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.registerIdlingResources;
import static androidx.test.espresso.Espresso.unregisterIdlingResources;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.atlas.Util.assertVisibility;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.findFirstParentLayoutOfClass;
import static com.kidscademy.atlas.Util.getActivity;
import static com.kidscademy.atlas.Util.isFactExpand;
import static com.kidscademy.atlas.Util.isFactValue;
import static com.kidscademy.atlas.Util.nestedScrollTo;
import static com.kidscademy.atlas.Util.sleep;
import static com.kidscademy.atlas.Util.unconstrainedClick;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withPageId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReaderTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private AtlasRepository repository;

    @BeforeClass
    public static void beforeClass() {
        // file location: /sdcard/ReaderTest.trace
        //Debug.startMethodTracing("ReaderTest");
    }

    @AfterClass
    public static void afterClass() {
        //Debug.stopMethodTracing();
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
    public void quickNavigateAllPages() {
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            onView(allOf(withId(R.id.reader_intro_title), withText(repository.getObjectByIndex(i).getDisplay()))).check(matches(isDisplayed()));
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeLeft());
        }
        for (int i = repository.getObjectsCount() - 1; i >= 0; --i) {
            onView(allOf(withId(R.id.reader_intro_title), withText(repository.getObjectByIndex(i).getDisplay()))).check(matches(isDisplayed()));
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeRight());
        }
    }

    @Test
    public void pagesBrowsing() {
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            final AtlasObject atlasObject = repository.getObjectByIndex(i);
            // wait swipe animation end
            sleep(500);

            // object name is used to signal page loaded
            onView(allOf(withId(R.id.reader_intro_title), withText(atlasObject.getDisplay()))).check(matches(isDisplayed()));

            // it seems there is an optimization on reader object drawing that ignore messages from main loop if not visible,
            // or something like that, and last reader view sections group (number 5) is not loaded
            // as a consequence images are not properly initialized and test fails
            // to force loading use next swipe up
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeUp());

            // all three statements are variants for the same reader object view, for code demo purpose
            onView(withTagValue(is((Object) atlasObject.getTag()))).check(matches(isDisplayed()));
            onView(allOf(withClassName(endsWith(ReaderObjectLayout.class.getSimpleName())), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.reader_page_object_view), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));

            // check if cover and contextual images are loaded, if object has them
            if (atlasObject.hasCoverImage()) {
                onView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }
            if (atlasObject.hasContextualImage()) {
                onView(withTagValue(is((Object) atlasObject.getContextualPath()))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }

            // instrument facts, extra section, related atlas and external sources are optional
            onView(withPageId(atlasObject.getTag(), R.id.reader_facts_view)).check(assertVisibility(atlasObject.hasFacts()));
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).check(assertVisibility(atlasObject.hasRelated()));
            onView(withPageId(atlasObject.getTag(), R.id.reader_links_view)).check(assertVisibility(atlasObject.hasLinks()));

            // swipe back on top
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeDown());

            // need to wait 1 seconds for above swipe down animation to finish otherwise next swipe left is not performed and page is not changed
            sleep(1000);
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeLeft());
        }
    }

    @Test
    public void toggleFactValue() {
        AtlasObject atlasObject = null;

        // browse till found an object with facts
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            atlasObject = repository.getObjectByIndex(i);
            if (atlasObject.hasFacts()) {
                break;
            }
            waitView(withText(repository.getObjectByIndex(i).getDisplay()));
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeLeft());
        }

        assertNotNull(atlasObject);
        assertTrue(atlasObject.hasFacts());

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to facts section
        onView(withPageId(atlasObject.getTag(), R.id.reader_facts_view)).perform(nestedScrollTo());

        // check that first fact value is not displayed
        onView(isFactValue(atlasObject.getTag())).check(matches(not(isDisplayed())));

        // expand first fact
        onView(isFactExpand(atlasObject.getTag())).check(matches(allOf(isDisplayed(), isEnabled(), isClickable()))).perform(click());

        // after fact expanded value should be displayed
        onView(isFactValue(atlasObject.getTag())).check(matches(isDisplayed()));

        // click again the first fact will collapse it
        onView(isFactExpand(atlasObject.getTag())).check(matches(allOf(isDisplayed(), isEnabled(), isClickable()))).perform(unconstrainedClick());

        // on a collapsed fact value is not displayed; not really clear why but need to wait till animation ends
        sleep(700);
        onView(isFactValue(atlasObject.getTag())).check(matches(not(isDisplayed())));
    }

    @Test
    public void scrollToRelatedObjects() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to related objects section
        onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(nestedScrollTo());

        // related objects list should be displayed
        onView(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_caption)).check(matches(isDisplayed()));
    }

    @Test
    public void scrollToLinks() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to external sources links section
        onView(withPageId(atlasObject.getTag(), R.id.reader_links_view)).perform(nestedScrollTo());

        // links list should be displayed
        onView(withPageId(atlasObject.getTag(), R.id.reader_links_view, R.id.reader_links_caption)).check(matches(isDisplayed()));
    }

    private static void waitTitle(String text) {
        final AtomicBoolean fail = new AtomicBoolean(false);
        for(;;) {
            fail.set(false);
            onView(allOf(withId(R.id.reader_intro_title), withText(text))).withFailureHandler(new FailureHandler() {
                @Override
                public void handle(Throwable error, Matcher<View> viewMatcher) {
                    fail.set(true);
                }
            }).check(matches(isDisplayed()));
            if(!fail.get()) {
                break;
            }
            sleep(20);
        }
    }

    //@Test
    public void relatedObjectsNavigation() {
        final Random random = new Random();
        AtlasObject atlasObject = repository.getObjectByIndex(0);

        for (int i = 0, position = 0; i < 20; ++i) {
//            waitTitle(atlasObject.getDisplay());

            // scroll to related objects section and wait animation end
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(nestedScrollTo());

//            final AtomicBoolean fail = new AtomicBoolean(false);
//            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).withFailureHandler(new FailureHandler() {
//                @Override
//                public void handle(Throwable error, Matcher<View> viewMatcher) {
//                    fail.set(true);
//                }
//            }).check(matches(isDisplayed()));
//            if (fail.get()) {
//                continue;
//            }

            // click on related object from current position - position is random generated couple lines below
//            onView(childAtPosition(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_objects), position)).perform(new ViewAction() {
//                @Override
//                public Matcher<View> getConstraints() {
//                    return ViewMatchers.isEnabled(); // no constraints, they are checked above
//                }
//
//                @Override
//                public String getDescription() {
//                    return "click plus button";
//                }
//
//                @Override
//                public void perform(UiController uiController, View view) {
//                    view.performClick();
//                }
//            });

            waitView(childAtPosition(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_objects), position)).perform(click());

                // prepare current object and next random related object position
            RelatedObject relatedObject = atlasObject.getRelated()[position];
            atlasObject = repository.getObjectByIndex(relatedObject.getIndex());
            // limit related object position to not exceed surface display
            position = random.nextInt(Math.min(atlasObject.getRelated().length, 5));
        }
    }
}
