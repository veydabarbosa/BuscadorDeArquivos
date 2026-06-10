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

// janela principal da aplicação — estende JFrame pra já vir com tudo de janela pronto
public class TelaBuscador extends JFrame {

    private static final long serialVersionUID = 1L;

    // nome do arquivo onde o índice fica salvo em disco
    private static final String ARQUIVO_INDICE = "indice.dat";

    // todas as cores da interface definidas aqui em cima pra facilitar se quiser mudar depois
    private static final Color BG           = new Color(10,  10,  12);
    private static final Color SURFACE      = new Color(18,  18,  22);
    private static final Color CARD         = new Color(24,  24,  30);
    private static final Color ELEVATED     = new Color(32,  32,  40);   // usado nos inputs e área de resultado
    private static final Color BORDER       = new Color(45,  45,  55);
    private static final Color BORDER_FOCUS = new Color(180, 120, 150);  // borda rosa quando o campo tá selecionado
    private static final Color MAUVE        = new Color(200, 130, 160);  // cor de acento principal
    private static final Color MAUVE_DIM    = new Color(200, 130, 160, 40); // mauve com transparência pro hover dos botões outline
    private static final Color MAUVE_BRIGHT = new Color(220, 160, 185);  // versão mais clara do mauve pro hover
    private static final Color GREEN        = new Color(100, 190, 140);  // feedback de sucesso
    private static final Color AMBER        = new Color(210, 165,  90);  // usado no botão carregar
    private static final Color TEXT         = new Color(235, 235, 240);  // texto principal
    private static final Color TEXT_SUB     = new Color(120, 118, 130);  // texto secundário, labels, etc
    private static final Color TEXT_MUTED   = new Color(65,  63,  75);   // texto bem apagado, placeholder

    // componentes que precisam ser acessados em vários métodos
    private JTextField campoDiretorio;
    private JTextField campoPesquisa;
    private JTextArea  areaResultado;
    private JLabel     statusLabel;
    private JLabel     contadorLabel;

    // as classes do nosso sistema que fazem o trabalho pesado de verdade
    private IndiceInvertido     indice;
    private IndexadorArquivos   indexador;
    private PersistenciaIndice  persistencia;
    private NormalizadorPalavra normalizador;

    public TelaBuscador() {
        // instancia tudo que vai precisar antes de montar a tela
        indice       = new IndiceInvertido();
        indexador    = new IndexadorArquivos();
        persistencia = new PersistenciaIndice();
        normalizador = new NormalizadorPalavra();

        configurarLookAndFeel();

        setTitle("Buscador de Arquivos");
        setSize(820, 640);
        setMinimumSize(new Dimension(640, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza a janela na tela
        getContentPane().setBackground(BG);

        criarLayout();
    }

    // sobrescreve o visual padrão do swing pra combinar com o tema escuro
    // sem isso, popups e caixas de diálogo aparecem com o visual branco padrão
    private void configurarLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("OptionPane.background",        CARD);
        UIManager.put("Panel.background",             CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.focus",                 new Color(0,0,0,0));
        UIManager.put("FileChooser.background",       CARD);
        UIManager.put("FileChooser.foreground",       TEXT);
    }

    // divide a janela em três faixas: topo, centro e rodapé
    private void criarLayout() {
        setLayout(new BorderLayout());
        add(criarTopBar(),   BorderLayout.NORTH);
        add(criarBody(),     BorderLayout.CENTER);
        add(criarFooter(),   BorderLayout.SOUTH);
    }

    // ── Top bar ───────────────────────────────────────────────

    private JPanel criarTopBar() {
        // sobrescreve o paintComponent pra desenhar o fundo e a linha divisória manualmente
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // linha de 1px embaixo da barra pra separar do conteúdo
                g2.setColor(BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 28, 14, 28));

        JLabel nome = new JLabel("Alunos: Nicole Bruch, Veyda C. Barbosa e Vitor W");
        nome.setFont(new Font("SansSerif", Font.BOLD, 15));
        nome.setForeground(TEXT);

        // o pontinho em mauve é só um detalhe visual mas deixa o nome mais interessante
        JLabel ponto = new JLabel(".");
        ponto.setFont(new Font("SansSerif", Font.BOLD, 15));
        ponto.setForeground(MAUVE);

        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esq.setOpaque(false);
        esq.add(nome);
        esq.add(ponto);

        bar.add(esq, BorderLayout.WEST);
        return bar;
    }

    private JLabel criarBadge(String texto) {
        // usa classe anônima pra sobrescrever o paintComponent e desenhar o fundo arredondado
        JLabel b = new JLabel(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.dispose();
                super.paintComponent(g); // chama o pai pra desenhar o texto em cima
            }
        };
        b.setFont(new Font("SansSerif", Font.PLAIN, 11));
        b.setForeground(TEXT_SUB);
        b.setBorder(new EmptyBorder(3, 9, 3, 9));
        b.setOpaque(false);
        return b;
    }

