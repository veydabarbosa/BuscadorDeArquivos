# Buscador de Arquivos

## Equipe

- Nicole Bruch
- Veyda Barbosa
- Vitor Wöstehoff

---

# 1. Introdução

Este projeto foi desenvolvido para a disciplina de **Algoritmos e Estruturas de Dados**.

O objetivo do trabalho é criar uma aplicação capaz de pesquisar palavras dentro de arquivos de texto.

A ideia principal é permitir que o usuário informe um diretório contendo arquivos `.txt`. O programa deve percorrer esse diretório, incluindo seus subdiretórios, ler os arquivos encontrados, extrair as palavras válidas e montar uma estrutura de busca eficiente.

Para isso, o projeto utiliza um **índice invertido** implementado com **mapa de dispersão**.

Depois que o índice é criado, ele é salvo em um arquivo no disco. Assim, quando o programa for executado novamente, o usuário pode carregar o índice salvo sem precisar ler e processar todos os arquivos outra vez.

---

# 2. O que foi pedido no trabalho

O trabalho solicitou uma aplicação com as seguintes funcionalidades:

1. O usuário deve informar um diretório.
2. O programa deve considerar todos os arquivos `.txt` desse diretório e dos subdiretórios.
3. O programa deve extrair as palavras dos arquivos.
4. As palavras devem ser tratadas seguindo algumas regras:
   - não diferenciar maiúsculas e minúsculas;
   - desprezar pontuações;
   - ignorar palavras formadas apenas por números;
   - ignorar palavras com menos de 3 caracteres.
5. O índice deve ser implementado usando mapa de dispersão.
6. Cada palavra deve estar associada à lista de documentos onde aparece.
7. Após a indexação, o índice deve ser salvo no disco.
8. Ao abrir o programa novamente, o índice deve ser carregado do disco para a memória.
9. A busca deve acontecer usando o índice carregado, sem precisar reindexar os arquivos.
10. O usuário deve poder pesquisar uma ou mais palavras.
11. Quando pesquisar várias palavras, o programa deve mostrar apenas documentos que contenham todas elas.
12. Não é permitido usar estruturas prontas do Java, como `ArrayList` e `HashMap`, para representar as estruturas principais do índice.

---

# 3. Ideia geral da solução

A solução foi pensada como um buscador simples de arquivos de texto.

Quando pesquisamos uma palavra, uma solução ruim seria abrir todos os arquivos e procurar a palavra dentro de cada um deles toda vez.

Por exemplo, se o usuário pesquisar:

```text
java
```

o programa teria que abrir:

```text
aula1.txt
aula2.txt
resumo.txt
trabalho.txt
...
```

e procurar a palavra dentro de cada arquivo novamente.

Isso seria lento, principalmente com muitos arquivos.

Para evitar isso, o programa monta um **índice** antes da busca.

Esse índice guarda, para cada palavra, a lista de documentos onde ela aparece.

Exemplo:

```text
java      -> aula1.txt, resumo.txt
mapa      -> aula1.txt, trabalho.txt
estrutura -> aula2.txt
```

Assim, quando o usuário pesquisa `java`, o programa não precisa ler todos os arquivos novamente. Ele consulta diretamente o índice.

---

# 4. O que é um índice invertido

Um índice invertido é uma estrutura que inverte a relação normal entre arquivos e palavras.

Normalmente pensamos assim:

```text
arquivo -> palavras dentro do arquivo
```

Exemplo:

```text
aula1.txt -> java, mapa, lista, estrutura
```

No índice invertido, a lógica é o contrário:

```text
palavra -> arquivos onde a palavra aparece
```

Exemplo:

```text
java      -> aula1.txt, resumo.txt
mapa      -> aula1.txt, trabalho.txt
lista     -> aula1.txt
estrutura -> aula2.txt, resumo.txt
```

Por isso o nome **índice invertido**.

Ele é muito usado em sistemas de busca, porque permite descobrir rapidamente em quais documentos uma palavra aparece.

Neste projeto, o índice invertido é representado pela classe:

```text
IndiceInvertido
```

Internamente, essa classe usa:

```java
MapaDispersao<String, ListaEncadeada<Documento>>
```

Ou seja:

```text
palavra -> lista de documentos
```

---

# 5. Exemplo prático de indexação

Imagine que existam dois arquivos.

## Arquivo `aula1.txt`

```text
Java usa mapa de dispersao.
Java tambem usa lista encadeada.
```

## Arquivo `aula2.txt`

