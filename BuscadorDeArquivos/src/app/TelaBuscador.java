package app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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

	private JTextField campoDiretorio;
	private JTextField campoPesquisa;
	private JTextArea areaResultado;

	private IndiceInvertido indice;
	private IndexadorArquivos indexador;
	private PersistenciaIndice persistencia;
	private NormalizadorPalavra normalizador;

	public TelaBuscador() {
		indice = new IndiceInvertido();
		indexador = new IndexadorArquivos();
		persistencia = new PersistenciaIndice();
		normalizador = new NormalizadorPalavra();

		setTitle("Buscador de Arquivos");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		criarComponentes();
	}

	private void criarComponentes() {
		JPanel painelPrincipal = new JPanel(new BorderLayout());

		JPanel painelSuperior = new JPanel(new BorderLayout());

		JPanel painelDiretorio = new JPanel(new FlowLayout());

		JLabel labelDiretorio = new JLabel("Diretório:");
		campoDiretorio = new JTextField(35);

		JButton botaoEscolher = new JButton("🔍 Escolher pasta");
		JButton botaoIndexar = new JButton("Indexar");
		JButton botaoCarregar = new JButton("Carregar índice");

		painelDiretorio.add(labelDiretorio);
		painelDiretorio.add(campoDiretorio);
		painelDiretorio.add(botaoEscolher);
		painelDiretorio.add(botaoIndexar);
		painelDiretorio.add(botaoCarregar);

		JPanel painelPesquisa = new JPanel(new FlowLayout());

		JLabel labelPesquisa = new JLabel("Pesquisar:");
		campoPesquisa = new JTextField(35);
		JButton botaoPesquisar = new JButton("Pesquisar");

		painelPesquisa.add(labelPesquisa);
		painelPesquisa.add(campoPesquisa);
		painelPesquisa.add(botaoPesquisar);

		painelSuperior.add(painelDiretorio, BorderLayout.NORTH);
		painelSuperior.add(painelPesquisa, BorderLayout.SOUTH);

		areaResultado = new JTextArea();
		areaResultado.setEditable(false);

		JScrollPane scroll = new JScrollPane(areaResultado);

		painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
		painelPrincipal.add(scroll, BorderLayout.CENTER);

		add(painelPrincipal);

		botaoEscolher.addActionListener(e -> escolherDiretorio());
		botaoIndexar.addActionListener(e -> indexarDiretorio());
		botaoCarregar.addActionListener(e -> carregarIndice());
		botaoPesquisar.addActionListener(e -> pesquisar());
	}

	private void escolherDiretorio() {
		JFileChooser seletor = new JFileChooser();

		seletor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int resultado = seletor.showOpenDialog(this);

		if (resultado == JFileChooser.APPROVE_OPTION) {
			File diretorio = seletor.getSelectedFile();
			campoDiretorio.setText(diretorio.getAbsolutePath());
		}
	}

	private void indexarDiretorio() {
		String caminho = campoDiretorio.getText();

		if (caminho == null || caminho.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Informe ou escolha um diretório.");
			return;
		}

		try {
			indice = new IndiceInvertido();

			indexador.indexarDiretorio(caminho, indice);

			persistencia.salvar(indice, ARQUIVO_INDICE);

			JOptionPane.showMessageDialog(this, "Indexação concluída.\nÍndice salvo em " + ARQUIVO_INDICE);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Erro ao indexar diretório:\n" + e.getMessage());
		}
	}

	private void carregarIndice() {
		try {
			indice = persistencia.carregar(ARQUIVO_INDICE);

			JOptionPane.showMessageDialog(this, "Índice carregado com sucesso.");

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar índice:\n" + e.getMessage());

		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar classe do índice.");
		}
	}

	private void pesquisar() {
		String pesquisa = campoPesquisa.getText();

		if (pesquisa == null || pesquisa.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Digite uma ou mais palavras para pesquisar.");
			return;
		}

		String[] palavrasBrutas = pesquisa.split("\\s+");
		String[] palavrasNormalizadas = new String[palavrasBrutas.length];

		int quantidadePalavrasValidas = 0;

		for (int i = 0; i < palavrasBrutas.length; i++) {
			String palavra = normalizador.normalizar(palavrasBrutas[i]);

			if (palavra != null) {
				palavrasNormalizadas[quantidadePalavrasValidas] = palavra;
				quantidadePalavrasValidas++;
			}
		}

		if (quantidadePalavrasValidas == 0) {
			areaResultado.setText("Nenhuma palavra válida foi informada.");
			return;
		}

		String[] palavrasBusca = new String[quantidadePalavrasValidas];

		for (int i = 0; i < quantidadePalavrasValidas; i++) {
			palavrasBusca[i] = palavrasNormalizadas[i];
		}

		ListaEncadeada<Documento> resultado;

		if (quantidadePalavrasValidas == 1) {
			resultado = indice.buscar(palavrasBusca[0]);
		} else {
			resultado = indice.buscarTodas(palavrasBusca);
		}

		mostrarResultado(resultado);
	}

	private void mostrarResultado(ListaEncadeada<Documento> documentos) {
		if (documentos == null || documentos.estaVazia()) {
			areaResultado.setText("Nenhum documento encontrado.");
			return;
		}

		String texto = "Documentos encontrados:\n\n";

		NoLista<Documento> p = documentos.getPrimeiro();

		while (p != null) {
			texto += p.getInfo().getCaminho() + "\n";
			p = p.getProximo();
		}

		areaResultado.setText(texto);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			TelaBuscador tela = new TelaBuscador();
			tela.setVisible(true);
		});
	}
}