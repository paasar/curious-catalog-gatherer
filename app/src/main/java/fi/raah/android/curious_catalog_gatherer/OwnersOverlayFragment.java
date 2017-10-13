package fi.raah.android.curious_catalog_gatherer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public class OwnersOverlayFragment extends Fragment {

    List<Ownage> ownageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.owners_overlay_fragment, container, false);
    }
}
