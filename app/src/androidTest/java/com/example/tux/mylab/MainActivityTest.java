package com.example.tux.mylab;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.tux.mylab.gallery.GalleryActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void checkOpenImagePicker() throws Exception {
        onView(withId(R.id.pick_image)).perform(ViewActions.click());

        intended(hasComponent(GalleryActivity.class.getName()));
    }
}