    // ── Body ──────────────────────────────────────────────────

    private JPanel criarBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(28, 28, 16, 28));

        JPanel titulo = new JPanel();
        titulo.setOpaque(false);
        titulo.setLayout(new BoxLayout(titulo, BoxLayout.Y_AXIS));

        JLabel h1 = new JLabel("Pesquisar arquivos:");
        h1.setFont(new Font("SansSerif", Font.BOLD, 24));
        h1.setForeground(TEXT);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Indexe um diretório e busque por palavras nos documentos :)");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXT_SUB);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        titulo.add(h1);
        titulo.add(Box.createVerticalStrut(4));
        titulo.add(sub);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(criarPainelControles(), BorderLayout.NORTH);
        centro.add(criarPainelResultado(), BorderLayout.CENTER);

        body.add(titulo,  BorderLayout.NORTH);
        body.add(centro,  BorderLayout.CENTER);
        return body;
    }

    // ── Controles ─────────────────────────────────────────────

    private JPanel criarPainelControles() {
        // GridLayout(1, 2) cria duas colunas de tamanho igual — perfeito pra dividir diretório e busca lado a lado
        JPanel painel = new JPanel(new GridLayout(1, 2, 12, 0));
        painel.setOpaque(false);

        // card da esquerda: escolher e indexar o diretório
        JPanel cardDir = criarCard();
        cardDir.setLayout(new BorderLayout(0, 10));

        JLabel lblDir = rotulo("Diretório");
        campoDiretorio = criarInput("Caminho da pasta...");

        JPanel botoesDir = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botoesDir.setOpaque(false);
        JButton btEscolher = botao("Escolher",  false, MAUVE); // outline
        JButton btIndexar  = botao("Indexar",   true,  MAUVE); // preenchido — ação principal
        JButton btCarregar = botao("Carregar",  false, AMBER); // outline em âmbar, é uma ação secundária
        botoesDir.add(btEscolher);
        botoesDir.add(btIndexar);
        botoesDir.add(btCarregar);

        cardDir.add(lblDir,          BorderLayout.NORTH);
        cardDir.add(campoDiretorio,  BorderLayout.CENTER);
        cardDir.add(botoesDir,       BorderLayout.SOUTH);

        // card da direita: campo de busca
        JPanel cardBusca = criarCard();
        cardBusca.setLayout(new BorderLayout(0, 10));

        JLabel lblBusca = rotulo("Buscar");
        campoPesquisa = criarInput("Palavras-chave...");

        JPanel btBuscaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btBuscaPanel.setOpaque(false);
        JButton btPesquisar = botao("Pesquisar", true, MAUVE);
        btBuscaPanel.add(btPesquisar);

        cardBusca.add(lblBusca,      BorderLayout.NORTH);
        cardBusca.add(campoPesquisa, BorderLayout.CENTER);
        cardBusca.add(btBuscaPanel,  BorderLayout.SOUTH);

        painel.add(cardDir);
        painel.add(cardBusca);

        // lambda conectando cada botão ao método correspondente
        btEscolher.addActionListener(e  -> escolherDiretorio());
        btIndexar.addActionListener(e   -> indexarDiretorio());
        btCarregar.addActionListener(e  -> carregarIndice());
        btPesquisar.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e -> pesquisar()); // Enter no campo também pesquisa

        return painel;
    }

    // ── Resultado ─────────────────────────────────────────────

    private JPanel criarPainelResultado() {
        JPanel card = criarCard();
        card.setLayout(new BorderLayout(0, 10));

        // header do card com o label à esquerda e o contador de resultados à direita
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblRes = rotulo("Resultados");

        // esse label começa vazio e é atualizado depois de cada pesquisa
        contadorLabel = new JLabel("");
        contadorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contadorLabel.setForeground(MAUVE);

        header.add(lblRes,        BorderLayout.WEST);
        header.add(contadorLabel, BorderLayout.EAST);

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setBackground(ELEVATED);
        areaResultado.setForeground(TEXT_SUB);
        areaResultado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true); // quebra linha sem cortar palavra no meio
        areaResultado.setBorder(new EmptyBorder(12, 14, 12, 14));
        areaResultado.setCaretColor(MAUVE);
        areaResultado.setText(
            "Nenhuma pesquisa realizada ainda.\n\n" +
            "  →  Escolha um diretório\n" +
            "  →  Clique em Indexar\n" +
            "  →  Digite palavras e pesquise"
        );

        // envolve a área de texto num scroll pra caso os resultados sejam muitos
        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(ELEVATED);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        estilizarScrollBar(scroll.getVerticalScrollBar());
        estilizarScrollBar(scroll.getHorizontalScrollBar());

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Footer ────────────────────────────────────────────────

    private JPanel criarFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // linha de 1px no topo do rodapé pra separar do conteúdo
                g.setColor(BORDER);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        footer.setBackground(SURFACE);
        footer.setBorder(new EmptyBorder(9, 28, 9, 28));

        // atualizado dinamicamente conforme o usuário usa a aplicação
        statusLabel = new JLabel("FURB - Universidade Regional de Blumenau");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);

        JLabel versao = new JLabel("v1.0 - Algoritmos & Estruturas de Dados");
        versao.setFont(new Font("SansSerif", Font.PLAIN, 11));
        versao.setForeground(TEXT_MUTED);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(versao,      BorderLayout.EAST);
        return footer;
    }

    // ── Helpers UI ────────────────────────────────────────────

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(TEXT_SUB);
        return l;
    }

    private JPanel criarCard() {
        // painel com canto arredondado desenhado manualmente — o Swing não tem isso nativo
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        return card;
    }

    private JTextField criarInput(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ELEVATED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
                // desenha o placeholder manualmente quando o campo tá vazio e sem foco
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(TEXT_MUTED);
                    g3.setFont(getFont());
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(placeholder, getInsets().left + 10,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g3.dispose();
                }
            }
        };
        tf.setOpaque(false);
        tf.setBackground(ELEVATED);
        tf.setForeground(TEXT);
        tf.setCaretColor(MAUVE);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        // muda a cor da borda quando o campo recebe ou perde foco
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(new CompoundBorder(new LineBorder(BORDER_FOCUS, 1, true), new EmptyBorder(8, 10, 8, 10)));
                tf.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
                tf.repaint();
            }
        });
        return tf;
    }

    private JButton botao(String texto, boolean preenchido, Color cor) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                // bloco de inicialização — roda quando o objeto é criado
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (preenchido) {
                    // botão primário: fundo sólido, clareia no hover
                    g2.setColor(hover ? MAUVE_BRIGHT : cor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                } else {
                    // botão outline: só borda, fundo leve no hover
                    g2.setColor(hover ? MAUVE_DIM : new Color(0,0,0,0));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(hover ? MAUVE_BRIGHT : BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(preenchido ? BG : TEXT_SUB);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // desativa o fundo padrão do Swing pra não conflitar com o nosso
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private void estilizarScrollBar(JScrollBar sb) {
        // substitui o visual padrão da scrollbar por um mais discreto e arredondado
        sb.setBackground(ELEVATED);
        sb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = BORDER;
                trackColor = ELEVATED;
            }
            // remove as setas de cima e baixo da scrollbar — deixa mais clean
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
                g2.setColor(new Color(80, 75, 95));
                g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
                g2.dispose();
            }
        });
    }

    // ── Lógica ────────────────────────────────────────────────

    private void setStatus(String msg) { statusLabel.setText(msg); }

    private void setResultado(String texto, Color cor) {
        areaResultado.setForeground(cor);
        areaResultado.setText(texto);
    }

    private void escolherDiretorio() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // só permite selecionar pastas
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            campoDiretorio.setText(fc.getSelectedFile().getAbsolutePath());
            setStatus("Diretório selecionado");
        }
    }

    private void indexarDiretorio() {
        String caminho = campoDiretorio.getText();
        if (caminho == null || caminho.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe ou escolha um diretório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setStatus("Indexando...");
        contadorLabel.setText("");
        setResultado("Indexando arquivos em:\n" + caminho + "\n\nAguarde...", TEXT_SUB);

        // invokeLater garante que a UI atualize antes de começar o processo pesado
        SwingUtilities.invokeLater(() -> {
            try {
                indice = new IndiceInvertido();
                indexador.indexarDiretorio(caminho, indice);
                persistencia.salvar(indice, ARQUIVO_INDICE); // salva em disco pra não precisar reindexar toda vez
                setStatus("Indexação concluída — índice salvo");
                setResultado("Indexação concluída.\n\nÍndice salvo em: " + ARQUIVO_INDICE + "\nPronto para pesquisar.", GREEN);
            } catch (IOException e) {
                setStatus("Erro ao indexar");
                JOptionPane.showMessageDialog(this, "Erro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void carregarIndice() {
        try {
            // carrega o índice salvo anteriormente em vez de reindexar tudo do zero
            indice = persistencia.carregar(ARQUIVO_INDICE);
            setStatus("Índice carregado");
            setResultado("Índice carregado.\nPronto para pesquisar.", GREEN);
        } catch (IOException e) {
            setStatus("Erro ao carregar índice");
            JOptionPane.showMessageDialog(this, "Erro:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar classe.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pesquisar() {
        String pesquisa = campoPesquisa.getText();
        if (pesquisa == null || pesquisa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite palavras para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // separa as palavras digitadas pelo espaço
        String[] brutas = pesquisa.split("\\s+");
        String[] norm   = new String[brutas.length];
        int validas = 0;

        // normaliza cada palavra — remove acentos, coloca em minúsculo, etc
        for (String b : brutas) {
            String p = normalizador.normalizar(b);
            if (p != null) norm[validas++] = p;
        }

        if (validas == 0) { setResultado("Nenhuma palavra válida.", TEXT_SUB); return; }

        // copia só as palavras válidas pro array final
        String[] busca = new String[validas];
        for (int i = 0; i < validas; i++) busca[i] = norm[i];

        // se for uma palavra só usa buscar, se forem várias usa buscarTodas (interseção)
        ListaEncadeada<Documento> res = validas == 1
            ? indice.buscar(busca[0])
            : indice.buscarTodas(busca);

        mostrarResultado(res, pesquisa);
    }

    private void mostrarResultado(ListaEncadeada<Documento> docs, String pesquisa) {
        if (docs == null || docs.estaVazia()) {
            setStatus("Nenhum resultado");
            contadorLabel.setText("0 resultados");
            setResultado("Nenhum documento encontrado para:\n\"" + pesquisa + "\"", TEXT_SUB);
            return;
        }

        StringBuilder sb = new StringBuilder();
        int count = 0;

        // percorre a lista encadeada de resultados e monta o texto
        NoLista<Documento> p = docs.getPrimeiro();
        while (p != null) {
            count++;
            sb.append(count).append("   ").append(p.getInfo().getCaminho()).append("\n");
            p = p.getProximo();
        }

        setStatus("Pesquisa concluída");
        // pluraliza "resultado" dependendo da quantidade
        contadorLabel.setText(count + " resultado" + (count > 1 ? "s" : ""));
        setResultado(sb.toString(), TEXT);
    }

    public static void main(String[] args) {
        // garante que a janela seja criada na thread de UI do Swing
        SwingUtilities.invokeLater(() -> new TelaBuscador().setVisible(true));
    }
}