```text
Mapa de dispersao ajuda na busca.
Pilha fila e arvore tambem sao estruturas.
```

Depois da leitura, o programa normaliza as palavras.

Por exemplo:

```text
Java  -> java
mapa  -> mapa
de    -> ignorado, pois tem menos de 3 letras
```

O índice gerado pode ficar parecido com:

```text
java       -> aula1.txt
usa        -> aula1.txt
mapa       -> aula1.txt, aula2.txt
dispersao  -> aula1.txt, aula2.txt
tambem     -> aula1.txt, aula2.txt
lista      -> aula1.txt
encadeada  -> aula1.txt
ajuda      -> aula2.txt
busca      -> aula2.txt
pilha      -> aula2.txt
fila       -> aula2.txt
arvore     -> aula2.txt
estruturas -> aula2.txt
```

Assim, se o usuário pesquisar:

```text
mapa
```

o programa retorna:

```text
aula1.txt
aula2.txt
```

---

# 6. Busca por uma palavra

Quando o usuário pesquisa uma única palavra, o programa procura essa palavra no índice.

Exemplo:

```text
Pesquisa: java
```

O índice contém:

```text
java -> aula1.txt
```

Então o resultado será:

```text
aula1.txt
```

A busca é feita diretamente no mapa de dispersão.

---

# 7. Busca por várias palavras

O trabalho também pede que o usuário possa pesquisar várias palavras ao mesmo tempo.

Quando o usuário pesquisa várias palavras, o programa deve retornar apenas os documentos que contêm todas elas.

Exemplo:

```text
Pesquisa: java mapa
```

Suponha que o índice tenha:

```text
java -> aula1.txt, resumo.txt
mapa -> aula1.txt, aula2.txt, resumo.txt
```

O resultado deve ser:

```text
aula1.txt
resumo.txt
```

Porque esses são os documentos que aparecem nas duas listas.

Esse processo é chamado de **interseção**.

A ideia é:

```text
documentos com "java"
E
documentos com "mapa"
```

O programa pega somente os documentos que estão nas duas listas.

---

# 8. Por que usar mapa de dispersão

O mapa de dispersão foi usado porque o trabalho exige uma estrutura de indexação eficiente.

O mapa funciona usando uma chave.

Neste projeto, a chave é uma palavra.

Exemplo:

```text
"java"
"mapa"
"estrutura"
```

Cada palavra é transformada em um número usando o método `hashCode()` do Java.

Depois, esse número é usado para calcular uma posição no vetor interno do mapa.

A ideia é:

```text
palavra -> hashCode -> posição no vetor
```

Exemplo simplificado:

```text
"java".hashCode() -> número
número % tamanhoDoMapa -> posição
```

Assim, quando queremos buscar a palavra `java`, o mapa calcula a posição onde essa palavra deve estar e procura diretamente nessa posição.

Isso torna a busca mais rápida do que percorrer todos os elementos um por um.

---

# 9. Colisões no mapa de dispersão

Pode acontecer de duas palavras diferentes caírem na mesma posição do vetor interno do mapa.

Isso é chamado de **colisão**.

Exemplo:

```text
"java" -> posição 10
"mapa" -> posição 10
```

As duas palavras caíram na mesma posição.

Para resolver isso, o projeto usa **listas encadeadas**.

Cada posição do vetor do mapa guarda uma lista.

Então, se houver colisão, os elementos ficam na mesma lista:

```text
posição 10 -> java -> mapa -> estrutura
```

Essa técnica é chamada de tratamento de colisão por **endereçamento separado**.

---

# 10. Por que salvar o índice no disco

O trabalho exige que, depois da indexação, o índice seja salvo em arquivo no disco.

Isso é importante porque a indexação pode ser demorada.

Se existirem muitos arquivos, o programa precisa:

```text
abrir arquivos
ler linhas
separar palavras
normalizar palavras
adicionar palavras no índice
```

Fazer isso toda vez que abrir o programa seria ineficiente.

Por isso, depois que o índice é criado, ele é salvo em um arquivo chamado:

```text
indice.dat
```

Quando o programa é aberto novamente, o usuário pode escolher carregar esse arquivo.

Assim, o índice volta para a memória e já pode ser usado nas buscas.

O fluxo fica assim:

```text
Primeira execução:
arquivos .txt -> indexação -> índice em memória -> indice.dat

Execução posterior:
indice.dat -> índice em memória -> busca
```

---

# 11. O que é serialização

Para salvar o índice no disco, foi usada a **serialização** do Java.

Serialização é o processo de transformar um objeto que está na memória em dados que podem ser gravados em arquivo.

