package gestion.de.produit.autre;






import gestion.de.produit.autre.Main;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class GestionDonneesAmelioree extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3307/chemstock";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection connection;
    private JTable table, detailTable;
    private DefaultTableModel tableModel, detailModel;
    private JTextField champRecherche;
    private Vector<String> categoriesMatiere = new Vector<>();

    public GestionDonneesAmelioree() {
        try {
            establishDatabaseConnection();
            setupUI();
            loadInitialData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du démarrage: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void establishDatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion à la base de données a échoué");
        }
    }

    private void setupUI() {
        setTitle("Gestion de Production");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        initializeTableModels();
        setupTables();
        setupUserInterface();
    }

    private void initializeTableModels() {
        tableModel = new DefaultTableModel(
            new Object[]{"ID Production", "Date", "Num Production", "Validation"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 3 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailModel = new DefaultTableModel(
            new Object[]{"ID Detail", "ID Production", "Produit", "Quantité", "Catégorie"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupTables() {
        table = new JTable(tableModel);
        detailTable = new JTable(detailModel);
        
        customizeTable(table);
        customizeTable(detailTable);
        setupSortingAndSearching();
        setupRowSelection();
    }

    private void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(70, 70, 70));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
    }

    private void setupSortingAndSearching() {
        TableRowSorter<DefaultTableModel> trieur = new TableRowSorter<>(tableModel);
        table.setRowSorter(trieur);
        
        champRecherche = new JTextField(20);
        champRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10))
        );
        champRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            private void filterTable() {
                String text = champRecherche.getText();
                if (text.trim().isEmpty()) {
                    trieur.setRowFilter(null);
                } else {
                    trieur.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                }
            }
        });
    }

    private void setupRowSelection() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                String selectedId = tableModel.getValueAt(
                    table.convertRowIndexToModel(table.getSelectedRow()), 0).toString();
                chargerDetailsProduction(selectedId);
            }
        });
    }

    private void setupUserInterface() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("PRODUCTION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 90, 180));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Recherche:"));
        searchPanel.add(champRecherche);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(table), new JScrollPane(detailTable));
        splitPane.setDividerLocation(400);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBoutons.setBackground(Color.WHITE);
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        panelBoutons.add(createStyledButton("Retour ↩️", new Color(100, 100, 100), this::retourMain));
        panelBoutons.add(createStyledButton("Ajouter Production ➕", new Color(0, 120, 215), this::ajouterProduction));
        panelBoutons.add(createStyledButton("Supprimer ❌", new Color(215, 60, 60), this::supprimerProduction));
        panelBoutons.add(createStyledButton("Ajouter Détail ➕", new Color(0, 120, 215), this::ajouterDetail));
        panelBoutons.add(createStyledButton("Supprimer Détail ❌", new Color(215, 60, 60), this::supprimerDetail));
        panelBoutons.add(createStyledButton("Valider ✔️", new Color(60, 170, 60), this::validerProduction));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(panelBoutons, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void retourMain() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            Main mainWindow = new Main();
            mainWindow.setVisible(true);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la fermeture de la connexion: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInitialData() {
        categoriesMatiere.add("Matière Primaire");
        categoriesMatiere.add("Matière Finale");
        chargerProductions();
    }

    private JButton createStyledButton(String text, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        button.addActionListener(e -> action.run());
        return button;
    }

    private void chargerProductions() {
        tableModel.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM production ORDER BY DateProduction DESC")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("idProduction"),
                    rs.getString("DateProduction"),
                    rs.getString("NumProduction"),
                    rs.getBoolean("Validation")
                });
            }
        } catch (SQLException ex) {
            showError("Erreur chargement productions: " + ex.getMessage());
        }
    }

    private void chargerDetailsProduction(String productionId) {
        detailModel.setRowCount(0);
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM detailproduction WHERE idProduction = ?")) {
            pstmt.setString(1, productionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                detailModel.addRow(new Object[]{
                    rs.getString("idDetailProduction"),
                    rs.getString("idProduction"),
                    rs.getString("Produit"),
                    rs.getString("Quantite"),
                    rs.getString("CatigorieMatiere")
                });
            }
        } catch (SQLException ex) {
            showError("Erreur chargement détails: " + ex.getMessage());
        }
    }

    private void ajouterProduction() {
        JDialog dialog = new JDialog(this, "Nouvelle Production", true);
        dialog.setSize(500, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JTextField champDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        JTextField champNum = new JTextField();

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(champDate);
        panel.add(new JLabel("Num Production:"));
        panel.add(champNum);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.addActionListener(e -> {
            try {
                if (champDate.getText().trim().isEmpty() || champNum.getText().trim().isEmpty()) {
                    showError("Tous les champs sont obligatoires");
                    return;
                }

                new SimpleDateFormat("yyyy-MM-dd").parse(champDate.getText());

                String sql = "INSERT INTO production (DateProduction, NumProduction) VALUES (?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, champDate.getText());
                    pstmt.setString(2, champNum.getText());
                    pstmt.executeUpdate();

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            String newId = generatedKeys.getString(1);
                            tableModel.addRow(new Object[]{newId, champDate.getText(), champNum.getText(), false});
                            showMessage("Production ajoutée avec ID: " + newId);
                            dialog.dispose();
                        }
                    }
                }
            } catch (java.text.ParseException ex) {
                showError("Format de date incorrect (doit être YYYY-MM-DD)");
            } catch (SQLException ex) {
                showError("Erreur lors de l'ajout de la production: " + ex.getMessage());
            }
        });
        
        JButton annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(annulerButton);
        buttonPanel.add(ajouterButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void ajouterDetail() {
        if (table.getSelectedRow() == -1) {
            showError("Sélectionnez d'abord une production");
            return;
        }

        int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
        boolean isValidee = (boolean) tableModel.getValueAt(selectedRow, 3);
        if (isValidee) {
            showError("Impossible d'ajouter un détail à une production validée");
            return;
        }

        String productionId = tableModel.getValueAt(selectedRow, 0).toString();

        JDialog dialog = new JDialog(this, "Nouveau Détail", true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JComboBox<String> comboMatiere = new JComboBox<>(categoriesMatiere);
        JComboBox<String> comboProduit = new JComboBox<>();
        JTextField champQuantite = new JTextField();

        // Remplir les produits selon la catégorie sélectionnée
        comboMatiere.addActionListener(e -> {
            comboProduit.removeAllItems();
            String selectedCategory = (String) comboMatiere.getSelectedItem();
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT Nom, MatierePremiere FROM produit")) {
                
                while (rs.next()) {
                    boolean isMatierePremiere = rs.getBoolean("MatierePremiere");
                    if ((selectedCategory.equals("Matière Primaire") && isMatierePremiere) ||
                        (selectedCategory.equals("Matière Finale") && !isMatierePremiere)) {
                        comboProduit.addItem(rs.getString("Nom"));
                    }
                }
            } catch (SQLException ex) {
                showError("Erreur lors du chargement des produits: " + ex.getMessage());
            }
        });

        // Initialiser avec la première catégorie
        comboMatiere.setSelectedIndex(0);

        panel.add(new JLabel("Catégorie:"));
        panel.add(comboMatiere);
        panel.add(new JLabel("Produit:"));
        panel.add(comboProduit);
        panel.add(new JLabel("Quantité:"));
        panel.add(champQuantite);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.addActionListener(e -> {
            try {
                if (champQuantite.getText().trim().isEmpty()) {
                    showError("La quantité est obligatoire");
                    return;
                }

                double quantite = Double.parseDouble(champQuantite.getText());
                if (quantite <= 0) {
                    showError("La quantité doit être supérieure à zéro");
                    return;
                }

                String sql = "INSERT INTO detailproduction (idProduction, Produit, Quantite, CatigorieMatiere) " +
                           "VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, productionId);
                    pstmt.setString(2, (String)comboProduit.getSelectedItem());
                    pstmt.setString(3, champQuantite.getText());
                    pstmt.setString(4, (String)comboMatiere.getSelectedItem());
                    pstmt.executeUpdate();

                    chargerDetailsProduction(productionId);
                    showMessage("Détail ajouté avec succès");
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                showError("La quantité doit être un nombre valide");
            } catch (SQLException ex) {
                showError("Erreur lors de l'ajout du détail: " + ex.getMessage());
            }
        });
        
        JButton annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(annulerButton);
        buttonPanel.add(ajouterButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void supprimerProduction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Sélectionnez une production à supprimer");
            return;
        }

        String id = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
        
        boolean isValidee = (boolean) tableModel.getValueAt(table.convertRowIndexToModel(row), 3);
        if (isValidee) {
            showError("Impossible de supprimer une production validée");
            return;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM detailproduction WHERE idProduction = ?")) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showError("Impossible de supprimer une production avec des détails associés");
                return;
            }
        } catch (SQLException ex) {
            showError("Erreur lors de la vérification des détails: " + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer cette production ?",
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                connection.createStatement().executeUpdate("DELETE FROM production WHERE idProduction = " + id);
                tableModel.removeRow(table.convertRowIndexToModel(row));
                showMessage("Production supprimée avec succès");
            } catch (SQLException ex) {
                showError("Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }

    private void supprimerDetail() {
        int row = detailTable.getSelectedRow();
        if (row == -1) {
            showError("Sélectionnez un détail à supprimer");
            return;
        }

        String id = detailModel.getValueAt(detailTable.convertRowIndexToModel(row), 0).toString();
        
        String productionId = detailModel.getValueAt(detailTable.convertRowIndexToModel(row), 1).toString();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT Validation FROM production WHERE idProduction = ?")) {
            pstmt.setString(1, productionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getBoolean("Validation")) {
                showError("Impossible de supprimer un détail d'une production validée");
                return;
            }
        } catch (SQLException ex) {
            showError("Erreur lors de la vérification de la production: " + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce détail ?",
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                connection.createStatement().executeUpdate("DELETE FROM detailproduction WHERE idDetailProduction = '" + id + "'");
                detailModel.removeRow(detailTable.convertRowIndexToModel(row));
                showMessage("Détail supprimé avec succès");
            } catch (SQLException ex) {
                showError("Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }

    private void validerProduction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Sélectionnez une production à valider");
            return;
        }

        String id = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
        try {
            connection.createStatement().executeUpdate(
                "UPDATE production SET Validation = true WHERE idProduction = " + id);
            tableModel.setValueAt(true, table.convertRowIndexToModel(row), 3);
            showMessage("Production validée avec succès");
        } catch (SQLException ex) {
            showError("Erreur lors de la validation: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            new GestionDonneesAmelioree().setVisible(true);
        });
    }
}

