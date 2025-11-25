package Run;

import javax.swing.*;

import Logger.Logger;
import Run.World.Snapshot;
import Things.Animal;
import Things.Egg;
import Things.Food;
import Things.Thing;
import Things.Wall;
import Things.Helpers.HasAppearance;
import Things.Helpers.NameCreator;
import Things.Helpers.Position;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.function.Consumer;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main GUI for the Simple Evolution Simulation.
 */
public class GUI {
    private World world;
    private static GUI instance;
    private WorldGridPanel worldGridPanel;

    private JTextField tickCountDisplay;
    private JTextArea selectedThingNameText;
    private JTextArea selectedThingInfoTextLeft;
    private JTextArea selectedThingInfoTextRight;
    private JLabel reportedmspt;
    private JTextField ticksToRun;
    private Thing selectedThing;
    protected JButton playPauseButton;

    private final WorldPlayer worldPlayer = new WorldPlayer();
    private static boolean optionsShown = false;

    private boolean updateWorldViewWorking = false;

    public static GUI getInstance() {
        return instance;
    }

    public static GUI getInstanceOrChangeWorld(World world) {
        if (instance == null) {
            instance = new GUI(world);
        } else {
            instance.world = world;
        }
        return instance;
    }

    private GUI(World world) {
        this.world = world;
    }

