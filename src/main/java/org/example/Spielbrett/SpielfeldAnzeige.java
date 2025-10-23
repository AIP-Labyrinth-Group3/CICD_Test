package org.example.Spielbrett;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Wiederverwendbares Schachbrett als Swing-Panel.
 * - Unicode-Wegekarten
 * - Feldklick-Listener
 */
public class SpielfeldAnzeige extends JPanel {

    private final Map<Character, Image> pieceImages = new HashMap<>();
    private int selRow = -1;
    private int selCol = -1;
    /** Listener für Klicks auf ein Feld */
    public interface SquareClickListener {
        /** @param file 0..7 (a..h), @param rank 0..7 (8..1 von oben nach unten), @param algebraic z.B. "e4" */
        void onSquareClicked(int file, int rank, String algebraic);
    }
    private final char[][] board = new char[7][7]; // [rank][file], rank=0 -> 8. Reihe (oben)
    private boolean whiteAtBottom = true;
    private boolean showCoords = true;

    // Farben
    private Color light = new Color(240, 217, 181);
    private Color dark  = new Color(181, 136,  99);
    private Color highlight = new Color(255, 215, 0, 120);
    private int highlightRow = -1, highlightCol = -1;
    private SquareClickListener clickListener;
    public SpielfeldAnzeige() {
        ladeWegKartenImages();
    }
    private void ladeWegKartenImages() {
        String[] names = {"╔", "╗", "╝", "╚", "═", "║", "╠", "╦", "╣", "╩"};
        for (String n : names) {
            String pieceName;
            switch (n.charAt(0)) {
                case '╔':
                    pieceName = "ecke_BC";
                    break;
                case '╗':
                    pieceName = "ecke_CD";
                    break;
                case '╝':
                    pieceName = "ecke_AD";
                    break;
                case '╚':
                    pieceName = "ecke_AB";
                    break;
                case '═':
                    pieceName = "gerade_BD";
                    break;
                case '║':
                    pieceName = "gerade_AC";
                    break;
                case '╠':
                    pieceName = "kreuzung_ABC";
                    break;
                case '╦':
                    pieceName = "kreuzung_BCD";
                    break;
                case '╣':
                    pieceName = "kreuzung_ACD";
                    break;
                case '╩':
                    pieceName = "kreuzung_ABD";
                    break;
                default:
                    continue;

            }
            String path = "/pieces/"  + pieceName + ".png";
            try {
                pieceImages.put(n.charAt(0), ImageIO.read(getClass().getResource(path)));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Fehler beim Laden: " + path + " → " + e);
            }
        }
    }

