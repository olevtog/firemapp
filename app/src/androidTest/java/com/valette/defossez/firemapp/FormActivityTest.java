package com.valette.defossez.firemapp;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.valette.defossez.firemapp.activity.FormAddFireworkActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class FormActivityTest {

    @Rule
    public ActivityTestRule<FormAddFireworkActivity> activityActivityTestRule = new ActivityTestRule<>(FormAddFireworkActivity.class);


    @Before
    public void init() {
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void Test() throws Throwable {
        activityActivityTestRule.runOnUiThread(new Runnable() {
            public void run() {
                activityActivityTestRule.launchActivity(new Intent()).validate();
            }
        });
    }

}