package org.unhack.bip38decrypt;

import android.support.v4.app.Fragment;

/**
 * Created by unhack on 6/23/16.
 */
public class mFragment extends Fragment implements imFragment {
    private int id;
    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getTabId(){
        return this.id;
    }


}
