

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
import java.util.Date; // Import Date
// Removed unused imports: import java.util.HashSet; import java.util.Set;
import com.toedter.calendar.JDateChooser;

/**
 * @author youcef-bl (Modifié par l'assistant IA)
 */
public class AyaCommandes extends javax.swing.JFrame {

    private static final int FIELD_HEIGHT = 35; // Reduced height
    private static final Dimension FIELD_DIMENSION = new Dimension(250, FIELD_HEIGHT);
    private static final Dimension BUTTON_DIMENSION = new Dimension(150, FIELD_HEIGHT); // For consistent button height
    private static final Font LABEL_FONT = new Font("Roboto", Font.BOLD, 14); // Slightly smaller label font
    private static final Font FIELD_FONT = new Font("Roboto", Font.PLAIN, 14); // Slightly smaller field font
    private static final Insets PANEL_INSETS = new Insets(10, 10, 10, 10); // Padding for panels
    private static final int GRID_GAP = 10; // Reduced grid gap

    // -----------------------------------
    // 1. المتغيرات الأساسية للنافذة الرئيسية
    // -----------------------------------
    private JPanel panneauPrincipal;
    private JPanel panneauContenu;
    private CardLayout cardLayoutContenu;

    // -----------------------------------
    // 2. المتغيرات الخاصة بواجهة Commandes
    // -----------------------------------
    private JPanel panneauCommandes;
    private JPanel panneauFormulaireCommandes;
    private JPanel panneauTableauCommandes;
    private JTabbedPane tabbedPaneCommandes;
    private JPanel panneauBaseCommande;
    private JPanel panneauPaiementCommande;
    private JPanel panneauDetailsCommande;
    private JPanel panneauMenuCommandes;
    private JPanel panneauBoutonsContainerCommandes; // Container for form buttons (bottom)
    private CardLayout cardLayoutBoutonsCommandes;
    private JPanel panneauBoutonsDetailsCommandes; // Panel for buttons specific to Details tab
    private JButton boutonDetailsCommande; // Top menu button
    private JButton boutonSauvegarderCommande; // Top menu button
    private JButton boutonSaisieCommande; // Top menu button
    private JButton boutonAffichageCommande; // Top menu button
    private JButton boutonAjouterCommande; // Top menu button
    private JButton boutonSupprimerCommandeTableau; // Button in table header for deletion
    private JButton boutonRechercherCommandeTableau; // Button in table header for search
    private JButton boutonImprimerBonCommande; // Top menu button
    private JTextField champIdCommande;
    private JDateChooser dateCommandeChooser;
    private JComboBox<String> comboValideCommande;
    private JComboBox<String> comboPayeeCommande;
    private JComboBox<String> comboModePaiementCommande;
    private JTextField champInfosPaiementCommande; // Réintégré
    private JTextField champAnneeBonCommande;      // Réintégré
    private JTextField champNumeroBonCommande;
    private JTextField champPrixTTCCommande;
    private JTextField champPrixTVACommande; // Ensure this exists in DB table Commande
    private JTextField champPrixHTCommande;
    private JDateChooser datePaiementChooserCommande;
    private JComboBox<String> comboFournisseur; // Renamed from comboLivreur for clarity
    private JComboBox<String> comboProduit; // Renamed from comboLivraisonCommande
    private JTextField champIdDetailCommande;
    private JTextField champQuantiteCommande;
    private JTextField champPrixAchatCommande; // Represents PrixUnitaire in DetailCommande
    private JDateChooser dateExpirationChooserCommande;
    private JDateChooser dateFabricationChooserCommande;
    private JTable tableauCommandes;
    private JButton boutonAjouterDetailsCommande; // Button in Details tab's button panel
    private JButton boutonSupprimerDetailsCommande; // Button in Details tab's button panel
    private JButton boutonConfirmerDetailsCommande; // Button in Details tab's button panel (renamed from boutonConfirmerCommande)
    private JLabel labelNombreCommandes;
    private Color couleurSauvegarderInitialeCommande = new Color(80, 200, 150);
    private Color couleurSauvegarderActiveeCommande = new Color(40, 180, 70);
    private JDateChooser dateRechercheCommande;

    // Database Connection Helper (Ensure DBConnection class exists and works)
    // Example: import static your_package.DBConnection.*;

    // -----------------------------------
    // 3. الدالة البنائية (Constructor) - CORRECTED ORDER
    // -----------------------------------
    public AyaCommandes() {
        // 1. Initialize all Swing components first
        initComponents();

        // 2. NOW load data into the created components
        loadFournisseurs();
        loadProduits();

        // 3. Load initial table data and update UI state
        updateTableCommandes();
        updateNombreCommandesLabel();

        // 4. Set initial view to the table
        panneauFormulaireCommandes.setVisible(false);
        panneauTableauCommandes.setVisible(true);
    }

    // -----------------------------------
    // 4. دالة التهيئة الرئيسية (initComponents)
    // -----------------------------------
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setTitle("Système de gestion - Commandes");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setLocationRelativeTo(null); // Maximized ignores this

