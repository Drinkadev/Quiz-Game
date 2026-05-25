# Quiz-Game

## Nome e descrição do projeto
Jogo de perguntas estilo “Show do Milhão”, executado no terminal, onde o jogador responde questões carregadas de um arquivo `questions.json` e pode utilizar ajudas extras.

## Pré-requisitos e como executar
- JDK 17+ instalado.
- Maven disponível no PATH.

Compilar:
- `mvn compile`

Executar o programa:
- `mvn exec:java`

Rodar os testes:
- `mvn test`

## Como jogar
- As perguntas são apresentadas em ordem por dificuldade: fáceis, médias e difíceis.
- Para cada pergunta, o jogador digita o número da alternativa correta.
- Se errar, o jogo termina imediatamente.
- Para vencer, o jogador precisa acertar todas as perguntas.

## Tabela de ajudas
- **[H] Dica**: exibe a dica da pergunta.
- **[X] 50/50**: reduz as opções, removendo algumas respostas incorretas.
- **[P] Pular**: pula a pergunta.

## Regra do bônus
- Acertar **5 perguntas fáceis ou médias seguidas** concede uma **ajuda extra aleatória**.
- Perguntas difíceis **não contam** para essa sequência.
- Ajuda extra pode acumular acima de 1.
- A ajuda extra é sorteada entre: Dica, 50/50 ou Pular.

## Estrutura de pastas do projeto
- `src/main/java/com/example/`: código-fonte Java.
- `src/main/resources/questions.json`: arquivo com as perguntas.
- `src/test/java/com/example/`: testes automatizados.

## Como adicionar perguntas no `questions.json`
O arquivo `questions.json` deve conter uma lista de objetos com o formato:

```json
[
  {
    "question": "Qual é a capital do Brasil?",
    "options": ["Brasília", "Rio de Janeiro", "Salvador"],
    "answer": 0,
    "difficulty": "fácil",
    "hint": "Pense no Distrito Federal."
  }
]
```

Observações:
- `answer` é o índice (0-based) da opção correta dentro de `options`.
- `difficulty` pode ser `fácil/facil`, `média/media` e `difícil/dificil`.

## Tecnologias utilizadas
- Java 17
- Maven
- Gson (parser do `questions.json`)
- JUnit Jupiter 5 (testes unitários)

