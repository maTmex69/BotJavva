package State;

import java.util.prefs.Preferences;
import javax.swing.JInternalFrame;

public abstract class AbstractWindow extends JInternalFrame implements WithState {

    private final String prefixWindowPreferences;
    private final String prefixWindowPositionX;
    private final String prefixWindowPositionY;
    private final String prefixWindowSizeWidth;
    private final String prefixWindowSizeHeight;

    public AbstractWindow() {
        super();
        this.prefixWindowPreferences = formatTitle("window preferences");
        this.prefixWindowPositionX = formatTitle("position x");
        this.prefixWindowPositionY = formatTitle("position y");
        this.prefixWindowSizeWidth = formatTitle("size width");
        this.prefixWindowSizeHeight = formatTitle("size height");
    }

        private Preferences getPreferences() {
        return Preferences.userRoot().node(prefixWindowPreferences);
    }

    private String formatTitle(String title) {
        String cased = title.toUpperCase();
        return cased.replaceAll(" +", "_");
    }

    @Override
    public void saveWindow() {
        Preferences preferences = getPreferences();

        String title = formatTitle(getTitle());

        preferences.putInt(prefixWindowPositionX + title, getX());
        preferences.putInt(prefixWindowPositionY + title, getY());
        preferences.putInt(prefixWindowSizeWidth + title, getWidth());
        preferences.putInt(prefixWindowSizeHeight + title, getHeight());
    }

    @Override
    public void loadWindow() {
        Preferences preferences = getPreferences();
        final int missing = -1;

        String title = formatTitle(getTitle());

        int x = preferences.getInt(prefixWindowPositionX + title, missing);
        int y = preferences.getInt(prefixWindowPositionY + title, missing);
        int width = preferences.getInt(prefixWindowSizeWidth + title, missing);
        int height = preferences.getInt(prefixWindowSizeHeight + title, missing);

        if (x == -1 || y == -1 || width == -1 || height == -1) {
            return;
        }

        setBounds(x, y, width, height);
    }
}
