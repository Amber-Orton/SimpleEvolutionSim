package Run.GUI.ControlPanel.ButtonPanel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import Logger.Logger;
import Run.Main;

public class DebugOptionsDialog {
    public static void showNewWorldDialog(Component parentRef) {
        Window mainWin = (parentRef instanceof Window) ? (Window) parentRef : SwingUtilities.getWindowAncestor(parentRef);
        JDialog dlg = new JDialog(mainWin, "Debug Options", Dialog.ModalityType.MODELESS);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(3, 2));

        JButton printDebugInfoButton = new JButton("Print Debug Info to Console");
        printDebugInfoButton.addActionListener(printDebugInfoButtonClicked);

        JButton inDepthDebugButton = new JButton(Logger.isInDepthDebugMode() ? "Disable In-Depth Debug Logging" : "Enable In-Depth Debug Logging");
        inDepthDebugButton.addActionListener(inDepthDebugButtonClicked);

        JButton autoPrintDebugInfoButton = new JButton(Logger.isAutoDebug() ? "Disable Auto Print Debug Info" : "Enable Auto Print Debug Info");
        autoPrintDebugInfoButton.addActionListener(autoPrintDebugInfoButtonClicked);

        JButton worldViewButton = new JButton(Main.isDoUpdateWorldView() ? "Disable World View (Can Improve Performance)" : "Enable World View");
        worldViewButton.addActionListener(worldViewButtonClicked);

        JButton waitForRenderButton = new JButton(Main.isWaitForLongUpdateAfterTick() ? "Disable Wait for Long Update After Tick (Can Improve Performance)" : "Enable Wait for Long Update After Tick");
        waitForRenderButton.addActionListener(waitForRenderButtonClicked);

        JButton loggingEnabledButton = new JButton(Logger.isLoggingEnabled() ? "Disable Logging to File" : "Enable Logging to File");
        loggingEnabledButton.addActionListener(loggingEnabledButtonClicked);

        content.add(printDebugInfoButton);
        content.add(inDepthDebugButton);
        content.add(autoPrintDebugInfoButton);
        content.add(worldViewButton);
        content.add(waitForRenderButton);
        content.add(loggingEnabledButton);

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(mainWin);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }


    private static final java.awt.event.ActionListener printDebugInfoButtonClicked = e -> {
        System.out.println(Logger.getMostRecentDebugInfo(Logger.isInDepthDebugMode()? 100 : 10));
    };

    private static final java.awt.event.ActionListener inDepthDebugButtonClicked = e -> {
        Logger.setInDepthDebugMode(!Logger.isInDepthDebugMode());
        ((JButton)e.getSource()).setText(Logger.isInDepthDebugMode() ? "Disable In-Depth Debug Logging" : "Enable In-Depth Debug Logging");
    };

    private static final java.awt.event.ActionListener autoPrintDebugInfoButtonClicked = e -> {
        Logger.setAutoDebug(!Logger.isAutoDebug());
        ((JButton)e.getSource()).setText(Logger.isAutoDebug() ? "Disable Auto Print Debug Info" : "Enable Auto Print Debug Info");
    };

    private static final java.awt.event.ActionListener worldViewButtonClicked = e -> {
        Main.setDoUpdateWorldView(!Main.isDoUpdateWorldView());
        ((JButton)e.getSource()).setText(Main.isDoUpdateWorldView() ? "Disable World View (Can Improve Performance)" : "Enable World View");
    };

    private static final java.awt.event.ActionListener waitForRenderButtonClicked = e -> {
        Main.setWaitForLongUpdateAfterTick(!Main.isWaitForLongUpdateAfterTick());
        ((JButton)e.getSource()).setText(Main.isWaitForLongUpdateAfterTick() ? "Disable Wait for Long Update After Tick (Can Improve Performance)" : "Enable Wait for Long Update After Tick");
    };

    private static final java.awt.event.ActionListener loggingEnabledButtonClicked = e -> {
        Logger.setLoggingEnabled(!Logger.isLoggingEnabled());
        ((JButton)e.getSource()).setText(Logger.isLoggingEnabled() ? "Disable Logging to File" : "Enable Logging to File");
    };
}
