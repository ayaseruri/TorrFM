package ayaseruri.torr.torrfm.db;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by ayaseruri on 15/12/16.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface SettingPrefs {
    @DefaultBoolean(true)
    boolean isFirstDislikeMusic();
}