    public void run() {
        JFrame frame = new JFrame("Simple Evolution Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    Main.play = false;
                    System.out.println("Game stopped!");
                    NameCreator.close();
                    Main.executorService.shutdown();
                    worldGridPanel.renderExecutor.shutdown();
                    if (Main.LOGGING_ENABLED) {
                        Logger.write();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        frame.setSize(900, 700);

        // Use BorderLayout so right panel does not slide when grid world size changes.
        frame.getContentPane().setLayout(new BorderLayout());

        // LEFT (grid + tick)
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel tickPanel = createTickPanel();
        leftPanel.add(tickPanel, BorderLayout.NORTH);
        JPanel gridPanel = createGridPanel();
        leftPanel.add(gridPanel, BorderLayout.CENTER);
        JPanel resetPanel = createResetPanel();
        leftPanel.add(resetPanel, BorderLayout.SOUTH);
        // Fix a reasonable minimum width; remaining space flexes without pushing right panel.
        leftPanel.setMinimumSize(new Dimension(300, 300));

        // RIGHT (controls)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(400, 700)); // fixed width, prevents sliding
        createControlPanel(rightPanel);

        frame.getContentPane().add(leftPanel, BorderLayout.CENTER);
        frame.getContentPane().add(rightPanel, BorderLayout.EAST);

        frame.setVisible(true);

        if (Main.SHOW_OPTIONS_ON_FIRST_OPEN && !optionsShown) {
            optionsShown = true;
            SwingUtilities.invokeLater(() -> showNewWorldDialog(frame));
        }
    }

    private void updateWorldView(long startTime) {
        if (worldGridPanel != null) {
            // Render off the EDT and update UI when done.
            worldGridPanel.renderWorldAsync(startTime, () -> {
                updateWorldViewWorking = false;
            });
        } else {
            Main.lastUpdateWorldViewTotalTime = System.nanoTime() - startTime;
            updateWorldViewWorking = false;
        }
    }

    private JPanel createGridPanel() {
        worldGridPanel = new WorldGridPanel(world, this::displayThingInfo);
        return worldGridPanel;
    }

    private void createControlPanel(JPanel panel) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Selected thing name
        selectedThingNameText = new JTextArea("Click on a Thing to show its name here");
        selectedThingNameText.setLineWrap(true);
        selectedThingNameText.setWrapStyleWord(true);
        selectedThingNameText.setEditable(false);
        selectedThingNameText.setMaximumSize(new Dimension(300, 60));
        controlPanel.add(selectedThingNameText);

        // Stats and Neural Net panels
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        selectedThingInfoTextLeft = new JTextArea("Click on a Thing to show its information here");
        selectedThingInfoTextRight = new JTextArea("");
        selectedThingInfoTextLeft.setEditable(false);
        selectedThingInfoTextRight.setEditable(false);
        selectedThingInfoTextLeft.setLineWrap(true);
        selectedThingInfoTextLeft.setWrapStyleWord(true);
        selectedThingInfoTextRight.setLineWrap(true);
        selectedThingInfoTextRight.setWrapStyleWord(true);
        infoPanel.add(wrapInScrollIfNeeded(selectedThingInfoTextLeft));
        infoPanel.add(wrapInScrollIfNeeded(selectedThingInfoTextRight));
        infoPanel.setPreferredSize(new Dimension(400, 200));
        controlPanel.add(infoPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // World Stats
        JPanel worldStatsPanel = WorldStatsPanel.create();
        controlPanel.add(worldStatsPanel);

        controlPanel.add(Box.createVerticalGlue());
        panel.add(controlPanel);
    }

    private static JComponent wrapInScrollIfNeeded(JTextArea comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    private JPanel createTickPanel() {
        JPanel tickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tickCountDisplay = new JTextField("Tick Count: " + world.getTickCount());
        tickCountDisplay.setColumns(10);
        tickCountDisplay.setEditable(false);
        tickPanel.add(tickCountDisplay);

        JButton tickButton = new JButton("Tick");
        tickButton.addActionListener(e -> tick());
        tickPanel.add(tickButton);

        JButton debugButton = new JButton("show debug info");
        debugButton.addActionListener(e -> {
            printDebugInfo();
        });
        tickPanel.add(debugButton);

        JButton toggleDebugButton = new JButton(Main.IN_DEPTH_DEBUG_MODE ? "Disable in-depth Debug" : "Enable in-depth Debug");
        toggleDebugButton.addActionListener(e -> {
            Main.IN_DEPTH_DEBUG_MODE = !Main.IN_DEPTH_DEBUG_MODE;
            toggleDebugButton.setText(Main.IN_DEPTH_DEBUG_MODE ? "Disable in-depth Debug" : "Enable in-depth Debug");
        });
        tickPanel.add(toggleDebugButton);

        JButton toggleAutoDebugButton = new JButton(Main.autoDebug ? "Disable Auto Debug" : "Enable Auto Debug");
        toggleAutoDebugButton.addActionListener(e -> {
            Main.autoDebug = !Main.autoDebug;
            toggleAutoDebugButton.setText(Main.autoDebug ? "Disable Auto Debug" : "Enable Auto Debug");
        });
        tickPanel.add(toggleAutoDebugButton);

        JButton toggleUpdateWorldViewButton = new JButton(Main.doUpdateWorldView ? "Disable World View" : "Enable World View");
        toggleUpdateWorldViewButton.addActionListener(e -> {
            Main.doUpdateWorldView = !Main.doUpdateWorldView;
            toggleUpdateWorldViewButton.setText(Main.doUpdateWorldView ? "Disable World View" : "Enable World View");
            if (Main.doUpdateWorldView && !updateWorldViewWorking) {
                updateWorldViewWorking = true;
                Main.lastUpdateWorldViewStartTime = System.nanoTime();
                updateWorldView(Main.lastUpdateWorldViewStartTime);
            }
        });
        tickPanel.add(toggleUpdateWorldViewButton);

        JButton toggleWaitForLongUpdateButton = new JButton(Main.waitForLongUpdateAfterTick ? "Disable Wait for Render" : "Enable Wait for Render");
        toggleWaitForLongUpdateButton.addActionListener(e -> {
            Main.waitForLongUpdateAfterTick = !Main.waitForLongUpdateAfterTick;
            toggleWaitForLongUpdateButton.setText(Main.waitForLongUpdateAfterTick ? "Disable Wait for Render" : "Enable Wait for Render");
        });
        tickPanel.add(toggleWaitForLongUpdateButton);

        return tickPanel;
    }

    private void printDebugInfo() {
        System.out.println();
        System.out.println("----------- DEBUG INFO -----------");
        System.out.println();
        System.out.println("Tick Time (see below if using Auto Debug) (ns): " + Main.lastTickTime + " seconds: " + (Main.lastTickTime / 1_000_000_000.0));
        System.out.println("Tick Count: " + world.getTickCount());
        System.out.println("Things in World: " + world.getThings().size());
        //System.out.println("Nothing Grid: " + Arrays.deepToString(world.nothingGrid));
        //System.out.println("Changed Grid: " + Arrays.deepToString(world.changedGrid));
        if (Main.IN_DEPTH_DEBUG_MODE) {
            System.out.println();
            System.out.println("------------- WORLD DEBUG INFO -------------");
            System.out.println();
            System.out.println(Logger.getLogEvent("Tick: " + world.getTickCount()));
            System.out.println(Logger.getLogEvent("WorldPlayer Tick: " + world.getTickCount()));
            System.out.println();
            System.out.println("------------- UPDATE AFTER TICK DEBUG INFO -------------");
            System.out.println();
            System.out.println(Logger.getLogEvent("updateAfterTick Tick: " + world.getTickCount()));
            System.out.println();
            System.out.println("------------- UPDATE WORLD VIEW DEBUG INFO -------------");
            System.out.println();
            System.out.println("time to complete last completed update world view from initialisation: " + Main.lastUpdateWorldViewTotalTime + " ns (" + (Main.lastUpdateWorldViewTotalTime / 1_000_000.0) + " ms)");
            System.out.println("actual time spent updating world view (excluding waiting): " + Main.lastUpdateWorldViewActualTime + " ns (" + (Main.lastUpdateWorldViewActualTime / 1_000_000.0) + " ms)");
        } else {
            System.out.println("for more indepth debugging information, enable in-depth debug mode.");
        }

    }
    
    private void displayThingInfo(int row, int col){
        if (!world.posIsNothing(new Position(row, col))) {
            selectedThing = world.getThingAt(row, col);
            selectedThingNameText.setText(selectedThing.getName());
            updateSelectedThingInfo(selectedThing);
        }
    }

    private void updateSelectedThingInfo(Thing thing) {
        if (thing == null) { return; }
        ThingInfoDisplay.display(thing, selectedThingInfoTextLeft, selectedThingInfoTextRight, 
                                 t -> { selectedThing = t; updateSelectedThingInfo(t); });
    }

    private void tick() {
        synchronized (world) {
            long startTime = System.nanoTime();
            worldPlayer.queueOneTick(startTime);
        }
    }

    protected void updateAfterTick(long startTime) {
        int tick = world.getTickCount();
        
        Logger.logEvent("updateAfterTick Tick: " + tick, "Start");
        
        ticksToRun.setText(Integer.toString(Main.ticksToRun));
        tickCountDisplay.setText("Tick Count: " + world.getTickCount());
        reportedmspt.setText("Actual: MSPT: " + Main.lastTickTime / 1_000_000.0 + ", TPS: " + 1_000_000_000.0 / Main.lastTickTime + ", Last render time(ms): " + Main.lastUpdateWorldViewTotalTime / 1_000_000.0 + ", Current render time(ms): " + (System.nanoTime() - Main.lastUpdateWorldViewStartTime) / 1_000_000.0);
        
        if (Main.waitForLongUpdateAfterTick) {
            if (!updateWorldViewWorking && Main.doUpdateWorldView) {
                updateWorldViewWorking = true;
                Main.lastUpdateWorldViewStartTime = System.nanoTime();
                updateWorldView(Main.lastUpdateWorldViewStartTime);
            }
            
            Logger.logEvent("updateAfterTick Tick: " + tick, "Updated World View");
            updateSelectedThingInfo(selectedThing);
            Main.lastTickTime = System.nanoTime() - startTime;
            Logger.logEvent("updateAfterTick Tick: " + tick, "Updated Selected Thing Info");
            if (Main.autoDebug) {
                printDebugInfo();
                Logger.logEvent("updateAfterTick Tick: " + tick, "Printed Debug Info");
            }
        } else {
            SwingUtilities.invokeLater(() -> {
                if (!updateWorldViewWorking && Main.doUpdateWorldView) {
                    updateWorldViewWorking = true;
                    Main.lastUpdateWorldViewStartTime = System.nanoTime();
                    updateWorldView(Main.lastUpdateWorldViewStartTime);
                }
                
                Logger.logEvent("updateAfterTick Tick: " + tick, "Updated World View");
                updateSelectedThingInfo(selectedThing);
                Main.lastTickTime = System.nanoTime() - startTime;
                Logger.logEvent("updateAfterTick Tick: " + tick, "Updated Selected Thing Info");
                if (Main.autoDebug) {
                    printDebugInfo();
                    Logger.logEvent("updateAfterTick Tick: " + tick, "Printed Debug Info");
                }
            });
        }
    }

    private JPanel createResetPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel newWorldRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton newWorldButton = new JButton("New World...");
        playPauseButton = new JButton(Main.play ? "Pause" : "Play");
        reportedmspt = new JLabel("Actual: MSPT: " + Main.lastTickTime / 1_000_000.0 + ", TPS: " + 1_000_000_000.0 / Main.lastTickTime);
        newWorldRow.add(newWorldButton);
        content.add(newWorldRow);
        
        
        newWorldButton.addActionListener(e -> {Main.play = false;
            playPauseButton.setText(Main.play ? "Pause" : "Play");
            showNewWorldDialog(rootPanel);
        });
        
        JPanel msptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        msptPanel.setBorder(BorderFactory.createTitledBorder("MSPT"));
        
        JTextField msptField = new JTextField(Integer.toString(Main.tickMillis), 8);
        msptField.setMaximumSize(new Dimension(100, 28));
        
        Runnable applyMspt = () -> {
            try {
                int val = Integer.parseInt(msptField.getText().trim());
                if (val < 0) val = 0;
                Main.tickMillis = val;
            } catch (NumberFormatException ex) {
                msptField.setText(Integer.toString(Main.tickMillis));
            }
        };
        
        msptField.addActionListener(e -> applyMspt.run());
        msptField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyMspt.run();
            }
        });

