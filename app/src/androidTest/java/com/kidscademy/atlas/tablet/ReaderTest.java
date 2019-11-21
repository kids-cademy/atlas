package com.kidscademy.atlas.tablet;

import android.view.View;
import android.widget.HorizontalScrollView;

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
import com.kidscademy.atlas.view.HorizontalScrollViewEx;

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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.kidscademy.atlas.Util.assertVisibility;
import static com.kidscademy.atlas.Util.childAtPosition;
import static com.kidscademy.atlas.Util.findFirstParentLayoutOfClass;
import static com.kidscademy.atlas.Util.isFactValue;
import static com.kidscademy.atlas.Util.sleep;
import static com.kidscademy.atlas.Util.waitView;
import static com.kidscademy.atlas.Util.withPageId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

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

        sleep(500);
        onView(withId(R.id.main_picture)).perform(click());
        onView(withId(R.id.menu_reader)).perform(click());

        // on slow devices need to wait a little for reader to open
        sleep(500);
    }

    @After
    public void afterTest() {
//        onView(withId(R.id.activity_atlas_reader_fab_control)).perform(click());
//        waitView(withId(R.id.activity_atlas_reader_fab_close)).perform(click());
        //onView(withId(R.id.main_close)).perform(pressBack());
    }

    @Test
    public void quickNavigateAllPages() {
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            sleep(200);
            waitView(withText(repository.getObjectByIndex(i).getDisplay())).check(matches(isDisplayed()));
            onView(withId(R.id.action_next)).perform(click());
        }
        for (int i = repository.getObjectsCount() - 1; i >= 0; --i) {
            sleep(200);
            waitView(withText(repository.getObjectByIndex(i).getDisplay())).check(matches(isDisplayed()));
            onView(withId(R.id.action_previous)).perform(click());
        }
    }

    @Test
    public void pagesBrowsing() {
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            System.out.println("---------------------------------------------------------");
            final AtlasObject atlasObject = repository.getObjectByIndex(i);

            // for slow devices allow time to render object view
            sleep(500);

            // all three statements are variants for the same reader object view, for code demo purpose
            onView(withTagValue(is((Object) atlasObject.getTag()))).check(matches(isDisplayed()));
            onView(allOf(withClassName(endsWith("ReaderObjectView")), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.reader_object_view), withTagValue(is((Object) atlasObject.getTag())))).check(matches(isDisplayed()));

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

            // swipe horizontally right and back on left
            sleep(1000);
            onView(withId(R.id.reader_page_scroll)).perform(swipeLeft());
            sleep(1000);
            onView(withId(R.id.reader_page_scroll)).perform(swipeRight());

            sleep(500);
            onView(withId(R.id.action_next)).perform(click());
        }
    }

    @Test
    public void toggleFactValue() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to facts section
        onView(withPageId(atlasObject.getTag(), R.id.reader_facts)).perform(horizontalScrollTo());

        // check that first fact value is not displayed
        onView(isFactValue(atlasObject.getTag())).check(matches(isDisplayed()));

        // expand first fact
        //onView(isFactView(atlasObject.getTag())).check(matches(isDisplayed())).perform(click());

        // after fact expanded value should be displayed
        onView(isFactValue(atlasObject.getTag())).check(matches(isDisplayed()));

        // click again the first fact will collapse it
        //onView(isFactView(atlasObject.getTag())).check(matches(isDisplayed())).perform(click());

        // on a collapsed fact value is not displayed; not really clear why but need to wait till animation end
        //sleep(700);
        //onView(isFactValue(atlasObject.getTag())).check(matches(not(isDisplayed())));
    }

    @Test
    public void scrollToRelatedObjects() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to related objects section
        onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(horizontalScrollTo());

        // related objects list should be displayed
        onView(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_caption)).check(matches(isDisplayed()));
    }

    @Test
    public void scrollToLinks() {
        final AtlasObject atlasObject = repository.getObjectByIndex(0);

        // object picture signals page loaded
        waitView(withTagValue(is((Object) atlasObject.getCoverPath()))).check(matches(isDisplayed()));

        // scroll to external sources links section
        onView(withPageId(atlasObject.getTag(), R.id.reader_links_view)).perform(horizontalScrollTo());

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
            onView(withPageId(atlasObject.getTag(), R.id.reader_related_view)).perform(horizontalScrollTo());

            // click on related object from current position - position is random generated couple lines below
            waitView(childAtPosition(withPageId(atlasObject.getTag(), R.id.reader_related_view, R.id.reader_related_objects), position)).perform(click());

            // prepare current object and next random related object position
            RelatedObject relatedObject = atlasObject.getRelated()[position];
            atlasObject = repository.getObjectByIndex(relatedObject.getIndex());
            // limit related object position to not exceed surface display
            position = random.nextInt(Math.min(atlasObject.getRelated().length, 6));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static ViewAction horizontalScrollTo() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return allOf(
                        isDescendantOfA(isAssignableFrom(HorizontalScrollViewEx.class)),
                        withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
            }

            @Override
            public String getDescription() {
                return "View is not HorizontalScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findFirstParentLayoutOfClass(view, HorizontalScrollViewEx.class);
                    if (horizontalScrollView != null) {
                        horizontalScrollView.scrollTo(view.getLeft(), 0);
                    } else {
                        throw new Exception("Unable to find HorizontalScrollView parent.");
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
