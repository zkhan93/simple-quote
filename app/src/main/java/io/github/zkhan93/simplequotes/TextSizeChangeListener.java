package io.github.zkhan93.simplequotes;

import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

class TextSizeChangeListener implements SeekBar.OnSeekBarChangeListener {
    public Map<Integer, WeakReference<TextView>> seekbarToViewMap;
    public TextSizeChangeListener() {
        seekbarToViewMap = new HashMap<>();
    }

    public void addViewToUpdate(int seekbarId, TextView view){
        seekbarToViewMap.put(seekbarId, new WeakReference<>(view));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        for (Map.Entry<Integer, WeakReference<TextView>> item :seekbarToViewMap.entrySet()) {
            if(item.getKey() == seekBar.getId()){
                TextView view = item.getValue().get();
                if(view!=null){
                    view.setTextSize(progress);
                    break;
                }
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
