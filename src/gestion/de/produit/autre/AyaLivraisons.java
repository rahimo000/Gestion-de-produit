package gestion.de.produit.autre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import com.toedter.calendar.JDateChooser;
import gestion.de.produit.DBConnection;
import java.util.Date;

public class AyaLivraisons extends javax.swing.JFrame {

    private JPanel panneauPrincipal;
    private JPanel panneauContenu;
    private CardLayout cardLayoutContenu;
    private JPanel panneauLivraisons;
    private JPanel panneauFormulaireLivraisons;
    private JPanel panneauTableauLivraisons;
    private JTabbedPane tabbedPaneLivraisons;
    private JPanel panneauBaseLivraison;
    private JPanel panneauPaiementLivraison;
    private JPanel panneauDetailsLivraison;
    private JPanel panneauMenuLivraisons;
    private JPanel panneauBoutonsContainerLivraisons;
    private CardLayout cardLayoutBoutonsLivraisons;
    private JPanel panneauBoutonsDefaultLivraisons;
    private JPanel panneauBoutonsDetailsLivraisons;
    private JButton boutonDetailsLivraison;
    private JButton boutonSauvegarderLivraison;
    private JButton boutonSaisieLivraison;
    private JButton boutonAffichageLivraison;
    private JButton boutonAjouterLivraisonMenu;
    private JComboBox<String> comboClient;
    private JDateChooser dateLivraisonChooser;
    private JTextField champStatuLivraison;
    private JComboBox<String> comboModeTransport;
    private JTextField champTransporteur;
    private JComboBox<String> comboValidation;
    private JTextField champAnneeLivraison;
    private JTextField champNumBonLivraison;
    private JDateChooser datePaiementChooserLivraison;
    private JComboBox<String> comboModePaiement;
    private JTextField champPrixHT;
    private JTextField champPrixTVA;
    private JTextField champPrixTTC;
    private JTextField champIdDetailLivraison;
    private JComboBox<String> comboProduit;
    private JTextField champNumLot;
    private JTextField champQuantiteLivraison;
    private JDateChooser dateExpirationChooserLivraison;
    private JTextField champObservation;
    private JTextField champPrixUnitaire;
    private JTextField champPrixTotal;
    private JTable tableauLivraisons;
    private JButton boutonImprimerBonLivraisonBas;
    private JDateChooser dateRechercheLivraison;
    private JButton boutonAjouterDetailsLivraison;
    private JButton boutonSupprimerDetailsLivraisons;
    private JButton boutonConfirmerLivraison;
    private Color couleurSauvegarderInitialeLivraison = new Color(80, 200, 150);
    private Color couleurSauvegarderActiveeLivraison = new Color(40, 180, 70);
    private Set<Integer> usedLivraisonIds = new HashSet<>();
    private final int FIELD_HEIGHT = 35;

    public AyaLivraisons() {
        validateDatabaseSchema(); // Check schema on startup
        initComponents();
        loadClients();
        loadProduits();
        panneauFormulaireLivraisons.setVisible(false);
        panneauTableauLivraisons.setVisible(true);
        updateTableLivraisons();
    }