        msptPanel.add(new JLabel("Target: "));
        msptPanel.add(msptField);
                
        ticksToRun = new JTextField("-1",8);
        ticksToRun.setMaximumSize(new Dimension(100, 28));
        
        Runnable applyTicksToRun = () -> {
            try {
                int val = Integer.parseInt(ticksToRun.getText().trim());
                if (val < 0) val = 0;
                Main.ticksToRun = val;
            } catch (NumberFormatException ex) {
                ticksToRun.setText(Integer.toString(Main.ticksToRun));
            }
        };
        
        ticksToRun.addActionListener(e -> applyTicksToRun.run());
        ticksToRun.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyTicksToRun.run();
            }
        });

        msptPanel.add(new JLabel("Ticks to Run (-1 for infinite): "));
        msptPanel.add(ticksToRun);
        
         playPauseButton.addActionListener(e -> {
             Main.play = !Main.play;
             playPauseButton.setText(Main.play ? "Pause" : "Play");
             if (Main.play) {
                 Thread worldPlayerThread = new Thread(worldPlayer);
                 worldPlayerThread.start();
             }
         });
         msptPanel.add(playPauseButton);
         msptPanel.add(reportedmspt);
        
         content.add(msptPanel);

        // Wrap content in scroll pane (scrolls if window too small)
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        rootPanel.add(sp, BorderLayout.CENTER);
        return rootPanel;
    }

    private void showNewWorldDialog(Component parentRef) {
        // If parentRef is itself a Window (initial call passes the JFrame), getWindowAncestor returns null.
        // Handle that so the original frame is properly disposed on first creation.
        Window mainWin = (parentRef instanceof Window)
                ? (Window) parentRef
                : SwingUtilities.getWindowAncestor(parentRef);
        JDialog dlg = new JDialog(mainWin, "Create New World", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel dlgContent = new JPanel(new GridBagLayout());
        dlgContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        dlgContent.add(new JLabel("World Width, recommend between 5 and 200"), gbc);
        gbc.gridx = 1;
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(Main.WORLD_WIDTH, 1, Integer.MAX_VALUE, 1));
        dlgContent.add(widthSpinner, gbc);
        gbc.gridx = 0; gbc.gridy++;
        dlgContent.add(new JLabel("World Height, recommend between 5 and 200"), gbc);
        gbc.gridx = 1;
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(Main.WORLD_HEIGHT, 1, Integer.MAX_VALUE, 1));
        dlgContent.add(heightSpinner, gbc);
        gbc.gridx = 0; gbc.gridy++;
        dlgContent.add(new JLabel("Initial Max Neural Net Layers, recommend between 1 and 20"), gbc);
        gbc.gridx = 1;
        JSpinner layersSpinner = new JSpinner(new SpinnerNumberModel(Main.MAX_LAYERS, 1, Integer.MAX_VALUE, 1));
        dlgContent.add(layersSpinner, gbc);
        gbc.gridx = 0; gbc.gridy++;
        dlgContent.add(new JLabel("Initial Animal Density (0..1)"), gbc);
        gbc.gridx = 1;
        JSpinner densitySpinner = new JSpinner(new SpinnerNumberModel((double) Main.INITIAL_ANIMAL_DENSITY, 0.0, 1.0, 0.01));
        ((JSpinner.NumberEditor) densitySpinner.getEditor()).getFormat().setMinimumFractionDigits(2);
        dlgContent.add(densitySpinner, gbc);
        gbc.gridx = 0; gbc.gridy++;
        dlgContent.add(new JLabel("Initial Neural Net Randomness (0..1), recommend 0.1"), gbc);
        gbc.gridx = 1;
        JSpinner randomnessSpinner = new JSpinner(new SpinnerNumberModel((double) Main.INITIAL_NEURAL_NET_RANDOMNESS, 0.0, 1.0, 0.01));
        ((JSpinner.NumberEditor) randomnessSpinner.getEditor()).getFormat().setMinimumFractionDigits(2);
        dlgContent.add(randomnessSpinner, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton create = new JButton("Create");
        buttons.add(cancel);
        buttons.add(create);
        dlgContent.add(buttons, gbc);
        cancel.addActionListener(ev -> dlg.dispose());
        create.addActionListener(ev -> {
            int newW = ((Number) widthSpinner.getValue()).intValue();
            int newH = ((Number) heightSpinner.getValue()).intValue();
            int newMaxLayers = ((Number) layersSpinner.getValue()).intValue();
            float newDensity = ((Number) densitySpinner.getValue()).floatValue();
            float newRandomness = ((Number) randomnessSpinner.getValue()).floatValue();
            Main.WORLD_WIDTH = newW;
            Main.WORLD_HEIGHT = newH;
            Main.MAX_LAYERS = newMaxLayers;
            Main.INITIAL_ANIMAL_DENSITY = newDensity;
            Main.INITIAL_NEURAL_NET_RANDOMNESS = newRandomness;
            Main.updateAttributes();
            World newWorld = Main.createWorld();
            Main.world = newWorld;
            GUI gui = GUI.getInstanceOrChangeWorld(newWorld);
            dlg.dispose();
            if (mainWin != null) mainWin.dispose();
            SwingUtilities.invokeLater(gui::run);
        });
        dlg.setContentPane(dlgContent);
        dlg.pack();
        dlg.setLocationRelativeTo(mainWin);
        dlg.setVisible(true);
    }
}

