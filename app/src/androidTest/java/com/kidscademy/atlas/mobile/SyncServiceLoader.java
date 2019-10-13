package com.kidscademy.atlas.mobile;

import android.view.View;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;

import com.kidscademy.atlas.Util;
import com.kidscademy.atlas.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.kidscademy.atlas.Util.getActivity;

final class SyncServiceLoader {
    private static boolean syncServiceActive;

    static void load() {
        if (syncServiceActive) {
            return;
        }

        onView(withId(R.id.main_picture)).perform(click());

        onView(withId(R.id.menu_sync)).perform(click());
        onView(withId(R.id.sync_start_button)).perform(click());

        // wait synchronization message to become visible, signaling that browser was connected to sync service
        final IdlingResource idlingResource = new Util.ViewVisibilityIdlingResource(getActivity().findViewById(R.id.sync_start_success), View.VISIBLE);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.sync_start_success)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        IdlingRegistry.getInstance().unregister(idlingResource);

        onView(withId(R.id.action_back)).perform(click());
        pressBack();

        syncServiceActive = true;
    }
}
