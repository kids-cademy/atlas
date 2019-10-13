package com.kidscademy.atlas.mobile;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.activity.MainActivity;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.RelatedObject;
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

import static androidx.test.espresso.Espresso.onView;
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
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.atlas.Util.assertVisibility;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.findFirstParentLayoutOfClass;
import static com.kidscademy.atlas.Util.isFactExpand;
import static com.kidscademy.atlas.Util.isFactValue;
import static com.kidscademy.atlas.Util.sleep;
import static com.kidscademy.atlas.Util.unconstrainedClick;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withPageId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MobileReaderTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private AtlasRepository repository;

    @BeforeClass
    public static void beforeClass() {
        // file location: /sdcard/MobileReaderTest.trace
        //Debug.startMethodTracing("MobileReaderTest");
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
            // wait for object picture to be loaded as a signal of page complete
            waitView(withTagValue(is((Object) repository.getObjectByIndex(i).getCoverPath()))).check(matches(isDisplayed()));
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeLeft());
        }
        for (int i = repository.getObjectsCount() - 1; i >= 0; --i) {
            waitView(withTagValue(is((Object) repository.getObjectByIndex(i).getCoverPath()))).check(matches(isDisplayed()));
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeRight());
        }
    }

    @Test
    public void pagesBrowsing() {
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            System.out.println("---------------------------------------------------------");
            final AtlasObject atlasObject = repository.getObjectByIndex(i);

            // all three statements are variants for the same reader object view, for code demo purpose
            onView(withTagValue(is((Object) atlasObject.getTag()))).check(matches(isDisplayed()));
            onView(allOf(withClassName(endsWith("ReaderObjectView")), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.reader_page_object_view), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));

            // object name and pictures path are used to signal page loaded
            waitView(withText(atlasObject.getDisplay())).check(matches(isDisplayed()));
            waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));
            if (atlasObject.hasContextualImage()) {
                waitView(withTagValue(is((Object) atlasObject.getContextualPath())));
                // instrument picture is not on visible area but should be loaded
                onView(withTagValue(is((Object) atlasObject.getContextualPath()))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }

            // instrument facts, extra section, related atlas and external sources are optional
            onView(withPageId(atlasObject.getTag(), R.id.reader_facts_view)).check(assertVisibility(atlasObject.hasFacts()));
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).check(assertVisibility(atlasObject.hasRelated()));
            onView(withPageId(atlasObject.getTag(), R.id.reader_links_view)).check(assertVisibility(atlasObject.hasLinks()));

            // swipe vertically down and back on top
            sleep(200);
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeUp());
            sleep(200);
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeDown());

            // need to wait 2 seconds for above swipe down animation to finish otherwise next swipe left is not performed and page is not changed
            sleep(2000);
            onView(withId(R.id.activity_atlas_reader_pager)).perform(swipeLeft());
        }
    }

    @Test
    public void toggleFactValue() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

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

    @Test
    public void relatedObjectsNavigation() {
        final Random random = new Random();
        AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        for (int i = 0, position = 0; i < 20; ++i) {
            // wait for reader page active, signaled by picture loaded
            waitView(withTagValue(is((Object) atlasObject.getContextualPath())));

            // scroll to related objects section
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(nestedScrollTo());

            // click on related object from current position - position is random generated couple lines below
            waitView(childAtPosition(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_objects), position)).perform(click());

            // prepare current object and next random related object position
            RelatedObject relatedObject = atlasObject.getRelated()[position];
            atlasObject = repository.getObjectByIndex(relatedObject.getIndex());
            // limit related object position to not exceed surface display
            position = random.nextInt(Math.min(atlasObject.getRelated().length, 6));
        }
    }

    // --------------------------------------------------------------------------------------------
    // UTILITY METHODS

    private static ViewAction nestedScrollTo() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return allOf(
                        isDescendantOfA(isAssignableFrom(NestedScrollView.class)),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
            }

            @Override
            public String getDescription() {
                return "View is not NestedScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    NestedScrollView nestedScrollView = (NestedScrollView) findFirstParentLayoutOfClass(view, ReaderPage.class);
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, view.getTop());
                    } else {
                        throw new Exception("Unable to find NestedScrollView parent.");
                    }
                } catch (Exception e) {
                    throw new PerformException.Builder()
                            .withActionDescription(this.getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build();
                }
                uiController.loopMainThreadUntilIdle();
            }

        };
    }
}
