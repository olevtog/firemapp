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
        Assert.assertEquals(1500, homeActivity.getTIME_MOVE_CAMERA_MAX());
        Assert.assertEquals(500, homeActivity.getTIME_MOVE_CAMERA_MIN());
        Assert.assertEquals(12.5f, homeActivity.getZOOM_CAMERA(), 0.01);
        Assert.assertEquals(0.02,homeActivity.getREMOVE_LATITUDE(),  0.01);
        Assert.assertEquals(10000, homeActivity.getDISTANCE_MOVE());
        Assert.assertEquals(60 * 60 * 1000, homeActivity.getDELAY_FIREWORK());
        Assert.assertTrue(homeActivity.getEMAIL_ADRESS().equals("defossez.valette@gmail.com"));
    }

    @Test
    public void MapTest() {
        HomeActivity homeActivity = activityActivityTestRule.launchActivity(new Intent());
        GoogleMap mMap = homeActivity.mMap;
        Assert.assertNotNull(mMap);
    }
}