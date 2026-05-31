package app;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import estruturas.ListaEncadeada;
import estruturas.NoLista;
import indice.IndiceInvertido;
import modelo.Documento;
import servico.IndexadorArquivos;
import servico.NormalizadorPalavra;
import servico.PersistenciaIndice;

public class TelaBuscador extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String ARQUIVO_INDICE = "indice.dat";

    // ── Paleta cremosa ────────────────────────────────────────
    private static final Color CREME        = new Color(255, 248, 240);
    private static final Color CREME_ESCURO = new Color(247, 236, 224);
    private static final Color CREME_CARD   = new Color(255, 252, 248);
    private static final Color ROSA         = new Color(219, 145, 155);
    private static final Color ROSA_CLARO   = new Color(242, 210, 215);
    private static final Color ROSA_ESCURO  = new Color(190, 100, 115);
    private static final Color MARSALA      = new Color(185, 110, 100);
    private static final Color SAGE         = new Color(130, 165, 130);
    private static final Color DOURADO      = new Color(200, 160, 100);
    private static final Color TEXTO        = new Color(80,  55,  55);
    private static final Color TEXTO_MUTED  = new Color(160, 130, 120);
    private static final Color BORDA        = new Color(225, 200, 195);

    private JTextField campoDiretorio;
    private JTextField campoPesquisa;
    private JTextArea  areaResultado;
    private JLabel     statusLabel;

    private IndiceInvertido     indice;
    private IndexadorArquivos   indexador;
    private PersistenciaIndice  persistencia;
    private NormalizadorPalavra normalizador;

    public TelaBuscador() {
        indice       = new IndiceInvertido();
        indexador    = new IndexadorArquivos();
        persistencia = new PersistenciaIndice();
        normalizador = new NormalizadorPalavra();

        configurarLookAndFeel();

        setTitle("Buscador de Arquivos");
        setSize(780, 660);
        setMinimumSize(new Dimension(620, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CREME);

        criarComponentes();
    }

    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("OptionPane.background",        CREME_ESCURO);
        UIManager.put("Panel.background",             CREME_ESCURO);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("Button.focus",                 new Color(0, 0, 0, 0));
        UIManager.put("FileChooser.background",       CREME_ESCURO);
        UIManager.put("FileChooser.foreground",       TEXTO);
    }

    private void criarComponentes() {
        setLayout(new BorderLayout());
        add(criarHeader(),   BorderLayout.NORTH);
        add(criarCentro(),   BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel criarHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(CREME_ESCURO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDA);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 36, 18, 36));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel flor = new JLabel("❀");
        flor.setFont(new Font("Serif", Font.PLAIN, 24));
        flor.setForeground(ROSA);

        JLabel titulo = new JLabel("Buscador de Arquivos");
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        titulo.setForeground(TEXTO);

        left.add(flor);
        left.add(titulo);

        // Tags de estruturas à direita
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tags.setOpaque(false);
        for (String t : new String[]{"Índice Invertido", "Hash Map", "Lista Encadeada"}) {
            tags.add(criarTag(t));
        }

        header.add(left, BorderLayout.WEST);
        header.add(tags, BorderLayout.EAST);
        return header;
    }

    private JLabel criarTag(String texto) {
        JLabel tag = new JLabel(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ROSA_CLARO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tag.setFont(new Font("Serif", Font.PLAIN, 11));
        tag.setForeground(ROSA_ESCURO);
        tag.setBorder(new EmptyBorder(3, 10, 3, 10));
        tag.setOpaque(false);
        return tag;
    }

    // ── Centro ────────────────────────────────────────────────
    private JPanel criarCentro() {
        // Painel externo com fundo creme
        JPanel externo = new JPanel(new BorderLayout());
        externo.setBackground(CREME);
        externo.setBorder(new EmptyBorder(28, 40, 20, 40));

        // Coluna central
        JPanel coluna = new JPanel();
        coluna.setOpaque(false);
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));

        coluna.add(criarCardCampos());
        coluna.add(Box.createVerticalStrut(16));
        coluna.add(criarCardResultado());

        externo.add(coluna, BorderLayout.CENTER);
        return externo;
    }

    // ── Card unificado de campos ──────────────────────────────
    private JPanel criarCardCampos() {
        JPanel card = criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─ Seção: diretório ─
        JLabel lblDir = criarRotulo("Diretório");
        lblDir.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoDiretorio = criarTextField("Caminho da pasta...");
        campoDiretorio.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoDiretorio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel botoesDir = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botoesDir.setOpaque(false);
        botoesDir.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton botaoEscolher = criarBotao("Escolher pasta",  ROSA,    false);
        JButton botaoIndexar  = criarBotao("Indexar",         MARSALA, true);
        JButton botaoCarregar = criarBotao("Carregar índice", DOURADO, false);
        botoesDir.add(botaoEscolher);
        botoesDir.add(botaoIndexar);
        botoesDir.add(botaoCarregar);

        // Divisória interna
        JSeparator divider = new JSeparator();
        divider.setForeground(BORDA);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ─ Seção: pesquisa ─
        JLabel lblPesq = criarRotulo("Buscar palavras");
        lblPesq.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaPesq = new JPanel(new BorderLayout(8, 0));
        linhaPesq.setOpaque(false);
        linhaPesq.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaPesq.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        campoPesquisa = criarTextField("Digite palavras-chave e pressione Enter...");
        JButton botaoPesquisar = criarBotao("Pesquisar", ROSA, true);

        linhaPesq.add(campoPesquisa,  BorderLayout.CENTER);
        linhaPesq.add(botaoPesquisar, BorderLayout.EAST);

        // Montar card
        card.add(lblDir);
        card.add(Box.createVerticalStrut(8));
        card.add(campoDiretorio);
        card.add(Box.createVerticalStrut(10));
        card.add(botoesDir);
        card.add(Box.createVerticalStrut(16));
        card.add(divider);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPesq);
        card.add(Box.createVerticalStrut(8));
        card.add(linhaPesq);

        // Ações
        botaoEscolher.addActionListener(e  -> escolherDiretorio());
        botaoIndexar.addActionListener(e   -> indexarDiretorio());
        botaoCarregar.addActionListener(e  -> carregarIndice());
        botaoPesquisar.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e  -> pesquisar());

        return card;
    }

    // ── Card de resultado ─────────────────────────────────────
    private JPanel criarCardResultado() {
        JPanel card = criarCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRes = criarRotulo("Resultados");

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setBackground(CREME_ESCURO);
        areaResultado.setForeground(TEXTO_MUTED);
        areaResultado.setFont(new Font("Serif", Font.PLAIN, 14));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setBorder(new EmptyBorder(10, 12, 10, 12));
        areaResultado.setCaretColor(ROSA);
        areaResultado.setText(
            "Nenhuma pesquisa realizada ainda.\n\n" +
            "  1. Escolha uma pasta\n" +
            "  2. Clique em Indexar\n" +
            "  3. Digite palavras e pesquise"
        );

        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(CREME_ESCURO);
        scroll.setBorder(new LineBorder(BORDA, 1, true));
        scroll.setPreferredSize(new Dimension(0, 220));
        estilizarScrollBar(scroll.getVerticalScrollBar());
        estilizarScrollBar(scroll.getHorizontalScrollBar());

        card.add(lblRes,  BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);

        return card;
    }

    // ── Status bar ────────────────────────────────────────────
    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BORDA);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        bar.setBackground(CREME_ESCURO);
        bar.setBorder(new EmptyBorder(8, 36, 8, 36));

        statusLabel = new JLabel("Pronto para usar");
        statusLabel.setFont(new Font("Serif", Font.ITALIC, 12));
        statusLabel.setForeground(TEXTO_MUTED);

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // ── Helpers UI ────────────────────────────────────────────
    private JLabel criarRotulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Serif", Font.BOLD, 13));
        lbl.setForeground(ROSA_ESCURO);
        return lbl;
    }

    private JPanel criarCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CREME_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        return card;
    }

    private JTextField criarTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CREME_ESCURO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(TEXTO_MUTED);
                    g3.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(placeholder, getInsets().left + 10,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g3.dispose();
                }
            }
        };
        tf.setOpaque(false);
        tf.setBackground(CREME_ESCURO);
        tf.setForeground(TEXTO);
        tf.setCaretColor(ROSA);
        tf.setFont(new Font("Serif", Font.PLAIN, 14));
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDA, 1, true),
            new EmptyBorder(7, 10, 7, 10)
        ));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(new CompoundBorder(new LineBorder(ROSA, 1, true), new EmptyBorder(7, 10, 7, 10)));
                tf.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(new CompoundBorder(new LineBorder(BORDA, 1, true), new EmptyBorder(7, 10, 7, 10)));
                tf.repaint();
            }
        });
        return tf;
    }

    private JButton criarBotao(String texto, Color cor, boolean preenchido) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (preenchido) {
                    g2.setColor(hover ? cor.darker() : cor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(hover ? new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 35) : new Color(0,0,0,0));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(cor);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(preenchido ? Color.WHITE : cor);
        btn.setFont(new Font("Serif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        return btn;
    }

    private void estilizarScrollBar(JScrollBar sb) {
        sb.setBackground(CREME_CARD);
        sb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = ROSA_CLARO;
                trackColor = CREME_CARD;
            }
            @Override protected JButton createDecreaseButton(int o) { return invisivel(); }
            @Override protected JButton createIncreaseButton(int o) { return invisivel(); }
            private JButton invisivel() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ROSA_CLARO);
                g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 8, 8);
                g2.dispose();
            }
        });
    }

    // ── Lógica (inalterada) ───────────────────────────────────
    private void setStatus(String msg) { statusLabel.setText(msg); }

    private void setResultadoTexto(String texto, Color cor) {
        areaResultado.setForeground(cor);
        areaResultado.setText(texto);
    }

    private void escolherDiretorio() {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            campoDiretorio.setText(seletor.getSelectedFile().getAbsolutePath());
            setStatus("Diretório selecionado.");
        }
    }

    private void indexarDiretorio() {
        String caminho = campoDiretorio.getText();
        if (caminho == null || caminho.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe ou escolha um diretório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setStatus("Indexando arquivos...");
        setResultadoTexto("Indexando: " + caminho + "\n\nAguarde...", TEXTO_MUTED);
        SwingUtilities.invokeLater(() -> {
            try {
                indice = new IndiceInvertido();
                indexador.indexarDiretorio(caminho, indice);
                persistencia.salvar(indice, ARQUIVO_INDICE);
                setStatus("Indexação concluída. Índice salvo.");
                setResultadoTexto("Indexação concluída!\n\nÍndice salvo em: " + ARQUIVO_INDICE + "\n\nPronto para pesquisar.", SAGE);
            } catch (IOException e) {
                setStatus("Erro ao indexar.");
                JOptionPane.showMessageDialog(this, "Erro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void carregarIndice() {
        try {
            indice = persistencia.carregar(ARQUIVO_INDICE);
            setStatus("Índice carregado.");
            setResultadoTexto("Índice carregado com sucesso!\n\nPronto para pesquisar.", SAGE);
        } catch (IOException e) {
            setStatus("Erro ao carregar índice.");
            JOptionPane.showMessageDialog(this, "Erro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar classe do índice.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pesquisar() {
        String pesquisa = campoPesquisa.getText();
        if (pesquisa == null || pesquisa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite palavras para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] brutas = pesquisa.split("\\s+");
        String[] normalizadas = new String[brutas.length];
        int validas = 0;

        for (String b : brutas) {
            String p = normalizador.normalizar(b);
            if (p != null) normalizadas[validas++] = p;
        }

        if (validas == 0) {
            setResultadoTexto("Nenhuma palavra válida informada.", TEXTO_MUTED);
            return;
        }

        String[] busca = new String[validas];
        for (int i = 0; i < validas; i++) busca[i] = normalizadas[i];

        ListaEncadeada<Documento> resultado = validas == 1
            ? indice.buscar(busca[0])
            : indice.buscarTodas(busca);

        mostrarResultado(resultado, pesquisa);
    }

    private void mostrarResultado(ListaEncadeada<Documento> documentos, String pesquisa) {
        if (documentos == null || documentos.estaVazia()) {
            setStatus("Nenhum resultado para \"" + pesquisa + "\".");
            setResultadoTexto("Nenhum documento encontrado para:\n\"" + pesquisa + "\"", TEXTO_MUTED);
            return;
        }

        StringBuilder sb = new StringBuilder();
        int count = 0;
        NoLista<Documento> p = documentos.getPrimeiro();
        while (p != null) {
            count++;
            sb.append(count).append(".  ").append(p.getInfo().getCaminho()).append("\n");
            p = p.getProximo();
        }

        setStatus(count + " documento(s) encontrado(s).");
        setResultadoTexto(count + " resultado(s) para: \"" + pesquisa + "\"\n\n" + sb, TEXTO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaBuscador().setVisible(true));
    }
}