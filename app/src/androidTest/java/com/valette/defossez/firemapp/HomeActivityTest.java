package com.valette.defossez.firemapp;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.valette.defossez.firemapp.activity.HomeActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> activityActivityTestRule = new ActivityTestRule<>(HomeActivity.class);


    @Before
    public void init() {
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void DonneesDeBaseBonnesTest() {
        HomeActivity homeActivity = activityActivityTestRule.launchActivity(new Intent());
        Assert.assertEquals(homeActivity.getTIME_MOVE_CAMERA_MAX(), 1500);
        Assert.assertEquals(homeActivity.getTIME_MOVE_CAMERA_MIN(), 500);
        Assert.assertEquals(homeActivity.getZOOM_CAMERA(), 12.5f, 0.01);
        Assert.assertEquals(homeActivity.getREMOVE_LATITUDE(), 0.02, 0.01);
        Assert.assertEquals(homeActivity.getDISTANCE_MOVE(), 10000);
        Assert.assertEquals(homeActivity.getDELAY_FIREWORK(), 60 * 60 * 1000);
        Assert.assertTrue(homeActivity.getEMAIL_ADRESS().equals("defossez.valette@gmail.com"));
    }

    @Test
    public void MapTest() {
        HomeActivity homeActivity = activityActivityTestRule.launchActivity(new Intent());
        GoogleMap mMap = homeActivity.mMap;
        Assert.assertNotNull(mMap);
    }
}