Neste projeto, o índice é um objeto Java.

Ele contém várias estruturas dentro dele:

```text
IndiceInvertido
└── MapaDispersao
    └── ListaEncadeada
        └── NoLista
            └── NoMapa
                └── Documento
```

Quando salvamos o índice, o Java precisa salvar tudo que está dentro dele.

Por isso, várias classes implementam:

```java
Serializable
```

Isso informa ao Java que objetos dessas classes podem ser salvos em arquivo.

Se apenas `IndiceInvertido` fosse serializável, mas `MapaDispersao`, `ListaEncadeada`, `NoLista`, `NoMapa` ou `Documento` não fossem, o Java não conseguiria salvar o índice completo.

---

# 12. O que é `serialVersionUID`

As classes serializáveis possuem o atributo:

```java
private static final long serialVersionUID = 1L;
```

Esse atributo funciona como uma identificação de versão da classe.

Quando o Java salva um objeto em arquivo, ele também registra a versão da classe.

Depois, quando o programa tenta carregar esse objeto, o Java verifica se a classe atual ainda é compatível com a versão usada quando o arquivo foi salvo.

Neste projeto, o valor `1L` indica que estamos usando a primeira versão serializável da classe.

---

# 13. Estrutura de pacotes do projeto

O projeto foi dividido em pacotes para separar responsabilidades.

```text
src
├── app
│   └── Principal.java
│
├── estruturas
│   ├── ListaEncadeada.java
│   ├── MapaDispersao.java
│   ├── NoLista.java
│   └── NoMapa.java
│
├── indice
│   └── IndiceInvertido.java
│
├── modelo
│   └── Documento.java
│
└── servico
    ├── IndexadorArquivos.java
    ├── NormalizadorPalavra.java
    └── PersistenciaIndice.java
```

---

# 14. Explicação das classes

## 14.1 `Principal`

Pacote:

```text
app
```

A classe `Principal` é a classe que inicia o programa.

Ela contém o método:

```java
public static void main(String[] args)
```

Essa classe apresenta o menu para o usuário.

O menu possui quatro opções:

```text
1 - Indexar diretorio
2 - Carregar indice salvo
3 - Pesquisar palavra(s)
4 - Sair
```

A função dessa classe é controlar o fluxo do programa.

Ela não faz a indexação diretamente. Ela chama outras classes para isso.

Por exemplo:

- quando o usuário escolhe indexar, a classe chama `IndexadorArquivos`;
- quando o usuário escolhe carregar, a classe chama `PersistenciaIndice`;
- quando o usuário escolhe pesquisar, a classe chama `IndiceInvertido`.

Ou seja, a `Principal` organiza o programa e faz a comunicação com o usuário.

---

## 14.2 `Documento`

Pacote:

```text
modelo
```

A classe `Documento` representa um arquivo de texto indexado.

Ela guarda o caminho do arquivo.

Exemplo:

```text
/home/usuario/BuscadorDeArquivos/arquivos/aula1.txt
```

A classe possui o atributo:

```java
private String caminho;
```

Esse caminho é usado para identificar o arquivo.

A classe também sobrescreve o método `equals`.

Dois objetos `Documento` são considerados iguais se possuem o mesmo caminho.

Isso é importante porque a mesma palavra pode aparecer várias vezes no mesmo arquivo.

Exemplo:

```text
java java java
```

Mesmo que a palavra apareça três vezes, o documento deve aparecer apenas uma vez na lista daquela palavra.

Sem essa comparação por caminho, o programa poderia inserir o mesmo arquivo repetidamente.

---

## 14.3 `NoLista`

Pacote:

```text
estruturas
```

A classe `NoLista` representa um nó da lista encadeada.

Cada nó guarda:

```text
info
proximo
```

O atributo `info` guarda o valor armazenado no nó.

O atributo `proximo` aponta para o próximo nó da lista.

Visualmente:

```text
[info | proximo] -> [info | proximo] -> [info | proximo] -> null
```

Essa classe é usada pela `ListaEncadeada`.

---

## 14.4 `ListaEncadeada`

Pacote:

```text
estruturas
```

A classe `ListaEncadeada` representa uma lista encadeada simples.

Ela é usada em duas partes do projeto.

A primeira é no mapa de dispersão, para resolver colisões.

Quando duas palavras caem na mesma posição do vetor do mapa, elas são armazenadas em uma lista encadeada.

A segunda é no índice invertido, para guardar a lista de documentos de cada palavra.

Exemplo:

```text
java -> aula1.txt -> resumo.txt -> trabalho.txt
```

Essa classe possui métodos como:

```java
inserir
buscar
retirar
estaVazia
inserirSeNaoExistir
```

O método `inserirSeNaoExistir` foi adicionado para o trabalho.

Ele verifica se um valor já existe na lista antes de inserir.

Isso evita documentos repetidos na lista de uma palavra.

---

## 14.5 `NoMapa`

Pacote:

```text
estruturas
```

A classe `NoMapa` representa um item dentro do mapa de dispersão.

Ela guarda:

```text
chave
valor
```

No projeto:

```text
chave = palavra
valor = lista de documentos
```

Exemplo:

```text
chave = "java"
valor = lista com aula1.txt e resumo.txt
```

O método `equals` compara os nós pela chave.

Isso é importante porque, quando o mapa busca uma palavra, ele precisa encontrar o `NoMapa` que possui a mesma chave.

---

## 14.6 `MapaDispersao`

Pacote:

```text
estruturas
```

A classe `MapaDispersao` é a principal estrutura usada para armazenar o índice.

Ela é genérica:

```java
MapaDispersao<K, T>
```

Isso significa que ela pode trabalhar com qualquer tipo de chave e qualquer tipo de valor.

No projeto, ela é usada assim:

```java
MapaDispersao<String, ListaEncadeada<Documento>>
```

Ou seja:

```text
K = String
T = ListaEncadeada<Documento>
```

A chave é uma palavra.

O valor é a lista de documentos onde a palavra aparece.

A classe possui um vetor interno de listas encadeadas.

Esse vetor é usado para distribuir os elementos conforme o hash da chave.

O método `calcularHash` transforma a chave em uma posição do vetor.

Como o `hashCode()` do Java pode retornar número negativo, o programa usa `Math.abs` para trabalhar com valor positivo.

Depois, usa o resto da divisão pelo tamanho do vetor.

A ideia é:

```text
Math.abs(chave.hashCode()) % tamanhoDoVetor
```

Assim, o resultado sempre será uma posição válida dentro do vetor.

---

## 14.7 `IndiceInvertido`

Pacote:

```text
indice
```

A classe `IndiceInvertido` é a classe central do projeto.

Ela representa o índice de palavras.

O atributo principal é:

```java
private MapaDispersao<String, ListaEncadeada<Documento>> indice;
```

Isso significa:

```text
palavra -> lista de documentos
```

O método mais importante é:

```java
adicionarPalavra(String palavra, Documento documento)
```

Esse método é chamado quando o programa encontra uma palavra válida dentro de um arquivo.

Exemplo:

```java
adicionarPalavra("java", documentoAula1);
```

Isso significa que a palavra `java` apareceu no arquivo representado por `documentoAula1`.

O método funciona assim:

1. Busca no mapa se a palavra já existe.
2. Se a palavra não existir, cria uma nova lista de documentos para ela.
3. Insere essa palavra no mapa associada à lista.
4. Adiciona o documento na lista da palavra.
5. Evita inserir o mesmo documento repetido.

A classe também possui o método:

```java
buscar(String palavra)
```

Esse método retorna os documentos onde uma palavra aparece.

E também possui:

```java
buscarTodas(String[] palavras)
```

Esse método faz a busca por várias palavras.

Quando há várias palavras, ele faz a interseção das listas de documentos.

Assim, retorna apenas documentos que contêm todas as palavras pesquisadas.

---

## 14.8 `NormalizadorPalavra`

Pacote:

```text
servico
```

A classe `NormalizadorPalavra` prepara as palavras antes de inseri-las no índice.

Ela recebe uma palavra bruta, do jeito que veio do arquivo, e transforma em uma palavra válida.

Exemplos:

```text
"Java,"   -> "java"
"MAPA."   -> "mapa"
"(pilha)" -> "pilha"
"de"      -> ignorado
"123"     -> ignorado
"..."     -> ignorado
```

Essa classe aplica as regras do trabalho:

1. transforma para minúsculas;
2. remove pontuações;
3. ignora palavras com menos de 3 caracteres;
4. ignora termos que não possuem letras.

Se a palavra for válida, o método retorna a palavra limpa.

Se a palavra não for válida, retorna `null`.

---

## 14.9 `IndexadorArquivos`

Pacote:

```text
servico
```

A classe `IndexadorArquivos` é responsável por ler os arquivos.

Ela recebe o caminho de um diretório informado pelo usuário.

Depois, percorre esse diretório e seus subdiretórios.