        // إعداد النافذة الرئيسية
        panneauPrincipal = new JPanel(new BorderLayout(15, 15)) { // Reduced gap
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 220, 220), 0, getHeight(), new Color(245, 245, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panneauPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Reduced border

        // إعداد لوحة المحتوى
        panneauContenu = new JPanel();
        cardLayoutContenu = new CardLayout();
        panneauContenu.setLayout(cardLayoutContenu);
        panneauContenu.setOpaque(false);

        // -----------------------------------
        // 5. إعداد واجهة Commandes
        // -----------------------------------
        panneauCommandes = new JPanel();
        panneauCommandes.setLayout(new BoxLayout(panneauCommandes, BoxLayout.Y_AXIS));
        panneauCommandes.setOpaque(false);

        // --- Top Menu Panel ---
        JPanel panneauMenuEtCardsCommandes = new JPanel(new BorderLayout());
        panneauMenuEtCardsCommandes.setOpaque(false);

        panneauMenuCommandes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Reduced gaps
        panneauMenuCommandes.setOpaque(false);

        JButton boutonRetour = createModernButton("Retour", new Color(90, 100, 220));
        boutonRetour.addActionListener(e -> {
            // Assuming AyaMain exists
             // new AyaMain().setVisible(true);
            dispose();
        });
        panneauMenuCommandes.add(boutonRetour);

        // 'Sauvegarder' Button (acts like Add/Update depending on context - simplified to Add)
        boutonSauvegarderCommande = createModernButton("Sauvegarder", couleurSauvegarderInitialeCommande);
        boutonSauvegarderCommande.setEnabled(false); // Initially disabled, enabled by timer/validation
        boutonSauvegarderCommande.setToolTipText("Enregistrer la commande actuelle et ses détails");
        boutonSauvegarderCommande.addActionListener(e -> {
            saveAllData(); // Saves data from all tabs
        });
        panneauMenuCommandes.add(boutonSauvegarderCommande);

        // 'Saisie des données' Button
        boutonSaisieCommande = createModernButton("Saisie des données", new Color(100, 150, 220));
        boutonSaisieCommande.setToolTipText("Afficher le formulaire pour saisir une nouvelle commande");
        boutonSaisieCommande.addActionListener(e -> {
            switchToFormView();
            clearFieldsCommandes(); // Clear fields for new entry
            champIdCommande.setText(""); // Ensure ID is cleared for new entry
            // Reset save button state maybe? Or let timer handle it.
            boutonSauvegarderCommande.setEnabled(false);
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
        });
        panneauMenuCommandes.add(boutonSaisieCommande);

        // 'Affichage des données' Button
        boutonAffichageCommande = createModernButton("Affichage des données", new Color(100, 150, 220));
        boutonAffichageCommande.setToolTipText("Afficher le tableau des commandes existantes");
        boutonAffichageCommande.addActionListener(e -> {
            switchToTableView();
            updateTableCommandes(); // Refresh table data
        });
        panneauMenuCommandes.add(boutonAffichageCommande);

        // 'Détails de commande' Button (Navigates within Form View)
        boutonDetailsCommande = createModernButton("Détails de commande", new Color(90, 100, 220));
        boutonDetailsCommande.setToolTipText("Aller à l'onglet des détails de la commande en cours de saisie");
        boutonDetailsCommande.addActionListener(e -> {
            switchToFormView(); // Ensure form is visible
            tabbedPaneCommandes.setSelectedIndex(2); // Go to the 'Détails' tab
        });
        panneauMenuCommandes.add(boutonDetailsCommande);

        // 'Ajouter Commande' Button (Now acts like 'Saisie des données')
        boutonAjouterCommande = createModernButton("Ajouter Commande", new Color(52, 199, 89));
        boutonAjouterCommande.setToolTipText("Préparer le formulaire pour une nouvelle commande");
        boutonAjouterCommande.addActionListener(e -> {
            // **MODIFIED ACTION:** Go to Saisie view and clear fields
            switchToFormView();
            clearFieldsCommandes();
            champIdCommande.setText(""); // Explicitly clear ID for new entry
            tabbedPaneCommandes.setSelectedIndex(0); // Start at the first tab
            boutonSauvegarderCommande.setEnabled(false); // Reset save button
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
        });
        panneauMenuCommandes.add(boutonAjouterCommande);

        // 'Imprimer Bon Commande' Button
        boutonImprimerBonCommande = createModernButton("Imprimer Bon Commande", new Color(245, 166, 35));
        boutonImprimerBonCommande.setToolTipText("Imprimer le bon pour la commande sélectionnée dans le tableau");
        boutonImprimerBonCommande.addActionListener(e -> {
             int selectedRow = tableauCommandes.getSelectedRow();
             if (selectedRow != -1) {
                 // Get ID from the correct column (assuming it's the first one)
                 Object idObj = tableauCommandes.getValueAt(selectedRow, 0);
                 if (idObj instanceof Integer) {
                     int idCommande = (Integer) idObj;
                     afficherDetailsPourImpression(idCommande);
                 } else {
                      JOptionPane.showMessageDialog(this, "ID de commande invalide dans la ligne sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande dans le tableau.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
             }
        });
        panneauMenuCommandes.add(boutonImprimerBonCommande);

        panneauMenuEtCardsCommandes.add(panneauMenuCommandes, BorderLayout.CENTER); // Changed to Center to take available space

        // --- Info Card (Nombre de commandes) ---
        JPanel cardNombreCommandes = new JPanel(new BorderLayout()) {
             @Override
             protected void paintComponent(Graphics g) {
                 // ... (painting code remains the same)
                 super.paintComponent(g);
                 Graphics2D g2d = (Graphics2D) g;
                 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 GradientPaint gp = new GradientPaint(0, 0, new Color(110, 50, 220), getWidth(), getHeight(), new Color(150, 120, 240));
                 g2d.setPaint(gp);
                 g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
             }
        };
        cardNombreCommandes.setPreferredSize(new Dimension(200, 80)); // Adjusted size
        cardNombreCommandes.setOpaque(false);
        JLabel titleNombreCommandes = new JLabel("Nombre de commandes");
        titleNombreCommandes.setForeground(new Color(230, 230, 230));
        titleNombreCommandes.setFont(new Font("Roboto", Font.PLAIN, 13)); // Slightly smaller
        titleNombreCommandes.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 0));
        cardNombreCommandes.add(titleNombreCommandes, BorderLayout.NORTH);
        labelNombreCommandes = new JLabel("0"); // Initial value
        labelNombreCommandes.setForeground(new Color(230, 230, 230));
        labelNombreCommandes.setFont(new Font("Roboto", Font.BOLD, 24)); // Larger number
        labelNombreCommandes.setHorizontalAlignment(SwingConstants.CENTER);
        cardNombreCommandes.add(labelNombreCommandes, BorderLayout.CENTER);

        JPanel cardContainerCommandes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Reduced gaps
        cardContainerCommandes.setOpaque(false);
        cardContainerCommandes.add(cardNombreCommandes);
        panneauMenuEtCardsCommandes.add(cardContainerCommandes, BorderLayout.EAST);

        panneauCommandes.add(panneauMenuEtCardsCommandes);

        // --- Form Panel ---
        panneauFormulaireCommandes = new JPanel(new BorderLayout(10, 10)); // Reduced gaps
        panneauFormulaireCommandes.setOpaque(false);
        // panneauFormulaireCommandes.setVisible(false); // Initially hidden, set in constructor

        // --- Tabbed Pane for Form ---
        tabbedPaneCommandes = new JTabbedPane();
        tabbedPaneCommandes.setFont(FIELD_FONT); // Consistent font

        // --- Tab 1: Informations de base ---
        panneauBaseCommande = new JPanel(new GridBagLayout()); // Using GridBagLayout for better control
        panneauBaseCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Informations de base de la commande", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30,30,30)));
        panneauBaseCommande.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbcBase = new GridBagConstraints();
        gbcBase.insets = new Insets(4, 8, 4, 8); // Cell padding
        gbcBase.anchor = GridBagConstraints.WEST;
        gbcBase.fill = GridBagConstraints.HORIZONTAL;

        // Hidden ID Field (Optional, good for updates, but we rely on DB sequence/auto-increment for new ones)
        champIdCommande = new JTextField(10);
        champIdCommande.setEditable(false);
        champIdCommande.setFont(FIELD_FONT);
        // You might not need to add this to the visible panel if it's just for internal tracking

        // Row 0
        addFormField(panneauBaseCommande, gbcBase, 0, "Date Commande:", dateCommandeChooser = new JDateChooser());
        // Row 1
        addFormField(panneauBaseCommande, gbcBase, 1, "Validé:", comboValideCommande = new JComboBox<>(new String[]{"Oui", "Non"}));
        // Row 2
        addFormField(panneauBaseCommande, gbcBase, 2, "Payé:", comboPayeeCommande = new JComboBox<>(new String[]{"Oui", "Non"}));
        // Row 3: Réintégré
        addFormField(panneauBaseCommande, gbcBase, 3, "Année Bon Commande:", champAnneeBonCommande = new JTextField());
        // Row 4: Ajusté
        addFormField(panneauBaseCommande, gbcBase, 4, "Numéro Bon Commande:", champNumeroBonCommande = new JTextField());
        // Row 5: Ajusté
        addFormField(panneauBaseCommande, gbcBase, 5, "Fournisseur:", comboFournisseur = new JComboBox<>()); // Populated later

        // Row 6: Placeholder (Ajusté)
        gbcBase.gridy = 6; gbcBase.gridx = 0; gbcBase.gridwidth = 2; gbcBase.weighty = 1.0; gbcBase.fill = GridBagConstraints.BOTH;
        panneauBaseCommande.add(new JPanel(){{setOpaque(false);}}, gbcBase);

        tabbedPaneCommandes.addTab("Informations de base", new JScrollPane(panneauBaseCommande){{ getViewport().setBackground(new Color(245, 245, 245)); setBorder(null); }});

        // --- Tab 2: Informations de paiement ---
        panneauPaiementCommande = new JPanel(new GridBagLayout());
        panneauPaiementCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Informations de paiement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30,30,30)));
        panneauPaiementCommande.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbcPaiement = (GridBagConstraints) gbcBase.clone(); // Reuse constraints

        // Row 0
        addFormField(panneauPaiementCommande, gbcPaiement, 0, "Mode de paiement:", comboModePaiementCommande = new JComboBox<>(new String[]{"Espèces", "Carte de crédit", "Virement bancaire", "Chèque"})); // Added Chèque
        // Row 1
        addFormField(panneauPaiementCommande, gbcPaiement, 1, "Date de paiement:", datePaiementChooserCommande = new JDateChooser());
        // Row 2: Réintégré
        addFormField(panneauPaiementCommande, gbcPaiement, 2, "Infos Paiement:", champInfosPaiementCommande = new JTextField());
        // Row 3: Ajusté
        addFormField(panneauPaiementCommande, gbcPaiement, 3, "Prix HT:", champPrixHTCommande = new JTextField());
        ((AbstractDocument) champPrixHTCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        // Row 4: Ajusté
        addFormField(panneauPaiementCommande, gbcPaiement, 4, "Prix TVA:", champPrixTVACommande = new JTextField());
        ((AbstractDocument) champPrixTVACommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        // Row 5: Ajusté
        addFormField(panneauPaiementCommande, gbcPaiement, 5, "Prix TTC:", champPrixTTCCommande = new JTextField());
        ((AbstractDocument) champPrixTTCCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());

        // Row 6: Placeholder (Ajusté)
        gbcPaiement.gridy = 6; gbcPaiement.gridx = 0; gbcPaiement.gridwidth = 2; gbcPaiement.weighty = 1.0; gbcPaiement.fill = GridBagConstraints.BOTH;
        panneauPaiementCommande.add(new JPanel(){{setOpaque(false);}}, gbcPaiement);

        tabbedPaneCommandes.addTab("Informations de paiement", new JScrollPane(panneauPaiementCommande){{ getViewport().setBackground(new Color(245, 245, 245)); setBorder(null); }});


        // --- Tab 3: Détails de commande ---
        panneauDetailsCommande = new JPanel(new GridBagLayout());
        panneauDetailsCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Détails de la commande", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30,30,30)));
        panneauDetailsCommande.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbcDetails = (GridBagConstraints) gbcBase.clone();

        // Row 0
        addFormField(panneauDetailsCommande, gbcDetails, 0, "Produit:", comboProduit = new JComboBox<>()); // Populated later
        // Row 1
        addFormField(panneauDetailsCommande, gbcDetails, 1, "ID Détail:", champIdDetailCommande = new JTextField());
        champIdDetailCommande.setToolTipText("Laisser vide pour génération automatique si possible");
        // Row 2
        addFormField(panneauDetailsCommande, gbcDetails, 2, "Quantité Commandée:", champQuantiteCommande = new JTextField());
        ((AbstractDocument) champQuantiteCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter()); // Allow decimals for quantity? Or use NumberOnlyFilter()
        // Row 3
        addFormField(panneauDetailsCommande, gbcDetails, 3, "Prix d'achat Unit.:", champPrixAchatCommande = new JTextField());
        ((AbstractDocument) champPrixAchatCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        // Row 4
        addFormField(panneauDetailsCommande, gbcDetails, 4, "Date d'expiration:", dateExpirationChooserCommande = new JDateChooser());
        // Row 5
        addFormField(panneauDetailsCommande, gbcDetails, 5, "Date de fabrication:", dateFabricationChooserCommande = new JDateChooser());

        // Row 6: Placeholder
        gbcDetails.gridy = 6; gbcDetails.gridx = 0; gbcDetails.gridwidth = 2; gbcDetails.weighty = 1.0; gbcDetails.fill = GridBagConstraints.BOTH;
        panneauDetailsCommande.add(new JPanel(){{setOpaque(false);}}, gbcDetails);

        tabbedPaneCommandes.addTab("Détails de commande", new JScrollPane(panneauDetailsCommande){{ getViewport().setBackground(new Color(245, 245, 245)); setBorder(null); }});


        // --- Tab Change Listener ---
        tabbedPaneCommandes.addChangeListener(e -> {
             int selectedIndex = tabbedPaneCommandes.getSelectedIndex();
             // Show specific buttons only when Details tab is active
             cardLayoutBoutonsCommandes.show(panneauBoutonsContainerCommandes, (selectedIndex == 2) ? "detailsButtons" : "noButtons"); // Show detail buttons or nothing
        });

        panneauFormulaireCommandes.add(tabbedPaneCommandes, BorderLayout.CENTER);

        // --- Form Button Panel (Bottom) ---
        panneauBoutonsContainerCommandes = new JPanel();
        cardLayoutBoutonsCommandes = new CardLayout();
        panneauBoutonsContainerCommandes.setLayout(cardLayoutBoutonsCommandes);
        panneauBoutonsContainerCommandes.setOpaque(false);
        panneauBoutonsContainerCommandes.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Top margin

        // Panel when NO specific buttons are needed (e.g., for Tab 1, Tab 2)
        JPanel panneauNoButtons = new JPanel();
        panneauNoButtons.setOpaque(false);
        panneauBoutonsContainerCommandes.add(panneauNoButtons, "noButtons");

        // Panel for Buttons specific to the Details Tab
        panneauBoutonsDetailsCommandes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); // Center buttons
        panneauBoutonsDetailsCommandes.setOpaque(false);

        // NOTE: Adding/Deleting details usually involves a sub-table or more complex logic.
        // These buttons are placeholders for future functionality or confirmation.
        // The current saveAllData() saves the *single* detail entered in the form.
        boutonAjouterDetailsCommande = createModernButton("Ajouter Ligne Détail", new Color(52, 199, 89));
        boutonAjouterDetailsCommande.setToolTipText("Fonctionnalité future: Ajouter une autre ligne de produit à cette commande");
        boutonAjouterDetailsCommande.addActionListener(e -> {
            // TODO: Implement logic to add multiple detail lines (e.g., clear detail fields, add to a temporary list/table)
            JOptionPane.showMessageDialog(this, "Fonctionnalité non implémentée.\nLa sauvegarde actuelle n'enregistre que les détails présents dans le formulaire.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        // panneauBoutonsDetailsCommandes.add(boutonAjouterDetailsCommande); // Uncomment when implemented

        boutonSupprimerDetailsCommande = createModernButton("Supprimer Ligne Détail", new Color(255, 59, 48));
         boutonSupprimerDetailsCommande.setToolTipText("Fonctionnalité future: Supprimer la ligne de détail sélectionnée");
        boutonSupprimerDetailsCommande.addActionListener(e -> {
            // TODO: Implement logic to remove a detail line from a temporary list/table
             JOptionPane.showMessageDialog(this, "Fonctionnalité non implémentée.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        // panneauBoutonsDetailsCommandes.add(boutonSupprimerDetailsCommande); // Uncomment when implemented

        // This button might just confirm the *details tab* is filled, before saving everything with the top 'Sauvegarder'
        boutonConfirmerDetailsCommande = createModernButton("Confirmer Détails", new Color(16, 185, 129));
        boutonConfirmerDetailsCommande.setToolTipText("Valider les informations de cet onglet");
         boutonConfirmerDetailsCommande.addActionListener(e -> {
             // Could add validation specific to details fields here
             JOptionPane.showMessageDialog(this, "Détails prêts. Utilisez 'Sauvegarder' en haut pour enregistrer.", "Info", JOptionPane.INFORMATION_MESSAGE);
             // Optionally switch back to first tab or keep user here
             // tabbedPaneCommandes.setSelectedIndex(0);
         });
        panneauBoutonsDetailsCommandes.add(boutonConfirmerDetailsCommande);

        panneauBoutonsContainerCommandes.add(panneauBoutonsDetailsCommandes, "detailsButtons");

        panneauFormulaireCommandes.add(panneauBoutonsContainerCommandes, BorderLayout.SOUTH);

        panneauCommandes.add(panneauFormulaireCommandes); // Add Form Panel to main command panel

        // --- Table Panel ---
        panneauTableauCommandes = new JPanel(new BorderLayout(10, 10)); // Gaps
        panneauTableauCommandes.setOpaque(false);
        // panneauTableauCommandes.setVisible(true); // Initially visible, set in constructor

        // --- Table Header Panel (Filters, Search, Actions) ---
        JPanel tableHeaderCommandes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Reduced gaps
        tableHeaderCommandes.setOpaque(false);

        // Sort By (Placeholder - implement sorting if needed)
        // JComboBox<String> sortByCommandes = new JComboBox<>(new String[]{"Trier par date de mise à jour", "Trier par ID", "Trier par Date Commande"});
        // sortByCommandes.setFont(FIELD_FONT);
        // sortByCommandes.setPreferredSize(FIELD_DIMENSION);
        // tableHeaderCommandes.add(sortByCommandes);

        // Search by Date
        JLabel labelRecherche = new JLabel("Rechercher par Date Commande:");
        labelRecherche.setFont(LABEL_FONT);
        labelRecherche.setForeground(new Color(30, 30, 30));
        tableHeaderCommandes.add(labelRecherche);

        dateRechercheCommande = new JDateChooser();
        dateRechercheCommande.setFont(FIELD_FONT);
        dateRechercheCommande.setPreferredSize(new Dimension(150, FIELD_HEIGHT)); // Smaller date chooser
        tableHeaderCommandes.add(dateRechercheCommande);

        // Search Button (for Table View)
        boutonRechercherCommandeTableau = createModernButton("Rechercher", new Color(0, 122, 255)); // Shorter text
        boutonRechercherCommandeTableau.setPreferredSize(BUTTON_DIMENSION);
        boutonRechercherCommandeTableau.setToolTipText("Filtrer les commandes par la date sélectionnée");
        boutonRechercherCommandeTableau.addActionListener(e -> rechercherCommandeParDate());
        tableHeaderCommandes.add(boutonRechercherCommandeTableau);

        // Reset Search Button
        JButton boutonResetRecherche = createModernButton("Afficher Tout", new Color(100, 100, 100)); // More descriptive text
        boutonResetRecherche.setPreferredSize(BUTTON_DIMENSION);
        boutonResetRecherche.setToolTipText("Effacer le filtre de date et afficher toutes les commandes");
        boutonResetRecherche.addActionListener(e -> {
            dateRechercheCommande.setDate(null); // Clear the date chooser
            updateTableCommandes(); // Reload all data
        });
        tableHeaderCommandes.add(boutonResetRecherche);

        // Delete Button (for Table View)
        boutonSupprimerCommandeTableau = createModernButton("Supprimer Commande", new Color(255, 59, 48));
        boutonSupprimerCommandeTableau.setPreferredSize(BUTTON_DIMENSION);
        boutonSupprimerCommandeTableau.setToolTipText("Supprimer la commande sélectionnée et ses détails associés");
        boutonSupprimerCommandeTableau.addActionListener(e -> supprimerCommandeSelectionnee()); // Call specific delete method
        tableHeaderCommandes.add(boutonSupprimerCommandeTableau);

        panneauTableauCommandes.add(tableHeaderCommandes, BorderLayout.NORTH);

        // --- Table ---
        // Define columns carefully based on what you actually fetch and need to display
        // NOTE: AnneeBonCommande and InfosPaiement are NOT added here by default
        String[] columnNames = {
                "ID", "Date Commande", "Validé", "Payé", "N° Bon", "Fournisseur", // Basic Info
                "Mode Paiement", "Date Paiement", "Prix HT", "Prix TVA", "Prix TTC", // Payment Info
                "ID Détail", "Produit", "Qté", "Prix Achat", "Date Exp.", "Date Fab." // Detail Info (from first detail line)
        };
        // Create a non-editable table model initially
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        tableauCommandes = new JTable(tableModel);
        setupTableStyle(tableauCommandes); // Apply styling

        // Add Mouse Listener for potential future actions (like double-click to edit)
        tableauCommandes.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 if (e.getClickCount() == 2) { // Double-click
                     int selectedRow = tableauCommandes.getSelectedRow();
                     if (selectedRow != -1) {
                         Object idObj = tableauCommandes.getValueAt(selectedRow, 0);
                         if (idObj instanceof Integer) {
                            int idCommande = (Integer) idObj;
                            // Optional: Load data into form for editing
                            // loadCommandeDataIntoForm(idCommande);
                            JOptionPane.showMessageDialog(AyaCommandes.this, "Chargement pour édition non implémenté (ID: " + idCommande + ").", "Info", JOptionPane.INFORMATION_MESSAGE);
                         }
                     }
                 }
                 // Single click selection is handled implicitly for delete/print buttons
             }
        });
        panneauTableauCommandes.add(new JScrollPane(tableauCommandes), BorderLayout.CENTER);

        panneauCommandes.add(panneauTableauCommandes); // Add Table Panel to main command panel

        // --- Add Command Panel to Content Panel ---
        panneauContenu.add(panneauCommandes, "commandes");

        // --- Add Content Panel to Main Panel ---
        panneauPrincipal.add(panneauContenu, BorderLayout.CENTER);

        // --- Add Main Panel to Frame ---
        getContentPane().add(panneauPrincipal);

        pack(); // Pack after adding components

        // -----------------------------------
        // 6. إعداد Timer للتحقق من الحقول (Enabling Save Button)
        // -----------------------------------
        Timer timerCommandes = new Timer(500, e -> checkFormCompletion()); // Check more frequently
        timerCommandes.start();
    }

    // --- Helper Method to Add Form Fields (GridBagLayout) ---
    private void addFormField(JPanel panel, GridBagConstraints gbc, int yPos, String labelText, Component component) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1; // Label column weight
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(30, 30, 30));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9; // Field column weight
        if (component instanceof JComboBox || component instanceof JDateChooser) {
            component.setPreferredSize(FIELD_DIMENSION);
            component.setFont(FIELD_FONT);
        } else if (component instanceof JTextField) {
            ((JTextField) component).setFont(FIELD_FONT);
            ((JTextField) component).setPreferredSize(FIELD_DIMENSION);
        }
        // Apply consistent styling
        component.setForeground(new Color(30, 30, 30));
        panel.add(component, gbc);
    }


    // --- Helper method to style the table ---
    private void setupTableStyle(JTable table) {
        table.setBackground(new Color(250, 250, 250)); // Lighter background
        table.setGridColor(new Color(210, 210, 210)); // Lighter grid lines
        table.setRowHeight(FIELD_HEIGHT); // Match field height
        table.setFillsViewportHeight(true); // Table fills scroll pane
        table.setFont(FIELD_FONT); // Consistent font
        table.setForeground(new Color(30, 30, 30));
        table.setSelectionBackground(new Color(180, 210, 255)); // Softer selection color
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(LABEL_FONT); // Header font
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setForeground(new Color(30, 30, 30));
        table.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        // Adjust column widths (example widths, adjust as needed)
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Allow horizontal scroll
        // Column widths array remains the same as AnneeBon and InfosPaiement are not shown in table
        int[] columnWidths = {
            50, 110, 50, 50, 70, 150, // Basic
            120, 110, 80, 80, 90,     // Payment
            60, 150, 50, 90, 110, 110 // Detail
        };
        // Ensure table model exists before setting widths
        if (table.getColumnCount() > 0 && table.getColumnCount() == columnWidths.length) { // Added length check
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
        } else if (table.getColumnCount() > 0) {
             System.err.println("Warning: Mismatch between column count (" + table.getColumnCount() + ") and defined widths (" + columnWidths.length + "). Using default widths.");
        }
    }

    // --- Method to switch to Form View ---
    private void switchToFormView() {
        panneauFormulaireCommandes.setVisible(true);
        panneauTableauCommandes.setVisible(false);
        // Optional: revalidate/repaint if needed, usually handled by Swing
        this.revalidate();
        this.repaint();
    }

    // --- Method to switch to Table View ---
     private void switchToTableView() {
        panneauFormulaireCommandes.setVisible(false);
        panneauTableauCommandes.setVisible(true);
        // Optional: revalidate/repaint
        this.revalidate();
        this.repaint();
    }

    // --- Check if form is sufficiently filled to enable Save ---
    private void checkFormCompletion() {
        // Only check if the form panel is visible and components are initialized
        if (!panneauFormulaireCommandes.isVisible() || dateCommandeChooser == null || comboFournisseur == null || comboModePaiementCommande == null || comboProduit == null) {
             if(boutonSauvegarderCommande != null) { // Check if button exists
                 boutonSauvegarderCommande.setEnabled(false);
                 boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
             }
             return;
        }

        // Now include the re-integrated fields in the check if they are mandatory
        boolean baseComplete = dateCommandeChooser.getDate() != null &&
                               !champNumeroBonCommande.getText().trim().isEmpty() &&
                               !champAnneeBonCommande.getText().trim().isEmpty() && // Added check
                               comboFournisseur.getSelectedIndex() > 0; // Needs a valid selection

        boolean paiementComplete = comboModePaiementCommande.getSelectedIndex() != -1 &&
                                   // champInfosPaiementCommande not mandatory for enabling save, maybe?
                                   !champPrixHTCommande.getText().trim().isEmpty() &&
                                   !champPrixTTCCommande.getText().trim().isEmpty();


         boolean detailsStarted = comboProduit.getSelectedIndex() > 0 && // Needs valid selection
                                  !champQuantiteCommande.getText().trim().isEmpty() &&
                                  !champPrixAchatCommande.getText().trim().isEmpty();


        if (baseComplete && paiementComplete && detailsStarted) {
            boutonSauvegarderCommande.setBackground(couleurSauvegarderActiveeCommande);
            boutonSauvegarderCommande.setEnabled(true);
        } else {
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
            boutonSauvegarderCommande.setEnabled(false);
        }
    }

    // -----------------------------------
    // 8. دوال مساعدة لتحديث الجداول وإدارة البيانات
    // -----------------------------------

    // --- Load Fournisseurs into ComboBox ---
    private void loadFournisseurs() {
        // Ensure component is initialized before using
        if (comboFournisseur == null) {
             System.err.println("Warning: comboFournisseur not initialized before loadFournisseurs call.");
             return;
        }
        // Clear existing items first
        comboFournisseur.removeAllItems();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection(); // Use your connection class
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT idFournisseur, Nom FROM Fournisseur ORDER BY Nom");
             comboFournisseur.addItem("Sélectionner..."); // Add a default prompt item
            while (rs.next()) {
                // Store both ID and Name, e.g., using a wrapper object or a Map
                // Simple approach: just add name, use index+1 for ID (if IDs are sequential 1, 2, 3...)
                comboFournisseur.addItem(rs.getString("Nom"));
                 // More robust: comboFournisseur.addItem(new ComboItem(rs.getInt("idFournisseur"), rs.getString("Nom")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des fournisseurs: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Log error
        } finally {
             // Close resources (implement safe close)
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
            // Do not close connection here if reused
        }
    }
    // --- Load Produits into ComboBox ---
    private void loadProduits() {
         if (comboProduit == null) {
             System.err.println("Warning: comboProduit not initialized before loadProduits call.");
             return;
         }
        comboProduit.removeAllItems();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT idProduit, Nom FROM Produit ORDER BY Nom");
            comboProduit.addItem("Sélectionner..."); // Add a default prompt item
            while (rs.next()) {
                 comboProduit.addItem(rs.getString("Nom"));
                 // More robust: comboProduit.addItem(new ComboItem(rs.getInt("idProduit"), rs.getString("Nom")));
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // --- Update Table Method ---
    private void updateTableCommandes() {
        // Ensure table model is initialized
        if (tableauCommandes == null || tableauCommandes.getModel() == null) {
             System.err.println("Warning: tableauCommandes or its model not initialized before updateTableCommandes call.");
            return;
        }
        // Column names remain the same, not adding AnneeBon/InfosPaiement to table view
        String[] columnNames = {
                "ID", "Date Commande", "Validé", "Payé", "N° Bon", "Fournisseur",
                "Mode Paiement", "Date Paiement", "Prix HT", "Prix TVA", "Prix TTC",
                "ID Détail", "Produit", "Qté", "Prix Achat", "Date Exp.", "Date Fab."
        };
        DefaultTableModel model = (DefaultTableModel) tableauCommandes.getModel();
        model.setRowCount(0); // Clear existing rows
        model.setColumnIdentifiers(columnNames); // Ensure columns are set correctly

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // The SELECT query also remains the same as we don't need the extra fields for the table view
        String query = "SELECT c.idCommande, c.DateCommande, c.Valide, c.Payer, c.NumeroBonCommande, f.Nom AS FournisseurNom, " +
                       "c.ModePaiement, c.DatePaiement, c.PrixHT, c.PrixTVA, c.PrixTTC, " +
                       "dc.idDetailCommande, p.Nom AS ProduitNom, dc.QuantiteCommande, dc.PrixUnitaire, dc.DateExpiration, dc.DateFabrication " +
                       "FROM Commande c " +
                       "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur " +
                       "LEFT JOIN (SELECT *, ROW_NUMBER() OVER(PARTITION BY idCommande ORDER BY idDetailCommande) as rn FROM DetailCommande) dc ON c.idCommande = dc.idCommande AND dc.rn = 1 " +
                       "LEFT JOIN Produit p ON dc.idProduit = p.idProduit " +
                       "ORDER BY c.DateCommande DESC, c.idCommande DESC"; // Example ordering

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                 // row data remains the same
                model.addRow(new Object[]{
                    rs.getInt("idCommande"),
                    rs.getTimestamp("DateCommande"), // Use Timestamp for date+time
                    rs.getBoolean("Valide") ? "Oui" : "Non",
                    rs.getBoolean("Payer") ? "Oui" : "Non",
                    rs.getObject("NumeroBonCommande") != null ? rs.getInt("NumeroBonCommande") : null, // Handle potential null
                    rs.getString("FournisseurNom"),
                    rs.getString("ModePaiement"),
                    rs.getDate("DatePaiement"), // Date only is fine here
                    rs.getObject("PrixHT") != null ? rs.getFloat("PrixHT") : null,
                    rs.getObject("PrixTVA") != null ? rs.getFloat("PrixTVA") : null, // Handle potential null
                    rs.getObject("PrixTTC") != null ? rs.getFloat("PrixTTC") : null,
                    // Detail Info (can be null if no details)
                    rs.getObject("idDetailCommande"), // Might be null if LEFT JOIN finds no match
                    rs.getString("ProduitNom"), // Might be null
                    rs.getObject("QuantiteCommande") != null ? rs.getFloat("QuantiteCommande") : null, // Might be null
                    rs.getObject("PrixUnitaire") != null ? rs.getFloat("PrixUnitaire") : null, // Might be null
                    rs.getDate("DateExpiration"), // Might be null
                    rs.getDate("DateFabrication") // Might be null
                });
            }
            // After updating the model, reapply the style/renderers if necessary
            setupTableStyle(tableauCommandes); // Re-apply column widths etc.

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la mise à jour du tableau: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Close resources safely
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
            // Don't close connection here
        }
    }

    // --- Search Command Method ---
    private void rechercherCommandeParDate() {
        Date selectedDate = dateRechercheCommande.getDate();
        if (selectedDate == null) {
            updateTableCommandes(); // Show all if no date selected
            return;
        }
        if (tableauCommandes == null || tableauCommandes.getModel() == null) return; // Check initialization

        String searchDateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
        DefaultTableModel model = (DefaultTableModel) tableauCommandes.getModel();
        model.setRowCount(0); // Clear existing rows

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // Query is similar to updateTable, but with a WHERE clause for the date
        // We don't need the extra columns here either for the table view
        String query = "SELECT c.idCommande, c.DateCommande, c.Valide, c.Payer, c.NumeroBonCommande, f.Nom AS FournisseurNom, " +
                       "c.ModePaiement, c.DatePaiement, c.PrixHT, c.PrixTVA, c.PrixTTC, " +
                       "dc.idDetailCommande, p.Nom AS ProduitNom, dc.QuantiteCommande, dc.PrixUnitaire, dc.DateExpiration, dc.DateFabrication " +
                       "FROM Commande c " +
                       "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur " +
                       "LEFT JOIN (SELECT *, ROW_NUMBER() OVER(PARTITION BY idCommande ORDER BY idDetailCommande) as rn FROM DetailCommande) dc ON c.idCommande = dc.idCommande AND dc.rn = 1 " +
                       "LEFT JOIN Produit p ON dc.idProduit = p.idProduit " +
                       "WHERE DATE(c.DateCommande) = ? " + // Filter by date part of DateCommande
                       "ORDER BY c.DateCommande DESC, c.idCommande DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setDate(1, new java.sql.Date(selectedDate.getTime())); // Set the date parameter
            rs = stmt.executeQuery();

            while (rs.next()) {
                // Add row logic is the same as in updateTableCommandes
                 model.addRow(new Object[]{
                    rs.getInt("idCommande"), rs.getTimestamp("DateCommande"),
                    rs.getBoolean("Valide") ? "Oui" : "Non", rs.getBoolean("Payer") ? "Oui" : "Non",
                    rs.getObject("NumeroBonCommande") != null ? rs.getInt("NumeroBonCommande") : null, rs.getString("FournisseurNom"),
                    rs.getString("ModePaiement"), rs.getDate("DatePaiement"),
                    rs.getObject("PrixHT") != null ? rs.getFloat("PrixHT") : null, rs.getObject("PrixTVA") != null ? rs.getFloat("PrixTVA") : null, rs.getObject("PrixTTC") != null ? rs.getFloat("PrixTTC") : null,
                    rs.getObject("idDetailCommande"), rs.getString("ProduitNom"),
                    rs.getObject("QuantiteCommande") != null ? rs.getFloat("QuantiteCommande") : null, rs.getObject("PrixUnitaire") != null ? rs.getFloat("PrixUnitaire") : null,
                    rs.getDate("DateExpiration"), rs.getDate("DateFabrication")
                });
            }
             setupTableStyle(tableauCommandes); // Re-apply style

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la recherche: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Close resources safely
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // --- Delete Command Method (with check and transaction) ---
    // No changes needed here for the reintegrated fields, as it deletes based on idCommande
    private void supprimerCommandeSelectionnee() {
        int selectedRow = tableauCommandes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande dans le tableau.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = tableauCommandes.getValueAt(selectedRow, 0); // Assuming ID is in the first column
        if (!(idObj instanceof Integer)) {
             JOptionPane.showMessageDialog(this, "ID de commande invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
             return;
        }
        int idCommandeToDelete = (Integer) idObj;

        Connection conn = null;
        PreparedStatement stmtCheckDetails = null;
        PreparedStatement stmtDeleteDetails = null;
        PreparedStatement stmtDeleteCommande = null;
        ResultSet rsCheck = null;
        boolean hasDetails = false;

        try {
            conn = DBConnection.getConnection();

            // 1. Check if details exist
            String checkQuery = "SELECT COUNT(*) FROM DetailCommande WHERE idCommande = ?";
            stmtCheckDetails = conn.prepareStatement(checkQuery);
            stmtCheckDetails.setInt(1, idCommandeToDelete);
            rsCheck = stmtCheckDetails.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                hasDetails = true;
            }
            rsCheck.close();
            stmtCheckDetails.close();

            // 2. Confirm Deletion
            String confirmationMessage = "Voulez-vous vraiment supprimer la commande ID: " + idCommandeToDelete + "?";
            if (hasDetails) {
                confirmationMessage += "\nATTENTION : Cela supprimera également tous les détails associés !";
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    confirmationMessage,
                    "Confirmation de Suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE); // Use WARNING icon

            if (confirm == JOptionPane.YES_OPTION) {
                // 3. Perform Deletion within a Transaction
                conn.setAutoCommit(false); // Start transaction

                // Delete details first (if any) - safe even if none exist
                String deleteDetailsQuery = "DELETE FROM DetailCommande WHERE idCommande = ?";
                stmtDeleteDetails = conn.prepareStatement(deleteDetailsQuery);
                stmtDeleteDetails.setInt(1, idCommandeToDelete);
                stmtDeleteDetails.executeUpdate();
                stmtDeleteDetails.close(); // Close statement after use

                // Delete the main command
                String deleteCommandeQuery = "DELETE FROM Commande WHERE idCommande = ?";
                stmtDeleteCommande = conn.prepareStatement(deleteCommandeQuery);
                stmtDeleteCommande.setInt(1, idCommandeToDelete);
                int rowsAffected = stmtDeleteCommande.executeUpdate();
                stmtDeleteCommande.close(); // Close statement

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    JOptionPane.showMessageDialog(this, "Commande ID " + idCommandeToDelete + " et ses détails supprimés avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    updateTableCommandes(); // Refresh table
                    updateNombreCommandesLabel(); // Update count
                } else {
                    conn.rollback(); // Rollback if command deletion failed
                    JOptionPane.showMessageDialog(this, "Erreur : Commande ID " + idCommandeToDelete + " non trouvée ou n'a pas pu être supprimée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on any SQL error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback : " + rollbackEx.getMessage());
            }
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression: " + ex.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Log the full error
        } finally {
            // Ensure resources are closed even if exceptions occur
            try { if (rsCheck != null) rsCheck.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmtCheckDetails != null) stmtCheckDetails.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmtDeleteDetails != null) stmtDeleteDetails.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmtDeleteCommande != null) stmtDeleteCommande.close(); } catch (SQLException e) { /* ignored */ }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default commit behavior
                    // Do NOT close the connection if it's managed globally or reused
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    // --- Print Command Details Method ---
    private void afficherDetailsPourImpression(int idCommande) {
        Connection conn = null;
        PreparedStatement stmtCommande = null;
        PreparedStatement stmtDetails = null;
        ResultSet rsCommande = null;
        ResultSet rsDetails = null;
        StringBuilder detailsText = new StringBuilder();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            conn = DBConnection.getConnection();

            // 1. Get Commande Info - Assuming '*' fetches the needed columns AnneeBonCommande, InfosPaiement
            // If not, specify them explicitly: SELECT c.*, f.Nom AS FournisseurNom, c.AnneeBonCommande, c.InfosPaiement ...
            String queryCommande = "SELECT c.*, f.Nom AS FournisseurNom " +
                                   "FROM Commande c " +
                                   "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur " +
                                   "WHERE c.idCommande = ?";
            stmtCommande = conn.prepareStatement(queryCommande);
            stmtCommande.setInt(1, idCommande);
            rsCommande = stmtCommande.executeQuery();

            if (rsCommande.next()) {
                detailsText.append("====================================\n");
                detailsText.append("         BON DE COMMANDE\n");
                detailsText.append("====================================\n\n");
                detailsText.append("COMMANDE N° : ").append(rsCommande.getInt("idCommande")).append("\n");
                detailsText.append("Date Commande : ").append(rsCommande.getTimestamp("DateCommande") != null ? dateTimeFormat.format(rsCommande.getTimestamp("DateCommande")) : "N/A").append("\n");
                detailsText.append("N° Bon        : ").append(rsCommande.getObject("NumeroBonCommande") != null ? rsCommande.getInt("NumeroBonCommande") : "N/A").append("\n");
                // Ajouté: Affichage AnneeBonCommande
                detailsText.append("Année Bon     : ").append(rsCommande.getObject("AnneeBonCommande") != null ? rsCommande.getString("AnneeBonCommande") : "N/A").append("\n\n"); // Adaptez getString/getInt si nécessaire

                detailsText.append("--- Fournisseur ---\n");
                detailsText.append("Nom           : ").append(rsCommande.getString("FournisseurNom") != null ? rsCommande.getString("FournisseurNom") : "N/A").append("\n");
                detailsText.append("\n");

                detailsText.append("--- Statut Commande ---\n");
                detailsText.append("Validé        : ").append(rsCommande.getBoolean("Valide") ? "Oui" : "Non").append("\n");
                detailsText.append("Payé          : ").append(rsCommande.getBoolean("Payer") ? "Oui" : "Non").append("\n\n");

                detailsText.append("--- Informations Paiement ---\n");
                detailsText.append("Mode Paiement : ").append(rsCommande.getString("ModePaiement") != null ? rsCommande.getString("ModePaiement") : "N/A").append("\n");
                detailsText.append("Date Paiement : ").append(rsCommande.getDate("DatePaiement") != null ? dateFormat.format(rsCommande.getDate("DatePaiement")) : "N/A").append("\n");
                // Ajouté: Affichage InfosPaiement
                detailsText.append("Infos Paiement: ").append(rsCommande.getString("InfosPaiement") != null ? rsCommande.getString("InfosPaiement") : "N/A").append("\n");
                detailsText.append("Prix HT       : ").append(String.format("%.2f", rsCommande.getFloat("PrixHT"))).append("\n");
                detailsText.append("Prix TVA      : ").append(String.format("%.2f", rsCommande.getFloat("PrixTVA"))).append("\n");
                detailsText.append("Prix TTC      : ").append(String.format("%.2f", rsCommande.getFloat("PrixTTC"))).append("\n\n");

                detailsText.append("--- Détails des Produits Commandés ---\n");
                detailsText.append("--------------------------------------------------\n");
                detailsText.append(String.format("%-8s | %-20s | %-5s | %-10s\n", "ID Dét.", "Produit", "Qté", "Prix Unit."));
                detailsText.append("--------------------------------------------------\n");

            } else {
                JOptionPane.showMessageDialog(this, "Aucune commande trouvée pour l'ID: " + idCommande, "Données Non Trouvées", JOptionPane.WARNING_MESSAGE);
                return; // Exit if command not found
            }
            rsCommande.close();
            stmtCommande.close();

            // 2. Get DetailCommande Info (potentially multiple lines)
            String queryDetails = "SELECT dc.*, p.Nom AS ProduitNom " +
                                 "FROM DetailCommande dc " +
                                 "LEFT JOIN Produit p ON dc.idProduit = p.idProduit " +
                                 "WHERE dc.idCommande = ? " +
                                 "ORDER BY dc.idDetailCommande"; // Order details
            stmtDetails = conn.prepareStatement(queryDetails);
            stmtDetails.setInt(1, idCommande);
            rsDetails = stmtDetails.executeQuery();

            boolean detailsFound = false;
            while (rsDetails.next()) {
                detailsFound = true;
                detailsText.append(String.format("%-8s | %-20s | %-5.2f | %-10.2f\n",
                        rsDetails.getString("idDetailCommande") !=null ? rsDetails.getString("idDetailCommande") : "N/A",
                        rsDetails.getString("ProduitNom") != null ? rsDetails.getString("ProduitNom") : "N/A",
                        rsDetails.getFloat("QuantiteCommande"),
                        rsDetails.getFloat("PrixUnitaire")));
                // Optionally add expiration/fabrication dates per line
                String expDate = rsDetails.getDate("DateExpiration") != null ? dateFormat.format(rsDetails.getDate("DateExpiration")) : "N/A";
                String fabDate = rsDetails.getDate("DateFabrication") != null ? dateFormat.format(rsDetails.getDate("DateFabrication")) : "N/A";
                detailsText.append(String.format("          | Exp: %-18s | Fab: %s\n", expDate, fabDate));
                 detailsText.append("--------------------------------------------------\n");

            }
             rsDetails.close();
             stmtDetails.close();

            if (!detailsFound) {
                detailsText.append("Aucun détail de produit trouvé pour cette commande.\n");
                detailsText.append("--------------------------------------------------\n");
            }

             detailsText.append("\n====================================\n");


            // 3. Display in a JTextArea
            JTextArea textArea = new JTextArea(detailsText.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced for alignment
            textArea.setEditable(false);
            textArea.setLineWrap(false); // Prevent wrapping to keep columns aligned
            // textArea.setWrapStyleWord(true); // Not needed if lineWrap is false

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 450)); // Adjust size as needed

            JOptionPane.showMessageDialog(this, scrollPane, "Bon de Commande - ID: " + idCommande, JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la récupération des détails pour l'impression: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Close all resources safely
             try { if (rsCommande != null) rsCommande.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmtCommande != null) stmtCommande.close(); } catch (SQLException e) { /* ignored */ }
             try { if (rsDetails != null) rsDetails.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmtDetails != null) stmtDetails.close(); } catch (SQLException e) { /* ignored */ }
             // Don't close connection
        }
    }

    // --- Update Command Count Label ---
    private void updateNombreCommandesLabel() {
        // Ensure label is initialized
        if (labelNombreCommandes != null) {
             labelNombreCommandes.setText(String.valueOf(getNombreCommandesFromTable()));
             // Or query DB again: labelNombreCommandes.setText(String.valueOf(getNombreCommandesFromDB()));
         }
    }

    // --- Get count directly from table model ---
    private int getNombreCommandesFromTable() {
        if (tableauCommandes != null && tableauCommandes.getModel() != null) {
             return tableauCommandes.getRowCount();
        }
        return 0;
    }

    // --- Get count from DB (alternative) ---
    private int getNombreCommandesFromDB() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM Commande";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log error
        } finally {
            // Close resources safely
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
        return count;
    }

    // --- Clear Form Fields ---
    private void clearFieldsCommandes() {
        // Check if components are initialized before clearing
        if (champIdCommande != null) champIdCommande.setText("");
        if (dateCommandeChooser != null) dateCommandeChooser.setDate(null);
        if (comboValideCommande != null) comboValideCommande.setSelectedIndex(0);
        if (comboPayeeCommande != null) comboPayeeCommande.setSelectedIndex(0);
        if (champAnneeBonCommande != null) champAnneeBonCommande.setText(""); // Réintégré
        if (champNumeroBonCommande != null) champNumeroBonCommande.setText("");
        if (comboFournisseur != null) comboFournisseur.setSelectedIndex(0); // Select the default prompt "Sélectionner..."
        if (comboModePaiementCommande != null) comboModePaiementCommande.setSelectedIndex(-1); // Deselect
        if (champInfosPaiementCommande != null) champInfosPaiementCommande.setText(""); // Réintégré
        if (champPrixTTCCommande != null) champPrixTTCCommande.setText("");
        if (champPrixTVACommande != null) champPrixTVACommande.setText("");
        if (champPrixHTCommande != null) champPrixHTCommande.setText("");
        if (datePaiementChooserCommande != null) datePaiementChooserCommande.setDate(null);
        // Clear Details Fields
        if (comboProduit != null) comboProduit.setSelectedIndex(0); // Select the default prompt "Sélectionner..."
        if (champIdDetailCommande != null) champIdDetailCommande.setText("");
        if (champQuantiteCommande != null) champQuantiteCommande.setText("");
        if (champPrixAchatCommande != null) champPrixAchatCommande.setText("");
        if (dateExpirationChooserCommande != null) dateExpirationChooserCommande.setDate(null);
        if (dateFabricationChooserCommande != null) dateFabricationChooserCommande.setDate(null);
        // Reset save button state
        if (boutonSauvegarderCommande != null) {
             boutonSauvegarderCommande.setEnabled(false);
             boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
        }
        // Ensure focus goes to the first field (optional) and first tab
        if (dateCommandeChooser != null) dateCommandeChooser.requestFocusInWindow();
        if (tabbedPaneCommandes != null) tabbedPaneCommandes.setSelectedIndex(0);
    }

    // --- Save All Data Method (Handles Insert) ---
    private void saveAllData() {
        // --- Basic Validation ---
        // (Validation logic remains similar, includes check for non-empty AnneeBonCommande if mandatory)
        if (dateCommandeChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de commande est obligatoire.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(0); dateCommandeChooser.requestFocusInWindow(); return;
        }
         if (champAnneeBonCommande.getText().trim().isEmpty()) { // Added validation if mandatory
             JOptionPane.showMessageDialog(this, "L'année du bon de commande est obligatoire.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
             tabbedPaneCommandes.setSelectedIndex(0); champAnneeBonCommande.requestFocusInWindow(); return;
         }
        if (champNumeroBonCommande.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "Le numéro de bon de commande est obligatoire.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
             tabbedPaneCommandes.setSelectedIndex(0); champNumeroBonCommande.requestFocusInWindow(); return;
        }
         if (comboFournisseur.getSelectedIndex() <= 0) {
             JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur valide.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
             tabbedPaneCommandes.setSelectedIndex(0); comboFournisseur.requestFocusInWindow(); return;
        }
        if (champPrixTTCCommande.getText().trim().isEmpty() /* || other payment fields */) {
             JOptionPane.showMessageDialog(this, "Les informations de prix sont obligatoires.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
             tabbedPaneCommandes.setSelectedIndex(1); champPrixTTCCommande.requestFocusInWindow(); return;
        }
        if (comboProduit.getSelectedIndex() <= 0 || champQuantiteCommande.getText().trim().isEmpty() || champPrixAchatCommande.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "Les informations de détail (Produit valide, Qté, Prix) sont obligatoires.", "Erreur de Saisie", JOptionPane.WARNING_MESSAGE);
             tabbedPaneCommandes.setSelectedIndex(2); comboProduit.requestFocusInWindow(); return;
        }


        Connection conn = null;
        PreparedStatement stmtCommande = null;
        PreparedStatement stmtDetailCommande = null;
        ResultSet generatedKeys = null;
        int generatedCommandeId = -1;

        boolean isUpdate = !champIdCommande.getText().isEmpty();
        if (isUpdate) {
             JOptionPane.showMessageDialog(this, "La mise à jour n'est pas implémentée dans cette version.\nUtilisez 'Ajouter Commande' pour une nouvelle saisie.", "Information", JOptionPane.INFORMATION_MESSAGE);
             return;
        }


        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // --- Insert into Commande ---
            // Modifié pour inclure AnneeBonCommande et InfosPaiement
            String queryCommande = "INSERT INTO Commande (DateCommande, Valide, Payer, AnneeBonCommande, NumeroBonCommande, idFournisseur, " + // Grouped base info
                                  "ModePaiement, DatePaiement, InfosPaiement, PrixHT, PrixTVA, PrixTTC) " + // Grouped payment info
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 12 placeholders
            stmtCommande = conn.prepareStatement(queryCommande, Statement.RETURN_GENERATED_KEYS);

            // --- Set Base Info Parameters ---
            // 1. Date Commande
            stmtCommande.setTimestamp(1, new Timestamp(dateCommandeChooser.getDate().getTime()));
            // 2. Valide
            stmtCommande.setBoolean(2, "Oui".equals(comboValideCommande.getSelectedItem()));
            // 3. Payer
            stmtCommande.setBoolean(3, "Oui".equals(comboPayeeCommande.getSelectedItem()));
            // 4. AnneeBonCommande (Assuming INT, handle potential errors and null)
            try {
                String anneeStr = champAnneeBonCommande.getText().trim();
                if (anneeStr.isEmpty()) {
                    stmtCommande.setNull(4, Types.INTEGER); // Ou Types.VARCHAR si textuel
                } else {
                    stmtCommande.setInt(4, Integer.parseInt(anneeStr)); // Ou setString
                }
            } catch (NumberFormatException nfe) {
                 conn.rollback(); JOptionPane.showMessageDialog(this,"Année Bon Commande doit être un nombre entier valide.","Erreur",JOptionPane.ERROR_MESSAGE); return;
            }
            // 5. Numero Bon Commande
            try {
                 stmtCommande.setInt(5, Integer.parseInt(champNumeroBonCommande.getText().trim()));
            } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this, "Le numéro de bon de commande doit être un nombre entier.","Erreur",JOptionPane.ERROR_MESSAGE); return; }
            // 6. idFournisseur
            String selectedFournisseurName = (String) comboFournisseur.getSelectedItem();
            int fournisseurId = getFournisseurIdFromName(selectedFournisseurName);
             if (fournisseurId == -1) { conn.rollback(); JOptionPane.showMessageDialog(this,"ID Fournisseur non trouvé pour: " + selectedFournisseurName,"Erreur",JOptionPane.ERROR_MESSAGE); return; }
             stmtCommande.setInt(6, fournisseurId);

            // --- Set Payment Info Parameters ---
            // 7. Mode Paiement
            stmtCommande.setString(7, (String) comboModePaiementCommande.getSelectedItem());
            // 8. Date Paiement
            if (datePaiementChooserCommande.getDate() != null) { stmtCommande.setDate(8, new java.sql.Date(datePaiementChooserCommande.getDate().getTime())); } else { stmtCommande.setNull(8, Types.DATE); }
            // 9. InfosPaiement (Assuming VARCHAR, nullable)
            String infosStr = champInfosPaiementCommande.getText().trim();
            if (infosStr.isEmpty()) { stmtCommande.setNull(9, Types.VARCHAR); } else { stmtCommande.setString(9, infosStr); }
            // 10. Prix HT
             try { stmtCommande.setFloat(10, Float.parseFloat(champPrixHTCommande.getText().trim())); } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this,"Prix HT invalide.","Erreur",JOptionPane.ERROR_MESSAGE); return; }
            // 11. Prix TVA
             try {
                 String tvaStr = champPrixTVACommande.getText().trim();
                 if (tvaStr.isEmpty()) stmtCommande.setNull(11, Types.FLOAT); else stmtCommande.setFloat(11, Float.parseFloat(tvaStr));
             } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this,"Prix TVA invalide.","Erreur",JOptionPane.ERROR_MESSAGE); return; }
            // 12. Prix TTC
             try { stmtCommande.setFloat(12, Float.parseFloat(champPrixTTCCommande.getText().trim())); } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this,"Prix TTC invalide.","Erreur",JOptionPane.ERROR_MESSAGE); return; }


            // --- Execute Commande Insert ---
            int affectedRows = stmtCommande.executeUpdate();
            if (affectedRows == 0) { throw new SQLException("La création de la commande a échoué, aucune ligne affectée."); }

            // Get generated ID
            generatedKeys = stmtCommande.getGeneratedKeys();
            if (generatedKeys.next()) { generatedCommandeId = generatedKeys.getInt(1); champIdCommande.setText(String.valueOf(generatedCommandeId)); }
            else { throw new SQLException("La création de la commande a échoué, impossible d'obtenir l'ID généré."); }
            generatedKeys.close();
            stmtCommande.close();


            // --- Insert into DetailCommande (No changes needed here for AnneeBon/InfosPaiement) ---
            String queryDetailCommande = "INSERT INTO DetailCommande (idDetailCommande, QuantiteCommande, PrixUnitaire, idProduit, DateExpiration, DateFabrication, idCommande) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtDetailCommande = conn.prepareStatement(queryDetailCommande);

            String idDetailStr = champIdDetailCommande.getText().trim();
            if (idDetailStr.isEmpty()) { stmtDetailCommande.setNull(1, Types.VARCHAR); } else { stmtDetailCommande.setString(1, idDetailStr); }
            try { stmtDetailCommande.setFloat(2, Float.parseFloat(champQuantiteCommande.getText().trim())); } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this,"Quantité invalide.","Erreur",JOptionPane.ERROR_MESSAGE); return; }
            try { stmtDetailCommande.setFloat(3, Float.parseFloat(champPrixAchatCommande.getText().trim())); } catch (NumberFormatException nfe) { conn.rollback(); JOptionPane.showMessageDialog(this,"Prix d'achat invalide.","Erreur",JOptionPane.ERROR_MESSAGE); return; }
            String selectedProduitName = (String) comboProduit.getSelectedItem();
             int produitId = getProduitIdFromName(selectedProduitName);
             if (produitId == -1) { conn.rollback(); JOptionPane.showMessageDialog(this,"ID Produit non trouvé pour: " + selectedProduitName,"Erreur",JOptionPane.ERROR_MESSAGE); return; }
             stmtDetailCommande.setInt(4, produitId);
            if (dateExpirationChooserCommande.getDate() != null) { stmtDetailCommande.setDate(5, new java.sql.Date(dateExpirationChooserCommande.getDate().getTime())); } else { stmtDetailCommande.setNull(5, Types.DATE); }
            if (dateFabricationChooserCommande.getDate() != null) { stmtDetailCommande.setDate(6, new java.sql.Date(dateFabricationChooserCommande.getDate().getTime())); } else { stmtDetailCommande.setNull(6, Types.DATE); }
            stmtDetailCommande.setInt(7, generatedCommandeId);

            stmtDetailCommande.executeUpdate();
            stmtDetailCommande.close();

            // --- Commit Transaction ---
            conn.commit();
            JOptionPane.showMessageDialog(this, "Commande (ID: " + generatedCommandeId + ") et ses détails ont été sauvegardés avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);

            // --- Post-Save Actions ---
            updateTableCommandes();
            updateNombreCommandesLabel();
            clearFieldsCommandes();


        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { System.err.println("Erreur lors du rollback : " + ex.getMessage()); }
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la sauvegarde des données: " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } catch (Exception e) {
             try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* Ignored */ }
             JOptionPane.showMessageDialog(this, "❌ Erreur inattendue: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
        finally {
            // Close resources safely
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmtCommande != null) stmtCommande.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmtDetailCommande != null) stmtDetailCommande.close(); } catch (SQLException e) { /* ignored */ }
            try { if (conn != null) { conn.setAutoCommit(true); /* Don't close connection */ }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // --- Placeholder functions for ID lookup (REPLACE with actual DB query) ---
    private int getFournisseurIdFromName(String name) {
        if (name == null || name.equals("Sélectionner...")) {
            return -1; // Invalid selection
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT idFournisseur FROM Fournisseur WHERE Nom = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("idFournisseur");
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Erreur de recherche ID fournisseur pour: " + name + "\n" + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
             // Do not close connection
        }
         return id;
    }

     private int getProduitIdFromName(String name) {
        if (name == null || name.equals("Sélectionner...")) {
             return -1; // Invalid selection
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = -1;
         try {
             conn = DBConnection.getConnection();
             String query = "SELECT idProduit FROM Produit WHERE Nom = ?";
             stmt = conn.prepareStatement(query);
             stmt.setString(1, name);
             rs = stmt.executeQuery();
             if (rs.next()) {
                 id = rs.getInt("idProduit");
             }
         } catch (SQLException e) {
              JOptionPane.showMessageDialog(this, "Erreur de recherche ID produit pour: " + name + "\n" + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
         } finally {
              try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
              try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
              // Do not close connection
         }
          return id;
     }


    // -----------------------------------
    // 9. دوال مساعدة لإنشاء الأزرار
    // -----------------------------------
    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(new Color(240, 240, 240)); // Slightly brighter text
        button.setFont(new Font("Roboto", Font.BOLD, 13)); // Bold and slightly smaller
        // button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Adjusted padding
        button.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(backgroundColor.darker(), 1), // Subtle border
             BorderFactory.createEmptyBorder(7, 14, 7, 14) // Padding inside border
        ));
         Color originalColor = backgroundColor; // Store original color
        button.setFocusPainted(false);
        button.setOpaque(true); // Needed for background color
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {


            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                 if(button.isEnabled()) {
                     button.setBackground(originalColor.brighter());
                 }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                 if(button.isEnabled()) {
                    button.setBackground(originalColor);
                 }
            }
            @Override
             public void mousePressed(MouseEvent e) {
                 if(button.isEnabled()) {
                    button.setBackground(originalColor.darker());
                 }
             }

             @Override
             public void mouseReleased(MouseEvent e) {
                  if(button.isEnabled()) {
                     // Use getComponent().contains() for better accuracy with component bounds
                     if (e.getComponent().contains(e.getPoint())) {
                         button.setBackground(originalColor.brighter());
                     } else {
                         button.setBackground(originalColor);
                     }
                 }
             }
        });

         // Change background slightly when disabled
         button.addPropertyChangeListener("enabled", evt -> {
             if (!(Boolean) evt.getNewValue()) {
                 button.setBackground(new Color(180, 180, 180)); // Grey out when disabled
                 button.setForeground(new Color(100,100,100));
             } else {
                  button.setBackground(originalColor);
                  button.setForeground(new Color(240, 240, 240));
             }
         });

        return button;
    }

    // -----------------------------------
    // 10. فلاتر مخصصة للحقول النصية
    // -----------------------------------
    // NumberOnlyFilter and NumberWithDecimalFilter remain the same as in your original code.
    class NumberOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d*")) { // Allow only digits
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("\\d*")) { // Allow only digits
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    class NumberWithDecimalFilter extends DocumentFilter {
         // Allow numbers and at most one decimal point
        private final String regex = "\\d*\\.?\\d*";

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String beforeOffset = currentText.substring(0, offset);
            String afterOffset = currentText.substring(offset);
            String proposedText = beforeOffset + string + afterOffset;

            if (proposedText.matches(regex)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
             if (text == null) return;
             String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
             String beforeOffset = currentText.substring(0, offset);
             String afterOffset = currentText.substring(offset + length);
             String proposedText = beforeOffset + text + afterOffset;

             if (proposedText.matches(regex)) {
                 super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // --- Placeholder for DBConnection class ---
    // Replace this with your actual database connection implementation
    static class DBConnection {
        // !! IMPORTANT: REPLACE THESE WITH YOUR ACTUAL DATABASE DETAILS !!
        private static final String URL = "jdbc:mysql://localhost:3307/chemstock"; // Added Timezone & SSL
        private static final String USER = "root"; // Your DB Username
        private static final String PASSWORD = ""; // Your DB Password
        private static Connection connection = null;

        public static Connection getConnection() throws SQLException {
             // Simple singleton pattern (improve for real applications with connection pooling)
            if (connection == null || connection.isClosed()) {
                 try {
                    // Load the MySQL driver (optional for newer JDBC versions)
                    // Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("Connecting to database..."); // Debug output
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                     System.out.println("Database connection successful!"); // Debug output
                 } catch (SQLException e) {
                     System.err.println("Database Connection Error: " + e.getMessage());
                     e.printStackTrace(); // Print full stack trace
                     throw e; // Re-throw exception
                 }
             }
             return connection;
        }

         // Add a method to close the connection when the application exits
         public static void closeConnection() {
             if (connection != null) {
                 try {
                     connection.close();
                     connection = null;
                     System.out.println("Database connection closed.");
                 } catch (SQLException e) {
                      System.err.println("Error closing database connection: " + e.getMessage());
                 }
             }
         }
    }


    // Main method to run the application
    public static void main(String[] args) {
         // Set Look and Feel (Optional, Nimbus is often nice)
         try {
              for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                  if ("Nimbus".equals(info.getName())) {
                      javax.swing.UIManager.setLookAndFeel(info.getClassName());
                      break;
                  }
              }
          } catch (Exception ex) {
              java.util.logging.Logger.getLogger(AyaCommandes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
          }

        SwingUtilities.invokeLater(() -> {
            AyaCommandes frame = new AyaCommandes();
            frame.setVisible(true);

             // Add a shutdown hook to close the DB connection properly
             Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                 DBConnection.closeConnection();
             }));
        });
    }
}
