import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path currentFilePath;

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataStreamsApp().setVisible(true));
    }

    public DataStreamsApp() {
        super("Data Stream Search");
        setupGUI();
    }

    private void setupGUI() {
        setLayout(new BorderLayout());

        // Top Panel: search and buttons
        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        add(topPanel, BorderLayout.NORTH);

        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane originalScroll = new JScrollPane(originalTextArea);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalScroll, filteredScroll);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchInFile);
        quitButton.addActionListener(e -> System.exit(0));

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFilePath = file.toPath();
            try (Stream<String> lines = Files.lines(currentFilePath)) {
                List<String> content = lines.collect(Collectors.toList());
                originalTextArea.setText(String.join("\n", content));
                filteredTextArea.setText(""); // Clear previous search
            } catch (IOException ex) {
                showError("Error loading file: " + ex.getMessage());
            }
        }
    }

    private void searchInFile(ActionEvent e) {
        String query = searchField.getText().trim();
        if (currentFilePath == null || query.isEmpty()) {
            showError("Please load a file and enter a search string.");
            return;
        }

        try (Stream<String> lines = Files.lines(currentFilePath)) {
            List<String> filtered = lines
                    .filter(line -> line.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            filteredTextArea.setText(String.join("\n", filtered));
        } catch (IOException ex) {
            showError("Error reading file: " + ex.getMessage());
        }
    }

}
