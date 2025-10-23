package org.example.Spielbrett;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Gamelayout extends JFrame {
    private SpielfeldAnzeige board;           // aktuelle Board-Instanz
    private final JPanel centerHolder = new JPanel(new BorderLayout()); // Container für das Board
    private final Spielbrett spielbrett;
    private int[]  coordForPush = new int[]{0, 0};
    private JTextField textFieldReihe = new JTextField(15); // 15 columns wide
    private JTextField textFieldSpalte = new JTextField(1); // 15 columns wide
    private JButton down;
    private JButton right;
    private JButton up;
    private JButton left;

    public Gamelayout() {
        setTitle("Das verrückte Labyrinth");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 800));

        // Spielbrett anlegen
        spielbrett = new Spielbrett(7, 7, 20, 18, 12);

        // --- Mitte: Board-Container
        add(centerHolder, BorderLayout.CENTER);
        refreshBoard(); // initial einmal erstellen

        // --- Rechts: Seitenleiste
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(220, 640));
        sidePanel.setBackground(new Color(40, 40, 40));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JLabel cardLabel = new JLabel();
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);
        final String[] plateStr = {"/pieces/ecke_AB.png"};
        char plate = spielbrett.getSpareCard().getWegSymbol();
        plateStr[0] = "/pieces/"+spielbrett.imageNamePlate(plate)+".png";
        try {
            Image img = ImageIO.read(getClass().getResource(plateStr[0]));
            cardLabel.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
        } catch (IOException | IllegalArgumentException e) {
            cardLabel.setText("Karte fehlt");
            cardLabel.setForeground(Color.WHITE);
        }
        JLabel cardTitle = new JLabel("Aktuelle Zielkarte", SwingConstants.CENTER);
        cardTitle.setForeground(Color.WHITE);
        cardTitle.setFont(cardTitle.getFont().deriveFont(Font.BOLD, 16f));
        JButton neuButton = new JButton("Spielfeld neu erstellen");
        JLabel labelReihe = new JLabel("Reihe:");
        JLabel labelSpalte = new JLabel("Spalte:");
        // Create a text box (JTextField)
        //JTextField textFieldReihe = new JTextField(15); // 15 columns wide
        textFieldReihe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        //JTextField textFieldSpalte = new JTextField(1); // 15 columns wide
        textFieldSpalte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        neuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelReihe.setAlignmentX(Component.CENTER_ALIGNMENT);
        textFieldReihe.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelSpalte.setAlignmentX(Component.CENTER_ALIGNMENT);
        textFieldSpalte.setAlignmentX(Component.CENTER_ALIGNMENT);
        up    = new JButton("↑");
        right  = new JButton("→");
        down = new JButton("↓");
        left   = new JButton("←");

        up.setActionCommand("UP");
        down.setActionCommand("DOWN");
        left.setActionCommand("LEFT");
        right.setActionCommand("RIGHT");

        up.setEnabled(false);
        down.setEnabled(false);
        left.setEnabled(false);
        right.setEnabled(false);





        // Jetzt einfach der Reihe nach hinzufügen
        sidePanel.add(cardTitle);
        sidePanel.add(cardLabel);
        sidePanel.add(Box.createVerticalStrut(10)); // Abstand
        sidePanel.add(labelReihe);
        sidePanel.add(textFieldReihe);
        sidePanel.add(labelSpalte);
        sidePanel.add(textFieldSpalte);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(neuButton);




        //  rechts aussen: Steuerungs-Panel
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Außenabstand

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10); // Abstand zwischen Buttons



        // Alle Buttons gleich groß wirken lassen
        Dimension btnSize = new Dimension(120, 60);
        up.setPreferredSize(btnSize);
        right.setPreferredSize(btnSize);
        down.setPreferredSize(btnSize);
        left.setPreferredSize(btnSize);

        // 3x3 Grid: (row, col)
        // oben   -> (0,1)
        c.gridx = 1; c.gridy = 0;
        p.add(up, c);

        // rechts -> (1,2)
        c.gridx = 2; c.gridy = 1;
        p.add(right, c);

        // unten  -> (2,1)
        c.gridx = 1; c.gridy = 2;
        p.add(down, c);

        // links  -> (1,0)
        c.gridx = 0; c.gridy = 1;
        p.add(left, c);
        //add(p, BorderLayout.WEST);

        sidePanel.add(p);

        add(sidePanel, BorderLayout.EAST);




        //Alle Pfeiltasten rufen den selben ActionListener auf
        ActionListener pushAction = e -> {
            int direction=0;
            String cmd = e.getActionCommand();  // hier steht z. B. "TOP"
            switch (cmd) {
                case "UP" ->  direction=3;
                case "DOWN" -> direction=4;
                case "LEFT" -> direction=1;
                case "RIGHT" -> direction=2;

            }




            Spielkarte mCard = spielbrett.getSpareCard();
            System.out.println("Spare Card: " + mCard.getWegSymbol());

            mCard = spielbrett.pushPlate(coordForPush[0], coordForPush[1], spielbrett.getSpareCard(), direction);
            spielbrett.setSparecard(mCard);

            System.out.println("New Spare Card: " + spielbrett.getSpareCard().getWegSymbol());
            refreshBoard();

            char plate1 = spielbrett.getSpareCard().getWegSymbol();
            String plateName = spielbrett.imageNamePlate(plate1);
            System.out.println(plateName);

            try {
                Image img = ImageIO.read(getClass().getResource("/pieces/" + plateName + ".png"));
                cardLabel.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            } catch (IOException | IllegalArgumentException s) {
                cardLabel.setText("Karte fehlt");
                cardLabel.setForeground(Color.WHITE);
            }

            cardLabel.revalidate();
            cardLabel.repaint();
        };

        // den Pfeilbuttons den ActionListener zuweisen
        up.addActionListener(pushAction);
        down.addActionListener(pushAction);
        left.addActionListener(pushAction);
        right.addActionListener(pushAction);




        // Klick: Spielfeld neu bauen (z. B. nachdem du am Spielbrett etwas geändert hast)
        neuButton.addActionListener(e -> {
            // Beispiel: irgendwas am Spielbrett ändern
            Spielkarte mCard = spielbrett.getSpareCard();
            System.out.println("Spare Card: " + mCard.getWegSymbol());
            mCard=spielbrett.pushPlate(coordForPush[0], coordForPush[1], spielbrett.getSpareCard(), 2);
            spielbrett.setSparecard(mCard);
            System.out.println("New Spare Card: " + spielbrett.getSpareCard().getWegSymbol());
            refreshBoard(); // -> ERNEUTES Erstellen + Anzeigen
            char plate1 = spielbrett.getSpareCard().getWegSymbol();
            plateStr[0] = spielbrett.imageNamePlate(plate1);
            System.out.println(plateStr[0]);
            try {
                Image img = ImageIO.read(getClass().getResource("/pieces/"+plateStr[0]+".png"));
                cardLabel.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            } catch (IOException | IllegalArgumentException s) {
                cardLabel.setText("Karte fehlt");
                cardLabel.setForeground(Color.WHITE);
            }
            cardLabel.revalidate();
            cardLabel.repaint();

        });

        // 🖱️ Klick-Listener auf Karte
        cardLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                spielbrett.rotatePlate();
                char plate = spielbrett.getSpareCard().getWegSymbol();
                plateStr[0] = spielbrett.imageNamePlate(plate);
                System.out.println(plateStr[0]);
                try {
                    Image img = ImageIO.read(getClass().getResource("/pieces/"+plateStr[0]+".png"));
                    cardLabel.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                } catch (IOException | IllegalArgumentException s) {
                    cardLabel.setText("Karte fehlt");
                    cardLabel.setForeground(Color.WHITE);

                }
                cardLabel.revalidate();
                cardLabel.repaint();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }






    /** Baut den Wegkarten-String neu und ersetzt die Board-Komponente in der Mitte. */
    private void refreshBoard() {
        String wege = buildWegkartenString(spielbrett);

        // altes Board raus
        if (board != null) {
            centerHolder.remove(board);
        }

        // neues Board rein
        board = new SpielfeldAnzeige(wege);
        board.setSquareClickListener((file, rank, alg) -> {
            board.setSquareHighlight(alg);
            coordForPush[0] = file;
            coordForPush[1] = rank;
            System.out.println("Koordinaten für Push gesetzt auf: Reihe " + coordForPush[0] + ", Spalte " + coordForPush[1]);
            textFieldReihe.setText(Integer.toString(coordForPush[0]));
            textFieldSpalte.setText(Integer.toString(coordForPush[1]));
            //je nach angeklickter karte Pfeilbuttons frei schalten
            down.setEnabled(coordForPush[0] == 0);
            up.setEnabled(coordForPush[0] == 6);
            right.setEnabled(coordForPush[1] == 0);
            left.setEnabled(coordForPush[1] == 6);

        });

        centerHolder.add(board, BorderLayout.CENTER);
        centerHolder.revalidate();
        centerHolder.repaint();
    }




    /** Hilfsfunktion: erzeugt deinen Wegkarten-String aus dem aktuellen Spielbrett-Zustand. */
    private static String buildWegkartenString(Spielbrett sb) {
        StringBuilder sbuf = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sbuf.append(sb.printLine(i)).append("/");
        }
        return sbuf.toString();
    }
}