Para cada arquivo encontrado, verifica se ele termina com `.txt`.

Se for um arquivo `.txt`, ele é lido linha por linha.

Cada linha é quebrada em palavras.

Cada palavra é enviada para o `NormalizadorPalavra`.

Se a palavra for válida, ela é adicionada ao `IndiceInvertido`.

O fluxo dessa classe é:

```text
diretório
   ↓
arquivos e subdiretórios
   ↓
arquivos .txt
   ↓
linhas do arquivo
   ↓
palavras
   ↓
normalização
   ↓
índice invertido
```

Essa classe é responsável pela indexação dos documentos.

---

## 14.10 `PersistenciaIndice`

Pacote:

```text
servico
```

A classe `PersistenciaIndice` é responsável por salvar e carregar o índice.

Ela possui dois métodos principais:

```java
salvar(IndiceInvertido indice, String caminhoArquivo)
```

e

```java
carregar(String caminhoArquivo)
```

O método `salvar` grava o índice no arquivo:

```text
indice.dat
```

O método `carregar` lê esse arquivo e reconstrói o objeto `IndiceInvertido` na memória.

Essa classe usa:

```java
ObjectOutputStream
```

para salvar objetos, e:

```java
ObjectInputStream
```

para carregar objetos.

Essa parte permite que o programa não precise indexar todos os arquivos novamente a cada execução.

---

# 15. Funcionamento do programa

O funcionamento geral do programa é:

```text
1. O usuário executa o programa.
2. O menu é exibido.
3. O usuário escolhe indexar um diretório.
4. O programa percorre a pasta e subpastas.
5. O programa lê arquivos .txt.
6. As palavras são extraídas.
7. As palavras são normalizadas.
8. As palavras válidas são adicionadas ao índice.
9. O índice é salvo em indice.dat.
10. O usuário pode pesquisar palavras.
11. O usuário pode encerrar o programa.
12. Em outra execução, pode carregar indice.dat sem reindexar.
```

---

# 16. Como executar

1. Abra o projeto no Eclipse.

2. Execute a classe:

```text
app.Principal
```

3. Crie uma pasta chamada `arquivos` na raiz do projeto.

Exemplo:

```text
BuscadorDeArquivos
├── src
├── arquivos
│   ├── aula1.txt
│   └── aula2.txt
└── indice.dat
```

4. No menu, escolha:

```text
1 - Indexar diretorio
```

5. Quando o programa pedir o caminho, digite:

```text
arquivos
```

6. Depois disso, o programa criará o arquivo:

```text
indice.dat
```

7. Para pesquisar, escolha:

```text
3 - Pesquisar palavra(s)
```

8. Para carregar um índice já salvo, escolha:

```text
2 - Carregar indice salvo
```

---

# 17. Exemplo de teste

## Arquivo `aula1.txt`

```text
Java usa mapa de dispersao.
Java tambem usa lista encadeada.
```

## Arquivo `aula2.txt`

```text
Mapa de dispersao ajuda na busca.
Pilha fila e arvore tambem sao estruturas.
```

---

## Pesquisa por `java`

Resultado esperado:

```text
aula1.txt
```

---

## Pesquisa por `mapa`

Resultado esperado:

```text
aula1.txt
aula2.txt
```

---

## Pesquisa por `java mapa`

Resultado esperado:

```text
aula1.txt
```

Porque `aula1.txt` contém as duas palavras.

---

# 18. Observação sobre o arquivo `indice.dat`

O arquivo `indice.dat` é gerado automaticamente pelo programa.

Ele não deve ser editado manualmente.

Esse arquivo não é um texto comum.

Ele é um arquivo binário criado pelo Java durante o processo de serialização.

Por isso, se ele for aberto no editor, podem aparecer caracteres estranhos.

Isso é normal.

---

# 19. Conclusão

O projeto implementa um buscador simples de arquivos de texto usando estruturas de dados estudadas na disciplina.

A aplicação utiliza:

```text
ListaEncadeada
MapaDispersao
Índice invertido
Serialização
Leitura de arquivos
Busca por uma ou várias palavras
```

O mapa de dispersão foi usado para associar palavras às listas de documentos.

A lista encadeada foi usada tanto para tratar colisões no mapa quanto para armazenar os documentos de cada palavra.

A serialização foi usada para salvar o índice no disco e permitir carregá-lo novamente sem reindexar os arquivos.

Com isso, o programa consegue indexar arquivos `.txt`, salvar o índice, carregar o índice salvo e realizar buscas eficientes em memória.