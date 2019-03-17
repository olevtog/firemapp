package com.valette.defossez.firemapp;

import android.support.test.filters.LargeTest;
import android.test.ActivityInstrumentationTestCase2;

import com.valette.defossez.firemapp.activity.HomeActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
public class HomeActivityTest2 extends ActivityInstrumentationTestCase2<HomeActivity> {

    //on précise que l'on veut tester un MainActivity
    public HomeActivityTest2() {
        super(HomeActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Test
    public void testContainsIntialViews() {
        onView(withId(R.id.search)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOpenMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.boutonLocalisation)).check(matches(isDisplayed()));
    }

}