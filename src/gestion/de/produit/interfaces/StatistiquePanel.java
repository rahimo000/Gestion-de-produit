/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion.de.produit.interfaces;

import com.toedter.calendar.JDateChooser;
import gestion.de.produit.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author rahim
 */
public class StatistiquePanel extends JPanel {
    private JTable tableCommandes;
    private JTable tableLivraisons;
    private JTable tableDetailsCommandes;
    private JTable tableDetailsLivraisons;
    private JDateChooser dateFrom;
    private JDateChooser dateTo;

    public StatistiquePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel de contrôle
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Composants de date
        controlPanel.add(new JLabel("Du:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("yyyy-MM-dd");
        dateFrom.setFont(new Font("Arial", Font.PLAIN, 14));
        dateFrom.setPreferredSize(new Dimension(150, 30));
        controlPanel.add(dateFrom);
        
        controlPanel.add(new JLabel("Au:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("yyyy-MM-dd");
        dateTo.setFont(new Font("Arial", Font.PLAIN, 14));
        dateTo.setPreferredSize(new Dimension(150, 30));
        controlPanel.add(dateTo);
        
        // Boutons
        JButton btnAfficher = createStyledButton("Afficher", new Color(70, 130, 180));
        btnAfficher.addActionListener(e -> loadStatistics());
        controlPanel.add(btnAfficher);
        
        JButton btnImprimer = createStyledButton("Imprimer", new Color(46, 125, 50));
        btnImprimer.addActionListener(e -> imprimerStatistiques());
        controlPanel.add(btnImprimer);
        
        JButton btnUnprime = createStyledButton("Unprime", new Color(128, 0, 128));
        btnUnprime.addActionListener(e -> exporterEnUnprime());
        controlPanel.add(btnUnprime);
        
        add(controlPanel, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Panneau Commandes
        JPanel commandesPanel = new JPanel(new BorderLayout());
        
        tableCommandes = new JTable();
        tableCommandes.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "N° Commande", "Statut", "Date Commande", "Paiement",
                "Moyen Paiement", "Mode Paiement", "Remise", "Année Bon",
                "N° Bon", "Prix TTC", "Prix HT", "Date Paiement",
                "Fournisseur", "Infos Paiement", "TVA"
            }
        ));
        styleTable(tableCommandes);
        commandesPanel.add(new JScrollPane(tableCommandes), BorderLayout.CENTER);
        
        // Détails Commandes
        JLabel lblDetailsCommandes = new JLabel("Détails des Commandes (Tous les DEPI)");
        lblDetailsCommandes.setFont(new Font("Arial", Font.BOLD, 14));
        lblDetailsCommandes.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        commandesPanel.add(lblDetailsCommandes, BorderLayout.NORTH);
        
        tableDetailsCommandes = new JTable();
        tableDetailsCommandes.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "N° Détail", "Quantité", "Prix Unitaire", "Produit",
                "Date Expiration", "Date Fabrication", "N° Commande"
            }
        ));
        styleTable(tableDetailsCommandes);
        commandesPanel.add(new JScrollPane(tableDetailsCommandes), BorderLayout.SOUTH);
        
        // Panneau Livraisons
        JPanel livraisonsPanel = new JPanel(new BorderLayout());
        
        tableLivraisons = new JTable();
        tableLivraisons.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "N° Livraison", "Date Livraison", "Mode Transport", "Transporteur",
                "Validation", "Date Paiement", "Moyen Paiement", "Prix TTC",
                "Prix HT", "N° Commande", "N° Bon Livraison", "Client",
                "Annexe", "Statut Livraison", "Année Livraison", "Observation", "TVA"
            }
        ));
        styleTable(tableLivraisons);
        livraisonsPanel.add(new JScrollPane(tableLivraisons), BorderLayout.CENTER);
        
        // Détails Livraisons
        JLabel lblDetailsLivraisons = new JLabel("Détails des Livraisons (Tous les DEPI)");
        lblDetailsLivraisons.setFont(new Font("Arial", Font.BOLD, 14));
        lblDetailsLivraisons.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        livraisonsPanel.add(lblDetailsLivraisons, BorderLayout.NORTH);
        
        tableDetailsLivraisons = new JTable();
        tableDetailsLivraisons.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "N° Détail", "N° Lot", "Quantité Livrée", "Date Expiration",
                "N° Livraison", "Produit", "Prix Unitaire", "Prix Total", "Observation"
            }
        ));
        styleTable(tableDetailsLivraisons);
        livraisonsPanel.add(new JScrollPane(tableDetailsLivraisons), BorderLayout.SOUTH);
        
        tabbedPane.addTab("Commandes", commandesPanel);
        tabbedPane.addTab("Livraisons", livraisonsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadStatistics() {
        Date from = dateFrom.getDate();
        Date to = dateTo.getDate();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner les dates de début et de fin",
                "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadCommandes(from, to);
        loadLivraisons(from, to);
        loadAllDetailsCommandes(from, to);
        loadAllDetailsLivraisons(from, to);
    }

    private void loadAllDetailsCommandes(Date from, Date to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DefaultTableModel model = (DefaultTableModel) tableDetailsCommandes.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT dc.*, p.nom as nomProduit FROM detailcommande dc " +
                     "JOIN produit p ON dc.idProduit = p.idproduit " +
                     "JOIN commande c ON dc.idCommande = c.idCommande " +
                     "WHERE c.DateCommande BETWEEN ? AND ? " +
                     "ORDER BY dc.idCommande, dc.idDetailCommande";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sdf.format(from));
            stmt.setString(2, sdf.format(to));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idDetailCommande"),
                    rs.getInt("QuantiteCommande"),
                    rs.getDouble("PrixUnitaire"),
                    rs.getString("nomProduit"),
                    rs.getDate("DateExpiration"),
                    rs.getDate("DateFabrication"),
                    rs.getInt("idCommande")
                });
            }
            
        } catch (SQLException e) {
            showError("Détails commande", e);
        }
    }

    private void loadAllDetailsLivraisons(Date from, Date to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DefaultTableModel model = (DefaultTableModel) tableDetailsLivraisons.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT dl.*, p.nom as nomProduit FROM detaillivraison dl " +
                     "JOIN produit p ON dl.idProduit = p.idproduit " +
                     "JOIN livraison l ON dl.idLivraison = l.idLivraison " +
                     "WHERE l.DateLivraison BETWEEN ? AND ? " +
                     "ORDER BY dl.idLivraison, dl.idDetailLivraison";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sdf.format(from));
            stmt.setString(2, sdf.format(to));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idDetailLivraison"),
                    rs.getString("NumLot"),
                    rs.getInt("QuantiteLivree"),
                    rs.getDate("DateExpiration"),
                    rs.getInt("idLivraison"),
                    rs.getString("nomProduit"),
                    rs.getDouble("PrixUnitaire"),
                    rs.getDouble("PrixTotal"),
                    rs.getString("Observation")
                });
            }
            
        } catch (SQLException e) {
            showError("Détails livraison", e);
        }
    }

    private void loadCommandes(Date from, Date to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql = "SELECT * FROM commande WHERE DateCommande BETWEEN ? AND ? ORDER BY DateCommande";

        DefaultTableModel model = (DefaultTableModel) tableCommandes.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sdf.format(from));
            stmt.setString(2, sdf.format(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idCommande"),
                    rs.getBoolean("Valide") ? "Valide" : "Non valide",
                    rs.getDate("DateCommande"),
                    rs.getBoolean("Payer") ? "Payé" : "Non payé",
                    rs.getString("MoyenPaiement"),
                    rs.getString("ModePaiement"),
                    rs.getFloat("Remise"),
                    rs.getString("AnneeBonCommande"),
                    rs.getInt("NumeroBonCommande"),
                    rs.getFloat("PrixTTC"),
                    rs.getFloat("PrixHT"),
                    rs.getDate("DatePaiement"),
                    rs.getInt("idFournisseur"),
                    rs.getString("InfosPaiement"),
                    rs.getFloat("PrixTVA")
                });
            }
            
            addTotalRow(tableCommandes, 9); // Total pour PrixTTC
            
        } catch (SQLException e) {
            showError("Commandes", e);
        }
    }

    private void loadLivraisons(Date from, Date to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql = "SELECT * FROM livraison WHERE DateLivraison BETWEEN ? AND ? ORDER BY DateLivraison";

        DefaultTableModel model = (DefaultTableModel) tableLivraisons.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sdf.format(from));
            stmt.setString(2, sdf.format(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idLivraison"),
                    rs.getDate("DateLivraison"),
                    rs.getString("ModeTransport"),
                    rs.getString("Transporteur"),
                    rs.getBoolean("Validation") ? "Validé" : "Non validé",
                    rs.getDate("DatePaiement"),
                    rs.getString("MoyenPaiement"),
                    rs.getFloat("PrixTTC"),
                    rs.getFloat("PrixHT"),
                    rs.getInt("idCommande"),
                    rs.getString("NumBonLivraison"),
                    rs.getInt("idClient"),
                    rs.getString("NomClient"),
                    rs.getString("AnnexeLivraison"),
                    rs.getString("StatutLivraison"),
                    rs.getInt("AnneeLivraison"),
                    rs.getString("Observation"),
                    rs.getFloat("PrixTVA")
                });
            }
            
            addTotalRow(tableLivraisons, 7); // Total pour PrixTTC
            
        } catch (SQLException e) {
            showError("Livraisons", e);
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220, 220, 220));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        table.setDefaultEditor(Object.class, null);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

    private void addTotalRow(JTable table, int columnIndex) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, columnIndex);
            if (value instanceof Number) {
                total += ((Number)value).doubleValue();
            }
        }
        
        Object[] totalRow = new Object[model.getColumnCount()];
        totalRow[columnIndex] = String.format("%,.2f", total);
        totalRow[columnIndex-1] = "Total:";
        model.addRow(totalRow);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (row == table.getRowCount() - 1) {
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    c.setForeground(Color.BLUE);
                }
                return c;
            }
        });
    }

    private void exporterEnUnprime() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter en format Unprime");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
            }
            public String getDescription() {
                return "Documents PDF (*.pdf)";
            }
        });
        fileChooser.setSelectedFile(new File("statistiques_unprime.pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(filePath + ".pdf");
            }
            
            try {
                JOptionPane.showMessageDialog(this,
                    "Export Unprime réussi vers:\n" + selectedFile.getAbsolutePath(),
                    "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'export: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void imprimerStatistiques() {
        try {
            boolean complete = tableCommandes.print();
            if (complete) {
                JOptionPane.showMessageDialog(this,
                    "Impression terminée avec succès",
                    "Impression", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Impression annulée",
                    "Impression", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'impression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String type, Exception e) {
        JOptionPane.showMessageDialog(this,
            "Erreur lors du chargement des " + type + ":\n" + e.getMessage(),
            "Erreur", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
