package Run;

import javax.swing.*;

import Things.Animal;
import Things.Egg;
import Things.Food;
import Things.Position;
import Things.Thing;
import Things.Wall;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Main GUI for the Simple Evolution Simulation.
 */
public class GUI {
    private World world;
    private static GUI instance;
    private JPanel[][] gridPanels;

    private JTextField tickCountDisplay;
    private JTextArea selectedThingNameText;
    private JTextArea selectedThingInfoTextLeft;
    private JTextArea selectedThingInfoTextRight;
    private Thing selectedThing;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final WorldPlayer worldPlayer = new WorldPlayer();
    private static boolean optionsShown = false;

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
        frame.setSize(900, 700);

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        // LEFT (grid + tick)
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel tickPanel = createTickPanel();
        leftPanel.add(tickPanel, BorderLayout.NORTH);
        JPanel gridPanel = createGridPanel();
        leftPanel.add(gridPanel, BorderLayout.CENTER);
        JPanel resetPanel = createResetPanel();
        leftPanel.add(resetPanel, BorderLayout.SOUTH);

        // RIGHT (controls)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        createControlPanel(rightPanel);

        frame.getContentPane().add(leftPanel);
        frame.getContentPane().add(rightPanel);

        frame.setVisible(true);

        if (Main.SHOW_OPTIONS_ON_FIRST_OPEN && !optionsShown) {
            optionsShown = true;
            SwingUtilities.invokeLater(() -> showNewWorldDialog(frame));
        }
    }

    private void updateWorldView() {
        Color[][] grid = world.getGridOfColors();
        int rows = world.getHeight(); // was world.getWidth()
        int cols = world.getWidth();  // was world.getHeight()

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (gridPanels != null && r < gridPanels.length && c < gridPanels[r].length) {
                    gridPanels[r][c].setBackground(grid[r][c]);
                }
            }
        }
    }

    private JPanel createGridPanel() {
        int rows = world.getHeight(); // was world.getWidth()
        int cols = world.getWidth();  // was world.getHeight()

        JPanel gridPanel = new JPanel(new SquareGridLayout(rows, cols));
        gridPanels = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(Color.LIGHT_GRAY);
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                gridPanels[r][c] = cell;
                gridPanel.add(cell);

                final int rr = r;
                final int cc = c;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        displayThingInfo(rr, cc);
                    }
                });
            }
        }

        updateWorldView();
        return gridPanel;
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

        return tickPanel;
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
            world.tick();
            updateAfterTick();
        }
    }

    protected void updateAfterTick() {
        updateWorldView();
            updateSelectedThingInfo(selectedThing);
            tickCountDisplay.setText("Tick Count: " + world.getTickCount());
    }

    private JPanel createResetPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Row: New World button (kept simple)
        JPanel newWorldRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton newWorldButton = new JButton("New World...");
        newWorldRow.add(newWorldButton);
        content.add(newWorldRow);

        newWorldButton.addActionListener(e -> showNewWorldDialog(rootPanel));

        JPanel msptPanel = SliderFactory.makeIntSlider(
            "MSPT", 1, 1000, Main.tickMillis,
            v -> { Main.tickMillis = v; }
        );
        JButton playPauseButton = new JButton("Play");
        playPauseButton.addActionListener(e -> {
            Main.play = !Main.play;
            playPauseButton.setText(Main.play ? "Pause" : "Play");
            if (Main.play) {
                executor.submit(worldPlayer::run);
            }
        });
        msptPanel.add(Box.createHorizontalStrut(8));
        msptPanel.add(playPauseButton);
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
 * Custom layout manager that arranges components in a grid where each cell is square.
 * Grid is centered in available area with padding if aspect ratio differs.
 */
class SquareGridLayout implements LayoutManager {
    private final int rows;
    private final int cols;

    public SquareGridLayout(int rows, int cols) {
        this.rows = Math.max(1, rows);
        this.cols = Math.max(1, cols);
    }

    @Override
    public void layoutContainer(Container parent) {
        int totalW = parent.getWidth();
        int totalH = parent.getHeight();

        if (totalW <= 0 || totalH <= 0) return;

        int cellSize = Math.min(totalW / cols, totalH / rows);
        if (cellSize <= 0) return;

        int gridW = cellSize * cols;
        int gridH = cellSize * rows;

        int xOffset = (totalW - gridW) / 2;
        int yOffset = (totalH - gridH) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                if (idx < parent.getComponentCount()) {
                    Component comp = parent.getComponent(idx);
                    int x = xOffset + c * cellSize;
                    int y = yOffset + r * cellSize;
                    comp.setBounds(x, y, cellSize, cellSize);
                }
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(cols * 20, rows * 20);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(cols * 5, rows * 5);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) { }
    @Override
    public void removeLayoutComponent(Component comp) { }
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
        if (animal.getHealth() <= 0) {
            leftArea.setText("This animal is dead.");
            rightArea.setText("");
        } else {
            leftArea.setText(
                "Health: " + animal.getHealth() + '/' + animal.getAnimalAttributes().getMaxHealth() 
                + "\nEnergy: " + animal.getEnergy() + '/' + animal.getAnimalAttributes().getMaxEnergy()
                + "\nAttack Damage: " + animal.getAnimalAttributes().getAttackDamage()
                + "\nReproduction Cost: " + animal.getAnimalAttributes().getReproductionCost()
                + "\nLast Action: " + animal.getAction());
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
            "Animal Attack Cost", 0, 50, Main.ANIMAL_ATTACK_COST,
            v -> { Main.ANIMAL_ATTACK_COST = v; Main.updateAttributes(); }
        ));

        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Move Cost", 0, 20, Main.ANIMAL_MOVE_COST,
            v -> { Main.ANIMAL_MOVE_COST = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Rest Energy", 0, 20, Main.ANIMAL_REST_ENERGY,
            v -> { Main.ANIMAL_REST_ENERGY = v; Main.updateAttributes(); }
        ));
        worldStatsContent.add(SliderFactory.makeIntSlider(
            "Animal Rest Health", 0, 20, Main.ANIMAL_REST_HEALTH,
            v -> { Main.ANIMAL_REST_HEALTH = v; Main.updateAttributes(); }
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
