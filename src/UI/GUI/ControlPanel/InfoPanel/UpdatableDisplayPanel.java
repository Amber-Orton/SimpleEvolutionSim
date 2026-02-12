package UI.GUI.ControlPanel.InfoPanel;

import java.awt.event.MouseEvent;

import Things.Helpers.Position;
import UI.GUI.UpdatableJPanel;

public abstract class UpdatableDisplayPanel extends UpdatableJPanel{
    public abstract void click(Position position, MouseEvent event);
}