    private void validateDatabaseSchema() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getColumns(null, null, "DetailLivraison", "Observation");
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "La colonne 'Observation' est manquante dans la table DetailLivraison. Veuillez exécuter : ALTER TABLE DetailLivraison ADD Observation TEXT;", "Erreur de Schéma", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            rs.close();
            rs = meta.getColumns(null, null, "Livraison", "StatutLivraison");
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "La colonne 'StatutLivraison' est manquante dans la table Livraison.", "Erreur de Schéma", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            rs.close();
            rs = meta.getColumns(null, null, "Livraison", "AnneeLivraison");
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "La colonne 'AnneeLivraison' est manquante dans la table Livraison.", "Erreur de Schéma", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la validation du schéma: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setTitle("Système de gestion - Livraisons");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        panneauPrincipal = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(235, 235, 235), 0, getHeight(), new Color(250, 250, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panneauPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panneauContenu = new JPanel();
        cardLayoutContenu = new CardLayout();
        panneauContenu.setLayout(cardLayoutContenu);
        panneauContenu.setOpaque(false);

        panneauLivraisons = new JPanel();
        panneauLivraisons.setLayout(new BoxLayout(panneauLivraisons, BoxLayout.Y_AXIS));
        panneauLivraisons.setOpaque(false);

        panneauMenuLivraisons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panneauMenuLivraisons.setOpaque(false);

        JButton boutonRetour = createModernButton("Retour", new Color(90, 100, 220));
        boutonRetour.addActionListener(e -> dispose());
        panneauMenuLivraisons.add(boutonRetour);

        boutonSauvegarderLivraison = createModernButton("Sauvegarder", couleurSauvegarderInitialeLivraison);
        boutonSauvegarderLivraison.setEnabled(false);
        panneauMenuLivraisons.add(boutonSauvegarderLivraison);

        boutonSaisieLivraison = createModernButton("Saisie des données", new Color(100, 150, 220));
        boutonSaisieLivraison.addActionListener(e -> showSaisiePanel());
        panneauMenuLivraisons.add(boutonSaisieLivraison);

        boutonAffichageLivraison = createModernButton("Affichage des données", new Color(100, 150, 220));
        boutonAffichageLivraison.addActionListener(e -> {
            panneauFormulaireLivraisons.setVisible(false);
            panneauTableauLivraisons.setVisible(true);
            updateTableLivraisons();
        });
        panneauMenuLivraisons.add(boutonAffichageLivraison);

        boutonDetailsLivraison = createModernButton("Détails de livraison", new Color(90, 100, 220));
        boutonDetailsLivraison.addActionListener(e -> {
            showSaisiePanel();
            tabbedPaneLivraisons.setSelectedIndex(2);
        });
        panneauMenuLivraisons.add(boutonDetailsLivraison);

        boutonAjouterLivraisonMenu = createModernButton("Ajouter Livraison", new Color(52, 199, 89));
        boutonAjouterLivraisonMenu.addActionListener(e -> showSaisiePanel());
        panneauMenuLivraisons.add(boutonAjouterLivraisonMenu);

        JButton boutonImprimerMenu = createModernButton("Imprimer Bon Livraison", new Color(0, 122, 255));
        boutonImprimerMenu.addActionListener(e -> afficherDetailsPourImpression());
        panneauMenuLivraisons.add(boutonImprimerMenu);

        panneauLivraisons.add(panneauMenuLivraisons);

        panneauFormulaireLivraisons = new JPanel(new BorderLayout(10, 10));
        panneauFormulaireLivraisons.setOpaque(false);
        panneauFormulaireLivraisons.setVisible(false);

        tabbedPaneLivraisons = new JTabbedPane();
        tabbedPaneLivraisons.setFont(new Font("Roboto", Font.PLAIN, 15));

        setupLivraisonsTabs();

        tabbedPaneLivraisons.addChangeListener(e -> {
            int selectedIndex = tabbedPaneLivraisons.getSelectedIndex();
            if (selectedIndex == 2) {
                cardLayoutBoutonsLivraisons.show(panneauBoutonsContainerLivraisons, "detailsButtons");
            } else {
                cardLayoutBoutonsLivraisons.show(panneauBoutonsContainerLivraisons, "defaultButtons");
            }
            checkFormCompletionForSave();
        });

        panneauFormulaireLivraisons.add(tabbedPaneLivraisons, BorderLayout.CENTER);

        panneauBoutonsContainerLivraisons = new JPanel();
        cardLayoutBoutonsLivraisons = new CardLayout();
        panneauBoutonsContainerLivraisons.setLayout(cardLayoutBoutonsLivraisons);
        panneauBoutonsContainerLivraisons.setOpaque(false);

        panneauBoutonsDefaultLivraisons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panneauBoutonsDefaultLivraisons.setOpaque(false);

        boutonImprimerBonLivraisonBas = createModernButton("Imprimer Bon Livraison", new Color(0, 122, 255));
        boutonImprimerBonLivraisonBas.addActionListener(e -> afficherDetailsPourImpression());
        panneauBoutonsDefaultLivraisons.add(boutonImprimerBonLivraisonBas);

        panneauBoutonsContainerLivraisons.add(panneauBoutonsDefaultLivraisons, "defaultButtons");

        panneauBoutonsDetailsLivraisons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panneauBoutonsDetailsLivraisons.setOpaque(false);

        boutonAjouterDetailsLivraison = createModernButton("Ajouter", new Color(52, 199, 89));
        boutonAjouterDetailsLivraison.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Les détails seront sauvegardés avec la livraison principale.", "Information", JOptionPane.INFORMATION_MESSAGE);
            clearDetailFieldsOnly();
        });
        panneauBoutonsDetailsLivraisons.add(boutonAjouterDetailsLivraison);

        boutonSupprimerDetailsLivraisons = createModernButton("Supprimer", new Color(255, 59, 48));
        boutonSupprimerDetailsLivraisons.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Pour supprimer les détails, supprimez la livraison entière.", "Information", JOptionPane.WARNING_MESSAGE);
        });
        panneauBoutonsDetailsLivraisons.add(boutonSupprimerDetailsLivraisons);

        boutonConfirmerLivraison = createModernButton("Confirmer", new Color(16, 185, 129));
        boutonConfirmerLivraison.addActionListener(e -> {
            checkFormCompletionForSave();
            if (boutonSauvegarderLivraison.isEnabled()) {
                int choice = JOptionPane.showConfirmDialog(this, "Sauvegarder les modifications avant de continuer ?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    saveAllData();
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            tabbedPaneLivraisons.setSelectedIndex(0);
            cardLayoutBoutonsLivraisons.show(panneauBoutonsContainerLivraisons, "defaultButtons");
        });
        panneauBoutonsDetailsLivraisons.add(boutonConfirmerLivraison);

        panneauBoutonsContainerLivraisons.add(panneauBoutonsDetailsLivraisons, "detailsButtons");

        panneauFormulaireLivraisons.add(panneauBoutonsContainerLivraisons, BorderLayout.SOUTH);

        panneauLivraisons.add(panneauFormulaireLivraisons);

        panneauTableauLivraisons = new JPanel(new BorderLayout(10, 10));
        panneauTableauLivraisons.setOpaque(false);
        panneauTableauLivraisons.setVisible(true);

        JPanel tableHeaderLivraisons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tableHeaderLivraisons.setOpaque(false);

        JLabel labelRecherche = new JLabel("Rechercher par Date Livraison:");
        labelRecherche.setFont(new Font("Roboto", Font.BOLD, 15));
        labelRecherche.setForeground(new Color(30, 30, 30));
        tableHeaderLivraisons.add(labelRecherche);

        dateRechercheLivraison = new JDateChooser();
        dateRechercheLivraison.setFont(new Font("Roboto", Font.PLAIN, 15));
        dateRechercheLivraison.setForeground(new Color(30, 30, 30));
        dateRechercheLivraison.setPreferredSize(new Dimension(180, FIELD_HEIGHT));
        tableHeaderLivraisons.add(dateRechercheLivraison);

        JButton boutonRechercherDate = createModernButton("Rechercher Livraison", new Color(0, 122, 255));
        boutonRechercherDate.addActionListener(e -> rechercherLivraisonParDate());
        tableHeaderLivraisons.add(boutonRechercherDate);

        JButton boutonResetRecherche = createModernButton("Réinitialiser", new Color(100, 100, 100));
        boutonResetRecherche.addActionListener(e -> {
            dateRechercheLivraison.setDate(null);
            updateTableLivraisons();
        });
        tableHeaderLivraisons.add(boutonResetRecherche);

        JButton boutonSupprimer = createModernButton("Supprimer livraison", new Color(255, 59, 48));
        boutonSupprimer.addActionListener(e -> supprimerLivraisonSelectionnee());
        tableHeaderLivraisons.add(boutonSupprimer);

        panneauTableauLivraisons.add(tableHeaderLivraisons, BorderLayout.NORTH);

        String[] colonnesLivraisons = {
            "ID", "Client", "Date Liv.", "Statut", "Transport", "Transporteur",
            "Validé", "Année", "N° Bon", "Date Paie.", "Mode Paie.", "Prix HT",
            "Prix TVA", "Prix TTC", "ID Détail", "Produit", "Num Lot", "Quantité",
            "Date Exp.", "Obs.", "Prix Unit.", "Prix Total"
        };
        DefaultTableModel tableModel = new DefaultTableModel(colonnesLivraisons, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableauLivraisons = new JTable(tableModel);
        tableauLivraisons.setBackground(new Color(255, 255, 255));
        tableauLivraisons.setGridColor(new Color(210, 210, 210));
        tableauLivraisons.setRowHeight(28);
        tableauLivraisons.setFillsViewportHeight(true);
        tableauLivraisons.setFont(new Font("Roboto", Font.PLAIN, 14));
        tableauLivraisons.setForeground(new Color(30, 30, 30));
        tableauLivraisons.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableauLivraisons.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumnWidths(tableauLivraisons, new int[]{
            50, 150, 100, 100, 80, 120, 60, 60, 80, 100, 100, 90, 90, 100,
            70, 150, 80, 80, 100, 150, 90, 100
        });

        JScrollPane scrollPaneTable = new JScrollPane(tableauLivraisons);
        scrollPaneTable.getViewport().setBackground(Color.WHITE);
        panneauTableauLivraisons.add(scrollPaneTable, BorderLayout.CENTER);

        panneauLivraisons.add(panneauTableauLivraisons);

        panneauContenu.add(panneauLivraisons, "livraisons");
        panneauPrincipal.add(panneauContenu, BorderLayout.CENTER);
        getContentPane().add(panneauPrincipal);

        pack();

        Timer timerCheckSave = new Timer(500, e -> checkFormCompletionForSave());
        timerCheckSave.start();

        boutonSauvegarderLivraison.addActionListener(e -> saveAllData());

        ActionListener checkSaveListener = e -> checkFormCompletionForSave();
        comboClient.addActionListener(checkSaveListener);
        dateLivraisonChooser.getDateEditor().addPropertyChangeListener("date", evt -> checkFormCompletionForSave());
        champStatuLivraison.getDocument().addDocumentListener(new SimpleDocumentListener(this::checkFormCompletionForSave));
        champPrixTTC.getDocument().addDocumentListener(new SimpleDocumentListener(this::checkFormCompletionForSave));
        champIdDetailLivraison.getDocument().addDocumentListener(new SimpleDocumentListener(this::checkFormCompletionForSave));
        comboProduit.addActionListener(checkSaveListener);
        champQuantiteLivraison.getDocument().addDocumentListener(new SimpleDocumentListener(this::checkFormCompletionForSave));
        dateExpirationChooserLivraison.getDateEditor().addPropertyChangeListener("date", evt -> checkFormCompletionForSave());
        champPrixTotal.getDocument().addDocumentListener(new SimpleDocumentListener(this::checkFormCompletionForSave));
    }

    private void setColumnWidths(JTable table, int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private Runnable updateAction;
        public SimpleDocumentListener(Runnable updateAction) { this.updateAction = updateAction; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAction.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAction.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAction.run(); }
    }

    private void showSaisiePanel() {
        panneauFormulaireLivraisons.setVisible(true);
        panneauTableauLivraisons.setVisible(false);
        tabbedPaneLivraisons.setSelectedIndex(0);
        cardLayoutBoutonsLivraisons.show(panneauBoutonsContainerLivraisons, "defaultButtons");
    }

    private void setupLivraisonsTabs() {
        Font labelFont = new Font("Roboto", Font.BOLD, 14);
        Color labelColor = new Color(40, 40, 40);
        Dimension fieldDim = new Dimension(200, FIELD_HEIGHT);

        panneauBaseLivraison = new JPanel(new GridBagLayout());
        panneauBaseLivraison.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Informations de base de la livraison ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Roboto", Font.BOLD, 14), new Color(50, 50, 100)));
        panneauBaseLivraison.setBackground(new Color(248, 248, 248));
        GridBagConstraints gbcBase = new GridBagConstraints();
        gbcBase.insets = new Insets(6, 8, 6, 8);
        gbcBase.anchor = GridBagConstraints.WEST;
        gbcBase.fill = GridBagConstraints.HORIZONTAL;

        gbcBase.gridx = 0; gbcBase.gridy = 0; gbcBase.weightx = 0.2;
        JLabel labelClient = createLabel("Client:", labelFont, labelColor);
        panneauBaseLivraison.add(labelClient, gbcBase);
        gbcBase.gridx = 1; gbcBase.weightx = 0.8;
        comboClient = new JComboBox<>();
        configureComponent(comboClient, fieldDim);
        panneauBaseLivraison.add(comboClient, gbcBase);

        gbcBase.gridx = 2; gbcBase.weightx = 0.2;
        JLabel labelDateLivraison = createLabel("Date Livraison:", labelFont, labelColor);
        panneauBaseLivraison.add(labelDateLivraison, gbcBase);
        gbcBase.gridx = 3; gbcBase.weightx = 0.8;
        dateLivraisonChooser = new JDateChooser();
        configureComponent(dateLivraisonChooser, fieldDim);
        panneauBaseLivraison.add(dateLivraisonChooser, gbcBase);

        gbcBase.gridx = 0; gbcBase.gridy = 1; gbcBase.weightx = 0.2;
        JLabel labelStatuLivraison = createLabel("Statut Livraison:", labelFont, labelColor);
        panneauBaseLivraison.add(labelStatuLivraison, gbcBase);
        gbcBase.gridx = 1; gbcBase.weightx = 0.8;
        champStatuLivraison = new JTextField();
        configureComponent(champStatuLivraison, fieldDim);
        panneauBaseLivraison.add(champStatuLivraison, gbcBase);

        gbcBase.gridx = 2; gbcBase.weightx = 0.2;
        JLabel labelModeTransport = createLabel("Mode Transport:", labelFont, labelColor);
        panneauBaseLivraison.add(labelModeTransport, gbcBase);
        gbcBase.gridx = 3; gbcBase.weightx = 0.8;
        comboModeTransport = new JComboBox<>(new String[]{"Route", "Air", "Mer"});
        configureComponent(comboModeTransport, fieldDim);
        panneauBaseLivraison.add(comboModeTransport, gbcBase);

        gbcBase.gridx = 0; gbcBase.gridy = 2; gbcBase.weightx = 0.2;
        JLabel labelTransporteur = createLabel("Transporteur:", labelFont, labelColor);
        panneauBaseLivraison.add(labelTransporteur, gbcBase);
        gbcBase.gridx = 1; gbcBase.weightx = 0.8;
        champTransporteur = new JTextField();
        configureComponent(champTransporteur, fieldDim);
        panneauBaseLivraison.add(champTransporteur, gbcBase);

        gbcBase.gridx = 2; gbcBase.weightx = 0.2;
        JLabel labelValidation = createLabel("Validation:", labelFont, labelColor);
        panneauBaseLivraison.add(labelValidation, gbcBase);
        gbcBase.gridx = 3; gbcBase.weightx = 0.8;
        comboValidation = new JComboBox<>(new String[]{"Oui", "Non"});
        configureComponent(comboValidation, fieldDim);
        panneauBaseLivraison.add(comboValidation, gbcBase);

        gbcBase.gridx = 0; gbcBase.gridy = 3; gbcBase.weightx = 0.2;
        JLabel labelAnneeLivraison = createLabel("Année Livraison:", labelFont, labelColor);
        panneauBaseLivraison.add(labelAnneeLivraison, gbcBase);
        gbcBase.gridx = 1; gbcBase.weightx = 0.8;
        champAnneeLivraison = new JTextField();
        configureComponent(champAnneeLivraison, fieldDim);
        ((AbstractDocument) champAnneeLivraison.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        panneauBaseLivraison.add(champAnneeLivraison, gbcBase);

        gbcBase.gridx = 2; gbcBase.weightx = 0.2;
        JLabel labelNumBonLivraison = createLabel("Num Bon Livraison:", labelFont, labelColor);
        panneauBaseLivraison.add(labelNumBonLivraison, gbcBase);
        gbcBase.gridx = 3; gbcBase.weightx = 0.8;
        champNumBonLivraison = new JTextField();
        configureComponent(champNumBonLivraison, fieldDim);
        ((AbstractDocument) champNumBonLivraison.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        panneauBaseLivraison.add(champNumBonLivraison, gbcBase);

        gbcBase.gridy = 4; gbcBase.gridx = 0; gbcBase.gridwidth = 4; gbcBase.weighty = 1.0; gbcBase.fill = GridBagConstraints.VERTICAL;
        panneauBaseLivraison.add(Box.createVerticalGlue(), gbcBase);

        tabbedPaneLivraisons.addTab("Informations de base", new JScrollPane(panneauBaseLivraison));

        panneauPaiementLivraison = new JPanel(new GridBagLayout());
        panneauPaiementLivraison.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Informations de Paiement ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Roboto", Font.BOLD, 14), new Color(50, 50, 100)));
        panneauPaiementLivraison.setBackground(new Color(248, 248, 248));
        GridBagConstraints gbcPaiement = new GridBagConstraints();
        gbcPaiement.insets = new Insets(6, 8, 6, 8);
        gbcPaiement.anchor = GridBagConstraints.WEST;
        gbcPaiement.fill = GridBagConstraints.HORIZONTAL;

        gbcPaiement.gridx = 0; gbcPaiement.gridy = 0; gbcPaiement.weightx = 0.3;
        JLabel labelDatePaiementLivraison = createLabel("Date Paiement:", labelFont, labelColor);
        panneauPaiementLivraison.add(labelDatePaiementLivraison, gbcPaiement);
        gbcPaiement.gridx = 1; gbcPaiement.weightx = 0.7;
        datePaiementChooserLivraison = new JDateChooser();
        configureComponent(datePaiementChooserLivraison, fieldDim);
        panneauPaiementLivraison.add(datePaiementChooserLivraison, gbcPaiement);

        gbcPaiement.gridx = 0; gbcPaiement.gridy = 1; gbcPaiement.weightx = 0.3;
        JLabel labelModePaiement = createLabel("Mode de Paiement:", labelFont, labelColor);
        panneauPaiementLivraison.add(labelModePaiement, gbcPaiement);
        gbcPaiement.gridx = 1; gbcPaiement.weightx = 0.7;
        comboModePaiement = new JComboBox<>(new String[]{"Espèces", "Chèque", "Virement"});
        configureComponent(comboModePaiement, fieldDim);
        panneauPaiementLivraison.add(comboModePaiement, gbcPaiement);

        gbcPaiement.gridx = 0; gbcPaiement.gridy = 2; gbcPaiement.weightx = 0.3;
        JLabel labelPrixHT = createLabel("Prix HT:", labelFont, labelColor);
        panneauPaiementLivraison.add(labelPrixHT, gbcPaiement);
        gbcPaiement.gridx = 1; gbcPaiement.weightx = 0.7;
        champPrixHT = new JTextField();
        configureComponent(champPrixHT, fieldDim);
        ((AbstractDocument) champPrixHT.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        panneauPaiementLivraison.add(champPrixHT, gbcPaiement);

        gbcPaiement.gridx = 0; gbcPaiement.gridy = 3; gbcPaiement.weightx = 0.3;
        JLabel labelPrixTVA = createLabel("Prix TVA:", labelFont, labelColor);
        panneauPaiementLivraison.add(labelPrixTVA, gbcPaiement);
        gbcPaiement.gridx = 1; gbcPaiement.weightx = 0.7;
        champPrixTVA = new JTextField();
        configureComponent(champPrixTVA, fieldDim);
        ((AbstractDocument) champPrixTVA.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        panneauPaiementLivraison.add(champPrixTVA, gbcPaiement);

        gbcPaiement.gridx = 0; gbcPaiement.gridy = 4; gbcPaiement.weightx = 0.3;
        JLabel labelPrixTTC = createLabel("Prix TTC:", labelFont, labelColor);
        panneauPaiementLivraison.add(labelPrixTTC, gbcPaiement);
        gbcPaiement.gridx = 1; gbcPaiement.weightx = 0.7;
        champPrixTTC = new JTextField();
        configureComponent(champPrixTTC, fieldDim);
        ((AbstractDocument) champPrixTTC.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        panneauPaiementLivraison.add(champPrixTTC, gbcPaiement);

        gbcPaiement.gridy = 5; gbcPaiement.gridx = 0; gbcPaiement.gridwidth = 2; gbcPaiement.weighty = 1.0; gbcPaiement.fill = GridBagConstraints.VERTICAL;
        panneauPaiementLivraison.add(Box.createVerticalGlue(), gbcPaiement);

        tabbedPaneLivraisons.addTab("Paiement", new JScrollPane(panneauPaiementLivraison));

        panneauDetailsLivraison = new JPanel(new GridBagLayout());
        panneauDetailsLivraison.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Détails de la livraison ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Roboto", Font.BOLD, 14), new Color(50, 50, 100)));
        panneauDetailsLivraison.setBackground(new Color(248, 248, 248));
        GridBagConstraints gbcDetails = new GridBagConstraints();
        gbcDetails.insets = new Insets(6, 8, 6, 8);
        gbcDetails.anchor = GridBagConstraints.WEST;
        gbcDetails.fill = GridBagConstraints.HORIZONTAL;

        gbcDetails.gridx = 0; gbcDetails.gridy = 0; gbcDetails.weightx = 0.2;
        JLabel labelIdDetailLivraison = createLabel("ID Détail:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelIdDetailLivraison, gbcDetails);
        gbcDetails.gridx = 1; gbcDetails.weightx = 0.8;
        champIdDetailLivraison = new JTextField();
        configureComponent(champIdDetailLivraison, fieldDim);
        ((AbstractDocument) champIdDetailLivraison.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        champIdDetailLivraison.setToolTipText("Laissez vide pour auto-génération ou entrez un ID spécifique si nécessaire.");
        panneauDetailsLivraison.add(champIdDetailLivraison, gbcDetails);

        gbcDetails.gridx = 2; gbcDetails.weightx = 0.2;
        JLabel labelProduit = createLabel("Produit:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelProduit, gbcDetails);
        gbcDetails.gridx = 3; gbcDetails.weightx = 0.8;
        comboProduit = new JComboBox<>();
        configureComponent(comboProduit, fieldDim);
        panneauDetailsLivraison.add(comboProduit, gbcDetails);

        gbcDetails.gridx = 0; gbcDetails.gridy = 1; gbcDetails.weightx = 0.2;
        JLabel labelNumLot = createLabel("Numéro de Lot:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelNumLot, gbcDetails);
        gbcDetails.gridx = 1; gbcDetails.weightx = 0.8;
        champNumLot = new JTextField();
        configureComponent(champNumLot, fieldDim);
        panneauDetailsLivraison.add(champNumLot, gbcDetails);

        gbcDetails.gridx = 2; gbcDetails.weightx = 0.2;
        JLabel labelQuantiteLivraison = createLabel("Quantité Livrée:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelQuantiteLivraison, gbcDetails);
        gbcDetails.gridx = 3; gbcDetails.weightx = 0.8;
        champQuantiteLivraison = new JTextField();
        configureComponent(champQuantiteLivraison, fieldDim);
        ((AbstractDocument) champQuantiteLivraison.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        panneauDetailsLivraison.add(champQuantiteLivraison, gbcDetails);

        gbcDetails.gridx = 0; gbcDetails.gridy = 2; gbcDetails.weightx = 0.2;
        JLabel labelDateExpirationLivraison = createLabel("Date d'expiration:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelDateExpirationLivraison, gbcDetails);
        gbcDetails.gridx = 1; gbcDetails.weightx = 0.8;
        dateExpirationChooserLivraison = new JDateChooser();
        configureComponent(dateExpirationChooserLivraison, fieldDim);
        panneauDetailsLivraison.add(dateExpirationChooserLivraison, gbcDetails);

        gbcDetails.gridx = 2; gbcDetails.weightx = 0.2;
        JLabel labelObservation = createLabel("Observation:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelObservation, gbcDetails);
        gbcDetails.gridx = 3; gbcDetails.weightx = 0.8;
        champObservation = new JTextField();
        configureComponent(champObservation, fieldDim);
        panneauDetailsLivraison.add(champObservation, gbcDetails);

        gbcDetails.gridx = 0; gbcDetails.gridy = 3; gbcDetails.weightx = 0.2;
        JLabel labelPrixUnitaire = createLabel("Prix Unitaire:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelPrixUnitaire, gbcDetails);
        gbcDetails.gridx = 1; gbcDetails.weightx = 0.8;
        champPrixUnitaire = new JTextField();
        configureComponent(champPrixUnitaire, fieldDim);
        ((AbstractDocument) champPrixUnitaire.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        panneauDetailsLivraison.add(champPrixUnitaire, gbcDetails);

        gbcDetails.gridx = 2; gbcDetails.weightx = 0.2;
        JLabel labelPrixTotal = createLabel("Prix Total:", labelFont, labelColor);
        panneauDetailsLivraison.add(labelPrixTotal, gbcDetails);
        gbcDetails.gridx = 3; gbcDetails.weightx = 0.8;
        champPrixTotal = new JTextField();
        configureComponent(champPrixTotal, fieldDim);
        ((AbstractDocument) champPrixTotal.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        panneauDetailsLivraison.add(champPrixTotal, gbcDetails);

        gbcDetails.gridy = 4; gbcDetails.gridx = 0; gbcDetails.gridwidth = 4; gbcDetails.weighty = 1.0; gbcDetails.fill = GridBagConstraints.VERTICAL;
        panneauDetailsLivraison.add(Box.createVerticalGlue(), gbcDetails);

        tabbedPaneLivraisons.addTab("Détails de livraison", new JScrollPane(panneauDetailsLivraison));
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void configureComponent(JComponent component, Dimension preferredSize) {
        component.setFont(new Font("Roboto", Font.PLAIN, 14));
        component.setForeground(new Color(30, 30, 30));
        if (preferredSize != null) {
            component.setPreferredSize(preferredSize);
            if (component instanceof JDateChooser) {
                ((JDateChooser) component).getDateEditor().getUiComponent().setPreferredSize(preferredSize);
                ((JDateChooser) component).getDateEditor().getUiComponent().setFont(new Font("Roboto", Font.PLAIN, 14));
            }
        }
    }

    private void loadClients() {
        comboClient.removeAllItems();
        comboClient.addItem("");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT idClient, Nom FROM Client ORDER BY Nom";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            HashMap<String, Integer> clientMap = new HashMap<>();
            clientMap.put("", null);

            while (rs.next()) {
                int idClient = rs.getInt("idClient");
                String nom = rs.getString("Nom");
                comboClient.addItem(nom);
                clientMap.put(nom, idClient);
            }

            comboClient.putClientProperty("clientMap", clientMap);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Échec du chargement des clients: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void loadProduits() {
        comboProduit.removeAllItems();
        comboProduit.addItem("");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT idProduit, Nom FROM Produit ORDER BY Nom";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            HashMap<String, Integer> produitMap = new HashMap<>();
            produitMap.put("", null);

            while (rs.next()) {
                int idProduit = rs.getInt("idProduit");
                String nom = rs.getString("Nom");
                comboProduit.addItem(nom);
                produitMap.put(nom, idProduit);
            }

            comboProduit.putClientProperty("produitMap", produitMap);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Échec du chargement des produits: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void updateTableLivraisons() {
        String[] colonnesLivraisons = {
            "ID", "Client", "Date Liv.", "Statut", "Transport", "Transporteur",
            "Validé", "Année", "N° Bon", "Date Paie.", "Mode Paie.", "Prix HT",
            "Prix TVA", "Prix TTC", "ID Détail", "Produit", "Num Lot", "Quantité",
            "Date Exp.", "Obs.", "Prix Unit.", "Prix Total"
        };
        DefaultTableModel model = (DefaultTableModel) tableauLivraisons.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(colonnesLivraisons);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String query = "SELECT l.idLivraison, c.Nom AS Client, l.DateLivraison, l.StatutLivraison, l.ModeTransport, " +
                          "l.Transporteur, l.Validation, l.AnneeLivraison, l.NumBonLivraison, l.DatePaiement, l.ModePaiement, " +
                          "l.PrixHT, l.PrixTVA, l.PrixTTC, dl.idDetailLivraison, p.Nom AS Produit, dl.NumLot, " +
                          "dl.QuantiteLivree, dl.DateExpiration, dl.Observation, dl.PrixUnitaire, dl.PrixTotal " +
                          "FROM Livraison l " +
                          "LEFT JOIN Client c ON l.idClient = c.idClient " +
                          "LEFT JOIN DetailLivraison dl ON l.idLivraison = dl.idLivraison " +
                          "LEFT JOIN Produit p ON dl.idProduit = p.idProduit " +
                          "ORDER BY l.DateLivraison DESC, l.idLivraison DESC";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idLivraison"),
                    rs.getString("Client"),
                    rs.getDate("DateLivraison") != null ? dateFormat.format(rs.getDate("DateLivraison")) : null,
                    rs.getString("StatutLivraison"),
                    rs.getString("ModeTransport"),
                    rs.getString("Transporteur"),
                    rs.getBoolean("Validation") ? "Oui" : "Non",
                    rs.getInt("AnneeLivraison"),
                    rs.getInt("NumBonLivraison"),
                    rs.getDate("DatePaiement") != null ? dateFormat.format(rs.getDate("DatePaiement")) : null,
                    rs.getString("ModePaiement"),
                    String.format("%.2f", rs.getFloat("PrixHT")),
                    String.format("%.2f", rs.getFloat("PrixTVA")),
                    String.format("%.2f", rs.getFloat("PrixTTC")),
                    rs.getString("idDetailLivraison"),
                    rs.getString("Produit"),
                    rs.getString("NumLot"),
                    rs.getFloat("QuantiteLivree"),
                    rs.getDate("DateExpiration") != null ? dateFormat.format(rs.getDate("DateExpiration")) : null,
                    rs.getString("Observation"),
                    rs.getFloat("PrixUnitaire") != 0 ? String.format("%.2f", rs.getFloat("PrixUnitaire")) : null,
                    rs.getFloat("PrixTotal") != 0 ? String.format("%.2f", rs.getFloat("PrixTotal")) : null
                });
            }
            setColumnWidths(tableauLivraisons, new int[]{
                50, 150, 100, 100, 80, 120, 60, 60, 80, 100, 100, 90, 90, 100,
                70, 150, 80, 80, 100, 150, 90, 100
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Échec de la mise à jour du tableau des livraisons: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void rechercherLivraisonParDate() {
        if (dateRechercheLivraison.getDate() == null) {
            updateTableLivraisons();
            return;
        }

        String searchDateStr = new SimpleDateFormat("yyyy-MM-dd").format(dateRechercheLivraison.getDate());
        DefaultTableModel model = (DefaultTableModel) tableauLivraisons.getModel();
        model.setRowCount(0);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String query = "SELECT l.idLivraison, c.Nom AS Client, l.DateLivraison, l.StatutLivraison, l.ModeTransport, " +
                          "l.Transporteur, l.Validation, l.AnneeLivraison, l.NumBonLivraison, l.DatePaiement, l.ModePaiement, " +
                          "l.PrixHT, l.PrixTVA, l.PrixTTC, dl.idDetailLivraison, p.Nom AS Produit, dl.NumLot, " +
                          "dl.QuantiteLivree, dl.DateExpiration, dl.Observation, dl.PrixUnitaire, dl.PrixTotal " +
                          "FROM Livraison l " +
                          "LEFT JOIN Client c ON l.idClient = c.idClient " +
                          "LEFT JOIN DetailLivraison dl ON l.idLivraison = dl.idLivraison " +
                          "LEFT JOIN Produit p ON dl.idProduit = p.idProduit " +
                          "WHERE DATE(l.DateLivraison) = ? " +
                          "ORDER BY l.DateLivraison DESC, l.idLivraison DESC";
            stmt = conn.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(searchDateStr));
            rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idLivraison"),
                    rs.getString("Client"),
                    rs.getDate("DateLivraison") != null ? dateFormat.format(rs.getDate("DateLivraison")) : null,
                    rs.getString("StatutLivraison"),
                    rs.getString("ModeTransport"),
                    rs.getString("Transporteur"),
                    rs.getBoolean("Validation") ? "Oui" : "Non",
                    rs.getInt("AnneeLivraison"),
                    rs.getInt("NumBonLivraison"),
                    rs.getDate("DatePaiement") != null ? dateFormat.format(rs.getDate("DatePaiement")) : null,
                    rs.getString("ModePaiement"),
                    String.format("%.2f", rs.getFloat("PrixHT")),
                    String.format("%.2f", rs.getFloat("PrixTVA")),
                    String.format("%.2f", rs.getFloat("PrixTTC")),
                    rs.getString("idDetailLivraison"),
                    rs.getString("Produit"),
                    rs.getString("NumLot"),
                    rs.getFloat("QuantiteLivree"),
                    rs.getDate("DateExpiration") != null ? dateFormat.format(rs.getDate("DateExpiration")) : null,
                    rs.getString("Observation"),
                    rs.getFloat("PrixUnitaire") != 0 ? String.format("%.2f", rs.getFloat("PrixUnitaire")) : null,
                    rs.getFloat("PrixTotal") != 0 ? String.format("%.2f", rs.getFloat("PrixTotal")) : null
                });
            }
            setColumnWidths(tableauLivraisons, new int[]{
                50, 150, 100, 100, 80, 120, 60, 60, 80, 100, 100, 90, 90, 100,
                70, 150, 80, 80, 100, 150, 90, 100
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Échec de la recherche par date: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void supprimerLivraisonSelectionnee() {
        int selectedRow = tableauLivraisons.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une livraison dans le tableau à supprimer.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = tableauLivraisons.getValueAt(selectedRow, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "Impossible de récupérer l'ID de la ligne sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idLivraisonToDelete = (Integer) idObj;

        Connection conn = null;
        PreparedStatement stmtCheckDetails = null;
        PreparedStatement stmtDeleteDetails = null;
        PreparedStatement stmtDeleteLivraison = null;
        ResultSet rsCheck = null;
        boolean hasDetails = false;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String checkQuery = "SELECT COUNT(*) FROM DetailLivraison WHERE idLivraison = ?";
            stmtCheckDetails = conn.prepareStatement(checkQuery);
            stmtCheckDetails.setInt(1, idLivraisonToDelete);
            rsCheck = stmtCheckDetails.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                hasDetails = true;
            }

            int confirm;
            if (hasDetails) {
                Object[] options = {"Supprimer Tout (Livraison + Détails)", "Annuler"};
                confirm = JOptionPane.showOptionDialog(this,
                        "Cette livraison a des détails associés.\n" +
                        "Voulez-vous supprimer la livraison ET tous ses détails ?",
                        "Confirmation de Suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null, options, options[1]);

                if (confirm == JOptionPane.YES_OPTION) {
                    String deleteDetailsQuery = "DELETE FROM DetailLivraison WHERE idLivraison = ?";
                    stmtDeleteDetails = conn.prepareStatement(deleteDetailsQuery);
                    stmtDeleteDetails.setInt(1, idLivraisonToDelete);
                    int detailsDeleted = stmtDeleteDetails.executeUpdate();
                    System.out.println("Supprimé " + detailsDeleted + " enregistrements de détails.");
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Suppression annulée.", "Annulé", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } else {
                confirm = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous vraiment supprimer la livraison ID: " + idLivraisonToDelete + " ?",
                        "Confirmation de Suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
            }

            if (confirm == JOptionPane.YES_OPTION) {
                String deleteLivraisonQuery = "DELETE FROM Livraison WHERE idLivraison = ?";
                stmtDeleteLivraison = conn.prepareStatement(deleteLivraisonQuery);
                stmtDeleteLivraison.setInt(1, idLivraisonToDelete);
                int rowsAffected = stmtDeleteLivraison.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    usedLivraisonIds.remove(idLivraisonToDelete);
                    updateTableLivraisons();
                    JOptionPane.showMessageDialog(this, "Livraison ID " + idLivraisonToDelete + " supprimée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "La livraison ID " + idLivraisonToDelete + " n'a pas pu être trouvée ou supprimée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Suppression annulée.", "Annulé", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rsCheck != null) rsCheck.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtCheckDetails != null) stmtCheckDetails.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtDeleteDetails != null) stmtDeleteDetails.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtDeleteLivraison != null) stmtDeleteLivraison.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void clearFieldsLivraisons() {
        comboClient.setSelectedIndex(0);
        dateLivraisonChooser.setDate(null);
        champStatuLivraison.setText("");
        comboModeTransport.setSelectedIndex(0);
        champTransporteur.setText("");
        comboValidation.setSelectedIndex(0);
        champAnneeLivraison.setText("");
        champNumBonLivraison.setText("");
        datePaiementChooserLivraison.setDate(null);
        comboModePaiement.setSelectedIndex(0);
        champPrixHT.setText("");
        champPrixTVA.setText("");
        champPrixTTC.setText("");
        clearDetailFieldsOnly();
        tabbedPaneLivraisons.setSelectedIndex(0);
        checkFormCompletionForSave();
    }

    private void clearDetailFieldsOnly() {
        champIdDetailLivraison.setText("");
        comboProduit.setSelectedIndex(0);
        champNumLot.setText("");
        champQuantiteLivraison.setText("");
        dateExpirationChooserLivraison.setDate(null);
        champObservation.setText("");
        champPrixUnitaire.setText("");
        champPrixTotal.setText("");
        checkFormCompletionForSave();
    }

    private void checkFormCompletionForSave() {
        boolean baseComplete = comboClient.getSelectedIndex() > 0
                             && dateLivraisonChooser.getDate() != null
                             && !champStatuLivraison.getText().trim().isEmpty()
                             && !champTransporteur.getText().trim().isEmpty()
                             && !champAnneeLivraison.getText().trim().isEmpty()
                             && !champNumBonLivraison.getText().trim().isEmpty();

        boolean paiementComplete = datePaiementChooserLivraison.getDate() != null
                                  && !champPrixHT.getText().trim().isEmpty()
                                  && !champPrixTVA.getText().trim().isEmpty()
                                  && !champPrixTTC.getText().trim().isEmpty();

        boolean detailsComplete = !champIdDetailLivraison.getText().trim().isEmpty()
                                 && comboProduit.getSelectedIndex() > 0
                                 && !champNumLot.getText().trim().isEmpty()
                                 && !champQuantiteLivraison.getText().trim().isEmpty()
                                 && dateExpirationChooserLivraison.getDate() != null
                                 && !champPrixUnitaire.getText().trim().isEmpty()
                                 && !champPrixTotal.getText().trim().isEmpty();

        boolean allComplete = baseComplete && paiementComplete && detailsComplete;

        boutonSauvegarderLivraison.setEnabled(allComplete);
        boutonSauvegarderLivraison.setBackground(allComplete ? couleurSauvegarderActiveeLivraison : couleurSauvegarderInitialeLivraison);

        boolean baseAndPaiementComplete = baseComplete && paiementComplete;
        boutonDetailsLivraison.setBackground(baseAndPaiementComplete ? new Color(40, 180, 70) : new Color(90, 100, 220));
    }

    private void saveAllData() {
        checkFormCompletionForSave();
        if (!boutonSauvegarderLivraison.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires dans tous les onglets.", "Champs Manquants", JOptionPane.WARNING_MESSAGE);
            if (comboClient.getSelectedIndex() <= 0 || dateLivraisonChooser.getDate() == null) {
                tabbedPaneLivraisons.setSelectedIndex(0);
            } else if (datePaiementChooserLivraison.getDate() == null || champPrixHT.getText().trim().isEmpty()) {
                tabbedPaneLivraisons.setSelectedIndex(1);
            } else {
                tabbedPaneLivraisons.setSelectedIndex(2);
            }
            return;
        }

        Connection conn = null;
        PreparedStatement stmtLivraison = null;
        PreparedStatement stmtDetailLivraison = null;
        ResultSet generatedKeys = null;
        int idLivraison = -1;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String queryLivraison = "INSERT INTO Livraison (DateLivraison, StatutLivraison, ModeTransport, Transporteur, Validation, AnneeLivraison, NumBonLivraison, DatePaiement, ModePaiement, PrixHT, PrixTVA, PrixTTC, idClient) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println("Executing Livraison query: " + queryLivraison);
            stmtLivraison = conn.prepareStatement(queryLivraison, Statement.RETURN_GENERATED_KEYS);

            stmtLivraison.setDate(1, new java.sql.Date(dateLivraisonChooser.getDate().getTime()));
            stmtLivraison.setString(2, champStatuLivraison.getText().trim());
            stmtLivraison.setString(3, (String) comboModeTransport.getSelectedItem());
            stmtLivraison.setString(4, champTransporteur.getText().trim());
            stmtLivraison.setBoolean(5, comboValidation.getSelectedItem().equals("Oui"));

            // Validate AnneeLivraison
            try {
                int anneeLivraison = Integer.parseInt(champAnneeLivraison.getText().trim());
              
                stmtLivraison.setInt(6, anneeLivraison);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Année invalide: " + e.getMessage(), "Erreur de Format", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            // Validate NumBonLivraison
            try {
                stmtLivraison.setInt(7, Integer.parseInt(champNumBonLivraison.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Numéro de bon invalide: " + e.getMessage(), "Erreur de Format", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            stmtLivraison.setDate(8, new java.sql.Date(datePaiementChooserLivraison.getDate().getTime()));
            stmtLivraison.setString(9, (String) comboModePaiement.getSelectedItem());

            // Validate PrixHT, PrixTVA, PrixTTC
            try {
                stmtLivraison.setFloat(10, Float.parseFloat(champPrixHT.getText().trim().replace(',', '.')));
                stmtLivraison.setFloat(11, Float.parseFloat(champPrixTVA.getText().trim().replace(',', '.')));
                stmtLivraison.setFloat(12, Float.parseFloat(champPrixTTC.getText().trim().replace(',', '.')));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Prix invalide (HT, TVA, ou TTC): " + e.getMessage(), "Erreur de Format", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            HashMap<String, Integer> clientMap = (HashMap<String, Integer>) comboClient.getClientProperty("clientMap");
            String selectedClient = (String) comboClient.getSelectedItem();
            Integer idClient = clientMap.get(selectedClient);
            if (idClient == null) {
                JOptionPane.showMessageDialog(this, "Client invalide.", "Erreur de Données", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }
            stmtLivraison.setInt(13, idClient);

            int affectedRows = stmtLivraison.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création de la livraison a échoué, aucune ligne affectée.");
            }

            generatedKeys = stmtLivraison.getGeneratedKeys();
            if (generatedKeys.next()) {
                idLivraison = generatedKeys.getInt(1);
                usedLivraisonIds.add(idLivraison);
                System.out.println("ID Livraison généré: " + idLivraison);
            } else {
                throw new SQLException("La création de la livraison a échoué, aucun ID obtenu.");
            }

            String queryDetailLivraison = "INSERT INTO DetailLivraison (idDetailLivraison, NumLot, QuantiteLivree, DateExpiration, Observation, PrixUnitaire, PrixTotal, idProduit, idLivraison) " +
                                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println("Executing DetailLivraison query: " + queryDetailLivraison);
            stmtDetailLivraison = conn.prepareStatement(queryDetailLivraison);

            String idDetailStr = champIdDetailLivraison.getText().trim();
            String finalIdDetail = idDetailStr.isEmpty() ? String.valueOf(idLivraison) : idDetailStr;
            stmtDetailLivraison.setString(1, finalIdDetail);
            stmtDetailLivraison.setString(2, champNumLot.getText().trim());

            // Validate QuantiteLivree
            try {
                stmtDetailLivraison.setFloat(3, Float.parseFloat(champQuantiteLivraison.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantité livrée invalide: " + e.getMessage(), "Erreur de Format", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            stmtDetailLivraison.setDate(4, new java.sql.Date(dateExpirationChooserLivraison.getDate().getTime()));
            stmtDetailLivraison.setString(5, champObservation.getText().trim().isEmpty() ? null : champObservation.getText().trim());

            // Validate PrixUnitaire, PrixTotal
            try {
                stmtDetailLivraison.setFloat(6, Float.parseFloat(champPrixUnitaire.getText().trim().replace(',', '.')));
                stmtDetailLivraison.setFloat(7, Float.parseFloat(champPrixTotal.getText().trim().replace(',', '.')));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Prix invalide (unitaire ou total): " + e.getMessage(), "Erreur de Format", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            HashMap<String, Integer> produitMap = (HashMap<String, Integer>) comboProduit.getClientProperty("produitMap");
            String selectedProduit = (String) comboProduit.getSelectedItem();
            Integer idProduit = produitMap.get(selectedProduit);
            if (idProduit == null) {
                JOptionPane.showMessageDialog(this, "Produit invalide.", "Erreur de Données", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }
            stmtDetailLivraison.setInt(8, idProduit);

            stmtDetailLivraison.setInt(9, idLivraison);

            stmtDetailLivraison.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Livraison (ID: " + idLivraison + ") et ses détails sauvegardés avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);

            updateTableLivraisons();
            clearFieldsLivraisons();
            panneauFormulaireLivraisons.setVisible(false);
            panneauTableauLivraisons.setVisible(true);

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la sauvegarde des données: " + e.getMessage() + "\n(Livraison ID: " + idLivraison + ")", "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtLivraison != null) stmtLivraison.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmtDetailLivraison != null) stmtDetailLivraison.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void afficherDetailsPourImpression() {
        int selectedRow = tableauLivraisons.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une livraison dans le tableau pour afficher les détails.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idLivraison = (Integer) tableauLivraisons.getValueAt(selectedRow, 0);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder detailsText = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            conn = DBConnection.getConnection();
            String query = "SELECT l.*, c.Nom AS ClientNom, c.Adresse AS ClientAdresse, " +
                          "dl.*, p.Nom AS ProduitNom " +
                          "FROM Livraison l " +
                          "LEFT JOIN Client c ON l.idClient = c.idClient " +
                          "LEFT JOIN DetailLivraison dl ON l.idLivraison = dl.idLivraison " +
                          "LEFT JOIN Produit p ON dl.idProduit = p.idProduit " +
                          "WHERE l.idLivraison = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, idLivraison);
            rs = stmt.executeQuery();

            boolean firstDetail = true;
            while (rs.next()) {
                if (firstDetail) {
                    detailsText.append("--- BON DE LIVRAISON ---\n");
                    detailsText.append("ID Livraison: ").append(rs.getInt("idLivraison")).append("\n");
                    detailsText.append("Date Livraison: ").append(rs.getTimestamp("DateLivraison") != null ? dateFormat.format(rs.getTimestamp("DateLivraison")) : "N/A").append("\n");
                    detailsText.append("Numéro Bon: ").append(rs.getInt("NumBonLivraison")).append("\n");
                    detailsText.append("Année: ").append(rs.getInt("AnneeLivraison")).append("\n\n");

                    detailsText.append("--- Client ---\n");
                    detailsText.append("Nom: ").append(rs.getString("ClientNom")).append("\n");
                    detailsText.append("Adresse: ").append(rs.getString("ClientAdresse") != null ? rs.getString("ClientAdresse") : "N/A").append("\n\n");

                    detailsText.append("--- Informations Livraison ---\n");
                    detailsText.append("Statut: ").append(rs.getString("StatutLivraison")).append("\n");
                    detailsText.append("Mode Transport: ").append(rs.getString("ModeTransport")).append("\n");
                    detailsText.append("Transporteur: ").append(rs.getString("Transporteur")).append("\n");
                    detailsText.append("Validation: ").append(rs.getBoolean("Validation") ? "Oui" : "Non").append("\n\n");

                    detailsText.append("--- Informations Paiement ---\n");
                    detailsText.append("Date Paiement: ").append(rs.getDate("DatePaiement") != null ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("DatePaiement")) : "N/A").append("\n");
                    detailsText.append("Mode Paiement: ").append(rs.getString("ModePaiement")).append("\n");
                    detailsText.append("Prix HT: ").append(String.format("%.2f", rs.getFloat("PrixHT"))).append("\n");
                    detailsText.append("Prix TVA: ").append(String.format("%.2f", rs.getFloat("PrixTVA"))).append("\n");
                    detailsText.append("Prix TTC: ").append(String.format("%.2f", rs.getFloat("PrixTTC"))).append("\n\n");

                    detailsText.append("--- Détails des Produits Livrés ---\n");
                    detailsText.append("------------------------------------\n");
                    firstDetail = false;
                }

                if (rs.getString("idDetailLivraison") != null) {
                    detailsText.append("ID Détail: ").append(rs.getString("idDetailLivraison")).append("\n");
                    detailsText.append("Produit: ").append(rs.getString("ProduitNom")).append("\n");
                    detailsText.append("Numéro de Lot: ").append(rs.getString("NumLot")).append("\n");
                    detailsText.append("Quantité Livrée: ").append(rs.getFloat("QuantiteLivree")).append("\n");
                    detailsText.append("Date Expiration: ").append(rs.getDate("DateExpiration") != null ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("DateExpiration")) : "N/A").append("\n");
                    detailsText.append("Prix Unitaire: ").append(String.format("%.2f", rs.getFloat("PrixUnitaire"))).append("\n");
                    detailsText.append("Prix Total (Détail): ").append(String.format("%.2f", rs.getFloat("PrixTotal"))).append("\n");
                    detailsText.append("Observation: ").append(rs.getString("Observation") != null ? rs.getString("Observation") : "").append("\n");
                    detailsText.append("------------------------------------\n");
                } else if (firstDetail) {
                    detailsText.append("Aucun détail de produit trouvé pour cette livraison.\n");
                    detailsText.append("------------------------------------\n");
                }
            }

            if (detailsText.length() == 0) {
                JOptionPane.showMessageDialog(this, "Aucune donnée trouvée pour la livraison ID: " + idLivraison, "Données Non Trouvées", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextArea textArea = new JTextArea(detailsText.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Bon de Livraison - ID: " + idLivraison, JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la récupération des détails pour l'impression: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        float[] hsb = Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), null);
        button.setForeground(hsb[2] < 0.6 ? Color.WHITE : new Color(30, 30, 30));
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, FIELD_HEIGHT));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                    Math.min(255, backgroundColor.getRed() + 20),
                    Math.min(255, backgroundColor.getGreen() + 20),
                    Math.min(255, backgroundColor.getBlue() + 20)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private static class NumberOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private static class NumberWithDecimalFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
            if (isValidDecimal(newText)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if (isValidDecimal(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + currentText.substring(offset + length);
            if (isValidDecimal(newText) || newText.isEmpty()) {
                super.remove(fb, offset, length);
            }
        }

        private boolean isValidDecimal(String text) {
           
            return text.isEmpty() || text.matches("^\\d*\\.?\\d*$");
        }
    }
    public static void main(String[] args) {
       
            
        AyaLivraisons frame = new AyaLivraisons();
            frame.setVisible(true);
        };
    }



