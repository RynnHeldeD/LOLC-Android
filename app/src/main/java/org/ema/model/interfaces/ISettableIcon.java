package org.ema.model.interfaces;

import android.graphics.Bitmap;

public interface ISettableIcon {
    public Bitmap getIcon();

    public void setIcon(Bitmap icon);

    public String getIconName();

    public void setIconName(String iconName);
}