/**
 * Custom JPanel that paints the entire world grid.
 */
class WorldGridPanel extends JPanel {
    private final World world;
    private final java.util.function.BiConsumer<Integer, Integer> onCellClick;

    // Continuous scaling so large worlds resize smoothly
    private double cellSize = 20.0;
    private int xOffset = 0;
    private int yOffset = 0;

    // Off-EDT buffered rendering
    protected final ExecutorService renderExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "WorldRenderer");
        t.setDaemon(true);
        return t;
    });
    private volatile BufferedImage backBuffer;

    public WorldGridPanel(World world, java.util.function.BiConsumer<Integer, Integer> onCellClick) {
        this.world = world;
        this.onCellClick = onCellClick;
        setBackground(Color.LIGHT_GRAY);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                computeMetrics();
                repaint(); 
            }
        });
    }

    // Compute cell size and centering offsets
    private void computeMetrics() {
        int rows = world.getHeight();
        int cols = world.getWidth();
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0 || rows <= 0 || cols <= 0) {
            cellSize = 1.0;
            xOffset = yOffset = 0;
            return;
        }
        double cw = w / (double) cols;
        double ch = h / (double) rows;
        cellSize = Math.max(0.25, Math.min(cw, ch)); // square cells

        double gridW = cellSize * cols;
        double gridH = cellSize * rows;
        xOffset = (int) Math.round((w - gridW) / 2.0);
        yOffset = (int) Math.round((h - gridH) / 2.0);
    }


    // Map pixel to logical index (clamped)
    private int colFromX(int x) {
        int col = (int) Math.floor((x - xOffset) / cellSize);
        return Math.min(Math.max(col, 0), world.getWidth() - 1);
    }
    private int rowFromY(int y) {
        int row = (int) Math.floor((y - yOffset) / cellSize);
        return Math.min(Math.max(row, 0), world.getHeight() - 1);
    }


    // Off-EDT full-frame render into a BufferedImage, then swap on EDT and repaint.
    public void renderWorldAsync(long startTime, Runnable onDone) {
        long startRenderTime = System.nanoTime();
        final Dimension size = getSize();
        if (size.width <= 0 || size.height <= 0) {
            SwingUtilities.invokeLater(() -> {
                repaint();
                if (onDone != null) onDone.run();
            });
            return;
        }



        

        renderExecutor.submit(() -> {
            createBufferedImage();

            Main.lastUpdateWorldViewActualTime = System.nanoTime() - startRenderTime;
            // Swap buffer and update UI on EDT
            SwingUtilities.invokeLater(() -> {
                // Track total render time based on the passed-in startTime
                Main.lastUpdateWorldViewTotalTime = System.nanoTime() - startTime;
                repaint();
                if (onDone != null) onDone.run();
            });
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage buf = backBuffer;
        if (buf != null) {
            drawScaledAndCentered(g, buf);
            return;
        }

        // Fallback: immediate painting path (used for first paint)
        createBufferedImage();
        drawScaledAndCentered(g, backBuffer);
    }

    private void drawScaledAndCentered(Graphics g, BufferedImage img) {
        int panelW = getWidth();
        int panelH = getHeight();
        int imgW = img.getWidth();
        int imgH = img.getHeight();
        
        double scale = Math.min(panelW / (double) imgW, panelH / (double) imgH);
        int drawW = Math.max(1, (int) Math.round(imgW * scale));
        int drawH = Math.max(1, (int) Math.round(imgH * scale));
        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;
        
        g.drawImage(img, x, y, drawW, drawH, null);
    }

    private void createBufferedImage(){
        final int rows = world.getHeight();
        final int cols = world.getWidth();
        final int imgW = cols * 8;
        final int imgH = rows * 8;

        BufferedImage img;
        if (backBuffer != null) {
            img = backBuffer;
        } else {
            img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2 = img.createGraphics();
        try {
            // Draw world cells

            Snapshot snapshot = world.getLatestSnapshot();
            Thing[][] thingGrid = snapshot.grid;
            Color[][] colorGrid = snapshot.colorGrid;
            boolean[][] changedGrid = snapshot.changedGrid;

            double scale = Math.min(getHeight() / (double) imgH, getWidth() / (double) imgW);
            boolean drawBorders = (scale * 8 >= 8);

            for (int r = 0; r < rows; r++) {
                int y = r * 8;

                for (int c = 0; c < cols; c++) {
                    int x = c * 8;

                    if (changedGrid[r][c]){
                        Color cellColor = colorGrid[r][c];
                        if (cellColor == null) {
                            Thing t = thingGrid[r][c];
                            if (t instanceof HasAppearance) {
                                RenderedImage imgCell = ((HasAppearance) t).getImage();
                                if (imgCell != null) {
                                    g2.drawRenderedImage(imgCell, AffineTransform.getTranslateInstance(x, y));
                                } else {
                                    g2.setColor(Color.GRAY);
                                    g2.fillRect(x, y, 8, 8);
                                }
                            } else {
                                g2.setColor(Color.GRAY);
                                g2.fillRect(x, y, 8, 8);
                            }
                        } else {
                            g2.setColor(cellColor);
                            g2.fillRect(x, y, 8, 8);
                        }
    
                        // Only draw borders when scaled cells will be large enough
                        if (drawBorders) {
                            g2.setColor(Color.DARK_GRAY);
                            g2.drawRect(x, y, 8, 8);
                        }
                    }
                }
            }

        } finally {
            g2.dispose();
        }
        backBuffer = img;
    }

    private void handleClick(int mx, int my) {
        int cols = world.getWidth();
        int rows = world.getHeight();
        int col = colFromX(mx);
        int row = rowFromY(my);
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            onCellClick.accept(row, col);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Stable hint; BorderLayout CENTER will expand it
        return new Dimension(600, 600);
    }
}

/**
 * Handles displaying information about Things in the GUI text areas.
 */
class ThingInfoDisplay {
    
    public static void display(Thing thing, JTextArea leftArea, JTextArea rightArea, Consumer<Thing> onUpdate) {
        if (thing instanceof Animal) {
            showAnimalInfo((Animal) thing, leftArea, rightArea);
        } else if (thing instanceof Egg) {
            showEggInfo((Egg) thing, leftArea, rightArea, onUpdate);
        } else if (thing instanceof Wall) {
            showWallInfo(leftArea, rightArea);
        } else if (thing instanceof Food) {
            showFoodInfo((Food) thing, leftArea, rightArea);
        } else {
            showErrorMessage(leftArea, rightArea);
        }
    }

    private static void showAnimalInfo(Animal animal, JTextArea leftArea, JTextArea rightArea) {
        leftArea.setText(
            "Health: " + animal.getHealth() + '/' + animal.getAnimalAttributes().getMaxHealth() 
            + "\nEnergy: " + animal.getEnergy() + '/' + animal.getAnimalAttributes().getMaxEnergy()
            + "\nAttack Damage: " + animal.getAnimalAttributes().getAttackDamage()
            + "\nReproduction Cost: " + animal.getAnimalAttributes().getReproductionCost()
            + "\nLast Action: " + animal.getAction());
        if (animal.getHealth() <= 0) {
            rightArea.setText("This animal is dead.");
        } else {
            rightArea.setText(animal.getAnimalAttributes().getNeuralNet().toString());
        }
    }

    private static void showEggInfo(Egg egg, JTextArea leftArea, JTextArea rightArea, Consumer<Thing> onUpdate) {
        if (!egg.isHatched()) {
            leftArea.setText(
                "Max Health: " + egg.getAnimalAttributes().getMaxHealth()
                + "\nMax Energy: " + egg.getAnimalAttributes().getMaxEnergy()
                + "\nAttack Damage: " + egg.getAnimalAttributes().getAttackDamage()
                + "\nStored Energy: " + egg.getAnimalAttributes().getReproductionCost()
            );
            rightArea.setText(
                "Parent: " + egg.getParent().getName()
                + "\nCycles to Hatch: " + egg.getCyclesToHatch()
            );
        } else {
            onUpdate.accept(egg.getChild());
        }
    }

    private static void showWallInfo(JTextArea leftArea, JTextArea rightArea) {
        leftArea.setText("Wall");
        rightArea.setText("Walls are impassable barriers.");
    }

    private static void showFoodInfo(Food food, JTextArea leftArea, JTextArea rightArea) {
        leftArea.setText("Energy: " + food.getEnergy());
        rightArea.setText("Food is a source of energy and can be eaten by animals, \nspawns from nothing and from dead animals.");
    }

    private static void showErrorMessage(JTextArea leftArea, JTextArea rightArea) {
        leftArea.setText("Something went wrong invalid selection");
        rightArea.setText("");
    }
}

/**
 * Creates and manages the World Stats panel with sliders for simulation parameters.
 */
class WorldStatsPanel {
    
    public static JPanel create() {
        JPanel worldStatsContainer = new JPanel(new BorderLayout());
        worldStatsContainer.setBorder(BorderFactory.createTitledBorder("World Stats"));
        worldStatsContainer.setPreferredSize(new Dimension(400, 260));

        JPanel worldStatsContent = new JPanel();
        worldStatsContent.setLayout(new BoxLayout(worldStatsContent, BoxLayout.Y_AXIS));

        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Stats Total", 50, 200, Main.ANIMAL_STAT_TOTAL,
            v -> { Main.ANIMAL_STAT_TOTAL = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Existence Cost", 0, 10, Main.ANIMAL_EXISTANCE_COST,
            v -> { Main.ANIMAL_EXISTANCE_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Attack Cost", 0, 50, Main.ANIMAL_ATTACK_COST,
            v -> { Main.ANIMAL_ATTACK_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Move Cost", 0, 20, Main.ANIMAL_MOVE_COST,
            v -> { Main.ANIMAL_MOVE_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Eat Cost", 0, 20, Main.ANIMAL_EAT_COST,
            v -> { Main.ANIMAL_EAT_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Turn Cost", 0, 20, Main.ANIMAL_TURN_COST,
            v -> { Main.ANIMAL_TURN_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Rest Energy", 0, 20, Main.ANIMAL_REST_COST,
            v -> { Main.ANIMAL_REST_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Rest Health", 0, 20, Main.ANIMAL_REST_HEALTH,
            v -> { Main.ANIMAL_REST_HEALTH = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Egg Hatch Cycles", 1, 20, Main.EGG_HATCH_CYCLES,
            v -> { Main.EGG_HATCH_CYCLES = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeZeroToOneSlider(
            "Mutation Rate (0..1)", Main.MUTATION_RATE,
            v -> { Main.MUTATION_RATE = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeZeroToOneSlider(
            "Mutation Factor (0..1)", Main.MUTATION_FACTOR,
            v -> { Main.MUTATION_FACTOR = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeZeroToOneSlider(
            "Node Insert/Delete Rate (0..1)", Main.NODE_INSERT_OR_DELETE_RATE,
            v -> { Main.NODE_INSERT_OR_DELETE_RATE = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Max Neural Net Layers", 1, 10, Main.MAX_LAYERS,
            v -> { Main.MAX_LAYERS = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeZeroToOneSlider(
            "Food Grow Rate (0..1)", Main.FOOD_GROW_RATE,
            v -> { Main.FOOD_GROW_RATE = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntBackedFloatSlider(
            "New Food Energy", 1, 100, Math.round(Main.NEW_FOOD_ENERGY),
            f -> { Main.NEW_FOOD_ENERGY = f; Main.updateAttributes(); }
        ));

        JScrollPane worldStatsScroll = new JScrollPane(worldStatsContent);
        worldStatsScroll.setBorder(null);
        worldStatsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        worldStatsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        worldStatsScroll.getVerticalScrollBar().setUnitIncrement(16);

        worldStatsContainer.add(worldStatsScroll, BorderLayout.CENTER);
        return worldStatsContainer;
    }
}

/**
 * Factory for creating slider rows with text input and out-of-range indicators.
 */
class SliderFactory {
    
    public static JPanel makeIntSlider(String title, int min, int max, int initial, Consumer<Integer> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        JSlider slider = new JSlider(min, max, Math.max(min, Math.min(max, initial)));
        slider.setPaintTicks(true);
        int range = Math.max(1, max - min);
        slider.setMajorTickSpacing(Math.max(1, range / 4));
        slider.setMinorTickSpacing(Math.max(1, range / 20));
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(Integer.toString(initial), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            int v = slider.getValue();
            tf.setText(Integer.toString(v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                int raw = Integer.parseInt(tf.getText().trim());
                if (raw < min) {
                    badge.setText("below min");
                    slider.setValue(min);
                } else if (raw > max) {
                    badge.setText("above max");
                    slider.setValue(max);
                } else {
                    badge.setText("");
                    slider.setValue(raw);
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(Integer.toString(slider.getValue()));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }

    public static JPanel makeZeroToOneSlider(String title, float initial, Consumer<Float> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        int initPct = Math.max(0, Math.min(100, Math.round(initial * 100f)));
        JSlider slider = new JSlider(0, 100, initPct);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(String.format("%.4f", initial), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            float v = slider.getValue() / 100f;
            tf.setText(String.format("%.4f", v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                float raw = Float.parseFloat(tf.getText().trim());
                if (raw < 0f) {
                    badge.setText("below min");
                    slider.setValue(0);
                } else if (raw > 1f) {
                    badge.setText("above max");
                    slider.setValue(100);
                } else {
                    badge.setText("");
                    slider.setValue(Math.round(raw * 100f));
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(String.format("%.4f", slider.getValue() / 100f));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }

    public static JPanel makeIntBackedFloatSlider(String title, int min, int max, int initialAsInt, Consumer<Float> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        JSlider slider = new JSlider(min, max, Math.max(min, Math.min(max, initialAsInt)));
        slider.setPaintTicks(true);
        int range = Math.max(1, max - min);
        slider.setMajorTickSpacing(Math.max(1, range / 4));
        slider.setMinorTickSpacing(Math.max(1, range / 20));
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(String.format("%.3f", (float) initialAsInt), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            float v = slider.getValue();
            tf.setText(String.format("%.3f", v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                float raw = Float.parseFloat(tf.getText().trim());
                if (raw < min) {
                    badge.setText("below min");
                    slider.setValue(min);
                } else if (raw > max) {
                    badge.setText("above max");
                    slider.setValue(max);
                } else {
                    badge.setText("");
                    slider.setValue(Math.round(raw));
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(String.format("%.3f", (float) slider.getValue()));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }
}
