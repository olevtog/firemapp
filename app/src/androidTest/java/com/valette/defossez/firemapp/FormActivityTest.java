package com.valette.defossez.firemapp;

import android.support.test.filters.LargeTest;
import android.test.ActivityInstrumentationTestCase2;

import com.valette.defossez.firemapp.activity.FormAddFireworkActivity;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

@LargeTest
public class FormActivityTest extends ActivityInstrumentationTestCase2<FormAddFireworkActivity> {

    //on précise que l'on veut tester un MainActivity
    public FormActivityTest() {
        super(FormAddFireworkActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        //doit être appelé dans le setup
        getActivity();
    }

    @Test
    public void testContainsIntialViews() {
        onView(withId(R.id.input_layout_address)).check(matches(isDisplayed()));
    }



}