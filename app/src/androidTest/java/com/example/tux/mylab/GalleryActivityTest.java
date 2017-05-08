package com.example.tux.mylab;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.tux.mylab.gallery.GalleryActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class GalleryActivityTest {
    @Rule
    public ActivityTestRule<GalleryActivity> activityTestRule = new ActivityTestRule<GalleryActivity>(GalleryActivity.class);

    @Test
    public void hasFab() throws Exception {
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }
}