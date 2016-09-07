package org.unhack.bip38decrypt.mfragments;

import android.support.v4.app.Fragment;

import org.unhack.bip38decrypt.mfragments.imFragment;

/**
 * Created by unhack on 6/23/16.
 */
public class mFragment extends Fragment implements imFragment {
    private int id;
    private String name;
    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getTabId(){
        return this.id;
    }

    public void setUniqueName(String name){
        this.name = name;
    }

    public String getUniqueName(){
        return this.name;
    }

}