    public SpielfeldAnzeige(String wegekarten){
        ladeWegKartenImages();
        setPreferredSize(new Dimension(640, 640));
        setBackground(new Color(32, 33, 36));
        setWegekarten(wegekarten);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                int W = getWidth();
                int H = getHeight();
                int margin = Math.max(20, Math.min(W, H) / 20);
                int size = Math.min(W, H) - 2 * margin;
                int cell = size / 7;
                size = cell * 7;
                int x0 = (W - size) / 2;
                int y0 = (H - size) / 2;

                // prüfen, ob innerhalb des Bretts
                if (x >= x0 && y >= y0 && x < x0 + size && y < y0 + size) {
                    int col = (x - x0) / cell;
                    int row = (y - y0) / cell;

                    // 👇 hier: speichern & repainten
                    selRow = row;
                    selCol = col;
                    repaint();

                    // Optional: Debugausgabe
                    int file = row;
                    int rank = col;
                    System.out.println("Klick auf Feld: " + file +"/"+ rank);
                    clickListener.onSquareClicked(file, rank, toAlg(col, row));
                }
            }
        });
    }

    /* ===================== Öffentliche API ===================== */

    /** Setzt Brett nach FEN (nur Brett-Teil: 8 Ranks getrennt durch '/') */
    public final void setWegekarten(String wegekarten) {
        String[] ranks = wegekarten.trim().split("/");
        if (ranks.length != 7) throw new IllegalArgumentException("Wegekarten  müssen in 7er Gruppen");
        for (int r = 0; r < 7; r++) {
            int c = 0;
            for (char ch : ranks[r].toCharArray()) {
                if (Character.isDigit(ch)) {
                    int n = ch - '0';
                    for (int i = 0; i < n; i++) board[r][c++] = ' ';
                } else {
                    board[r][c++] = ch;
                }
            }
            if (c != 7) throw new IllegalArgumentException("Wegekarten  müssen in 7er Gruppen: " + ranks[r]);
        }
        repaint();
    }

    /** Verschiebt eine Figur von z.B. "e2" nach "e4". */
    public void move(String fromAlg, String toAlg) {
        int[] f = fromAlg(fromAlg);
        int[] t = fromAlg(toAlg);
        char piece = board[f[1]][f[0]];
        board[f[1]][f[0]] = ' ';
        board[t[1]][t[0]] = piece;
        repaint();
    }

    /** Setzt/entfernt ein Highlight auf einem Feld (z.B. nach Klick). */
    public void setSquareHighlight(String algebraicOrNull) {
        if (algebraicOrNull == null) {
            highlightRow = highlightCol = -1;
        } else {
            int[] rc = fromAlg(algebraicOrNull);
            highlightCol = rc[0];
            highlightRow = rc[1];
        }
        repaint();
    }

    /** Listener registrieren (optional) */
    public void setSquareClickListener(SquareClickListener l) { this.clickListener = l; }

    /** Orientierung: true=Weiß unten (Standard), false=Schwarz unten */
    public void setBoardOrientationWhiteAtBottom(boolean whiteBottom) {
        this.whiteAtBottom = whiteBottom;
        repaint();
    }

    /** Koordinaten anzeigen a–h, 8–1 */
    public void setShowCoordinates(boolean show) { this.showCoords = show; repaint(); }

    /** Farben anpassen (hell, dunkel, highlight) */
    public void setColors(Color light, Color dark, Color highlight) {
        if (light != null) this.light = light;
        if (dark != null)  this.dark = dark;
        if (highlight != null) this.highlight = highlight;
        repaint();
    }






    /* ===================== Rendering ===================== */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int W = getWidth(), H = getHeight();
        int margin = Math.max(20, Math.min(W, H) / 20);
        int size = Math.min(W, H) - 2 * margin;
        int cell = size / 7;
        size = cell * 7;
        int x0 = (W - size) / 2;
        int y0 = (H - size) / 2;

        // === Koordinaten (a–h, 8–1) ===
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(getFont().deriveFont(Font.PLAIN, Math.max(12, cell * 0.18f)));
        String files = "abcdefgh";
        for (int c = 0; c < 7; c++) {
            String s = String.valueOf(c+1);
            //String s = String.valueOf(files.charAt(c));
            //int x = x0 + c * cell + cell / 2 - g2.getFontMetrics().stringWidth(s) / 2;
            int x = x0 + c * cell + cell / 2 - g2.getFontMetrics().stringWidth(s) / 2;
            g2.drawString(s, x, y0 - 6);
            g2.drawString(s, x, y0 + size + g2.getFontMetrics().getAscent() + 4);
        }
        for (int r = 0; r < 7; r++) {
            String s = String.valueOf( r+1);
            int y = y0 + r * cell + (cell + g2.getFontMetrics().getAscent()) / 2 - 2;
            g2.drawString(s, x0 - g2.getFontMetrics().stringWidth(s) - 6, y);
            g2.drawString(s, x0 + size + 6, y);
        }

        // === Felder + Wegekarten ===
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                boolean dark = ((r + c) % 2 == 1);
                g2.setColor(dark ? new Color(181, 136, 99) : new Color(240, 217, 181));
                int x = x0 + c * cell;
                int y = y0 + r * cell;
                g2.fillRect(x, y, cell, cell);

                // Falls Highlight gesetzt (z. B. bei Klick)
                if (r == selRow && c == selCol) {
                    g2.setColor(new Color(255, 215, 0, 120));
                    g2.fillRect(x, y, cell, cell);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRect(x, y, cell, cell);
                }
                // === Wegekarte zeichnen ===
                char piece = board[r][c];
                if (piece != ' ') {
                    Image img = pieceImages.get(piece);
                    if (img != null) {
                        int padding = (int) (cell * 0.05);
                        padding=0;
                        g2.drawImage(
                                img,
                                x + padding, y + padding,
                                cell - 2 * padding, cell - 2 * padding,
                                this
                        );
                    } else {
                        // Fallback: Unicode-Symbol falls kein Bild vorhanden
                        String sym = pieceSymbol(piece);
                        g2.setFont(getFont().deriveFont(cell * 0.7f));
                        FontMetrics fm = g2.getFontMetrics();
                        int sx = x + (cell - fm.stringWidth(sym)) / 2;
                        int sy = y + (cell + fm.getAscent() - fm.getDescent()) / 2;
                        g2.setColor(Color.BLACK);
                        g2.drawString(sym, sx, sy);
                    }
                }
            }
        }
        g2.dispose();
    }


    private void drawCoordinates(Graphics2D g2, CellGeom geom) {
        g2.setColor(new Color(230, 230, 230));
        g2.setFont(getFont().deriveFont(Font.PLAIN, Math.max(12, geom.cell * 0.18f)));

        String files = "0123456789";
        for (int c = 0; c < 7; c++) {
            //int logicalFile = whiteAtBottom ? c : 6 - c;
            //String s = String.valueOf(files.charAt(logicalFile));
            String s = String.valueOf(c+1);
            int x = geom.x0 + c * geom.cell + geom.cell/2 - g2.getFontMetrics().stringWidth(s)/2;
            g2.drawString(s, x, geom.y0 - 6);
            g2.drawString(s, x, geom.y0 + geom.size + g2.getFontMetrics().getAscent() + 4);
        }
        for (int r = 0; r < 7; r++) {
            //int logicalRank = whiteAtBottom ? (7 - r) : (r + 1);
            //String s = String.valueOf(logicalRank);
            String s = String.valueOf(r+1);
            int y = geom.y0 + r * geom.cell + (geom.cell + g2.getFontMetrics().getAscent())/2 - 2;
            g2.drawString(s, geom.x0 - g2.getFontMetrics().stringWidth(s) - 6, y);
            g2.drawString(s, geom.x0 + geom.size + 6, y);
        }
    }

    private static final class CellGeom {
        int x0, y0, size, cell;
    }

    private CellGeom computeGeom() {
        CellGeom cg = new CellGeom();
        int W = getWidth(), H = getHeight();
        int margin = Math.max(20, Math.min(W, H) / 20); // Platz für Koordinaten
        int size = Math.min(W, H) - 2 * margin;
        int cell = Math.max(1, size / 7);
        size = cell * 7;
        int x0 = (W - size) / 2;
        int y0 = (H - size) / 2;

        cg.x0 = x0; cg.y0 = y0; cg.size = size; cg.cell = cell;
        return cg;
    }

    /* ===================== Hilfsfunktionen ===================== */

    private String pieceSymbol(char p) {
        //switch (p) {
        //    case 'K': return "♔"; case 'Q': return "♕"; case 'R': return "♖";
        //    case 'B': return "♗"; case 'N': return "♘"; case 'P': return "♙";
        //    case 'k': return "♚"; case 'q': return "♛"; case 'r': return "♜";
        //    case 'b': return "♝"; case 'n': return "♞"; case 'p': return "♟";
        //    default: return "";
        //}
        return String.valueOf(p);
    }

    /** "e4" -> [file,rank] (0..7, 0..7), rank 0 = 8. Reihe */
    private int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) throw new IllegalArgumentException("Algebraisch erwartet, z.B. e2");
        char f = Character.toLowerCase(alg.charAt(0));
        char r = alg.charAt(1);
        if (f < 'a' || f > 'h' || r < '1' || r > '8') throw new IllegalArgumentException("Ungültig: " + alg);
        int file = f - 'a';
        int rank = 8 - (r - '0'); // '8' -> 0, '1' -> 7
        return new int[]{file, rank};
    }

    /** [file,rank] -> "e4" */
    private String toAlg(int file, int rank) {
        return "" + (char)('a' + file) + (7 - rank);
    }


}
