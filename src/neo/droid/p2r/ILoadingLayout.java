
package neo.droid.p2r;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public interface ILoadingLayout {

    public void setLastUpdatedLabel(CharSequence label);

    public void setLoadingDrawable(Drawable drawable);

    public void setPullLabel(CharSequence pullLabel);

    public void setRefreshingLabel(CharSequence refreshingLabel);

    public void setReleaseLabel(CharSequence releaseLabel);

    public void setTextTypeface(Typeface tf);

}
