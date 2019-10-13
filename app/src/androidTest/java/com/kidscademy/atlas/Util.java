package com.kidscademy.atlas;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.kidscademy.atlas.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class Util {
    public static Matcher<View> childAtPosition(Matcher<View> parentMatcher, int position) {
        class ChildAtPosition extends TypeSafeMatcher<View> {
            private final Matcher<View> parentMatcher;
            private final int position;

            private ChildAtPosition(Matcher<View> parentMatcher, int position) {
                this.parentMatcher = parentMatcher;
                this.position = position;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        }
        return new ChildAtPosition(parentMatcher, position);
    }

    public static Matcher<View> firstOf(Matcher<View> parentMatcher) {
        class FirstOf extends TypeSafeMatcher<View> {
            private final Matcher<View> parentMatcher;

            private FirstOf(Matcher<View> parentMatcher) {
                this.parentMatcher = parentMatcher;
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }
                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(0).equals(view);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with first child view of parent ");
                description.appendDescriptionOf(parentMatcher);
            }
        }
        return new FirstOf(parentMatcher);
    }

    private static final int MAX_POSITION = 10;

    public static Matcher<View> withIdPath(int... ids) {
        Matcher<View> matcher = withId(ids[0]);
        for (int i = 1; i < ids.length; ++i) {
            if (ids[i] < MAX_POSITION) {
                matcher = childAtPosition(matcher, ids[1]);
            } else {
                matcher = childWithId(matcher, ids[i]);
            }
        }
        return matcher;
    }

    private static Matcher<View> childWithId(Matcher<View> parentMatcher, int childId) {
        return allOf(isDescendantOfA(parentMatcher), withId(childId));
    }

    public static ViewInteraction waitView(Matcher<View> matcher) {
        for (int j = 0; ; j++) {
            if (j == 200) {
                throw new AssertionError("View not loaded: " + matcher.toString());
            }
            try {
                return onView(allOf(matcher, isDisplayed()));
            } catch (Throwable ignore) {
            }
            sleep(40);
        }
    }

    public static ViewInteraction onDisplayView(Matcher<View> matcher) {
        return onView(allOf(matcher, isDisplayed()));
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * matches view with ID from page identified by given explorer page tag.
     *
     * @param pageTag explorer page tag,
     * @param childId view child ID.
     * @return matcher instance for page child.
     */
    public static Matcher<View> withPageId(Object pageTag, int childId) {
        return allOf(
                isDescendantOfA(withTagValue(is(pageTag))),
                withId(childId));
    }

    public static Matcher<View> withPageId(Object pageTag, int parentId, int childId) {
        return allOf(
                isDescendantOfA(withPageId(pageTag, parentId)),
                withId(childId));
    }

    public static ViewAssertion assertVisibility(boolean visible) {
        return matches(withEffectiveVisibility(visible ? ViewMatchers.Visibility.VISIBLE : ViewMatchers.Visibility.GONE));
    }

    /**
     * Locate first fact view on reader page with requested tag and return descendant view with requested fact child ID.
     * A fact view is displaying a facts, that is a name / value pair. A child fact view is a descendant of a fact view.
     *
     * @param pageTag explorer page tag to search for fact view.
     * @return matcher instance for fact child view.
     */
    public static Matcher<View> isFactValue(Object pageTag) {
        return allOf(
                withId(R.id.item_fact_value),
                isDescendantOfA(isFactView(pageTag)));
    }

    public static Matcher<View> isFactExpand(Object pageTag) {
        return allOf(
                withId(R.id.item_fact_expand),
                isDescendantOfA(isFactView(pageTag)));
    }

    public static Matcher<View> isFactView(Object pageTag) {
        return firstOf(allOf(isDescendantOfA(withTagValue(is(pageTag))), withId(R.id.reader_facts_view)));
    }

    public static View findFirstParentLayoutOfClass(View view, Class<?> clazz) {
        ViewParent parent = new FrameLayout(view.getContext());
        ViewParent incrementView = null;
        int i = 0;
        while (parent != null && !(parent.getClass() == clazz)) {
            if (i == 0) {
                parent = findParent(view);
            } else {
                parent = findParent(incrementView);
            }
            incrementView = parent;
            i++;
        }
        return (View) parent;
    }

    public static ViewParent findParent(View view) {
        return view.getParent();
    }

    public static ViewParent findParent(ViewParent view) {
        return view.getParent();
    }

    public static Activity getActivity() {
        class ResumedActivitiesIterator implements Runnable {
            private Activity activity;

            @Override
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    activity = resumedActivities.iterator().next();
                }
            }
        }

        ResumedActivitiesIterator iterator = new ResumedActivitiesIterator();
        getInstrumentation().runOnMainSync(iterator);
        return iterator.activity;
    }

    /**
     * Click action that does not check for 90% of view area displayed.
     *
     * @return unconstrained click action.
     */
    public static ViewAction unconstrainedClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isEnabled(); // no constraints, they are checked above
            }

            @Override
            public String getDescription() {
                return "click plus button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    // ---------------------------------------------------------------------------------------------

    public static class ViewVisibilityIdlingResource implements IdlingResource {
        private final View view;
        private final int expectedVisibility;

        private boolean idle;
        private IdlingResource.ResourceCallback resourceCallback;

        public ViewVisibilityIdlingResource(final View view, final int expectedVisibility) {
            this.view = view;
            this.expectedVisibility = expectedVisibility;
        }

        @Override
        public final String getName() {
            return getClass().getSimpleName();
        }

        @Override
        public final boolean isIdleNow() {
            idle = idle || view.getVisibility() == expectedVisibility;

            if (idle) {
                if (resourceCallback != null) {
                    resourceCallback.onTransitionToIdle();
                }
            }

            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
    }
}
